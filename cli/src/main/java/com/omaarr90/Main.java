package com.omaarr90;

import com.omaarr90.core.circuit.CircuitBuilder;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        var circuit = CircuitBuilder.of(3)
                                    .h(0)
                                    .cx(0, 1)
                                    .rz(1, Math.PI / 2)
                                    .barrier()          // full barrier
                                    .measureAll()
                                    .build();

        System.out.println("Circuit built successfully!");
        System.out.println("Qubits: " + circuit.qubitCount());
        System.out.println("Operations: " + circuit.operationCount());
        System.out.println("Measurements: " + circuit.measurementCount());
        System.out.println("Classical bits: " + circuit.classicalBits());
        System.out.println("Created at: " + circuit.createdAt());

        // Show operations
        System.out.println("\nOperations:");
        for (int i = 0; i < circuit.ops().size(); i++) {
            var op = circuit.ops().get(i);
            System.out.println((i + 1) + ". " + op);
        }

        // Show measurements
        System.out.println("\nMeasurements:");
        circuit.measureMap().forEach((qubit, cbit) ->
                                             System.out.println("Qubit " + qubit + " -> Classical bit " + cbit));
    }
}