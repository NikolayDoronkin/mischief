package inc.mischief.mischief.domain;

import inc.mischief.mischief.domain.enumeration.ticket.TicketPriority;
import inc.mischief.mischief.domain.enumeration.ticket.TicketStatus;
import inc.mischief.mischief.domain.enumeration.ticket.TicketType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Ticket extends GenericEntity {

	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(nullable = false, updatable = false)
	private int number;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String description;

	@Column(name = "fk_assignee")
	private UUID assigneeId;

	@Column(name = "fk_reporter", nullable = false)
	private UUID reporterId;

	@Column(name = "fk_reviewer")
	private UUID reviewerId;

	@Column(nullable = false, updatable = false)
	private LocalDate created;//Дата создания

	@Column(nullable = false, insertable = false)
	private LocalDate updated;//Дата обновления

	@Column(name = "relatable_finished_date")
	private LocalDate relatableFinishedDate;//Дата желаемого завершения

	@Column(name = "priority_name", nullable = false)
	@Enumerated(EnumType.STRING)
	private TicketPriority priorityName;// Приоритет задачи

	@Column(nullable = false)
	private int priority;// Приоритет задачи

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TicketStatus status;// Статус задачи

	@Column(name = "fk_related_project", nullable = false)
	private UUID relatedProjectId;

	private LocalDate started;

	private LocalDate finished;

	private long duration;

	@Column(nullable = false)
	private int difficulty;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TicketType type;//Тип задачи

	@Column(name = "fk_parent")
	private UUID parentTicketId;

	@ManyToOne
	@JoinColumn(name = "fk_assignee", insertable = false, updatable = false)
	private User assignee;

	@ManyToOne
	@JoinColumn(name = "fk_reporter", insertable = false, updatable = false)
	private User reporter;

	@ManyToOne
	@JoinColumn(name = "fk_reviewer", insertable = false, updatable = false)
	private User reviewer;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "user_m2m_ticket_listener",
			joinColumns = @JoinColumn(name = "fk_ticket"),
			inverseJoinColumns = @JoinColumn(name = "fk_user"))
	private Set<User> listeners;//

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "user_m2m_ticket_access",
			joinColumns = @JoinColumn(name = "fk_ticket"),
			inverseJoinColumns = @JoinColumn(name = "fk_user"))
	private Set<User> accessableUsers;//Список пользователей, кому доступен просмотр задачи

	@ManyToOne
	@JoinColumn(name = "fk_related_project", insertable = false, updatable = false)
	private Project relatedProject;// Проект, к которому относится задача

	@OneToMany(mappedBy = "relatedTicket")
	private List<Comment> comments;//Комментарии
//	private Ticket rootTicket;// Подзадачи

	public void setReporter(User reporter) {
		this.reporter = reporter;
		this.reporterId = reporter.getId() != null ? reporter.getId() : null;
	}

	public void setPriorityName(TicketPriority priorityName) {
		this.priorityName = priorityName;
		this.priority = priorityName.getPriority();
	}

	@PrePersist
	public void setupFields() {
		this.created = LocalDate.now();
	}

	@PreUpdate
	public void updateFields() {
		this.updated = LocalDate.now();
	}
}
