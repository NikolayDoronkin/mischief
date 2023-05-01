package inc.mischief.mischief.model.request.comment;

import inc.mischief.mischief.model.request.ticket.UpdateTicketRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {
	private UUID authorId;
	private UpdateTicketRequest relatedTicket;
	private String value;
}
