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
    implementation(libs.picocli)
    
    annotationProcessor(libs.picocli.codegen)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
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