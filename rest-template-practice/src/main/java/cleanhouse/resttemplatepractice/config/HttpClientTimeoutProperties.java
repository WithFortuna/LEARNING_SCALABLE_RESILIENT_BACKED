package cleanhouse.resttemplatepractice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ConfigurationProperties(prefix = "timeout")	// 여기선 생성자 주입을 사용. @Setter 불필요
@Getter
@RequiredArgsConstructor
public class HttpClientTimeoutProperties {
	private final int response;
	private final int connection;
	private final int requestConnection;
	private final int poolMaxTotal;
	private final int poolMaxPerRoute;
}
