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
include(":example")
