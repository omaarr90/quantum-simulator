plugins {
    id("java")
    alias(libs.plugins.jmh)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.jmh.core)
    annotationProcessor(libs.jmh.generator.annprocess)
}

jmh {
    jvmArgs.addAll(listOf(
        "--add-modules", "jdk.incubator.vector",
        "--enable-preview",
        "-Djdk.incubator.concurrent.enablePreview",
        "-Xms4g",
        "-Xmx4g"
    ))
    
    // Default JMH configuration for development
    fork.set(1)
    warmupIterations.set(3)
    iterations.set(5)
    timeUnit.set("ns")
    
    // Results format
    resultsFile.set(project.file("${project.buildDir}/reports/jmh/results.txt"))
    humanOutputFile.set(project.file("${project.buildDir}/reports/jmh/human.txt"))
    resultFormat.set("JSON")
}

// Task for smoke benchmark (CI-friendly)
tasks.register("jmhSmoke") {
    group = "benchmark"
    description = "Run smoke benchmark for CI"
    dependsOn("jmhCompileGeneratedClasses")
    
    doLast {
        javaexec {
            classpath = configurations.jmhRuntimeClasspath.get() + 
                       sourceSets.main.get().runtimeClasspath +
                       sourceSets.getByName("jmh").runtimeClasspath
            mainClass.set("org.openjdk.jmh.Main")
            jvmArgs("--add-modules", "jdk.incubator.vector", "--enable-preview")
            args("-f", "1", "-wi", "1", "-i", "1", ".*ComplexArrayBenchmark.*")
        }
    }
}

// Task for StateVector parallel benchmark
tasks.register("jmhParallel") {
    group = "benchmark"
    description = "Run StateVector parallel execution benchmark"
    dependsOn("jmhCompileGeneratedClasses")
    
    doLast {
        val outputDir = project.file("${project.buildDir}/reports/jmh")
        outputDir.mkdirs()
        
        javaexec {
            classpath = configurations.jmhRuntimeClasspath.get() + 
                       sourceSets.main.get().runtimeClasspath +
                       sourceSets.getByName("jmh").runtimeClasspath
            mainClass.set("org.openjdk.jmh.Main")
            jvmArgs(
                "--add-modules", "jdk.incubator.vector",
                "--enable-preview",
                "-Djdk.incubator.concurrent.enablePreview",
                "-Xms4g", "-Xmx4g"
            )
            args(
                "-f", "1",
                "-wi", "5",
                "-i", "10",
                "-rf", "JSON",
                "-rff", "${outputDir}/statevector-parallel.json",
                ".*StateVectorParallelBenchmark.*"
            )
        }
    }
}

// Task for CI benchmark with specific parameters
tasks.register("jmhCI") {
    group = "benchmark"
    description = "Run StateVector parallel benchmark for CI (qubits=20, gates=1024)"
    dependsOn("jmhCompileGeneratedClasses")
    
    doLast {
        val outputDir = project.file("${project.buildDir}/reports/jmh")
        outputDir.mkdirs()
        
        javaexec {
            classpath = configurations.jmhRuntimeClasspath.get() + 
                       sourceSets.main.get().runtimeClasspath +
                       sourceSets.getByName("jmh").runtimeClasspath
            mainClass.set("org.openjdk.jmh.Main")
            jvmArgs(
                "--add-modules", "jdk.incubator.vector",
                "--enable-preview",
                "-Djdk.incubator.concurrent.enablePreview",
                "-Xms4g", "-Xmx4g"
            )
            args(
                "-f", "1",
                "-wi", "5",
                "-i", "10",
                "-rf", "JSON",
                "-rff", "${outputDir}/statevector-parallel.json",
                "-p", "qubits=20",
                "-p", "gates=1024",
                ".*StateVectorParallelBenchmark.*"
            )
        }
    }
}