OPENQASM 3.0;

// GHZ state circuit with 3 qubits
qubit[3] q;

// Create GHZ state |000⟩ + |111⟩
h q[0];
cx q[0], q[1];
cx q[1], q[2];

// Add some parametrized rotations
rz(π/2) q[0];
ry(π/4) q[1];
rx(π/8) q[2];

// Add a barrier for visualization
barrier;

// Measure all qubits
measure q[0] -> c[0];
measure q[1] -> c[1];
measure q[2] -> c[2];