package inc.mischief.mischief.model.request.ticket;

import inc.mischief.mischief.domain.enumeration.ticket.TicketType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest extends AbstractTicketRequest{

	@NotNull
	private TicketType type;
}
