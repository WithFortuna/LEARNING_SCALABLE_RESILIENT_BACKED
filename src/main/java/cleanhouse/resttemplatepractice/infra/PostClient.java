package cleanhouse.resttemplatepractice.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import cleanhouse.resttemplatepractice.infra.dto.PostDemoRequest;
import cleanhouse.resttemplatepractice.infra.dto.PostDemoResponse;

@Component
public class PostClient {
	private final String url = "http://localhost:8080";

	private final RestTemplate simpleRestTemplate;

	@Autowired
	public PostClient(@Qualifier("simpleRestTemplate") RestTemplate simpleRestTemplate) {
		this.simpleRestTemplate = simpleRestTemplate;
	}

	// 2. RestTemplate 으로 post 요청 보내기
	public void sendPostRequest() {
		PostDemoRequest request = new PostDemoRequest(1L, "elon", 30);

		PostDemoResponse response = simpleRestTemplate.postForObject(
			url,
			request,
			PostDemoResponse.class
		);
	}
}
