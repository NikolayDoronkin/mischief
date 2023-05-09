package inc.mischief.mischief.model.request.user;

import inc.mischief.mischief.domain.enumeration.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateUserRequest extends AbstractUserRequest {

	@NotNull
	private String password;

	@NotNull
	private String repeatPassword;

	private UserRole userRole;
}
