package com.zitt.xbalancer.resource;

import com.zitt.xbalancer.AbstractIntegrationTest;
import com.zitt.xbalancer.domain.BalancingMode;
import com.zitt.xbalancer.configuration.XbalancerConfiguration;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by lkrzywda on 4/16/17.
 */
public class XbalancerResourceTest extends AbstractIntegrationTest {

    private static final List<String> HOSTS = Arrays.asList("thost1.com", "thost2.com", "thost3.com");
    private static final List<String> KEYS = Arrays.asList("a", "b");
    private static final String APP_NAME = "TEST_APP";
    private static final String TARGET_URL = "http://test.com?";
    private static final String EXPECTED_STATUS_JSON = "{\"status\":\"UP\"}";


    /**
     * @return
     */
    private static XbalancerResource getResource(BalancingMode balancingMode) {
        XbalancerConfiguration config = new XbalancerConfiguration();
        config.setAppBalancingMode(balancingMode.name());
        config.setKeysForRoute(KEYS);
        config.setAppHosts(HOSTS);
        config.setAppName(APP_NAME);
        config.setIsLoadBalanced(false);
        return new XbalancerResource(config);
    }

    @Test
    public void testStatusEndpoint() throws Exception {
        String entity = get(XbalancerResource.STATUS_ENDPOINT);
        assertEquals(EXPECTED_STATUS_JSON, entity);
    }

    @Test
    public void testGetKeyHash() {

        // Hit the endpoint and get the raw json string
        Response r1 = resource.client().target(TARGET_URL)
                .queryParam("a", "1")
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .request()
                .get();

        Response r2 = resource.client().target(TARGET_URL)
                .queryParam("a", "2")
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .request()
                .get();

        Response r3 = resource.client().target(TARGET_URL)
                .queryParam("a", "3")
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .request()
                .get();

        assertEquals(HttpStatus.MOVED_TEMPORARILY_302, r1.getStatus());
        assertEquals(HttpStatus.MOVED_TEMPORARILY_302, r2.getStatus());
        assertEquals(HttpStatus.MOVED_TEMPORARILY_302, r3.getStatus());

    }

    @Test
    public void testGetRandom() {

        // Hit the endpoint and get the raw json string
        Response r1 = resource.client().target(TARGET_URL)
                .queryParam("a", "1")
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .request()
                .get();

        Response r2 = resource.client().target(TARGET_URL)
                .queryParam("a", "2")
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .request()
                .get();

        Response r3 = resource.client().target(TARGET_URL)
                .queryParam("a", "3")
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .request()
                .get();

        assertEquals(HttpStatus.MOVED_TEMPORARILY_302, r1.getStatus());
        assertEquals(HttpStatus.MOVED_TEMPORARILY_302, r2.getStatus());
        assertEquals(HttpStatus.MOVED_TEMPORARILY_302, r3.getStatus());

    }


    @Test
    public void testGetRoundRobin() {

        // Hit the endpoint and get the raw json string
        Response r1 = resource.client().target(TARGET_URL)
                .queryParam("a", "1")
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .request()
                .get();

        Response r2 = resource.client().target(TARGET_URL)
                .queryParam("a", "2")
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .request()
                .get();

        Response r3 = resource.client().target(TARGET_URL)
                .queryParam("a", "3")
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .request()
                .get();

        assertEquals(HttpStatus.MOVED_TEMPORARILY_302, r1.getStatus());
        assertEquals(HttpStatus.MOVED_TEMPORARILY_302, r2.getStatus());
        assertEquals(HttpStatus.MOVED_TEMPORARILY_302, r3.getStatus());
    }

    @Test
    public void testKeyHash() {
        XbalancerResource res = getResource(BalancingMode.KEY_HASH);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

        Mockito.when(req.getQueryString()).thenReturn("a=0");
        String host1 = res.selectRoute(req, null, null);

        Mockito.when(req.getQueryString()).thenReturn("a=0&b=1");
        String host2 = res.selectRoute(req, null, null);

        assertNotEquals(host1, host2);
    }

    @Test
    public void testRoundRobin() {
        XbalancerResource res = getResource(BalancingMode.ROUND_ROBIN);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

        assertEquals(HOSTS.get(0), res.selectRoute(req, null, null));
        assertEquals(HOSTS.get(1), res.selectRoute(req, null, null));
        assertEquals(HOSTS.get(2), res.selectRoute(req, null, null));
        assertEquals(HOSTS.get(0), res.selectRoute(req, null, null));
        assertEquals(HOSTS.get(1), res.selectRoute(req, null, null));
        assertEquals(HOSTS.get(2), res.selectRoute(req, null, null));
    }

    @Test
    public void testRandom() {

        XbalancerResource res = getResource(BalancingMode.RANDOM);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

        Set<String> routes = new HashSet<>();
        routes.add(res.selectRoute(req, null, null));
        routes.add(res.selectRoute(req, null, null));
        routes.add(res.selectRoute(req, null, null));
        routes.add(res.selectRoute(req, null, null));
        routes.add(res.selectRoute(req, null, null));

        assertTrue(routes.size() > 1 && routes.size() <= 5);

    }


    @Test
    public void testTimestampHash() throws Exception {

        XbalancerResource res = getResource(BalancingMode.TIMESTAMP_HASH);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);


        String route1 = res.selectRoute(req, null, null);
        // after 1 sec we should get different result
        Thread.sleep(1000);
        String route2 = res.selectRoute(req, null, null);

        assertNotEquals(route1, route2);

    }

}
