package com.joyent.autopilot.springboot.config;

import com.google.common.base.Joiner;
import com.joyent.autopilot.springboot.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Singleton
@Named
public class ConfigurationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final String DEPENDENT_SERVICE = "my-service";

    private final boolean throwErrorOnInitFailure;
    private final DiscoveryClient discoveryClient;
    private final Function<Void, Optional<RuntimeException>> reloadFunction;

    @Inject
    public ConfigurationManager(final DiscoveryClient discoveryClient) {
        this.throwErrorOnInitFailure = false;
        this.discoveryClient = discoveryClient;

        /* Lambda function that provides the reload logic on SIGHUP. For this example.
         * we are reloading a service list from Consul. */
        this.reloadFunction = (aVoid) -> {
            try {
                List<ServiceInstance> list = this.discoveryClient.getInstances(DEPENDENT_SERVICE);
                String nodes = Joiner.on("\n").join(list);
                LOGGER.info("Reloading configuration. Nodes:\n{}", nodes);
            } catch (RuntimeException e) {
                return Optional.of(e);
            }

            return Optional.empty();
        };
    }

    /**
     * Safely registers a SIGHUP handler that works for systems that support signals.
     * This seemingly complex way of loading the sun.misc.* classes is important because
     * it allows us to not explicitly depend on them thereby making this method safe to
     * run on any JVM.
     */
    public void enableConfigReloadOnSighup() {
        try {
            Class<?> signalClass = Class.forName("sun.misc.Signal");
            Class<?> signalHandlerClass = Class.forName("sun.misc.SignalHandler");
            Constructor<?> signalClassConstructor = signalClass.getConstructor(String.class);
            Method handle = signalClass.getMethod("handle", signalClass, signalHandlerClass);
            Object signalInstance = signalClassConstructor.newInstance("HUP");
            handle.invoke(null, signalInstance, new SighupHandler(reloadFunction));
        } catch (ReflectiveOperationException e) {
            final String msg = "Unable to register signal handler via reflection";
            if (throwErrorOnInitFailure) {
                throw new UnsupportedOperationException(msg, e);
            } else {
                LOGGER.error(msg, e);
            }
        }
    }
}
