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
    }

    /**
     * @param appName
     * @param appHosts
     * @param keysForRoute
     * @param mode
     */
    public XbalancerAppEnvironment(String appName,
                                   List<String> appHosts,
                                   List<String> keysForRoute,
                                   BalancingMode mode) {
        this.appName = appName;
        this.appHosts = appHosts;
        this.mode = mode;
        this.keysForRoute = keysForRoute;
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

    @Override
    public String toString() {
        return "XbalancerAppEnvironment{" +
                "appName='" + appName + '\'' +
                ", appHosts=" + appHosts +
                ", keysForRoute=" + keysForRoute +
                ", mode=" + mode +
                '}';
    }
}
