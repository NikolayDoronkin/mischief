package inc.mischief.mischief.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	@JoinColumn(name = "fk_author")
	private User author; //- Автор комментария

	private LocalDate created; //- Дата создания
	private LocalDate updated; //- Дата редактирования

//	@ManyToMany
//	private Set<User> notifiedUsers; //- Затронутые пользователи (через тег выслать уведомление)

	@Column(name = "fk_ticket", nullable = false, insertable = false, updatable = false)
	private UUID relatedTicketId;

	@ManyToOne
	@JoinColumn(name = "fk_ticket")
	private Ticket relatedTicket;
}
