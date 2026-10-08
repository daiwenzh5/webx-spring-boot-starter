plugins {
    alias(libs.plugins.kotlinJvm)
    `java-library`
}

java {
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
    api(project(":webx-core"))

    // SB 2.7 BOM 仅在本模块生效
    implementation(platform(libs.springBoot2Bom))

    api(libs.springBoot2Autoconfigure)
    api(libs.javaxServletApi)

    // 运行时传递 kotlin-stdlib，业务项目无需显式声明
    api(libs.kotlin.stdlib)

    testImplementation(libs.junit5)
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            artifactId = "webx-spring-boot-starter-2"
        }
    }
}
