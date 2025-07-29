plugins {
    id("antlr")
}

dependencies {
    // Core module dependency
    implementation(project(":core"))
    
    // ANTLR dependencies
    antlr(libs.antlr4)
    implementation(libs.antlr4.runtime)
    
    // Test dependencies
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor", "-package", "com.omaarr90.parser.qasm")
    outputDirectory = file("src/main/java/com/omaarr90/parser/qasm")
}

// Ensure generated sources are compiled
sourceSets {
    main {
        java {
            srcDirs("src/main/java", "src/main/antlr")
        }
    }
}

// GraalVM native image compatibility will be configured later