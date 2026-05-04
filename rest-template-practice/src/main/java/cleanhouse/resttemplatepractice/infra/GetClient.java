package cleanhouse.resttemplatepractice.infra;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cleanhouse.resttemplatepractice.infra.dto.GetDemoResponse;

@Component
public class GetClient {
	private final String url = "http://localhost:8080";

	private final RestTemplate simpleRestTemplate;

	@Autowired
	public GetClient(@Qualifier("simpleRestTemplate") RestTemplate simpleRestTemplate) {
		this.simpleRestTemplate = simpleRestTemplate;
	}

	// 1. RestTemplate 으로 get 요청 보내기
	public void sendGetRequest() {
		GetDemoResponse response = simpleRestTemplate.getForObject(
			url,
			GetDemoResponse.class
		);
	}

	// 1-2. RestTemplate 으로 get 요청 보내기 + 경로변수
	public void sendGetRequestWithPathVariable() {
		GetDemoResponse response = simpleRestTemplate.getForObject(
			"http://localhost:8080/resources/{}",
			GetDemoResponse.class,
			1L
		);
	}

	// 1-3. RestTemplate 으로 get 요청 보내기 + 쿼리파람
	public void sendGetRequestWithQueryParam() {
		URI uri = UriComponentsBuilder
			.fromUriString("http://localhost:8080")
			.queryParam("name", "kim")
			.queryParam("age", 10)
			.build()
			.toUri();

		GetDemoResponse response = simpleRestTemplate.getForObject(
			uri,
			GetDemoResponse.class
		);
	}

}
