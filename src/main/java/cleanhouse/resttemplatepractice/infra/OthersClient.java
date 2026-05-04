package cleanhouse.resttemplatepractice.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import cleanhouse.resttemplatepractice.infra.dto.PostDemoRequest;
import cleanhouse.resttemplatepractice.infra.dto.PostDemoResponse;

@Component
public class OthersClient {
	private final String url = "http://localhost:8080";

	private final RestTemplate simpleRestTemplate;

	@Autowired
	public OthersClient(@Qualifier("simpleRestTemplate") RestTemplate simpleRestTemplate) {
		this.simpleRestTemplate = simpleRestTemplate;
	}

	// 5. others
	public void sendWithHeader(){
		HttpEntity<PostDemoRequest> httpEntity = buildHttpEntity();
		ResponseEntity<PostDemoResponse> bodyWithHeader = simpleRestTemplate.postForEntity(
			url,
			httpEntity,
			PostDemoResponse.class
		);

		PostDemoResponse bodyOnly = simpleRestTemplate.postForObject(
			url,
			httpEntity,
			PostDemoResponse.class
		);

	}

	private static HttpEntity<PostDemoRequest> buildHttpEntity() {
		PostDemoRequest request = new PostDemoRequest(1L, "elon", 10);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth("access-token-value");

		return new HttpEntity<>(request, headers);
	}
}
