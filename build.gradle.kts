import io.github.zap.build.gradle.convention.*

plugins {
    id("io.github.zap.build.gradle.convention.shadow-mc-plugin") version "1.1.0"
}

dependencies {
    paperApi("1.16.5-R0.1-SNAPSHOT")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.google.inject:guice:5.0.1")

    relocate("com.fasterxml.jackson.core:jackson-core:2.12.5")
    relocate("com.fasterxml.jackson.core:jackson-databind:2.12.5")
    relocate("com.fasterxml.jackson.core:jackson-annotations:2.12.5")
}

publishToZGpr()
