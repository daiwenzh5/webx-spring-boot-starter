plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

java {
    // webx-core 是被 sb2/sb3 都引用的最低公共层，必须保�?jvmTarget=1.8
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

    testImplementation(libs.junit5)
}
