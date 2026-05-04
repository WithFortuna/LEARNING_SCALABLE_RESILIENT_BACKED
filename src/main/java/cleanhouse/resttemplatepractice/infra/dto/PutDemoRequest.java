package cleanhouse.resttemplatepractice.infra.dto;

public record PutDemoRequest(
	Long id,
	String name,
	Integer age
) {
}
