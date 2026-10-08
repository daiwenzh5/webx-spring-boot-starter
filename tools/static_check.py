"""
webx-spring-boot-starter 静态检查脚本。
检查项：
  1. 所有 Kotlin 源文件的 package 声明与所在目录一致
  2. settings.gradle.kts 列出的 module 目录都有 build.gradle.kts
  3. starter 模块的 META-INF 注册文件存在
  4. 没有遗留 com.example.webx 占位符
  5. javax.servlet 只在 sb2 模块；jakarta.servlet 只在 sb3 模块
  6. GitHub Actions workflow 结构合法
  7. 根 build.gradle.kts 引用 maven-publish 插件

路径解析：优先用命令行参数 --root，其次环境变量 WEBX_ROOT，否则用脚本所在目录的父级。
"""
from __future__ import annotations

import argparse
import os
import re
import sys
from pathlib import Path

errors: list[str] = []
warnings: list[str] = []


def err(msg: str):
    errors.append(msg)
    print(f"  [ERR] {msg}")


def warn(msg: str):
    warnings.append(msg)
    print(f"  [WARN] {msg}")


def ok(msg: str):
    print(f"  [OK] {msg}")


def section(name: str):
    print(f"\n=== {name} ===")


def safe_read(f: Path) -> str | None:
    for enc in ("utf-8-sig", "utf-8", "gbk", "latin-1"):
        try:
            return f.read_text(encoding=enc)
        except UnicodeDecodeError:
            continue
    return None


def resolve_root() -> Path:
    parser = argparse.ArgumentParser(add_help=False)
    parser.add_argument("--root", type=str, default=None)
    args, _ = parser.parse_known_args()

    if args.root:
        return Path(args.root).resolve()
    if os.environ.get("WEBX_ROOT"):
        return Path(os.environ["WEBX_ROOT"]).resolve()
    # 默认：本脚本所在目录的父级（项目根）
    return Path(__file__).resolve().parent.parent


ROOT = resolve_root()
print(f"Project root: {ROOT}\n")

# ---------------------------------------------------------------------------
section("1. Package/directory consistency")

pkg_re = re.compile(r"^package\s+([\w.]+)", re.MULTILINE)
kt_files = list(ROOT.glob("**/src/main/kotlin/**/*.kt"))

for f in kt_files:
    rel = f.relative_to(ROOT)
    parts = rel.parts
    try:
        i = parts.index("kotlin")
        pkg_dirs = parts[i + 1 : -1]
    except ValueError:
        err(f"{rel}: kotlin/ segment missing")
        continue
    expected_pkg = ".".join(pkg_dirs)
    text = safe_read(f)
    if text is None:
        err(f"{rel}: cannot decode")
        continue
    m = pkg_re.search(text)
    actual_pkg = m.group(1) if m else None
    if actual_pkg is None:
        err(f"{rel}: missing package declaration")
    elif actual_pkg != expected_pkg:
        err(f"{rel}: package '{actual_pkg}' does not match dir '{expected_pkg}'")

if not errors:
    ok(f"all {len(kt_files)} Kotlin files have matching package")


# ---------------------------------------------------------------------------
section("2. No leftover placeholders")

for placeholder in ("com.example.webx", "com/example/webx"):
    found = [p for p in ROOT.rglob("*") if placeholder in str(p)]
    if found:
        for p in found[:5]:
            err(f"leftover placeholder {placeholder!r}: {p.relative_to(ROOT)}")
    else:
        ok(f"no leftover {placeholder!r}")


# ---------------------------------------------------------------------------
section("3. javax/jakarta separation")

for sb_dir, forbidden, expected in [
    ("webx-sb2-starter", ["jakarta.servlet", "jakarta.persistence"], "javax.servlet"),
    ("webx-sb3-starter", ["javax.servlet"], "jakarta.servlet"),
]:
    base = ROOT / sb_dir
    if not base.exists():
        err(f"module dir missing: {sb_dir}")
        continue
    kt_in_mod = list(base.rglob("*.kt"))
    for f in kt_in_mod:
        text = safe_read(f)
        if text is None:
            continue
        for bad in forbidden:
            if bad in text:
                err(f"{sb_dir}/{f.relative_to(base)}: contains {bad!r}")
    has_expected = any(expected in (safe_read(f) or "") for f in kt_in_mod)
    if has_expected:
        ok(f"{sb_dir}: uses {expected}")
    else:
        warn(f"{sb_dir}: no use of {expected} (filter may be missing)")


# ---------------------------------------------------------------------------
section("4. settings.gradle.kts modules")

settings_path = ROOT / "settings.gradle.kts"
if not settings_path.exists():
    err(f"settings.gradle.kts missing at {settings_path}")
else:
    settings = safe_read(settings_path) or ""
    module_re = re.compile(r'include\(["\']:([\w\-]+)["\']\)')
    modules = module_re.findall(settings)
    for m in modules:
        build_file = ROOT / m / "build.gradle.kts"
        if not build_file.exists():
            err(f"module :{m} listed but {build_file.relative_to(ROOT)} missing")
        else:
            ok(f"module :{m} -> {build_file.relative_to(ROOT)}")


# ---------------------------------------------------------------------------
section("5. META-INF registration files")

for starter_module, expected_files in [
    ("webx-sb2-starter", ["META-INF/spring.factories"]),
    ("webx-sb3-starter", [
        "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"
    ]),
]:
    base = ROOT / starter_module / "src" / "main" / "resources"
    for ef in expected_files:
        p = base / ef
        if not p.exists():
            err(f"{starter_module}: missing {ef}")
            continue
        content = (safe_read(p) or "").strip()
        if "." not in content:
            err(f"{starter_module}/{ef}: empty or invalid")
        else:
            ok(f"{starter_module}/{ef}: -> {content.splitlines()[0]}")


# ---------------------------------------------------------------------------
section("6. GitHub Actions workflows")

workflows_dir = ROOT / ".github" / "workflows"
if not workflows_dir.exists():
    warn(".github/workflows/ directory missing")
else:
    wf_files = sorted(workflows_dir.glob("*.yml")) + sorted(workflows_dir.glob("*.yaml"))
    for wf in wf_files:
        text = safe_read(wf)
        if text is None:
            err(f"{wf.relative_to(ROOT)}: cannot decode")
            continue
        if "jobs:" not in text or "steps:" not in text:
            err(f"{wf.relative_to(ROOT)}: missing jobs or steps")
        else:
            ok(f"{wf.relative_to(ROOT)}: structure valid")

    expected_workflows = {"ci.yml", "publish.yml"}
    actual = {wf.name for wf in wf_files}
    missing = expected_workflows - actual
    for m in missing:
        warn(f"recommended workflow missing: {m}")


# ---------------------------------------------------------------------------
section("7. Build scripts reference correct plugin ids")

root_build = ROOT / "build.gradle.kts"
if root_build.exists():
    text = safe_read(root_build) or ""
    if "maven-publish" not in text:
        err("root build.gradle.kts does not reference maven-publish plugin")
    else:
        ok("root build.gradle.kts: maven-publish plugin reference present")


# ---------------------------------------------------------------------------
section("Summary")
print(f"  Errors:   {len(errors)}")
print(f"  Warnings: {len(warnings)}")
sys.exit(1 if errors else 0)
