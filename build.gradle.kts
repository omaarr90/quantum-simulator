plugins {
    id("java")
    id("org.graalvm.buildtools.native") version "0.11.0" apply false
    id("com.diffplug.spotless") version "6.25.0" apply false
    id("com.github.spotbugs") version "6.0.15" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
}

allprojects {
    group = "com.omaarr90"
    version = "1.0-SNAPSHOT"

    apply(plugin = "java")
    apply (plugin = "org.graalvm.buildtools.native")
    apply (plugin = "com.diffplug.spotless")
    apply (plugin = "com.github.spotbugs")
    apply (plugin = "org.jetbrains.dokka")

    repositories {
        mavenCentral()
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(24))
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
        options.compilerArgs = listOf("-parameters", "-Xdoclint:none", "-Xlint:all", "-Xlint:-exports",
                                      "-Xlint:-serial", "-Xlint:-try", "-Xlint:-requires-transitive-automatic",
                                      "-Xlint:-requires-automatic", "-Xlint:-missing-explicit-ctor",
                                      "-Xlint:-processing")
    }

    tasks.compileTestJava {
        options.encoding = "UTF-8"
        options.compilerArgs = listOf("-parameters", "-Xdoclint:none", "-Xlint:all", "-Xlint:-exports",
            "-Xlint:-serial", "-Xlint:-try", "-Xlint:-requires-transitive-automatic",
            "-Xlint:-requires-automatic", "-Xlint:-missing-explicit-ctor",
            "-Xlint:-processing")
    }

    tasks.test {
        useJUnitPlatform()
    }

    // Configure Spotless for code formatting
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        java {
            googleJavaFormat("1.22.0").aosp().reflowLongStrings()
            target("src/**/*.java")
            targetExclude("**/generated/**", "**/build/**")
        }
        kotlin {
            ktfmt("0.46").kotlinlangStyle()
            target("src/**/*.kt")
            targetExclude("**/generated/**", "**/build/**")
        }
    }

    // Configure SpotBugs for static analysis
    configure<com.github.spotbugs.snom.SpotBugsExtension> {
        ignoreFailures.set(false)
        showStackTraces.set(true)
        showProgress.set(true)
        effort.set(com.github.spotbugs.snom.Effort.MAX)
        reportLevel.set(com.github.spotbugs.snom.Confidence.MEDIUM)
    }

    // Make build fail on Spotless violations
    tasks.named("check") {
        dependsOn("spotlessCheck")
    }
}