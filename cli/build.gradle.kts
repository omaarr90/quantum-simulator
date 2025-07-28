plugins {
    application
}

var incubatorArguments = listOf("--enable-preview", "--add-modules", "jdk.incubator.vector")

application {
    mainClass.set("com.omaarr90.Main")
    applicationDefaultJvmArgs = incubatorArguments
}

dependencies {
    implementation(project(":core"))
    implementation(project(":parser"))
    implementation(project(":engines:statevector"))
    implementation("info.picocli:picocli:4.7.5")
    
    annotationProcessor("info.picocli:picocli-codegen:4.7.5")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    jvmArgs = incubatorArguments
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("qsim")
            mainClass.set(application.mainClass)
            buildArgs.addAll(
                "--enable-preview",
                "--add-modules", "jdk.incubator.vector",
                "-H:+UnlockExperimentalVMOptions",
                "-O3", "-march=native"
            )
        }
    }
}