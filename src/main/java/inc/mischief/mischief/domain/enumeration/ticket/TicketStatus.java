package inc.mischief.mischief.domain.enumeration.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum TicketStatus {
	OPEN(Set.of("READY")),
	READY(Set.of("IN_PROGRESS", "CLOSE")),
	IN_PROGRESS(Set.of("TESTING", "DONE", "READY", "ON_REVIEW")),
	TESTING(Set.of("IN_PROGRESS", "READY", "ON_REVIEW")),
	ON_REVIEW(Set.of("READY")),
	DONE(Set.of("CLOSED")),
	CLOSE(Set.of());

	private final Set<String> availableStatuses;
}
