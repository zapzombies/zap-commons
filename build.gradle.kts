import io.github.zap.build.gradle.convention.*

plugins {
    id("io.github.zap.build.gradle.convention.lib") version "1.1.0"
}

dependencies {
    paperApi("1.16.5-R0.1-SNAPSHOT")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.google.inject:guice:5.0.1")
}

publishToZGpr()
