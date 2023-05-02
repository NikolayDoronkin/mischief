package inc.mischief.mischief.model.response.notification;

import inc.mischief.mischief.model.response.ticket.TicketResponse;
import inc.mischief.mischief.model.response.user.UserResponse;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record NotificationResponse(
		UUID id,
		String template,
		UserResponse author,
		Set<UserResponse> receiver,
		UUID relatedTicketId,
		TicketResponse relatedTicket,
		LocalDate created,
		LocalDate viewed
) {
}
