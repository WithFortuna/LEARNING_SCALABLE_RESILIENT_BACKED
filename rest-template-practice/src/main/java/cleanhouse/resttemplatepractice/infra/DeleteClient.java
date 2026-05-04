package cleanhouse.resttemplatepractice.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DeleteClient {
	private final String url = "http://localhost:8080";

	private final RestTemplate simpleRestTemplate;

	@Autowired
	public DeleteClient(@Qualifier("simpleRestTemplate") RestTemplate simpleRestTemplate) {
		this.simpleRestTemplate = simpleRestTemplate;
	}

	// 4. RestTemplate 으로 delete 요청 보내기
	public void sendDeleteRequest() {
		simpleRestTemplate.delete(
			"http://localhost:8080/resources/{number}",
			26L
		);
	}

}
