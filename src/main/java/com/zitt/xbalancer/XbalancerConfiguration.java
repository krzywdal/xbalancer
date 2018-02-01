package com.zitt.xbalancer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Created by zitt on 4/16/17.
 */
public class XbalancerConfiguration extends Configuration {

    @NotEmpty
    private String appName;

    @NotEmpty
    private List<String> appHosts;

    @NotEmpty
    private String appBalancingMode;

    private List<String> keysForRoute;

    @JsonProperty
    public String getAppName() {
        return appName;
    }

    @JsonProperty
    public void setAppName(final String appName) {
        this.appName = appName;
    }

    @JsonProperty
    public List<String> getAppHosts() {
        return appHosts;
    }

    @JsonProperty
    public void setAppHosts(List<String> appHosts) {
        this.appHosts = appHosts;
    }

    @JsonProperty
    public String getAppBalancingMode() {
        return appBalancingMode;
    }

    @JsonProperty
    public void setAppBalancingMode(String appBalancingMode) {
        this.appBalancingMode = appBalancingMode;
    }

    public List<String> getKeysForRoute() {
        return keysForRoute;
    }

    public void setKeysForRoute(List<String> keysForRoute) {
        this.keysForRoute = keysForRoute;
    }

    @Override
    public String toString() {
        return "XbalancerConfiguration{" +
                "appName='" + appName + '\'' +
                ", appHosts=" + appHosts +
                ", appBalancingMode=" + appBalancingMode +
                ", keysForRoute=" + keysForRoute +
                '}';
    }
}
