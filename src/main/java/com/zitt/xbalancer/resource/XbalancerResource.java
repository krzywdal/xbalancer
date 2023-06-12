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
package com.zitt.xbalancer.resource;

import com.codahale.metrics.annotation.Timed;
import com.zitt.xbalancer.configuration.XbalancerConfiguration;
import com.zitt.xbalancer.domain.AppStatus;
import com.zitt.xbalancer.domain.BalancingMode;
import com.zitt.xbalancer.domain.XbalancerAppEnvironment;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lkrzywda on 4/16/17.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class XbalancerResource {

    public final static String STATUS_ENDPOINT = "/balancer_status";

    private final static Logger LOG = Logger.getLogger(XbalancerResource.class);
    private final static String SUBRESOURCES_REGEX_ENDPOINT = "{subResources:.*}";
    private final static String X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private final static String XBALANCER_COOKIE = "XBALANCER_COOKIE";
    private final static String UTF_8 = "UTF-8";
    private final static int MAX_COOKIE_AGE = (int) TimeUnit.HOURS.toSeconds(1); // 1 hour
    private final static int TIMESTAMP_DURATION_MILLIS = 1000;
    private final static Object SEMAPHORE = new Object();

    private final String appName;
    private AtomicLong counter;
    private HashMap<String, XbalancerAppEnvironment> appMap;
    private List<Integer> weightsConfig;
    private int lastWeightIndex;
    private int[] weightsCounter;


    /**
     * @param conf
     */
    public XbalancerResource(XbalancerConfiguration conf) {
        LOG.info(conf);
        this.appName = conf.getAppName();
        this.appMap = new HashMap<>();
        this.counter = new AtomicLong(-1); // -1 because round-robin counter is pre-incremented
        this.lastWeightIndex = -1; // -1 because is pre-incremented
        this.weightsConfig = conf.getWeights();
        this.weightsCounter = new int[weightsConfig.size()];

        final int[] i = {0};
        this.weightsConfig.stream().forEach(k -> {
            weightsCounter[i[0]] = k;
            i[0]++;
        });

        this.appMap.put(conf.getAppName(),
                new XbalancerAppEnvironment(conf.getAppName(),
                        conf.getAppHosts(),
                        conf.getKeysForRoute(),
                        conf.getWeights(),
                        BalancingMode
                                .getByName(conf.getAppBalancingMode())
                                .orElse(BalancingMode.ROUND_ROBIN),
                        conf.getIsLoadBalanced()));

    }

    @GET
    @Timed
    @Path(SUBRESOURCES_REGEX_ENDPOINT)
    public Response balanceGet(@Context HttpServletRequest request,
                               @Context HttpServletResponse response,
                               @CookieParam(XBALANCER_COOKIE) Cookie cookie) throws URISyntaxException {
        String url = getUrl(request);

        // handle status request
        if (url.equals(STATUS_ENDPOINT)) {
            return Response.ok(new AppStatus(AppStatus.UP)).build();
        }

        String fullUrl = selectRoute(request, response, cookie) + url;
        LOG.info("balance get: " + fullUrl);
        URI uri = new URI(fullUrl);
        return Response.seeOther(uri).build();
    }


    /**
     * @param request
     * @param response
     * @param cookie
     * @return
     */
    public String selectRoute(HttpServletRequest request, HttpServletResponse response, Cookie cookie) {

        XbalancerAppEnvironment env = appMap.get(appName);
        BalancingMode mode = env.getMode();
        int availableAppHosts = env.getAppHosts().size();
        int index = 0;

        if (mode.equals(BalancingMode.ROUND_ROBIN)) {
            //ROUND ROBIN
            index = (int) counter.updateAndGet(curVal -> (Long.MAX_VALUE == curVal) ? 0 : ++curVal) % availableAppHosts;
        } else if (mode.equals(BalancingMode.RANDOM)) {
            // RANDOM
            index = (int) (Math.random() * availableAppHosts);
        } else if (mode.equals(BalancingMode.STICKY)) {
            // STICKY
            index = getIndexFromCookie(cookie, env, response);
        } else if (mode.equals(BalancingMode.IP_HASH)) {
            // IP HASH
            index = getIndexFromIp(request, env);
        } else if (mode.equals(BalancingMode.KEY_HASH)) {
            // KEY HASH
            index = getIndexFromKeyHash(getParamsMap(request), env);
        } else if (mode.equals(BalancingMode.TIMESTAMP_HASH)) {
            // TIMESTAMP HASH
            index = getIndexFromTimestamp(env);
        } else if (mode.equals(BalancingMode.LEAST_CONNECTIONS)) {
            // LEAST_CONNECTIONS
            index = 0;
            // TODO - implement me
        } else if (mode.equals(BalancingMode.WEIGHTED_LEAST_CONNECTIONS)) {
            // WEIGHTED_LEAST_CONNECTIONS
            index = 0;
            // TODO - implement me
        } else if (mode.equals(BalancingMode.WEIGHTED)) {
            // WEIGHTED
            index = 0;

            // initialize weights again if it does not contain positive weights
            if (!containsPositiveWeights(weightsCounter)) {
                final int[] i = {0};
                this.weightsConfig.stream().forEach(k -> {
                    weightsCounter[i[0]] = k;
                    i[0]++;
                });
            } else {
                lastWeightIndex++;
                // reset index if we reached an end
                if (lastWeightIndex >= weightsCounter.length) {
                    lastWeightIndex = 0;
                }
                // keep increasing until we reach weight > 0
                while (weightsCounter[lastWeightIndex] <= 0) {

                    if (lastWeightIndex < weightsCounter.length - 1) {
                        lastWeightIndex++;
                    }
                    if (lastWeightIndex > weightsCounter.length) {
                        lastWeightIndex = 0;
                    }
                }
                index = lastWeightIndex;
                // decrease counter value by 1
                synchronized (SEMAPHORE) {
                    weightsCounter[lastWeightIndex]--;
                }
            }
        }

        return env.getAppHosts().get(index);

    }


    /**
     * @param params
     * @param env
     * @return
     */
    private static int getIndexFromKeyHash(Map<String, String> params,
                                           XbalancerAppEnvironment env) {
        int hash = 0;
        for (String key : env.getKeysForRoute()) {
            hash += Objects.hashCode(params.get(key));
        }
        return Math.abs(hash) % env.getAppHosts().size();
    }

    /**
     * @param request
     * @param env
     * @return
     */
    private static int getIndexFromIp(HttpServletRequest request,
                                      XbalancerAppEnvironment env) {
        int hash = 0;
        if (env.getLoadBalanced()) {
            hash = Objects.hashCode(request.getHeader(X_FORWARDED_FOR));
        } else {
            hash = Objects.hashCode(request.getRemoteAddr());
        }
        return Math.abs(hash) % env.getAppHosts().size();
    }


    /**
     * @param cookie
     * @param env
     * @param response
     * @return
     */
    private static int getIndexFromCookie(Cookie cookie,
                                          XbalancerAppEnvironment env,
                                          HttpServletResponse response) {
        String cookieVal;
        if (cookie == null) {
            cookieVal = UUID.randomUUID().toString();
            cookie = new Cookie(XBALANCER_COOKIE, cookieVal);
            javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(cookie.getName(), cookieVal);
            c.setMaxAge(MAX_COOKIE_AGE);
            c.setPath("/");
            response.addCookie(c);
        } else {
            cookieVal = cookie.getValue();
        }
        return Math.abs(Objects.hashCode(cookieVal)) % env.getAppHosts().size();
    }


    /**
     * @param env
     * @return
     */
    private static int getIndexFromTimestamp(XbalancerAppEnvironment env) {
        int hash = Objects.hashCode(System.currentTimeMillis() / TIMESTAMP_DURATION_MILLIS);
        return Math.abs(hash) % env.getAppHosts().size();
    }

    /**
     * @param weightsCounter
     * @return
     */
    private static boolean containsPositiveWeights(int[] weightsCounter) {
        LOG.info("containsPositiveWeights for: " + Arrays.toString(weightsCounter));

        for (int i = 0; i < weightsCounter.length; i++) {
            if (weightsCounter[i] > 0) {
                LOG.info("containsPositiveWeights: " + true);
                return true;
            }
        }
        LOG.info("containsPositiveWeights: " + false);
        return false;
    }

    /**
     * @param request
     * @return
     */
    public static String getUrl(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        String queryString = request.getQueryString();
        if (queryString == null) {
            return pathInfo;
        } else {
            return pathInfo + '?' + queryString;
        }
    }

    /**
     * @param request
     * @return
     */
    private Map<String, String> getParamsMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        if (request != null && request.getQueryString() != null) {
            String query;
            try {
                query = URLDecoder.decode(request.getQueryString(), UTF_8);
            } catch (UnsupportedEncodingException e) {
                LOG.error(e.getStackTrace());
                query = request.getQueryString();
            }
            String[] splits = query.split("&");
            for (String keyValParam : splits) {
                String pair[] = keyValParam.split("=");
                if (pair.length == 2) {
                    map.put(pair[0], pair[1]);
                } else {
                    map.put(pair[0], "");
                }
            }
        }
        return map;
    }

}
