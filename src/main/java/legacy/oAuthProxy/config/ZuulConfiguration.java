package legacy.oAuthProxy.config;


import legacy.oAuthProxy.filters.HeaderFilter;
import legacy.oAuthProxy.user.HeadersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableZuulProxy
@Configuration
public class ZuulConfiguration {

    @Bean
    @Autowired
    public HeaderFilter headerFilter(HeadersService headersService) {
        return new HeaderFilter(headersService);
    }
}
