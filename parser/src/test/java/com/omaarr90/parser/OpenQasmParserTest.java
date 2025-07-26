package com.omaarr90.parser;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.GateOp;
import com.omaarr90.core.gate.GateType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for OpenQASM 3 parser functionality.
 */
class OpenQasmParserTest {

    @Test
    void testParseBellCircuit() throws ParseException, IOException {
        Path bellFile = Paths.get("src/test/resources/bell.qasm");
        Circuit circuit = OpenQasmParser.parse(bellFile);
        
        // Verify circuit structure
        assertEquals(2, circuit.qubitCount(), "Bell circuit should have 2 qubits");
        assertEquals(2, circuit.operationCount(), "Bell circuit should have 2 operations (H + CX)");
        assertEquals(2, circuit.measurementCount(), "Bell circuit should have 2 measurements");
        assertTrue(circuit.hasMeasurements(), "Bell circuit should have measurements");
        
        // Verify operations
        var ops = circuit.ops();
        
        // First operation should be H gate on qubit 0
        assertTrue(ops.get(0) instanceof GateOp.Gate);
        GateOp.Gate hGate = (GateOp.Gate) ops.get(0);
        assertEquals(GateType.H, hGate.type());
        assertArrayEquals(new int[]{0}, hGate.qubits());
        
        // Second operation should be CX gate on qubits 0,1
        assertTrue(ops.get(1) instanceof GateOp.Gate);
        GateOp.Gate cxGate = (GateOp.Gate) ops.get(1);
        assertEquals(GateType.CX, cxGate.type());
        assertArrayEquals(new int[]{0, 1}, cxGate.qubits());
        
        // Verify measurements
        var measureMap = circuit.measureMap();
        assertEquals(2, measureMap.size());
        assertEquals(0, measureMap.get(0)); // qubit 0 -> classical bit 0
        assertEquals(1, measureMap.get(1)); // qubit 1 -> classical bit 1
    }
    
    @Test
    void testParseGhzCircuit() throws ParseException, IOException {
        Path ghzFile = Paths.get("src/test/resources/ghz.qasm");
        Circuit circuit = OpenQasmParser.parse(ghzFile);
        
        // Verify circuit structure
        assertEquals(3, circuit.qubitCount(), "GHZ circuit should have 3 qubits");
        assertEquals(7, circuit.operationCount(), "GHZ circuit should have 7 operations");
        assertEquals(3, circuit.measurementCount(), "GHZ circuit should have 3 measurements");
        
        var ops = circuit.ops();
        
        // Verify first few operations
        // H gate on qubit 0
        assertTrue(ops.get(0) instanceof GateOp.Gate);
        GateOp.Gate hGate = (GateOp.Gate) ops.get(0);
        assertEquals(GateType.H, hGate.type());
        assertArrayEquals(new int[]{0}, hGate.qubits());
        
        // CX gate on qubits 0,1
        assertTrue(ops.get(1) instanceof GateOp.Gate);
        GateOp.Gate cx1Gate = (GateOp.Gate) ops.get(1);
        assertEquals(GateType.CX, cx1Gate.type());
        assertArrayEquals(new int[]{0, 1}, cx1Gate.qubits());
        
        // CX gate on qubits 1,2
        assertTrue(ops.get(2) instanceof GateOp.Gate);
        GateOp.Gate cx2Gate = (GateOp.Gate) ops.get(2);
        assertEquals(GateType.CX, cx2Gate.type());
        assertArrayEquals(new int[]{1, 2}, cx2Gate.qubits());
        
        // Verify parametrized rotation gates
        // RZ(π/2) on qubit 0
        assertTrue(ops.get(3) instanceof GateOp.Gate);
        GateOp.Gate rzGate = (GateOp.Gate) ops.get(3);
        assertEquals(GateType.RZ, rzGate.type());
        assertArrayEquals(new int[]{0}, rzGate.qubits());
        assertEquals(Math.PI / 2, rzGate.params()[0], 1e-10);
        
        // RY(π/4) on qubit 1
        assertTrue(ops.get(4) instanceof GateOp.Gate);
        GateOp.Gate ryGate = (GateOp.Gate) ops.get(4);
        assertEquals(GateType.RY, ryGate.type());
        assertArrayEquals(new int[]{1}, ryGate.qubits());
        assertEquals(Math.PI / 4, ryGate.params()[0], 1e-10);
        
        // RX(π/8) on qubit 2
        assertTrue(ops.get(5) instanceof GateOp.Gate);
        GateOp.Gate rxGate = (GateOp.Gate) ops.get(5);
        assertEquals(GateType.RX, rxGate.type());
        assertArrayEquals(new int[]{2}, rxGate.qubits());
        assertEquals(Math.PI / 8, rxGate.params()[0], 1e-10);
        
        // Barrier
        assertTrue(ops.get(6) instanceof GateOp.Barrier);
        GateOp.Barrier barrier = (GateOp.Barrier) ops.get(6);
        assertEquals(0, barrier.qubits().length); // Full barrier
    }
    
    @Test
    void testParseStringSource() throws ParseException {
        String qasmSource = """
            OPENQASM 3.0;
            qubit[1] q;
            x q[0];
            measure q[0] -> c[0];
            """;
        
        Circuit circuit = OpenQasmParser.parse(qasmSource);
        
        assertEquals(1, circuit.qubitCount());
        assertEquals(1, circuit.operationCount());
        assertEquals(1, circuit.measurementCount());
        
        // Verify X gate
        var ops = circuit.ops();
        assertTrue(ops.get(0) instanceof GateOp.Gate);
        GateOp.Gate xGate = (GateOp.Gate) ops.get(0);
        assertEquals(GateType.X, xGate.type());
        assertArrayEquals(new int[]{0}, xGate.qubits());
    }
    
    @Test
    void testParseInvalidFile() {
        Path invalidFile = Paths.get("src/test/resources/invalid.qasm");
        
        assertThrows(ParseException.class, () -> {
            OpenQasmParser.parse(invalidFile);
        }, "Parsing invalid QASM file should throw ParseException");
    }
    
    @Test
    void testParseInvalidString() {
        String invalidQasm = """
            OPENQASM 3.0;
            qubit[2] q;
            invalid_gate q[0];
            """;
        
        assertThrows(ParseException.class, () -> {
            OpenQasmParser.parse(invalidQasm);
        }, "Parsing invalid QASM string should throw ParseException");
    }
    
    @Test
    void testParseMissingSemicolon() {
        String invalidQasm = """
            OPENQASM 3.0;
            qubit[1] q;
            h q[0]
            """;
        
        assertThrows(ParseException.class, () -> {
            OpenQasmParser.parse(invalidQasm);
        }, "Missing semicolon should throw ParseException");
    }
    
    @Test
    void testParseNonExistentFile() {
        Path nonExistentFile = Paths.get("src/test/resources/nonexistent.qasm");
        
        assertThrows(IOException.class, () -> {
            OpenQasmParser.parse(nonExistentFile);
        }, "Parsing non-existent file should throw IOException");
    }
    
    @Test
    void testParseMultipleRegisters() throws ParseException {
        String qasmSource = """
            OPENQASM 3.0;
            qreg q1[2];
            qreg q2[1];
            creg c1[2];
            creg c2[1];
            h q1[0];
            cx q1[0], q1[1];
            x q2[0];
            measure q1[0] -> c1[0];
            measure q1[1] -> c1[1];
            measure q2[0] -> c2[0];
            """;
        
        Circuit circuit = OpenQasmParser.parse(qasmSource);
        
        assertEquals(3, circuit.qubitCount(), "Should have 3 qubits total (2+1)");
        assertEquals(3, circuit.operationCount(), "Should have 3 operations (H + CX + X)");
        assertEquals(3, circuit.measurementCount(), "Should have 3 measurements");
        
        // Verify operations
        var ops = circuit.ops();
        
        // H gate on qubit 0 (q1[0])
        assertTrue(ops.get(0) instanceof GateOp.Gate);
        GateOp.Gate hGate = (GateOp.Gate) ops.get(0);
        assertEquals(GateType.H, hGate.type());
        assertArrayEquals(new int[]{0}, hGate.qubits());
        
        // CX gate on qubits 0,1 (q1[0], q1[1])
        assertTrue(ops.get(1) instanceof GateOp.Gate);
        GateOp.Gate cxGate = (GateOp.Gate) ops.get(1);
        assertEquals(GateType.CX, cxGate.type());
        assertArrayEquals(new int[]{0, 1}, cxGate.qubits());
        
        // X gate on qubit 2 (q2[0])
        assertTrue(ops.get(2) instanceof GateOp.Gate);
        GateOp.Gate xGate = (GateOp.Gate) ops.get(2);
        assertEquals(GateType.X, xGate.type());
        assertArrayEquals(new int[]{2}, xGate.qubits());
    }
    
    @Test
    void testParseGateAliases() throws ParseException {
        String qasmSource = """
            OPENQASM 3.0;
            qreg q[2];
            cx q[0], q[1];
            cnot q[1], q[0];
            """;
        
        Circuit circuit = OpenQasmParser.parse(qasmSource);
        
        assertEquals(2, circuit.qubitCount());
        assertEquals(2, circuit.operationCount());
        
        var ops = circuit.ops();
        
        // Both should be CX gates
        assertTrue(ops.get(0) instanceof GateOp.Gate);
        GateOp.Gate cx1Gate = (GateOp.Gate) ops.get(0);
        assertEquals(GateType.CX, cx1Gate.type());
        assertArrayEquals(new int[]{0, 1}, cx1Gate.qubits());
        
        assertTrue(ops.get(1) instanceof GateOp.Gate);
        GateOp.Gate cx2Gate = (GateOp.Gate) ops.get(1);
        assertEquals(GateType.CX, cx2Gate.type());
        assertArrayEquals(new int[]{1, 0}, cx2Gate.qubits());
    }
    
    @Test
    void testParseMultipleStatementsPerLine() throws ParseException {
        String qasmSource = """
            OPENQASM 3.0;
            qreg q[2]; creg c[2];
            h q[0]; cx q[0], q[1]; measure q[0] -> c[0]; measure q[1] -> c[1];
            """;
        
        Circuit circuit = OpenQasmParser.parse(qasmSource);
        
        assertEquals(2, circuit.qubitCount());
        assertEquals(2, circuit.operationCount());
        assertEquals(2, circuit.measurementCount());
        
        var ops = circuit.ops();
        
        // H gate
        assertTrue(ops.get(0) instanceof GateOp.Gate);
        GateOp.Gate hGate = (GateOp.Gate) ops.get(0);
        assertEquals(GateType.H, hGate.type());
        
        // CX gate
        assertTrue(ops.get(1) instanceof GateOp.Gate);
        GateOp.Gate cxGate = (GateOp.Gate) ops.get(1);
        assertEquals(GateType.CX, cxGate.type());
    }
    
    @Test
    void testParseExceptionWithLineColumn() {
        String invalidQasm = """
            OPENQASM 3.0;
            qreg q[2];
            invalid_gate q[0];
            """;
        
        ParseException exception = assertThrows(ParseException.class, () -> {
            OpenQasmParser.parse(invalidQasm);
        }, "Should throw ParseException for invalid gate");
        
        assertTrue(exception.getLine() > 0, "Should have valid line number");
        assertTrue(exception.getColumn() >= 0, "Should have valid column number");
        assertNotNull(exception.getMessage(), "Should have error message");
        assertTrue(exception.getMessage().contains("Line"), "Error message should contain line info");
    }
    
    @Test
    void testParseExceptionForUndefinedQubit() {
        String invalidQasm = """
            OPENQASM 3.0;
            qreg q[2];
            h undefined[0];
            """;
        
        ParseException exception = assertThrows(ParseException.class, () -> {
            OpenQasmParser.parse(invalidQasm);
        }, "Should throw ParseException for undefined qubit");
        
        assertTrue(exception.getMessage().contains("Undefined qubit"), "Should mention undefined qubit");
        assertTrue(exception.getLine() > 0, "Should have valid line number");
        assertNotNull(exception.getSuggestion(), "Should have helpful suggestion");
    }
    
    @Test
    void testParseExceptionForDuplicateRegister() {
        String invalidQasm = """
            OPENQASM 3.0;
            qreg q[2];
            qreg q[1];
            """;
        
        ParseException exception = assertThrows(ParseException.class, () -> {
            OpenQasmParser.parse(invalidQasm);
        }, "Should throw ParseException for duplicate register");
        
        assertTrue(exception.getMessage().contains("already declared"), "Should mention duplicate declaration");
        assertEquals(3, exception.getLine(), "Should point to line 3");
        assertNotNull(exception.getSuggestion(), "Should have helpful suggestion");
        assertTrue(exception.getSuggestion().contains("different register name"), "Should suggest using different name");
    }
    
    @Test
    void testParseArithmeticExpressions() throws ParseException {
        String qasmSource = """
            OPENQASM 3.0;
            qreg q[1];
            rx(π/2) q[0];
            ry(π/4) q[0];
            rz(2*π/3) q[0];
            """;
        
        Circuit circuit = OpenQasmParser.parse(qasmSource);
        
        assertEquals(1, circuit.qubitCount());
        assertEquals(3, circuit.operationCount());
        
        var ops = circuit.ops();
        
        // RX(π/2)
        assertTrue(ops.get(0) instanceof GateOp.Gate);
        GateOp.Gate rxGate = (GateOp.Gate) ops.get(0);
        assertEquals(GateType.RX, rxGate.type());
        assertEquals(Math.PI / 2, rxGate.params()[0], 1e-10);
        
        // RY(π/4)
        assertTrue(ops.get(1) instanceof GateOp.Gate);
        GateOp.Gate ryGate = (GateOp.Gate) ops.get(1);
        assertEquals(GateType.RY, ryGate.type());
        assertEquals(Math.PI / 4, ryGate.params()[0], 1e-10);
        
        // RZ(2*π/3)
        assertTrue(ops.get(2) instanceof GateOp.Gate);
        GateOp.Gate rzGate = (GateOp.Gate) ops.get(2);
        assertEquals(GateType.RZ, rzGate.type());
        assertEquals(2 * Math.PI / 3, rzGate.params()[0], 1e-10);
    }
}