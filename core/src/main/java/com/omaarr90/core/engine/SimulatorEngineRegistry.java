package com.omaarr90.core.engine;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Registry for discovering and accessing quantum simulation engines via ServiceLoader.
 *
 * <p>This utility class provides a convenient interface for working with simulation engines that
 * are registered through Java's Service Provider Interface (SPI). Engines are automatically
 * discovered at runtime from the classpath.
 *
 * <p>Engine implementations must be registered in {@code
 * META-INF/services/com.omaarr90.qsim.engine.SimulatorEngine} files.
 *
 * <p>This class is thread-safe and all methods can be called concurrently.
 *
 * @see SimulatorEngine
 * @see java.util.ServiceLoader
 */
public final class SimulatorEngineRegistry {

    private static final ServiceLoader<SimulatorEngine> LOADER =
            ServiceLoader.load(SimulatorEngine.class);

    // Private constructor to prevent instantiation
    private SimulatorEngineRegistry() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Returns the set of available engine identifiers.
     *
     * <p>This method scans the classpath for registered engine implementations and returns their
     * unique identifiers. The returned set is immutable.
     *
     * @return immutable set of available engine IDs, never null but may be empty
     */
    public static Set<String> available() {
        synchronized (LOADER) {
            return LOADER.stream()
                    .map(ServiceLoader.Provider::get)
                    .map(SimulatorEngine::id)
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    /**
     * Returns the simulation engine with the specified identifier.
     *
     * <p>This method searches for an engine implementation with the given ID. Engine IDs are
     * case-sensitive and should match exactly.
     *
     * @param id the unique identifier of the desired engine
     * @return the simulation engine with the specified ID
     * @throws IllegalArgumentException if id is null or empty
     * @throws NoSuchElementException if no engine with the specified ID is found
     */
    public static SimulatorEngine get(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Engine ID cannot be null or empty");
        }

        synchronized (LOADER) {
            return LOADER.stream()
                    .map(ServiceLoader.Provider::get)
                    .filter(engine -> id.equals(engine.id()))
                    .findFirst()
                    .orElseThrow(
                            () ->
                                    new NoSuchElementException(
                                            "No simulation engine found with ID: "
                                                    + id
                                                    + ". Available engines: "
                                                    + available()));
        }
    }

    /**
     * Returns the first available simulation engine.
     *
     * <p>This method is useful when you need any available engine and don't care about the specific
     * implementation. The order is not guaranteed and may vary between JVM runs.
     *
     * @return the first available simulation engine
     * @throws NoSuchElementException if no engines are available
     */
    public static SimulatorEngine first() {
        synchronized (LOADER) {
            return LOADER.stream()
                    .map(ServiceLoader.Provider::get)
                    .findFirst()
                    .orElseThrow(
                            () ->
                                    new NoSuchElementException(
                                            "No simulation engines are available. Make sure engine"
                                                + " implementations are on the classpath and"
                                                + " properly registered in META-INF/services."));
        }
    }

    /**
     * Reloads the service loader to pick up any newly available engines.
     *
     * <p>This method is useful in dynamic environments where engine implementations might be added
     * to the classpath at runtime. In most cases, this method is not needed as the ServiceLoader
     * will automatically discover engines.
     */
    public static void reload() {
        synchronized (LOADER) {
            LOADER.reload();
        }
    }
}
