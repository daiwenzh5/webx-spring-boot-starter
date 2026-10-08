// 根构建脚本：插件版本对齐 + 共享 POM 元数据 + 共享 GitHub Packages 仓库
//
// 不在 plugins{} block 里声明 maven-publish：Gradle 8.x 不允许对核心插件 apply false。
plugins {
    alias(libs.plugins.kotlinJvm) apply false
}

allprojects {
    group = "io.github.daiwenzh5.webx"
    version = (project.findProperty("version") as? String) ?: "0.1.0-SNAPSHOT"
}

val githubPackagesUrl = "https://maven.pkg.github.com/daiwenzh5/webx-spring-boot-starter"

subprojects {
    val isPublishable = project.name in setOf("webx-core", "webx-sb2-starter", "webx-sb3-starter")

    if (isPublishable) {
        apply(plugin = "maven-publish")

        afterEvaluate {
            val pub = extensions.getByType(org.gradle.api.publish.PublishingExtension::class.java)

            // 创建 publication（在 java-library 已加载后）
            pub.publications.create<MavenPublication>("maven") {
                from(components["java"])
            }

            pub.repositories.maven {
                name = "GitHubPackages"
                url = uri(githubPackagesUrl)
                credentials {
                    val user: String? = (project.findProperty("gpr.user") as? String)
                        ?: System.getenv("GITHUB_ACTOR")
                    // 优先 GRADLE_PUBLISH_TOKEN（CI 中是 PAT），其次 GITHUB_TOKEN（本地开发）
                    val token: String? = (project.findProperty("gpr.token") as? String)
                        ?: System.getenv("GRADLE_PUBLISH_TOKEN")
                        ?: System.getenv("GITHUB_TOKEN")
                    if (user != null) setUsername(user)
                    if (token != null) setPassword(token)
                }
            }

            // 在所有 publication 上设置 artifactId + POM
            pub.publications.withType(org.gradle.api.publish.maven.MavenPublication::class.java).configureEach {
                artifactId = when (project.name) {
                    "webx-core" -> "webx-core"
                    "webx-sb2-starter" -> "webx-spring-boot-starter-2"
                    "webx-sb3-starter" -> "webx-spring-boot-starter"
                    else -> project.name
                }
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
}
