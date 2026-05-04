package cleanhouse.resttemplatepractice.config;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class SimpleRestTemplateConfigTest {

	@Autowired
	@Qualifier("simpleRestTemplate")
	private RestTemplate simpleRestTemplate;

	@Test
	@DisplayName("디폴트 RestTemplate의 response 타임아웃 시간은 무기한이다")
	@Disabled("external-api(localhost:3333을 실행시키고 테스트를 돌려야합니다")
	public void 기본_RestTemplate_기본_응답_타임아웃_시간은_무기한이다() {
		// given
		String externalApiUrl = "http://localhost:3333/hello";

		// when & then
		assertThatThrownBy(
			() -> assertTimeoutPreemptively(Duration.ofMinutes(4), () -> {
				simpleRestTemplate.getForObject(
					externalApiUrl,
					String.class
				);
			})
		).isInstanceOf(AssertionFailedError.class);
	}

	@DisplayName("디폴트 RestTemplate의 connection 타임아웃 시간은 무기한이 아니다")
	@Test
	public void 기본_RestTemplate_기본_커넥션_타임아웃_시간은_무기한이_아니다() {
		// given
		long from = System.currentTimeMillis();

		// when & then
		assertThatThrownBy(() -> simpleRestTemplate.getForObject(
			"http://192.167.1.1:8080/hello",
			String.class)
		).isInstanceOf(ResourceAccessException.class);

	}


}
