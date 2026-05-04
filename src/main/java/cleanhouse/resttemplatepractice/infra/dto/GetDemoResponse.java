package cleanhouse.resttemplatepractice.infra.dto;

public record GetDemoResponse(
	Long id,
	String name,
	Integer age
) {
}
