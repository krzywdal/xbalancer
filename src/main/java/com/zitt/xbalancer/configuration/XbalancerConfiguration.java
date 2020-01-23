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
package com.zitt.xbalancer.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
