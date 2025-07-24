plugins {
    id("java")
    id("org.graalvm.buildtools.native") version "0.11.0" apply false
}

allprojects {
    group = "com.omaarr90"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}