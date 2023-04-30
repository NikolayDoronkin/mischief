package inc.mischief.mischief.model.response.ticket;

import inc.mischief.mischief.domain.enumeration.ticket.TicketPriority;
import inc.mischief.mischief.domain.enumeration.ticket.TicketStatus;
import inc.mischief.mischief.domain.enumeration.ticket.TicketType;
import inc.mischief.mischief.model.response.project.ProjectResponse;
import inc.mischief.mischief.model.response.user.UserResponse;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record TicketResponse(
		UUID id,
		int number,
		String title,
		String description,
		UUID assigneeId,
		UUID reporterId,
		UserResponse reporter,
		UserResponse assignee,
		UserResponse reviewer,
		ProjectResponse relatedProject,
		LocalDate created,
		LocalDate updated,
		LocalDate relatableFinishedDate,
		TicketPriority priorityName,
		TicketStatus status,
		UUID relatedProjectId,
		TicketType type,
		UUID parentTicketId,
		Set<UserResponse> listeners,
		Set<UserResponse> accessableUsers
) {
}
