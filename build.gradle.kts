// 根构建脚本：插件版本对齐 + 共享 POM 元数据 + 共享 GitHub Packages 仓库
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    `maven-publish` apply false
}

allprojects {
    group = "io.github.daiwenzh5.webx"
    // CI 发布时通过 -Pversion=0.1.0 覆盖；本地开发默认 SNAPSHOT
    version = (project.findProperty("version") as? String) ?: "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

val githubPackagesUrl = "https://maven.pkg.github.com/daiwenzh5/webx-spring-boot-starter"

listOf("webx-core", "webx-sb2-starter", "webx-sb3-starter").forEach { moduleName ->
    project(":$moduleName").apply {
        apply(plugin = "maven-publish")

        extensions.configure(org.gradle.api.publish.PublishingExtension::class.java).apply {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri(githubPackagesUrl)
                    credentials {
                        // CI 通过环境变量 GITHUB_TOKEN / GITHUB_ACTOR 注入；
                        // 本地开发在 ~/.gradle/gradle.properties 配置 gpr.user / gpr.token
                        val user: String? = (project.findProperty("gpr.user") as? String)
                            ?: System.getenv("GITHUB_ACTOR")
                        val token: String? = (project.findProperty("gpr.token") as? String)
                            ?: System.getenv("GITHUB_TOKEN")
                        if (user != null) setUsername(user)
                        if (token != null) setPassword(token)
                    }
                }
            }
        }
    }
}

// 共享 POM 元数据：所有 MavenPublication 自动应用
allprojects {
    extensions.configure(org.gradle.api.publish.PublishingExtension::class.java) {
        publications.withType(org.gradle.api.publish.maven.MavenPublication::class.java).configureEach {
            pom {
                name.set(project.name)
                description.set("Web extension starter for Spring Boot")
                url.set("https://github.com/daiwenzh5/webx-spring-boot-starter")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("daiwenzh5")
                        name.set("daiwenzh5")
                        url.set("https://github.com/daiwenzh5")
                    }
                }
                scm {
                    url.set("https://github.com/daiwenzh5/webx-spring-boot-starter")
                    connection.set("scm:git:git://github.com/daiwenzh5/webx-spring-boot-starter.git")
                    developerConnection.set("scm:git:ssh://git@github.com/daiwenzh5/webx-spring-boot-starter.git")
                }
            }
        }
    }
}
