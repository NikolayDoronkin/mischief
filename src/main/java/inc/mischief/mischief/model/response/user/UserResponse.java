package inc.mischief.mischief.model.response.user;

import inc.mischief.mischief.domain.enumeration.UserRole;
import inc.mischief.mischief.model.response.project.ProjectResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
public class UserResponse {

	private UUID id;
	private String firstName;
	private String lastName;
	private String login;
	private LocalDate created;
	private LocalDate deleted;
	private UserRole userRole;
	private String description;
	private String address;
	private String city;
	private String country;
	private String image;
	private String email;
	private Set<ProjectResponse> creatorProjects;
}
