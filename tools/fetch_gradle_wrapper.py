"""
下载 Gradle Wrapper 必需文件到 webx-spring-boot-starter 项目根目录。
不需要本机预装 Gradle / Java。
"""
import os
import sys
import urllib.request
from pathlib import Path

PROJECT_ROOT = Path(r"C:\NoneDrive\Agents\Mimo\web toolkit\webx-spring-boot-starter")
WRAPPER_DIR = PROJECT_ROOT / "gradle" / "wrapper"
WRAPPER_DIR.mkdir(parents=True, exist_ok=True)

# 锁定的 Gradle 版本（与 libs.versions.toml 配合）
GRADLE_VERSION = "8.10.2"

# 标准文件下载 URL（来自 Gradle 官方 distribution）
GRADLE_DIST = f"https://services.gradle.org/distributions/gradle-{GRADLE_VERSION}-bin.zip"
WRAPPER_JAR_URL = "https://raw.githubusercontent.com/gradle/gradle/v{ver}/gradle/wrapper/gradle-wrapper.jar"
GRADLEW_URL = "https://raw.githubusercontent.com/gradle/gradle/v{ver}/gradlew"
GRADLEW_BAT_URL = "https://raw.githubusercontent.com/gradle/gradle/v{ver}/gradlew.bat"


def fetch(url: str, dest: Path):
    print(f"  -> {dest.relative_to(PROJECT_ROOT)}")
    with urllib.request.urlopen(url, timeout=30) as r:
        data = r.read()
    dest.write_bytes(data)


def main():
    print(f"Downloading Gradle Wrapper {GRADLE_VERSION} into {PROJECT_ROOT}")

    # 1. gradle-wrapper.jar
    jar_url = WRAPPER_JAR_URL.format(ver=f"{GRADLE_VERSION}")
    fetch(jar_url, WRAPPER_DIR / "gradle-wrapper.jar")

    # 2. gradle-wrapper.properties
    props = f"""distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-{GRADLE_VERSION}-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
"""
    (WRAPPER_DIR / "gradle-wrapper.properties").write_text(props, encoding="utf-8")
    print("  -> gradle/wrapper/gradle-wrapper.properties")

    # 3. gradlew (POSIX shell)
    sh_url = GRADLEW_URL.format(ver=f"{GRADLE_VERSION}")
    fetch(sh_url, PROJECT_ROOT / "gradlew")
    os.chmod(PROJECT_ROOT / "gradlew", 0o755)

    # 4. gradlew.bat
    bat_url = GRADLEW_BAT_URL.format(ver=f"{GRADLE_VERSION}")
    fetch(bat_url, PROJECT_ROOT / "gradlew.bat")

    # 5. .gitattributes：保证 wrapper jar 在 Windows/Linux 上 LF/CRLF 一致
    (PROJECT_ROOT / ".gitattributes").write_text(
        "* text=auto eol=lf\n"
        "*.bat text eol=crlf\n"
        "*.jar binary\n",
        encoding="utf-8",
    )
    print("  -> .gitattributes")

    print("Done.")


if __name__ == "__main__":
    main()
