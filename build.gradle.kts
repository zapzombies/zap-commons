import io.github.zap.build.gradle.convention.*

// Uncomment to use local maven version - help local testing faster
plugins {
    // id("io.github.zap.build.gradle.convention.lib") version "0.0.0-SNAPSHOT"
    id("io.github.zap.build.gradle.convention.lib") version "1.0.0"
}

dependencies {
    paperApi("1.16.5-R0.1-SNAPSHOT")
    implementation("org.apache.commons:commons-lang3:3.12.0")
}

publishToZGpr()
