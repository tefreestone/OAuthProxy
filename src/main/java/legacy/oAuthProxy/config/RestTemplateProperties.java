package legacy.oAuthProxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "rest-template")
@Component
@Data
@RefreshScope
public class RestTemplateProperties {

    private static final MediaType mediaType = MediaType.APPLICATION_JSON;
    private int httpClientDefaultMaxTotalConnections;
    private int httpClientDefaultMaxConnectionsPerRoute;
    private int httpClientReadTimeouteMillSec;
    private int httpClientConnectionRequestTimeOut;
    private int httpClientDefaultConnectTimeout;
    private int httpClientDefaultSocketTimeout;
    private int httpClientMaxPerRoute;
    private int httpClientMaxTotal;
    private int httpValidateAfterInactivity;

    public static MediaType getMediaType() {
        return mediaType;
    }


}
