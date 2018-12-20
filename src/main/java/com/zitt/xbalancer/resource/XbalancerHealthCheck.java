package com.zitt.xbalancer.resource;

import com.codahale.metrics.health.HealthCheck;

/**
 *
 */
public class XbalancerHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
