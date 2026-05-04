package cleanhouse.resttemplatepractice.infra;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import cleanhouse.resttemplatepractice.infra.dto.GetDemoResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExceptionHandlingClient {
	private final String url = "http://localhost:8080";

	private final RestTemplate simpleRestTemplate;

	public ExceptionHandlingClient(@Qualifier("simpleRestTemplate") RestTemplate simpleRestTemplate) {
		this.simpleRestTemplate = simpleRestTemplate;
	}

	public void handlingRestTemplateException() {
		try {
			simpleRestTemplate.getForObject(
				url,
				GetDemoResponse.class
			);
		} catch (HttpClientErrorException e) { // 4xx errors
			log.error("4xx 에러 발생 코드: {} ", e.getStatusCode());
			log.error("4xx 에러 발생 http body: {}", e.getResponseBodyAsString());
		} catch (HttpServerErrorException e) {
			log.error("5xx 에러 발생 코드: {}", e.getStatusCode());
			log.error("5xx 에러 발생 http body: {}", e.getResponseBodyAsString());
		} catch (RestClientException e) {
			// RestClientException이 RestTemplate의 최상위 예외다. ResourceAccessException도 여기에 잡힘
			log.error("그 외 RestClient에러");
		}
	}
}
