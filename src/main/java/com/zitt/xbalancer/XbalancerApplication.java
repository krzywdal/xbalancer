package com.zitt.xbalancer;

import com.zitt.xbalancer.configuration.XbalancerConfiguration;
import com.zitt.xbalancer.resource.XbalancerResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class
 * <p>
 * Created by lkrzywda on 4/16/17.
 */
public class XbalancerApplication extends Application<XbalancerConfiguration> {


    private static final Logger LOGGER = LoggerFactory.getLogger(XbalancerApplication.class);

    private static final String APP_NAME = "xbalancer";

    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        new XbalancerApplication().run(args);
    }

    @Override
    public String getName() {
        return APP_NAME;
    }

    @Override
    public void initialize(Bootstrap bootstrap) {
        // initialize here
    }

    @Override
    public void run(XbalancerConfiguration configuration, Environment environment) throws Exception {

        LOGGER.info("Starting " + APP_NAME + " with configuration: ", configuration.toString());

        final XbalancerResource resource = new XbalancerResource(configuration);

        environment.jersey().register(resource);
    }

}
