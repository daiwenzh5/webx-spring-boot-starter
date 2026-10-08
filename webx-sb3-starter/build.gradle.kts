import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.kotlinJvm)
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api(project(":webx-core"))

    implementation(platform(libs.springBoot3Bom))

    api(libs.springBoot3Autoconfigure)
    // 提供 HttpMessageConverter / ServerHttpRequest / ResponseBodyAdvice 等
    api("org.springframework.boot:spring-boot-starter-web")
    api(libs.jakartaServletApi)

    api(libs.kotlin.stdlib)

    testImplementation(libs.junit5)
}
