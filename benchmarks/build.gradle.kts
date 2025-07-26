plugins {
    id("java")
    id("me.champeau.jmh") version "0.7.2"
}

dependencies {
    implementation(project(":core"))
    implementation("org.openjdk.jmh:jmh-core:1.37")
    annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")
}

jmh {
    jvmArgs.addAll(listOf(
        "--add-modules", "jdk.incubator.vector",
        "--enable-preview"
    ))
    
    // Default JMH configuration for development
    fork.set(1)
    warmupIterations.set(3)
    iterations.set(5)
    timeUnit.set("ns")
    
    // Results format
    resultsFile.set(project.file("${project.buildDir}/reports/jmh/results.txt"))
    humanOutputFile.set(project.file("${project.buildDir}/reports/jmh/human.txt"))
}

// Task for smoke benchmark (CI-friendly)
tasks.register("jmhSmoke") {
    group = "benchmark"
    description = "Run smoke benchmark for CI"
    dependsOn("jmhCompileGeneratedClasses")
    
    doLast {
        javaexec {
            classpath = configurations.jmhRuntimeClasspath.get()
            mainClass.set("org.openjdk.jmh.Main")
            jvmArgs("--add-modules", "jdk.incubator.vector", "--enable-preview")
            args("-f", "1", "-wi", "1", "-i", "1", ".*ComplexArrayBenchmark.*")
        }
    }
}