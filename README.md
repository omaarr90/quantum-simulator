# Quantum Simulator

[![CI](https://github.com/omaarr90/quantum-simulator/actions/workflows/test.yml/badge.svg)](https://github.com/omaarr90/quantum-simulator/actions/workflows/test.yml)
[![codecov](https://codecov.io/gh/omaarr90/quantum-simulator/branch/main/graph/badge.svg)](https://codecov.io/gh/omaarr90/quantum-simulator)

A modular quantum circuit simulation framework with pluggable simulation engines.

## Quick Start

### Prerequisites

- **GraalVM 24** or **OpenJDK 24** with preview features enabled
- **Required GraalVM version**: GraalVM CE 24.0.1+ for native image compilation

### Installation

1. Clone the repository:
```bash
git clone https://github.com/omaarr90/quantum-simulator.git
cd quantum-simulator
```

2. Build the project:
```bash
./gradlew build
```

3. Build native image (requires GraalVM):
```bash
./gradlew :cli:nativeCompile
```

### Running Examples

#### Using the CLI

Run a simple Bell state circuit:
```bash
# Using JVM
./gradlew :cli:run

# Using native image (after building)
./cli/build/native/nativeCompile/cli
```

#### Using OpenQASM files

```bash
# Run with a QASM file
./cli/build/native/nativeCompile/cli path/to/circuit.qasm

# Example QASM content (bell.qasm):
OPENQASM 3.0;
include "stdgates.inc";

qubit[2] q;
bit[2] c;

h q[0];
cx q[0], q[1];
measure q -> c;
```

#### Programmatic Usage

```java
import com.omaarr90.core.circuit.CircuitBuilder;
import com.omaarr90.core.engine.SimulatorEngineRegistry;

// Build a quantum circuit
var circuit = CircuitBuilder.of(2)
    .h(0)                    // Hadamard gate on qubit 0
    .cx(0, 1)               // CNOT gate
    .measureAll()           // Measure all qubits
    .build();

// Run simulation with state vector engine
var engine = SimulatorEngineRegistry.getEngine("statevector");
var result = engine.run(circuit);
System.out.println("Result: " + result);
```

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
./gradlew :cli:nativeCompile
```

To run benchmarks:
```bash
# Full benchmark suite
./gradlew :benchmarks:jmh

# Smoke benchmark (CI-friendly)
./gradlew :benchmarks:jmhSmoke
```

To check code formatting:
```bash
# Check formatting
./gradlew spotlessCheck

# Apply formatting
./gradlew spotlessApply
```


## Documentation

### API Documentation

- **[Javadoc/Dokka Documentation](./build/dokka/html/index.html)** - Complete API documentation

Generate documentation:
```bash
./gradlew dokkaHtml
```

### Performance Benchmarks

JMH benchmark results are available in the CI artifacts and can be generated locally:
```bash
./gradlew :benchmarks:jmh
# Results will be in benchmarks/build/reports/jmh/
```

### Native Image

The CLI can be compiled to a native executable using GraalVM:
- **Image size**: ~61MB (well within 80-150MB target)
- **Startup time**: Near-instantaneous
- **Memory usage**: Reduced compared to JVM

## Architecture

The simulator follows a modular architecture with pluggable engines:

```
quantum-simulator/
├── core/           # Core simulation framework
├── cli/            # Command-line interface
├── parser/         # OpenQASM 3.0 parser
├── benchmarks/     # JMH performance benchmarks
├── engines/        # Simulation engines
│   ├── statevector/    # State vector simulation
│   ├── stabilizer/     # Stabilizer formalism
│   └── noop/          # No-operation (testing)
└── noise/          # Noise models (future)