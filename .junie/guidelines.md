# Quantum Simulator Development Guidelines

This document provides essential development information for the quantum simulator project, focusing on project-specific requirements and conventions.

## Build and Configuration

### Prerequisites
- **Java 24**: The project requires Java 24 with toolchain configuration
- **Gradle**: Uses Gradle build system with wrapper (use `./gradlew`)

### Key Build Features
- **Multi-module architecture**: 8 modules (core, parser, cli, benchmarks, noise, engines/*)
- **GraalVM Native Image**: Supported for CLI module with optimized native compilation
- **Advanced Java Features**: 
  - Preview features enabled (`--enable-preview`)
  - Incubator Vector API (`jdk.incubator.vector`) for SIMD optimizations
  - Modern language features (records, pattern matching, etc.)

### Essential Build Commands
```bash
# Build entire project
./gradlew build

# Run all tests
./gradlew test

# Code quality checks
./gradlew spotlessCheck spotbugsMain

# Native image compilation (requires GraalVM)
./gradlew :cli:nativeCompile

# Run benchmarks
./gradlew :benchmarks:jmh
```

### Module Dependencies
- **Core**: Foundation module with quantum circuit, engine, gate, and math components
- **Engines**: Pluggable simulation engines using ServiceLoader pattern
  - `engines:statevector`: State vector simulation
  - `engines:stabilizer`: Stabilizer formalism simulation  
  - `engines:noop`: No-operation engine for testing
- **Parser**: OpenQASM 3 parser using ANTLR grammar
- **CLI**: Command-line interface with native image support

## Testing

### Framework and Setup
- **JUnit 5** (Jupiter) with BOM version management (5.10.0)
- **ServiceLoader Testing**: Engine discovery tested at runtime
- **CI/CD**: GitHub Actions with matrix testing (Temurin + GraalVM JDK 24)

### Testing Conventions
```java
package com.omaarr90.core.math;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ExampleTest {
    private static final double EPSILON = 1e-9;

    @Test
    void testBasicFunctionality() {
        // Arrange
        Complex z = new Complex(3.0, 4.0);
        
        // Act
        double magnitude = z.abs();
        
        // Assert
        assertEquals(5.0, magnitude, EPSILON);
    }

    @Test
    void testEdgeCase() {
        Complex zero = Complex.ZERO;
        Complex nonZero = new Complex(1.0, 1.0);
        
        assertThrows(ArithmeticException.class, () -> nonZero.div(zero));
    }
}
```

### Running Tests
```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :core:test

# Run specific test class
./gradlew :core:test --tests "ComplexTest"

# Run with detailed output
./gradlew test --info
```

### Test Organization
- **Unit Tests**: Located in `src/test/java` following package structure
- **Test Resources**: QASM files and test data in `src/test/resources`
- **Integration Tests**: Engine discovery and circuit execution tests
- **Benchmarks**: JMH performance tests in dedicated `benchmarks` module

## Code Style and Development Patterns

### Code Quality Tools
- **Spotless**: Google Java Format with AOSP style, automatic formatting
- **SpotBugs**: Static analysis with custom exclusions for performance-critical code
- **Compiler Warnings**: Strict linting enabled with specific exclusions

### Key Development Patterns

#### 1. Immutability First
```java
/**
 * IMMUTABILITY GUARANTEE: This class is completely immutable.
 * Every operation returns a new instance.
 */
public record Complex(double real, double imaginary) {
    public Complex add(Complex other) {
        return new Complex(real + other.real, imaginary + other.imaginary);
    }
}
```

#### 2. Performance-Conscious Design
- Use primitive types over boxed types
- Leverage JIT optimizations (scalar replacement, SIMD)
- Provide both `abs()` and `abs2()` methods (avoid sqrt when possible)
- Consider memory allocation patterns in hot paths

#### 3. Comprehensive Documentation
```java
/**
 * Brief description of the class/method purpose.
 *
 * <p>Detailed explanation with implementation notes.
 *
 * <p><strong>PERFORMANCE NOTE:</strong> Specific optimization details.
 *
 * @param parameter description
 * @return description with constraints
 * @throws ExceptionType when and why
 */
```

#### 4. Modern Java Features
- **Records**: For immutable data structures
- **Pattern Matching**: In switch expressions where applicable
- **Text Blocks**: For multi-line strings (QASM, etc.)
- **Sealed Classes**: For controlled inheritance hierarchies

#### 5. Plugin Architecture
- Use ServiceLoader for engine discovery
- Implement proper service provider interfaces
- Include META-INF/services files for automatic discovery

### Code Formatting
```bash
# Check formatting
./gradlew spotlessCheck

# Apply formatting
./gradlew spotlessApply
```

### Static Analysis Exclusions
The project allows specific SpotBugs violations for performance reasons:
- `EI_EXPOSE_REP*`: Exposing internal representations (performance-critical code)
- `SF_SWITCH_NO_DEFAULT`: Missing default in exhaustive switches
- `DLS_DEAD_LOCAL_STORE`: Dead stores for code clarity

## Development Workflow

### Before Committing
```bash
# Full quality check
./gradlew spotlessCheck spotbugsMain test

# Or use the check task (includes spotlessCheck)
./gradlew check
```

### Adding New Modules
1. Add module to `settings.gradle.kts`
2. Create `build.gradle.kts` with appropriate plugins
3. Follow package naming convention: `com.omaarr90.*`
4. Add ServiceLoader configuration if implementing engines

### Performance Considerations
- The project uses incubator Vector API for SIMD operations
- Complex number operations are optimized for JIT compilation
- Consider memory allocation patterns in quantum state operations
- Use `abs2()` instead of `abs()` when possible to avoid square root

### Debugging
- Enable preview features in IDE settings
- Use `--enable-preview` and `--add-modules jdk.incubator.vector` JVM args
- Vector API operations may require specific JVM flags for optimal performance

## Architecture Notes

### Engine System
- Engines implement `SimulatorEngine` interface
- Automatic discovery via ServiceLoader
- Each engine returns specific result types (`StateVectorResult`, `StabilizerResult`, etc.)
- Engines are loaded at runtime from classpath

### Parser Integration
- OpenQASM 3 grammar generates ANTLR parser
- AST visitor pattern converts to internal circuit representation
- Generated files excluded from code quality checks

### Native Image Support
- CLI module configured for GraalVM native compilation
- Native image properties in `META-INF/native-image/`
- Reflection configuration may be needed for ServiceLoader

---

*Last updated: 2025-07-28*