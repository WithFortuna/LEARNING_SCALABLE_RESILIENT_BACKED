package cleanhouse.resttemplatepractice.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import cleanhouse.resttemplatepractice.infra.dto.PutDemoRequest;

@Component
public class PutClient {
	private final String url = "http://localhost:8080";

	private final RestTemplate simpleRestTemplate;

	@Autowired
	public PutClient(@Qualifier("simpleRestTemplate") RestTemplate simpleRestTemplate) {
		this.simpleRestTemplate = simpleRestTemplate;
	}

	// 3. RestTemplate 으로 put 요청 보내기
	public void sendPutRequest() {
		PutDemoRequest request = new PutDemoRequest(1L, "elon", 30);
		simpleRestTemplate.put(
			url,
			request
		);


	}
}
