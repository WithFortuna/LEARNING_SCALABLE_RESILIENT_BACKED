package cleanhouse.resttemplatepractice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(HttpClientTimeoutProperties.class)
@Configuration
public class HttpClientTimeoutConfig {
}
