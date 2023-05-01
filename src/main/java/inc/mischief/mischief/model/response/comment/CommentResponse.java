package inc.mischief.mischief.model.response.comment;

import inc.mischief.mischief.domain.User;
import inc.mischief.mischief.model.response.ticket.TicketResponse;

import java.time.LocalDate;
import java.util.UUID;

public record CommentResponse(
		UUID id,
		User author,
		LocalDate created,
		LocalDate updated,
		TicketResponse relatedTicket,
		String value
) {
}
