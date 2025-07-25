plugins {
    id("java-library")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    
    // Engine modules for ServiceLoader discovery during testing
    testRuntimeOnly(project(":engines:statevector"))
    testRuntimeOnly(project(":engines:stabilizer"))
}

var incubatorArguments = listOf("--enable-preview", "--add-modules", "jdk.incubator.vector")

tasks.withType<JavaCompile> {
    options.compilerArgs = options.compilerArgs + incubatorArguments
}

tasks.withType<Test> {
    jvmArgs = incubatorArguments
}

tasks.withType<JavaExec> {
    jvmArgs = incubatorArguments
}
