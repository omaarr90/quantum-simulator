plugins {
    id("java")
    id("org.graalvm.buildtools.native") version "0.11.0" apply false
    id("com.diffplug.spotless") version "6.25.0" apply false
    id("com.github.spotbugs") version "6.0.15" apply false
}

allprojects {
    group = "com.omaarr90"
    version = "1.0-SNAPSHOT"

    apply(plugin = "java")
    apply (plugin = "org.graalvm.buildtools.native")
    apply (plugin = "com.diffplug.spotless")
    apply (plugin = "com.github.spotbugs")

    repositories {
        mavenCentral()
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(24))
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}