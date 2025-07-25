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

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("cli")
            mainClass.set(application.mainClass)
            buildArgs.addAll(
                "--enable-preview",
                "--add-modules=jdk.incubator.vector",
                "-H:+VectorAPISupport",
                "-H:+UnlockExperimentalVMOptions",
                "-O3", "-march=native"
            )
        }
    }
}