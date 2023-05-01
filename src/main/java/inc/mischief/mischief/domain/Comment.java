package inc.mischief.mischief.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Comment extends GenericEntity {

	@ManyToOne
	@JoinColumn(name = "fk_author", insertable = false, updatable = false)
	private User author; //- Автор комментария

	@Column(name = "fk_author", nullable = false)
	private UUID authorId;

	@Column(nullable = false)
	private String value;

	private LocalDate created; //- Дата создания
	private LocalDate updated; //- Дата редактирования

//	@ManyToMany
//	private Set<User> notifiedUsers; //- Затронутые пользователи (через тег выслать уведомление)

	@Column(name = "fk_ticket", nullable = false, insertable = false, updatable = false)
	private UUID relatedTicketId;

	@ManyToOne
	@JoinColumn(name = "fk_ticket")
	private Ticket relatedTicket;

	@PrePersist
	public void setupFields() {
		this.created = LocalDate.now();
	}

	@PreUpdate
	public void updateFields() {
		this.updated = LocalDate.now();
	}

	public void setRelatedTicket(Ticket relatedTicket) {
		this.relatedTicket = relatedTicket;
		this.relatedTicketId = relatedTicket != null ? relatedTicket.getId() : null;
	}
}
