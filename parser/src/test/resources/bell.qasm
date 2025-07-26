OPENQASM 3.0;

// Bell state circuit
qreg q[2];
creg c[2];

// Create Bell state |00⟩ + |11⟩
h q[0];
cx q[0], q[1];

// Measure both qubits
measure q[0] -> c[0];
measure q[1] -> c[1];