package com.joyent.autopilot.springboot;

import com.joyent.autopilot.springboot.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import javax.inject.Inject;

@SpringBootApplication
@EnableAutoConfiguration
@EnableDiscoveryClient(autoRegister = false)
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private ConfigurationManager configurationManager;

    @Inject
    public Application(final ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
        configurationManager.enableConfigReloadOnSighup();
    }

    public static void main(String[] args) {
        // You want to disable health checks because Container Pilot will do it for you
        System.setProperty("spring.cloud.discovery.client.health-indicator.enabled", "false");

        // You can specify a custom path to Consul by setting these properties
//        System.setProperty("spring.cloud.consul.host", "localhost");
//        System.setProperty("spring.cloud.consul.port", "8500");
//        System.setProperty("spring.application.name", "autopilot-springboot");

        SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
        builder.run(args);
    }
}