package cleanhouse.resttemplatepractice.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

/**
 * http요청 timeout 종류
 *
 * 1. 커넥션 타임아웃
 * 2. 응답 타임아웃
 * 3. 커넥션 요청 타임아웃
 *
 */
@Configuration
@RequiredArgsConstructor
public class PracticalRestTemplateConfig {

	private final HttpClientTimeoutProperties timeoutProperties;

	@Bean
	public RestTemplate practicalRestTemplate() {
		CloseableHttpClient httpClient = buildHttpClient();

		ClientHttpRequestFactory factory = buildHttpRequestFactory(httpClient);

		return new RestTemplate(factory);
	}

	private HttpComponentsClientHttpRequestFactory buildHttpRequestFactory(CloseableHttpClient httpClient) {
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	/**
	 * !!! httpClient는 apache client 5를 사용하기로 합니다
	 * RestTemplate은 스프링 라이브러리 클래스임
	 * - 실제 요청을 보내는거는 ClientHttpRequestFactory 라는 인터페이스 한테 위임함
	 * - 그 factory 구현체에 httpClient로 아파치꺼를 개발자가 선택하는거
	 */
	private CloseableHttpClient buildHttpClient() {
		// 1. [타임아웃] response timeout, connection request timeout
		RequestConfig requestTimeout = RequestConfig.custom()
			.setResponseTimeout(Timeout.ofSeconds(timeoutProperties.getResponse()))			// TCP 연결 성공 <-> 응답오기 까지의 시간
			.setConnectionRequestTimeout(Timeout.ofSeconds(timeoutProperties.getRequestConnection()))	// 커넥션 요청 <-> 획득 까지의 시간
			.build()
			;

		// 2. [타임아웃] connection timeout
		ConnectionConfig connectionTimeout = ConnectionConfig.custom()
			.setConnectTimeout(Timeout.ofSeconds(timeoutProperties.getConnection()))
			.build()
			;

		// 3. [http 커넥션 풀]
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setDefaultConnectionConfig(connectionTimeout);
		connectionManager.setMaxTotal(timeoutProperties.getPoolMaxTotal());
		connectionManager.setDefaultMaxPerRoute(timeoutProperties.getPoolMaxPerRoute());

		CloseableHttpClient httpClient = HttpClients.custom()
			.setDefaultRequestConfig(requestTimeout)
			.setConnectionManager(connectionManager)
			.evictExpiredConnections()
			.evictIdleConnections(TimeValue.ofSeconds(60))
			.build()
			;
		return httpClient;
	}
}

