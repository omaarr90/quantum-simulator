package com.omaarr90.core.circuit;

import com.omaarr90.core.gate.GateType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for CircuitBuilder DSL.
 * Covers all acceptance tests from the requirements.
 */
class CircuitBuilderTest {

    @Nested
    @DisplayName("TC1: Sample Circuit Building")
    class SampleCircuitTest {
        
        @Test
        @DisplayName("Sample circuit builds without exceptions and has correct structure")
        void sampleCircuit() {
            // Build the sample circuit from the requirements
            var circuit = CircuitBuilder.of(3)
                    .h(0)
                    .cx(0, 1)
                    .rz(1, Math.PI / 2)
                    .barrier()          // full barrier
                    .measureAll()
                    .build();
            
            // Verify basic properties
            assertNotNull(circuit);
            assertEquals(3, circuit.qubitCount());
            assertEquals(4, circuit.operationCount()); // h, cx, rz, barrier
            assertEquals(3, circuit.measurementCount());
            assertEquals(3, circuit.classicalBits());
            assertTrue(circuit.createdAt().isBefore(Instant.now().plusSeconds(1)));
            
            // Verify operations
            var ops = circuit.ops();
            assertEquals(4, ops.size());
            
            // Check H gate
            assertTrue(ops.get(0) instanceof GateOp.Gate);
            var hGate = (GateOp.Gate) ops.get(0);
            assertEquals(GateType.H, hGate.type());
            assertArrayEquals(new int[]{0}, hGate.qubits());
            
            // Check CX gate
            assertTrue(ops.get(1) instanceof GateOp.Gate);
            var cxGate = (GateOp.Gate) ops.get(1);
            assertEquals(GateType.CX, cxGate.type());
            assertArrayEquals(new int[]{0, 1}, cxGate.qubits());
            
            // Check RZ gate
            assertTrue(ops.get(2) instanceof GateOp.Gate);
            var rzGate = (GateOp.Gate) ops.get(2);
            assertEquals(GateType.RZ, rzGate.type());
            assertArrayEquals(new int[]{1}, rzGate.qubits());
            assertArrayEquals(new double[]{Math.PI / 2}, rzGate.params());
            
            // Check barrier
            assertTrue(ops.get(3) instanceof GateOp.Barrier);
            var barrier = (GateOp.Barrier) ops.get(3);
            assertEquals(0, barrier.qubits().length); // Full barrier
            
            // Verify measurements
            var measureMap = circuit.measureMap();
            assertEquals(3, measureMap.size());
            assertEquals(0, measureMap.get(0));
            assertEquals(1, measureMap.get(1));
            assertEquals(2, measureMap.get(2));
        }
    }

    @Nested
    @DisplayName("TC2: Validation Tests")
    class ValidationTest {
        
        @Test
        @DisplayName("Out-of-range qubit index throws IllegalArgumentException")
        void outOfRangeQubitThrowsException() {
            var builder = CircuitBuilder.of(2);
            
            // Test negative qubit
            var exception1 = assertThrows(IllegalArgumentException.class, () -> builder.h(-1));
            assertTrue(exception1.getMessage().contains("out of range"));
            
            // Test qubit >= qubitCount
            var exception2 = assertThrows(IllegalArgumentException.class, () -> builder.h(2));
            assertTrue(exception2.getMessage().contains("out of range"));
            
            // Test multi-qubit gates
            var exception3 = assertThrows(IllegalArgumentException.class, () -> builder.cx(0, 3));
            assertTrue(exception3.getMessage().contains("out of range"));
        }
        
        @Test
        @DisplayName("Duplicate measurement throws IllegalArgumentException")
        void duplicateMeasurementThrowsException() {
            var builder = CircuitBuilder.of(2);
            
            // First measurement should succeed
            builder.measure(0, 0);
            
            // Second measurement on same qubit should fail
            var exception = assertThrows(IllegalArgumentException.class, () -> builder.measure(0, 1));
            assertTrue(exception.getMessage().contains("already measured"));
        }
        
        @Test
        @DisplayName("Invalid gate parameters throw exceptions")
        void invalidGateParametersThrowExceptions() {
            var builder = CircuitBuilder.of(2);
            
            // Same qubit for control and target
            var exception1 = assertThrows(IllegalArgumentException.class, () -> builder.cx(0, 0));
            assertTrue(exception1.getMessage().contains("must be different"));
            
            var exception2 = assertThrows(IllegalArgumentException.class, () -> builder.swap(1, 1));
            assertTrue(exception2.getMessage().contains("must be different"));
        }
        
        @Test
        @DisplayName("Negative classical bit index throws exception")
        void negativeClassicalBitThrowsException() {
            var builder = CircuitBuilder.of(2);
            
            var exception = assertThrows(IllegalArgumentException.class, () -> builder.measure(0, -1));
            assertTrue(exception.getMessage().contains("cannot be negative"));
        }
    }

    @Nested
    @DisplayName("TC3: Gate Operations")
    class GateOperationsTest {
        
        @Test
        @DisplayName("All single-qubit gates work correctly")
        void singleQubitGates() {
            var circuit = CircuitBuilder.of(1)
                    .h(0)
                    .x(0)
                    .y(0)
                    .z(0)
                    .rx(0, Math.PI)
                    .ry(0, Math.PI / 2)
                    .rz(0, Math.PI / 4)
                    .build();
            
            assertEquals(7, circuit.operationCount());
            
            var ops = circuit.ops();
            assertEquals(GateType.H, ((GateOp.Gate) ops.get(0)).type());
            assertEquals(GateType.X, ((GateOp.Gate) ops.get(1)).type());
            assertEquals(GateType.Y, ((GateOp.Gate) ops.get(2)).type());
            assertEquals(GateType.Z, ((GateOp.Gate) ops.get(3)).type());
            assertEquals(GateType.RX, ((GateOp.Gate) ops.get(4)).type());
            assertEquals(GateType.RY, ((GateOp.Gate) ops.get(5)).type());
            assertEquals(GateType.RZ, ((GateOp.Gate) ops.get(6)).type());
        }
        
        @Test
        @DisplayName("All multi-qubit gates work correctly")
        void multiQubitGates() {
            var circuit = CircuitBuilder.of(3)
                    .cx(0, 1)
                    .cz(1, 2)
                    .swap(0, 2)
                    .build();
            
            assertEquals(3, circuit.operationCount());
            
            var ops = circuit.ops();
            var cxGate = (GateOp.Gate) ops.get(0);
            assertEquals(GateType.CX, cxGate.type());
            assertArrayEquals(new int[]{0, 1}, cxGate.qubits());
            
            var czGate = (GateOp.Gate) ops.get(1);
            assertEquals(GateType.CZ, czGate.type());
            assertArrayEquals(new int[]{1, 2}, czGate.qubits());
            
            var swapGate = (GateOp.Gate) ops.get(2);
            assertEquals(GateType.SWAP, swapGate.type());
            assertArrayEquals(new int[]{0, 2}, swapGate.qubits());
        }
        
        @Test
        @DisplayName("Generic gate methods work correctly")
        void genericGateMethods() {
            var circuit = CircuitBuilder.of(2)
                    .gate(GateType.H, 0)
                    .gate(GateType.RZ, Math.PI / 3, 1)
                    .gate(GateType.CX, 0, 1)
                    .build();
            
            assertEquals(3, circuit.operationCount());
            
            var ops = circuit.ops();
            assertEquals(GateType.H, ((GateOp.Gate) ops.get(0)).type());
            assertEquals(GateType.RZ, ((GateOp.Gate) ops.get(1)).type());
            assertArrayEquals(new double[]{Math.PI / 3}, ((GateOp.Gate) ops.get(1)).params());
            assertEquals(GateType.CX, ((GateOp.Gate) ops.get(2)).type());
        }
    }

    @Nested
    @DisplayName("TC4: Classical Controls")
    class ClassicalControlsTest {
        
        @Test
        @DisplayName("cIf gating appends operations")
        void cIfGatingAppendsOperations() {
            var circuit = CircuitBuilder.of(2)
                    .h(0)
                    .measure(0, 0)
                    .cIf(0, 1, builder -> builder.x(1))
                    .build();
            
            // Should have h, x operations (measure doesn't add to ops list)
            assertEquals(2, circuit.operationCount());
            assertEquals(1, circuit.measurementCount());
            
            var ops = circuit.ops();
            assertEquals(GateType.H, ((GateOp.Gate) ops.get(0)).type());
            assertEquals(GateType.X, ((GateOp.Gate) ops.get(1)).type());
        }
        
        @Test
        @DisplayName("cIf validates classical bit parameters")
        void cIfValidatesParameters() {
            var builder = CircuitBuilder.of(2);
            
            // Negative classical bit
            var exception1 = assertThrows(IllegalArgumentException.class, 
                () -> builder.cIf(-1, 0, b -> b.x(0)));
            assertTrue(exception1.getMessage().contains("cannot be negative"));
            
            // Invalid value
            var exception2 = assertThrows(IllegalArgumentException.class, 
                () -> builder.cIf(0, 2, b -> b.x(0)));
            assertTrue(exception2.getMessage().contains("must be 0 or 1"));
        }
    }

    @Nested
    @DisplayName("TC5: Barrier Functionality")
    class BarrierTest {
        
        @Test
        @DisplayName("Full barrier works correctly")
        void fullBarrier() {
            var circuit = CircuitBuilder.of(3)
                    .h(0)
                    .barrier()
                    .x(1)
                    .build();
            
            assertEquals(3, circuit.operationCount());
            
            var barrier = (GateOp.Barrier) circuit.ops().get(1);
            assertEquals(0, barrier.qubits().length); // Full barrier
        }
        
        @Test
        @DisplayName("Partial barrier works correctly")
        void partialBarrier() {
            var circuit = CircuitBuilder.of(3)
                    .h(0)
                    .barrier(0, 2)
                    .x(1)
                    .build();
            
            assertEquals(3, circuit.operationCount());
            
            var barrier = (GateOp.Barrier) circuit.ops().get(1);
            assertArrayEquals(new int[]{0, 2}, barrier.qubits());
        }
        
        @Test
        @DisplayName("Barrier validates qubit indices")
        void barrierValidatesQubits() {
            var builder = CircuitBuilder.of(2);
            
            var exception = assertThrows(IllegalArgumentException.class, () -> builder.barrier(0, 3));
            assertTrue(exception.getMessage().contains("out of range"));
        }
    }

    @Nested
    @DisplayName("TC6: Measurement Functionality")
    class MeasurementTest {
        
        @Test
        @DisplayName("Individual measurements work correctly")
        void individualMeasurements() {
            var circuit = CircuitBuilder.of(3)
                    .h(0)
                    .measure(0, 0)
                    .measure(2, 1)
                    .build();
            
            assertEquals(2, circuit.measurementCount());
            assertEquals(2, circuit.classicalBits());
            
            var measureMap = circuit.measureMap();
            assertEquals(0, measureMap.get(0));
            assertEquals(1, measureMap.get(2));
            assertFalse(measureMap.containsKey(1));
        }
        
        @Test
        @DisplayName("measureAll works correctly")
        void measureAllWorks() {
            var circuit = CircuitBuilder.of(3)
                    .h(0)
                    .measureAll()
                    .build();
            
            assertEquals(3, circuit.measurementCount());
            assertEquals(3, circuit.classicalBits());
            
            var measureMap = circuit.measureMap();
            assertEquals(0, measureMap.get(0));
            assertEquals(1, measureMap.get(1));
            assertEquals(2, measureMap.get(2));
        }
        
        @Test
        @DisplayName("Classical register auto-expands")
        void classicalRegisterAutoExpands() {
            var circuit = CircuitBuilder.of(2)
                    .measure(0, 5)  // Should expand to 6 classical bits
                    .build();
            
            assertEquals(6, circuit.classicalBits());
            assertEquals(5, circuit.measureMap().get(0));
        }
        
        @Test
        @DisplayName("measureAll preserves existing measurements")
        void measureAllPreservesExisting() {
            var circuit = CircuitBuilder.of(3)
                    .measure(1, 2)  // Custom mapping
                    .measureAll()   // Should not override existing
                    .build();
            
            assertEquals(3, circuit.measurementCount());
            assertEquals(3, circuit.classicalBits());
            
            var measureMap = circuit.measureMap();
            assertEquals(0, measureMap.get(0));
            assertEquals(2, measureMap.get(1)); // Preserved custom mapping
            assertEquals(2, measureMap.get(2));
        }
    }

    @Nested
    @DisplayName("Builder Properties")
    class BuilderPropertiesTest {
        
        @Test
        @DisplayName("Builder is chainable")
        void builderIsChainable() {
            // This should compile and work without issues
            var circuit = CircuitBuilder.of(2)
                    .h(0).x(1).cx(0, 1).barrier().measureAll().build();
            
            assertNotNull(circuit);
            assertEquals(4, circuit.operationCount()); // h, x, cx, barrier
        }
        
        @Test
        @DisplayName("Builder helper methods work")
        void builderHelperMethods() {
            var builder = CircuitBuilder.of(3)
                    .h(0)
                    .cx(0, 1)
                    .measure(0, 0);
            
            assertEquals(2, builder.operationCount());
            assertEquals(1, builder.measurementCount());
        }
        
        @Test
        @DisplayName("Circuit is immutable")
        void circuitIsImmutable() {
            var circuit = CircuitBuilder.of(2)
                    .h(0)
                    .measureAll()
                    .build();
            
            // Verify collections are immutable
            assertThrows(UnsupportedOperationException.class, 
                () -> circuit.ops().add(new GateOp.Gate(GateType.X, 0)));
            
            assertThrows(UnsupportedOperationException.class, 
                () -> circuit.measureMap().put(1, 1));
        }
        
        @Test
        @DisplayName("Negative qubit count throws exception")
        void negativeQubitCountThrowsException() {
            var exception = assertThrows(IllegalArgumentException.class, () -> CircuitBuilder.of(-1));
            assertTrue(exception.getMessage().contains("cannot be negative"));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTest {
        
        @Test
        @DisplayName("Empty circuit builds successfully")
        void emptyCircuitBuilds() {
            var circuit = CircuitBuilder.of(0).build();
            
            assertEquals(0, circuit.qubitCount());
            assertEquals(0, circuit.operationCount());
            assertEquals(0, circuit.measurementCount());
            assertEquals(0, circuit.classicalBits());
        }
        
        @Test
        @DisplayName("Single qubit circuit works")
        void singleQubitCircuit() {
            var circuit = CircuitBuilder.of(1)
                    .h(0)
                    .measure(0, 0)
                    .build();
            
            assertEquals(1, circuit.qubitCount());
            assertEquals(1, circuit.operationCount());
            assertEquals(1, circuit.measurementCount());
        }
        
        @Test
        @DisplayName("Large circuit builds efficiently")
        void largeCircuitBuilds() {
            var builder = CircuitBuilder.of(100);
            
            // Add many operations
            for (int i = 0; i < 100; i++) {
                builder.h(i);
            }
            
            var circuit = builder.measureAll().build();
            
            assertEquals(100, circuit.qubitCount());
            assertEquals(100, circuit.operationCount());
            assertEquals(100, circuit.measurementCount());
        }
    }
}