package legacy.oAuthProxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;


@SpringBootApplication
@RefreshScope
public class OAuthProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(OAuthProxyApplication.class, args);
	}
}
