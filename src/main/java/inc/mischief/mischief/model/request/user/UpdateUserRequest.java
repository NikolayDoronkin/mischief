package inc.mischief.mischief.model.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateUserRequest extends AbstractUserRequest {

	@NotNull
	private UUID id;
}
