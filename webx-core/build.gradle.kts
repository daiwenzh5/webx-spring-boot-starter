plugins {
    alias(libs.plugins.kotlinJvm)
    `java-library`
}

java {
    // webx-core 是被 sb2/sb3 都引用的最低公共层，必须保持 jvmTarget=1.8
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.slf4j.api)

    // @ConfigurationProperties 等注解来自 spring-boot-autoconfigure。
    // 用 compileOnly 不暴露版本（避免与 sb2/sb3 各自的 SB 版本冲突）；
    // sb2 / sb3 starter 模块已传递依赖各自的 spring-boot-autoconfigure。
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")

    testImplementation(libs.junit5)
}
