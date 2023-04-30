package inc.mischief.mischief.domain.enumeration.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TicketPriority {
	THE_LOWEST(100),
	LOW(200),
	MEDIUM(300),
	HIGH(400),
	THE_HIGHEST(500),
	CRITICAL(600);

	private final int priority;
}
