package legacy.oAuthProxy.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import legacy.oAuthProxy.config.JwtProperties;
import legacy.oAuthProxy.user.HeadersService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class JWTFilter extends ZuulFilter {
    private static final String OAUTH_TOKEN_TYPE_KEY = "sso_oauth_token_type";
    private static final String OAUTH_TOKEN_VALUE_KEY = "sso_oauth_token_value";
    private static final String SUB = "sub";

    @Autowired
    @NonNull
    private JwtProperties jwtProperties;

    @Autowired
    @NonNull
    private HeadersService headersService;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

//    Authentication Explanation
//    https://tools.ietf.org/html/rfc6750
//    1.2. Terminology
//
//    Bearer Token
//
//    A security token with the property that any party in possession of the token (a "bearer") can use the token in any
//    way that any other party in possession of it can. Using a bearer token does not require a bearer to prove possession
//    of cryptographic key material (proof-of-possession).
//
//    see https://jwt.io/introduction/

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        String authHeaderValue = JwtProperties.BEARER + createJWT();
        if (log.isDebugEnabled()) {
            log.debug("******* " + JwtProperties.AUTHORIZATION_HEADER + ": " + authHeaderValue);
        }
        ctx.addZuulRequestHeader(JwtProperties.AUTHORIZATION_HEADER, authHeaderValue);

        return null;
    }

    public String createJWT() {
        if (jwtProperties != null) {
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            //We will sign our JWT with our ApiKey secret
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtProperties.getSigningSecret());
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, jwtProperties.getSignatureAlgorithm().getJcaName());

            Map additionalClaims = headersService.getHeaders();
            additionalClaims.putAll(getOAuthClaims());

            //Let's set the JWT Claims
            if (additionalClaims.get(SUB) != null) {
                io.jsonwebtoken.JwtBuilder builder = Jwts.builder().setId(jwtProperties.getIssuerId())
                        .setIssuedAt(now)
                        .setSubject((String) additionalClaims.get(SUB))
                        .compressWith(CompressionCodecs.GZIP)
                        .setIssuer(jwtProperties.getIssuer())
                        .addClaims(additionalClaims)
                        .signWith(jwtProperties.getSignatureAlgorithm(), signingKey);
                //if it has been specified, let's add the expiration
                if (jwtProperties.getExpireTime() >= 0) {
                    long expMillis = nowMillis + jwtProperties.getExpireTime();
                    Date exp = new Date(expMillis);
                    builder.setExpiration(exp);
                }
                String result = builder.compact();
                if (log.isDebugEnabled()) {
                    log.debug("built JWT : " + result);
                }
                return result;
            } else
                throw new IllegalStateException("Sub == null");
        } else
            throw new IllegalStateException("jwtProperties is null!");
    }

    private Map<String, String> getOAuthClaims() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Map<String, String> oAuthClaims = new HashMap<>();
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) request.getUserPrincipal();
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails) oAuth2Authentication.getDetails();
        if (oAuth2Authentication.isAuthenticated()) {
            oAuthClaims.put(OAUTH_TOKEN_TYPE_KEY, oAuth2AuthenticationDetails.getTokenType());
            oAuthClaims.put(OAUTH_TOKEN_VALUE_KEY, oAuth2AuthenticationDetails.getTokenValue());
            UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) oAuth2Authentication.getUserAuthentication();
            oAuthClaims.putAll((Map<String, String>) user.getDetails());
        }
        return oAuthClaims;
    }
}
