package com.zitt.xbalancer;

import com.codahale.metrics.annotation.Timed;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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

    private final String appName;
    private AtomicLong counter;
    private HashMap<String, XbalancerAppEnvironment> appMap;


    /**
     * @param conf
     */
    public XbalancerResource(XbalancerConfiguration conf) {
        LOG.info(conf);
        this.appName = conf.getAppName();
        this.appMap = new HashMap<>();
        this.counter = new AtomicLong(0);

        this.appMap.put(conf.getAppName(),
                new XbalancerAppEnvironment(conf.getAppName(),
                        conf.getAppHosts(),
                        conf.getKeysForRoute(),
                        BalancingMode
                                .getByName(conf.getAppBalancingMode())
                                .orElse(BalancingMode.ROUND_ROBIN),
                        conf.getIsLoadBalanced()));

    }

    @GET
    @Path(SUBRESOURCES_REGEX_ENDPOINT)
    @Timed
    public Response balanceGet(@Context HttpServletRequest request,
                               @Context HttpServletResponse response,
                               @CookieParam(XBALANCER_COOKIE) Cookie cookie) throws IOException, URISyntaxException {
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
            index = (int) (counter.getAndIncrement() % availableAppHosts);
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
        String cookieVal = "";
        if (cookie == null) {
            cookieVal = UUID.randomUUID().toString();
            cookie = new Cookie(XBALANCER_COOKIE, cookieVal);
            javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(cookie.getName(), cookieVal);
            c.setMaxAge(3600);
            c.setPath("/");
            response.addCookie(c);
        } else {
            cookieVal = cookie.getValue();
        }
        return Math.abs(Objects.hashCode(cookieVal)) % env.getAppHosts().size();
    }

    /**
     * @param request
     * @return
     */
    public static String getUrl(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        String queryString = request.getQueryString();
        if (queryString == null) {
            return pathInfo.toString();
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
            String query = null;
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