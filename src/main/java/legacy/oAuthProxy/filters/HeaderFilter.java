package legacy.oAuthProxy.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import legacy.oAuthProxy.user.HeadersService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class HeaderFilter extends ZuulFilter {

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

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        request.getUserPrincipal();

        headersService.getHeaders().forEach((key, value) -> {
                    ctx.addZuulRequestHeader(key, value);
                }
        );
        if (log.isDebugEnabled()) {
            log.debug(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
            log.debug("headers added : " + ctx.getZuulRequestHeaders());
        }

        return null;
    }


}
