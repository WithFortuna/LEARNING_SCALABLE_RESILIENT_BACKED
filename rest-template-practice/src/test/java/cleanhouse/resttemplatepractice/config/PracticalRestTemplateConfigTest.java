package cleanhouse.resttemplatepractice.config;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class PracticalRestTemplateConfigTest {
	private static final String CONNECTION_TIMEOUT_MESSAGE = "Connect timed out";
	private static final String RESPONSE_TIMEOUT_MESSAGE = "Read timed out";
	private static final String REQUEST_CONNECTION_TIMEOUT_MESSAGE = "Timeout deadline";


	@Autowired
	@Qualifier("practicalRestTemplate")
	private RestTemplate practicalRestTemplate;

	@Autowired
	private HttpClientTimeoutProperties timeoutProperties;


	@DisplayName("apache client5의 connectionRequest 타임아웃 측정")
	@Test
	public void apache_client_기본_응답_타임아웃_시간_3분() {
		// 측정할 필요 없네
		log.info("apache client5 의 connectionRequest timeout: {}", ConnectionConfig.DEFAULT.getConnectTimeout());
	}

	@DisplayName("apache client5의 default response 타임아웃은 3분이다")
	@Test
	public void apache_client5_기본_response_타임아웃_시간은_3분이다() {
		// given
		AtomicLong from = new AtomicLong(1);

		String url = "http://localhost:3333/hello";
		RestTemplate defaultApacheTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(
			HttpClients.custom().build()
		));

		// when

		assertThatThrownBy(
			() -> assertTimeoutPreemptively(Duration.ofMinutes(4), () ->
			{
				from.set(System.currentTimeMillis());
				defaultApacheTemplate.getForObject(
					url,
					String.class
				);
			})
		)
			.isInstanceOf(ResourceAccessException.class)
			.satisfies(e->{
				long to = System.currentTimeMillis();
				log.info("기본 응답시간 타임아웃: {}", to - from.longValue());
			})
			.hasMessageContaining(RESPONSE_TIMEOUT_MESSAGE)
		;


		// then
		// assertThat();
	}

	@DisplayName("apache client5의 response 타임아웃은 내가 설정한 시간 내에 터져야한다")
	@Test
	public void apache_client5_response_타임아웃은_설정값에_발생한다() {
		// given
		String url = "http://localhost:3333/hello";

		// when & then
		assertThatThrownBy(
			() -> {
				int responseTimeout = timeoutProperties.getResponse();
				assertTimeoutPreemptively(Duration.ofSeconds(responseTimeout + 15),
					() -> practicalRestTemplate.getForObject(
						url,
						String.class
					));
			}
		)
			.isInstanceOf(ResourceAccessException.class)
			.satisfies(e -> log.error("exception message: {}", e.toString()))
			.hasMessageContaining(RESPONSE_TIMEOUT_MESSAGE)
		;
	}
	@DisplayName("apache client5의 connection 타임아웃은 내가 설정한 시간 내에 터져야한다")
	@Test
	public void apache_client5_connection_타임아웃은_설정값에_발생한다() {
		// given
		String url = "http://192.167.1.1:3333/hello";

		// when & then
		assertThatThrownBy(
			() -> {
				int connectionTimeout = timeoutProperties.getConnection();
				assertTimeoutPreemptively(Duration.ofSeconds(connectionTimeout + 15),
					() -> practicalRestTemplate.getForObject(
						url,
						String.class
					));
			}
		)
			.isInstanceOf(ResourceAccessException.class)
			.satisfies(e -> log.error("exception message: {}", e.toString()))
			.hasMessageContaining(CONNECTION_TIMEOUT_MESSAGE)
		;

	}

	/// http 커넥션풀 요청 타임아웃 ver1
	//  http 커넥션 풀 테스트에 비적합한 테스트코드 이유: 단일스레드구조 + RestTemplate은 동기 블로킹

	@DisplayName("apache client5의 requestConnection 타임아웃은 내가 설정한 시간 내에 터져야한다")
	@Disabled
	@Deprecated
	@Test
	public void apache_client5_requestConnection_타임아웃은_설정값에_발생한다_ver1() {
		// given
		String url = "http://localhost:3333/hello";

		// when & then
		// 1. 요청1: response timeout
		assertThatThrownBy(() -> practicalRestTemplate.getForObject(url, String.class))
			.isInstanceOf(ResourceAccessException.class);


		// 2. 요청2: request connection timeout
		assertThatThrownBy(
			() -> {
				int requestConnectionTimeout = timeoutProperties.getRequestConnection();
				assertTimeoutPreemptively(Duration.ofSeconds(requestConnectionTimeout + 5),
					() -> practicalRestTemplate.getForObject(
						url,
						String.class
					));
			}
		)
			.isInstanceOf(ResourceAccessException.class)
			.satisfies(e -> log.error("exception message: {}", e.toString()))
		;

	}
	/// http 커넥션풀 요청 타임아웃 ver2
	// 멀티스레드
	@DisplayName("apache client5의 connectionRequest 타임아웃은 내가 설정한 시간 내에 터져야한다")
	@Test
	public void apache_client5_connectionRequest_타임아웃은_설정값에_발생한다_ver2() throws InterruptedException {

		// given
		ExecutorService executor = Executors.newFixedThreadPool(2);

		String url = "http://localhost:3333/hello";

		// when & then
		// 1. 요청1: response timeout
		CountDownLatch poolOccupied = new CountDownLatch(1);
		Future<String> first = executor.submit(() -> {
				poolOccupied.countDown();
				return practicalRestTemplate.getForObject(url, String.class);
			}
		);

		poolOccupied.await();	  // 첫 스레드가 실제로 깨어나서 일을 시작했다.(~ 곧 요청풀 점유할거임)
		Thread.sleep(1000); // 테스트 메인 스레드 sleep

		// 1. 요청2: requestConnection timeout
		Future<String> second = executor.submit(() -> practicalRestTemplate.getForObject(url, String.class));

		assertThatThrownBy(
			() -> assertTimeoutPreemptively(
				Duration.ofSeconds(timeoutProperties.getRequestConnection() + 5L),
				() -> second.get()
			)
		) // Future.get을 호출한 스레드는 그 값을 얻기위해 대기한다.
			.isInstanceOf(ExecutionException.class)
			.hasCauseInstanceOf(ResourceAccessException.class)
			.satisfies(e -> log.error("error message: {}", e.getCause().toString()))
			.hasMessageContaining(REQUEST_CONNECTION_TIMEOUT_MESSAGE)
		;

		executor.shutdownNow();
	}
}
