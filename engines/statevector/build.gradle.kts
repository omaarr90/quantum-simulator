plugins {
    id("java")
}

group = "com.omaarr90"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
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

tasks.test {
    useJUnitPlatform()
}