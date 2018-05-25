package legacy.oAuthProxy.config;

import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "filter")
@Component
@Data
@RefreshScope
public class FilterProperties {
    public static final String HEADERS_FILTER = "headers";
    public static final String JWT_FILTER = "jwt";

    @NonNull
    private String filterType = HEADERS_FILTER;
}
