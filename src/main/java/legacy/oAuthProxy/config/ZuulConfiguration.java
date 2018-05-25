package legacy.oAuthProxy.config;


import com.netflix.zuul.ZuulFilter;
import legacy.oAuthProxy.filters.HeaderFilter;
import legacy.oAuthProxy.filters.JWTFilter;
import legacy.oAuthProxy.user.HeadersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableZuulProxy
@Configuration
@Slf4j
public class ZuulConfiguration {

    @Autowired(required = false)
    private FilterProperties filterProperties;

    @Autowired(required = false)
    private JwtProperties jwtProperties;

    @Autowired
    private HeadersService headersService;

    @Bean
    @ConditionalOnClass(HeadersService.class)
    public ZuulFilter headerFilter() {
        if (headersService != null) {
            if (filterProperties != null && filterProperties.getFilterType() != null) {
                if (filterProperties.getFilterType().equalsIgnoreCase(FilterProperties.JWT_FILTER)) {
                    return getJwtFilter();
                }
            }
            log.info("Using Header Filter");
            return new HeaderFilter(headersService);
        } else {
            throw new IllegalArgumentException("headers service  == null");
        }
    }


    @ConditionalOnProperty("jwt")
    public JWTFilter getJwtFilter() {
        if (jwtProperties != null) {
            log.info("Using JWT Filter");
            return new JWTFilter(jwtProperties, headersService);
        } else {
            throw new IllegalStateException("jwt filter is not configured");
        }
    }

}
