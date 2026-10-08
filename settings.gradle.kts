rootProject.name = "webx-spring-boot-starter"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

include(":webx-core")
include(":webx-sb2-starter")
include(":webx-sb3-starter")

// example 模块仅在 JDK >= 17 时 include，避免在 JDK 8 runner 上评估 SB 3.x plugin 失败
val includeExample = System.getProperty("java.version").let { v ->
    // java.version 形如 "17.0.10"、"1.8.0_392"
    val major = v.substringBefore('.').toIntOrNull() ?: 0
    val effective = if (v.startsWith("1.")) v.substring(2).substringBefore('.').toIntOrNull() ?: 0 else major
    effective >= 9
}
if (includeExample) {
    include(":example")
    println("[settings] JVM ${System.getProperty("java.version")} -> include :example")
} else {
    println("[settings] JVM ${System.getProperty("java.version")} -> skip :example")
}
