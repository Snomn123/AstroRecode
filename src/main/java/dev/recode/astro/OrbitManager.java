package dev.recode.astro;

import dev.recode.astro.api.event.orbit.EventBus;
import dev.recode.astro.api.event.orbit.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class OrbitManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("astrorecode");

    public static final String PACKAGE_PREFIX = "dev.recode.astro";
    public static final IEventBus EVENT_BUS = new EventBus();

    private static boolean initialized = false;


    public static void initialize() {
        if (initialized) {
            LOGGER.warn("orbit initialized! (astro recode)");
            return;
        }

        try {

            EVENT_BUS.registerLambdaFactory(
                    PACKAGE_PREFIX,
                    (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup())
            );

            initialized = true;

        } catch (Exception e) {

        }
    }
    public static IEventBus getEventBus() {
        if (!initialized) {

        }
        return EVENT_BUS;
    }
    public static boolean isInitialized() {
        return initialized;
    }
}