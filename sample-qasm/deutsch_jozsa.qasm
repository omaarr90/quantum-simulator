OPENQASM 2.0;

qreg q[3];
x q[2];
h q[0];
h q[1];
h q[2];
cx q[0], q[2];
cx q[1], q[2];
h q[0];
h q[1];

measure q[0] -> c[0];
measure q[1] -> c[1];