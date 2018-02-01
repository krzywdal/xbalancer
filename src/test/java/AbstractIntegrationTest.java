import com.zitt.xbalancer.XbalancerApplication;
import com.zitt.xbalancer.XbalancerConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.ClassRule;

import java.io.IOException;

public abstract class AbstractIntegrationTest {

    @ClassRule
    public static final DropwizardAppRule<XbalancerConfiguration> resource =
            new DropwizardAppRule<>(XbalancerApplication.class, "config/app.yml");

    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();

    protected String get(String urlPath) throws IOException {
        HttpGet httpGet = new HttpGet(String.format("http://127.0.0.1:%d%s", resource.getLocalPort(), urlPath));
        CloseableHttpResponse response = HTTP_CLIENT.execute(httpGet);
        return EntityUtils.toString(response.getEntity());
    }
}
