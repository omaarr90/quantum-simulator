plugins {
    application
    id("org.graalvm.buildtools.native")
}

application {
    mainClass.set("com.omaarr90.Main")
}

dependencies {
    implementation(project(":core"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("qsim")
            mainClass.set(application.mainClass)
        }
    }
}