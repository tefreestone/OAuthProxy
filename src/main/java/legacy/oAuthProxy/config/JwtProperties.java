package legacy.oAuthProxy.config;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Component
@Data
@RefreshScope
public class JwtProperties {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";

    private String signingSecret;

    private String encryptionSecret;

    private String issuer;

    private String issuerId;

    private long expireTime = 3600;

    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
}
