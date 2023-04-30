package inc.mischief.mischief.domain;

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
public class Project extends GenericEntity {

	@Column(nullable = false)
	private String name;

	@Column(name = "fk_creator", nullable = false, updatable = false, insertable = false)
	private UUID creatorId;

	@Column(name = "short_name", nullable = false)
	private String shortName;

	@Column
	private String description;

	@Column(nullable = false, updatable = false)
	private LocalDate created;

	@Column(insertable = false)
	private LocalDate deleted;

	@ManyToOne
	@JoinColumn(name = "fk_creator")
	private User creator;

	@EqualsAndHashCode.Exclude
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_m2m_project",
			joinColumns = @JoinColumn(name = "fk_project"),
			inverseJoinColumns = @JoinColumn(name = "fk_user"))
	private Set<User> users;

	public void setCreator(User creator) {
		this.creator = creator;
		this.creatorId = creator.getId() != null ? creator.getId() : null;
	}

	@PrePersist
	public void setupFields() {
		this.created = LocalDate.now();
	}
}
