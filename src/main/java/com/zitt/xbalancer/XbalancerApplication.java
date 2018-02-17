package com.zitt.xbalancer;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class
 * <p>
 * Created by lkrzywda on 4/16/17.
 */
public class XbalancerApplication extends Application<XbalancerConfiguration> {


    public static final Logger LOGGER = LoggerFactory.getLogger(XbalancerApplication.class);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        new XbalancerApplication().run(args);
    }

    @Override
    public void run(XbalancerConfiguration configuration, Environment environment) throws Exception {

        LOGGER.info("Configuration: ", configuration.toString());

        final XbalancerResource resource = new XbalancerResource(configuration);

        environment.jersey().register(resource);
    }

}
