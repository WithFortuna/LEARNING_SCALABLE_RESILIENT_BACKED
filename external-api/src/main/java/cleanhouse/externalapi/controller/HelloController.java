package cleanhouse.externalapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/hello")
	public void hello()  throws InterruptedException {
		Thread.sleep(Long.MAX_VALUE);
	}
}
