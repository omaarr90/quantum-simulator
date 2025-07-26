# Quantum Simulator

A modular quantum circuit simulation framework with pluggable simulation engines.

## Available Engines

The following simulation engines are available:

- **statevector** - State vector simulation engine for exact quantum state simulation
- **stabilizer** - Stabilizer simulation engine for efficient Clifford circuit simulation  
- **noop** - No-operation engine that returns empty results without performing simulation

## Adding New Engines

To add a new simulation engine to the framework:

### 1. Create Engine Module

Create a new module under the `engines/` directory:

```
engines/
└── your-engine/
    ├── build.gradle.kts
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── com/omaarr90/qsim/yourengine/
        │   │       └── YourEngine.java
        │   └── resources/
        │       └── META-INF/
        │           └── services/
        │               └── com.omaarr90.core.engine.SimulatorEngine
        └── test/
            └── java/
                └── com/omaarr90/qsim/yourengine/
                    └── YourEngineTest.java
```

### 2. Implement SimulatorEngine Interface

Your engine class must implement `com.omaarr90.core.engine.SimulatorEngine`:

```java
public final class YourEngine implements SimulatorEngine {
    
    @Override
    public SimulationResult run(Circuit circuit) {
        // Implement your simulation logic here
        // Return appropriate SimulationResult implementation
    }
    
    @Override
    public String id() {
        return "your-engine"; // Unique identifier
    }
}
```

### 3. Register with ServiceLoader

Create the service registration file at:
`src/main/resources/META-INF/services/com.omaarr90.core.engine.SimulatorEngine`

Add your engine's fully qualified class name:
```
com.omaarr90.qsim.yourengine.YourEngine
```

### 4. Add to Project Configuration

Add your module to `settings.gradle.kts`:
```kotlin
include("engines:your-engine")
```

### 5. Create Tests

Add tests to verify your engine is discoverable via ServiceLoader and functions correctly.

### 6. Build Configuration

Create `build.gradle.kts` for your engine module:
```kotlin
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
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
```

## Building and Testing

To build the project:
```bash
./gradlew build
```

To run tests:
```bash
./gradlew test
```

To build native image:
```bash
./gradlew nativeCompile
```