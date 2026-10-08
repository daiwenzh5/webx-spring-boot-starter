plugins {
    alias(libs.plugins.kotlin.jvm)
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

    implementation(platform(libs.spring.boot.`3-bom`))

    api(libs.spring.boot.`3-autoconfigure`)
    api(libs.jakarta.servlet.api)

    api(libs.kotlin.stdlib)

    testImplementation(libs.junit5)
    testImplementation("org.springframework.boot:spring-boot-starter-web")
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            artifactId = "webx-spring-boot-starter"
        }
    }
}
