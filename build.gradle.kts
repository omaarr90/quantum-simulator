plugins {
    id("java")
    alias(libs.plugins.graalvm.buildtools) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.dokka) apply false
}

allprojects {
    group = "com.omaarr90"
    version = "1.0-SNAPSHOT"

    apply(plugin = "java")
    apply(plugin = "org.graalvm.buildtools.native")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "org.jetbrains.dokka")

    repositories {
        mavenCentral()
        mavenLocal()
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(24))
    }

    spotless {
        java {
            target("src/**/*.java")
            eclipse().configFile(rootProject.file("config/eclips-format.xml"))
            importOrderFile(rootProject.file("config/eclipse.importoder"))
            removeUnusedImports()
            formatAnnotations()
        }
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
        options.compilerArgs = listOf(
            "-parameters", "-Xdoclint:none", "-Xlint:all", "-Xlint:-exports",
            "-Xlint:-serial", "-Xlint:-try", "-Xlint:-requires-transitive-automatic",
            "-Xlint:-requires-automatic", "-Xlint:-missing-explicit-ctor",
            "-Xlint:-processing"
        )
    }

    tasks.compileTestJava {
        options.encoding = "UTF-8"
        options.compilerArgs = listOf(
            "-parameters", "-Xdoclint:none", "-Xlint:all", "-Xlint:-exports",
            "-Xlint:-serial", "-Xlint:-try", "-Xlint:-requires-transitive-automatic",
            "-Xlint:-requires-automatic", "-Xlint:-missing-explicit-ctor",
            "-Xlint:-processing"
        )
    }

    tasks.test {
        useJUnitPlatform()
    }

    // Configure Spotless for code formatting
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        java {
            googleJavaFormat(libs.versions.google.java.format.get()).aosp().reflowLongStrings()
            target("src/**/*.java")
            targetExclude("**/generated/**", "**/build/**", "**/qasm/**")
        }
        kotlin {
            ktfmt(libs.versions.ktfmt.get()).kotlinlangStyle()
            target("src/**/*.kt")
            targetExclude("**/generated/**", "**/build/**")
        }
    }


    // Make build fail on Spotless violations
    tasks.named("check") {
        dependsOn("spotlessCheck")
    }
}