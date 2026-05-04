package cleanhouse.resttemplatepractice.infra.dto;

public record PostDemoRequest(
	Long id,
	String name,
	Integer age
) {
}
