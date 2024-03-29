package inc.mischief.mischief.domain;

import inc.mischief.mischief.domain.enumeration.NotificationTemplate;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Notification extends GenericEntity {

	@Column(updatable = false)
	private String template; //- Шаблон уведомления

	@ManyToOne
	@JoinColumn(name = "fk_author")
	private User author; //- От кого уведомление

	@ManyToMany
	@JoinTable(
			name = "user_m2m_notification",
			joinColumns = @JoinColumn(name = "fk_notification", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "fk_user", referencedColumnName = "id"))
	private Set<User> receiver;

	@Column(name = "fk_related_ticket", nullable = false, insertable = false, updatable = false)
	private UUID relatedTicketId;

	@ManyToOne
	@JoinColumn(name = "fk_related_ticket")
	private Ticket relatedTicket; //- Ссылка, где уведомили

	@Column(nullable = false, updatable = false, insertable = false)
	private LocalDate created; //- Дата создания

	private LocalDate viewed;

	@PrePersist
	public void setupFields() {
		this.created = LocalDate.now();
	}

	public void setRelatedTicket(Ticket relatedTicket) {
		this.relatedTicket = relatedTicket;
		this.relatedTicketId = relatedTicket != null ? relatedTicket.getId() : null;
	}
}
