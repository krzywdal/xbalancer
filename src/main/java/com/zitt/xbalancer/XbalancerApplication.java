/**
 * MIT License
 * <p>
 * Copyright (c) 2018-2023 Lukasz Krzywda
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zitt.xbalancer;

import com.zitt.xbalancer.configuration.XbalancerConfiguration;
import com.zitt.xbalancer.resource.XbalancerHealthCheck;
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

        environment.healthChecks().register(APP_NAME, new XbalancerHealthCheck());
        environment.jersey().register(resource);

    }

}
