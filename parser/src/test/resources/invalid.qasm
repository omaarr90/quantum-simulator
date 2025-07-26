OPENQASM 3.0;

// Invalid QASM file for testing error handling
qubit[2] q;

// Invalid gate name
invalid_gate q[0];

// Missing semicolon
h q[1]

// Invalid qubit reference
cx q[0], q[5];