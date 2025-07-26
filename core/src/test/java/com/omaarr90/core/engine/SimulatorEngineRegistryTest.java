package com.omaarr90.core.engine;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SimulatorEngineRegistry}. */
class SimulatorEngineRegistryTest {

    @Test
    void testAvailableEngines() {
        Set<String> available = SimulatorEngineRegistry.available();

        assertNotNull(available, "Available engines set should not be null");
        assertTrue(
                available.contains("statevector"),
                "Should contain statevector engine. Available: " + available);
        assertTrue(
                available.contains("stabilizer"),
                "Should contain stabilizer engine. Available: " + available);
    }

    @Test
    void testGetStatevectorEngine() {
        SimulatorEngine engine = SimulatorEngineRegistry.get("statevector");

        assertNotNull(engine, "Statevector engine should not be null");
        assertEquals("statevector", engine.id(), "Engine ID should match");
    }

    @Test
    void testGetStabilizerEngine() {
        SimulatorEngine engine = SimulatorEngineRegistry.get("stabilizer");

        assertNotNull(engine, "Stabilizer engine should not be null");
        assertEquals("stabilizer", engine.id(), "Engine ID should match");
    }

    @Test
    void testGetNonExistentEngine() {
        assertThrows(
                NoSuchElementException.class,
                () -> SimulatorEngineRegistry.get("nonexistent"),
                "Should throw NoSuchElementException for non-existent engine");
    }

    @Test
    void testGetWithNullId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SimulatorEngineRegistry.get(null),
                "Should throw IllegalArgumentException for null ID");
    }

    @Test
    void testGetWithEmptyId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SimulatorEngineRegistry.get(""),
                "Should throw IllegalArgumentException for empty ID");
    }

    @Test
    void testGetWithBlankId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SimulatorEngineRegistry.get("   "),
                "Should throw IllegalArgumentException for blank ID");
    }

    @Test
    void testFirstEngine() {
        SimulatorEngine engine = SimulatorEngineRegistry.first();

        assertNotNull(engine, "First engine should not be null");
        assertNotNull(engine.id(), "First engine ID should not be null");
        assertFalse(engine.id().trim().isEmpty(), "First engine ID should not be empty");

        // Verify it's one of our known engines
        Set<String> available = SimulatorEngineRegistry.available();
        assertTrue(
                available.contains(engine.id()), "First engine ID should be in available engines");
    }

    @Test
    void testEngineThreadSafety() throws InterruptedException {
        // Test that multiple threads can safely access the registry
        final int numThreads = 10;
        final Thread[] threads = new Thread[numThreads];
        final Exception[] exceptions = new Exception[numThreads];

        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] =
                    new Thread(
                            () -> {
                                try {
                                    // Each thread tries to get engines multiple times
                                    for (int j = 0; j < 100; j++) {
                                        SimulatorEngine engine1 =
                                                SimulatorEngineRegistry.get("statevector");
                                        SimulatorEngine engine2 =
                                                SimulatorEngineRegistry.get("stabilizer");
                                        Set<String> available = SimulatorEngineRegistry.available();
                                        SimulatorEngine first = SimulatorEngineRegistry.first();

                                        assertNotNull(engine1);
                                        assertNotNull(engine2);
                                        assertNotNull(available);
                                        assertNotNull(first);
                                    }
                                } catch (Exception e) {
                                    exceptions[threadIndex] = e;
                                }
                            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Check that no exceptions occurred
        for (int i = 0; i < numThreads; i++) {
            if (exceptions[i] != null) {
                fail("Thread " + i + " threw exception: " + exceptions[i].getMessage());
            }
        }
    }

    @Test
    void testReload() {
        // Test that reload doesn't break anything
        Set<String> beforeReload = SimulatorEngineRegistry.available();

        SimulatorEngineRegistry.reload();

        Set<String> afterReload = SimulatorEngineRegistry.available();

        assertEquals(
                beforeReload, afterReload, "Available engines should be the same after reload");
    }
}
