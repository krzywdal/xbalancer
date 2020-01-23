/**
 * MIT License
 * <p>
 * Copyright (c) 2018-2020 Lukasz Krzywda
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
package com.zitt.xbalancer.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents an environment of a single app configuration
 * <p>
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
