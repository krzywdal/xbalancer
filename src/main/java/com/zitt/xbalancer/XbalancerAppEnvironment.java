package com.zitt.xbalancer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkrzywda on 4/16/17.
 */
public class XbalancerAppEnvironment {

    private String appName;
    private List<String> appHosts;
    private List<String> keysForRoute;
    private BalancingMode mode;
    private Boolean isLoadBalanced;

    /**
     * @param appName
     * @param appHosts
     * @param mode
     */
    public XbalancerAppEnvironment(String appName,
                                   List<String> appHosts,
                                   BalancingMode mode) {
        this.appName = appName;
        this.appHosts = appHosts;
        this.mode = mode;
        this.keysForRoute = new ArrayList<>();
        this.isLoadBalanced = false;
    }

    /**
     * @param appName
     * @param appHosts
     * @param keysForRoute
     * @param mode
     * @param isLoadBalanced
     */
    public XbalancerAppEnvironment(String appName,
                                   List<String> appHosts,
                                   List<String> keysForRoute,
                                   BalancingMode mode,
                                   Boolean isLoadBalanced) {
        this.appName = appName;
        this.appHosts = appHosts;
        this.keysForRoute = keysForRoute;
        this.mode = mode;
        this.isLoadBalanced = isLoadBalanced;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<String> getAppHosts() {
        return appHosts;
    }

    public void setAppHosts(List<String> appHosts) {
        this.appHosts = appHosts;
    }

    public List<String> getKeysForRoute() {
        return keysForRoute;
    }

    public void setKeysForRoute(List<String> keysForRoute) {
        this.keysForRoute = keysForRoute;
    }

    public BalancingMode getMode() {
        return mode;
    }

    public void setMode(BalancingMode mode) {
        this.mode = mode;
    }

    public Boolean getLoadBalanced() {
        return isLoadBalanced;
    }

    public void setLoadBalanced(Boolean loadBalanced) {
        isLoadBalanced = loadBalanced;
    }

    @Override
    public String toString() {
        return "XbalancerAppEnvironment{" +
                "appName='" + appName + '\'' +
                ", appHosts=" + appHosts +
                ", keysForRoute=" + keysForRoute +
                ", mode=" + mode +
                ", isLoadBalanced=" + isLoadBalanced +
                '}';
    }
}
