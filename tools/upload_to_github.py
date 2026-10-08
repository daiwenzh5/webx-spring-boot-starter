"""
通过 GitHub Contents API 把本地仓库的所有文件上传到 GitHub。
由于网络环境无法走 git push，改用 gh CLI 已建立的认证通道走 REST API。

策略：
  - 把要上传的文件 base64 编码
  - 对每个文件 PUT 到 /repos/{owner}/{repo}/contents/{path}
  - 第一个文件时 GitHub 自动创建 main 分支的初始 commit
"""
from __future__ import annotations

import base64
import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(r"C:\NoneDrive\Agents\Mimo\web toolkit\webx-spring-boot-starter")
REPO = "daiwenzh5/webx-spring-boot-starter"

# 排除：.git 目录、tools/ 临时脚本、index.html 项目预览页（项目预览放在仓库外）
EXCLUDE_DIRS = {".git", ".gradle", "build", "node_modules"}
EXCLUDE_FILES = {"index.html"}  # 这是项目根的工作目录预览页，不是仓库内容


def upload_one(rel_path: str, content_bytes: bytes, message: str) -> bool:
    api_path = f"/repos/{REPO}/contents/{rel_path}"
    encoded = base64.b64encode(content_bytes).decode("ascii")
    payload = {
        "message": message,
        "content": encoded,
        "branch": "main",
    }

    # 用 gh api 调用，--input 通过 stdin 传 body
    result = subprocess.run(
        ["gh", "api", "--method", "PUT", api_path, "--input", "-"],
        input=json.dumps(payload).encode("utf-8"),
        capture_output=True,
        check=False,
    )
    if result.returncode != 0:
        stderr = result.stderr.decode("utf-8", errors="replace")[:500]
        print(f"  [ERR] {rel_path}: {stderr}")
        return False
    return True


def main():
    files = []
    for p in ROOT.rglob("*"):
        if not p.is_file():
            continue
        rel = p.relative_to(ROOT)
        parts = rel.parts
        if any(part in EXCLUDE_DIRS for part in parts):
            continue
        if rel.name in EXCLUDE_FILES:
            continue
        files.append((p, rel))

    files.sort(key=lambda x: str(x[1]))
    print(f"Uploading {len(files)} files to {REPO}...")

    succeeded = 0
    failed = 0
    for i, (abs_path, rel) in enumerate(files, 1):
        try:
            content = abs_path.read_bytes()
        except OSError as e:
            print(f"  [{i}/{len(files)}] SKIP {rel}: {e}")
            failed += 1
            continue

        ok = upload_one(
            str(rel).replace("\\", "/"),
            content,
            message=f"upload {rel}",
        )
        marker = "OK  " if ok else "FAIL"
        print(f"  [{i}/{len(files)}] {marker} {rel}")
        if ok:
            succeeded += 1
        else:
            failed += 1

    print(f"\nDone. Succeeded: {succeeded}, Failed: {failed}")
    sys.exit(0 if failed == 0 else 1)


if __name__ == "__main__":
    main()
