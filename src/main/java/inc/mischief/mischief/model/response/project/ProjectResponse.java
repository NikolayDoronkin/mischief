package inc.mischief.mischief.model.response.project;

import inc.mischief.mischief.model.response.user.UserResponse;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record ProjectResponse(
		UUID id,
		String name,
		String shortName,
		String description,
		UUID creatorId,
		UserResponse creator,
		LocalDate created,
		Set<UserResponse> users
) {
}
