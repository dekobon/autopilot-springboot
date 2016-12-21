package com.joyent.autopilot.springboot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class SighupHandler implements SignalHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SighupHandler.class);

    private final Function<Void, Optional<RuntimeException>> reloadFunction;

    public SighupHandler(Function<Void, Optional<RuntimeException>> reloadFunction) {
        this.reloadFunction = reloadFunction;
    }

    @Override
    public void handle(final Signal signal) {
        LOGGER.debug("SIGHUP received");
        reloadFunction.apply(null).ifPresent(e -> { throw e; });
    }
}
