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

    // @ConfigurationProperties / @ConstructorBinding 来自 spring-boot-autoconfigure。
    // core 编译期需要它们，但运行期由 sb2/sb3 starter 提供（各自 BOM 版本）。
    // SB 注解 API 在 2.7 与 3.x 之间稳定兼容，所以固定一个 compileOnly 版本即可。
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:3.3.5")

    testImplementation(libs.junit5)
}
