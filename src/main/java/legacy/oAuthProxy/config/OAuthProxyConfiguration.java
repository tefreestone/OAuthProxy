package legacy.oAuthProxy.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@RefreshScope
@EnableAutoConfiguration
public class OAuthProxyConfiguration {

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter
                = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }


    @Bean(name = "restTemplate")
    @Autowired
    protected RestTemplate getRestTemplate(RestTemplateProperties restTemplateProperties) {
        RestTemplate t = new RestTemplate(httpRequestFactory(restTemplateProperties));
        return t;
    }

    @Bean
    protected ClientHttpRequestFactory httpRequestFactory(RestTemplateProperties restTemplateProperties) {
        return new HttpComponentsClientHttpRequestFactory(getHttpClient(restTemplateProperties));
    }

    protected HttpClient getHttpClient(RestTemplateProperties restTemplateProperties) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        connectionManager.setMaxTotal(restTemplateProperties.getHttpClientDefaultMaxTotalConnections());
        connectionManager.setDefaultMaxPerRoute(restTemplateProperties.getHttpClientDefaultMaxConnectionsPerRoute());
        connectionManager.setValidateAfterInactivity(restTemplateProperties.getHttpValidateAfterInactivity());
        RequestConfig config = RequestConfig.custom()
                // don't carry cookies between different uses of rest template
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setConnectionRequestTimeout(restTemplateProperties.getHttpClientConnectionRequestTimeOut())
                .setConnectTimeout(restTemplateProperties.getHttpClientDefaultConnectTimeout())
                .setSocketTimeout(restTemplateProperties.getHttpClientDefaultSocketTimeout())
                .setExpectContinueEnabled(false)
                .setRedirectsEnabled(false).build();
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoKeepAlive(true)
                .setTcpNoDelay(true)
                .build();
        CloseableHttpClient defaultHttpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultSocketConfig(socketConfig)
                .setDefaultRequestConfig(config)
                .build();
        return defaultHttpClient;
    }


}
