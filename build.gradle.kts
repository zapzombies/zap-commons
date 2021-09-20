import io.github.zap.build.gradle.convention.*

// Uncomment to use local maven version - help local testing faster
plugins {
    id("io.github.zap.build.gradle.convention.lib") version "1.0.0"
    //id("io.github.zap.build.gradle.convention.lib") version "1.0.0"
}

dependencies {
    paperApi("1.16.5-R0.1-SNAPSHOT")
}

publishToZGpr()
