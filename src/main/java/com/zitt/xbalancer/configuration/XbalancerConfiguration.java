package com.zitt.xbalancer.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Configuration of xbalancer application
 * <p>
 * Created by lkrzywda on 4/16/17.
 */
public class XbalancerConfiguration extends Configuration {

    @NotEmpty
    private String appName;

    @NotEmpty
    private List<String> appHosts;

    @NotEmpty
    private String appBalancingMode;

    private List<String> keysForRoute;

    @NotNull
    private Boolean isLoadBalanced;

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

    @JsonProperty
    public List<String> getKeysForRoute() {
        return keysForRoute;
    }

    @JsonProperty
    public void setKeysForRoute(List<String> keysForRoute) {
        this.keysForRoute = keysForRoute;
    }

    @JsonProperty
    public Boolean getIsLoadBalanced() {
        return isLoadBalanced;
    }

    @JsonProperty
    public void setIsLoadBalanced(Boolean isLoadBalanced) {
        this.isLoadBalanced = isLoadBalanced;
    }

    @Override
    public String toString() {
        return "XbalancerConfiguration{" +
                "appName='" + appName + '\'' +
                ", appHosts=" + appHosts +
                ", appBalancingMode='" + appBalancingMode + '\'' +
                ", keysForRoute=" + keysForRoute +
                ", isLoadBalanced='" + isLoadBalanced + '\'' +
                '}';
    }
}
