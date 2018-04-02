package legacy.oAuthProxy.user.impl;

import legacy.oAuthProxy.user.HeadersService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class HeadersServiceImpl implements HeadersService {
    @Override
    public Map<String, String> getHeaders() {
        return new HashMap<>();
    }
}
