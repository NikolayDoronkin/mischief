package inc.mischief.mischief.model.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractUserRequest {

	@NotNull
	private String firstName;

	@NotNull
	private String lastName;

	@NotNull
	private String login;
	private String description;
	private String address;
	private String city;
	private String country;

	private String image;

	@Email
	private String email;
}
