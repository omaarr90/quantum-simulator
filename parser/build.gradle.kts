plugins {
    id("antlr")
}

dependencies {
    // Core module dependency
    implementation(project(":core"))
    
    // ANTLR dependencies
    antlr("org.antlr:antlr4:4.13.1")
    implementation("org.antlr:antlr4-runtime:4.13.1")
    
    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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