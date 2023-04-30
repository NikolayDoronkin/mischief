package inc.mischief.mischief.model.request.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {

	@NotNull
	private String name;

	@NotNull
	private String shortName;

	@NotNull
	private String description;

	private List<UUID> accessedUserIds;
}
