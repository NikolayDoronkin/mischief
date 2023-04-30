package inc.mischief.mischief.model.request.ticket;

import inc.mischief.mischief.domain.enumeration.ticket.TicketPriority;
import inc.mischief.mischief.domain.enumeration.ticket.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractTicketRequest {

	@NotNull
	private String title;

	@NotNull
	private String description;

	private UUID assigneeId;

	private UUID reviewerId;

	private LocalDate relatableFinishedDate;

	@NotNull
	private TicketPriority priorityName;

	@NotNull
	private TicketStatus status;

	@NotNull
	private UUID relatedProjectId;

	private UUID parentTicketId;

	private Set<UUID> accessedUserIds = Set.of();

	private Set<UUID> listenerIds  = Set.of();
}
