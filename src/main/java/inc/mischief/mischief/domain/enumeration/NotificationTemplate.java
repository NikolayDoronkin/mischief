package inc.mischief.mischief.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationTemplate {
	NOTIFY("%s");

	private final String template;
}
