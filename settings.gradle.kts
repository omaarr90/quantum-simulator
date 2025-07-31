pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "quantum-simulator"

include("core")
include("parser")
include("noise")
include("cli")
include("benchmarks")
include("engines:statevector")
include("engines:stabilizer")
include("engines:noop")