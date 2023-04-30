package inc.mischief.mischief.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import inc.mischief.mischief.domain.enumeration.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"user\"")
@ToString
public class User extends GenericEntity {

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(nullable = false)
	private String login;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, insertable = false, updatable = false)
	private LocalDate created;

	@Column(nullable = false)
	private LocalDate deleted;

	@Column(name = "user_role", nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	/*@OneToMany(mappedBy = "creator", fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<Project> creatorProjects;

	@ManyToMany(mappedBy = "users")
	private Set<Project> accessedProjects;

	@ManyToMany(mappedBy = "accessableUsers")
	private Set<Ticket> accessToTicketUser;

	@ManyToMany(mappedBy = "listeners")
	private Set<Ticket> listenerToTicketUsers;*/

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "user_m2m_comment",
			joinColumns = @JoinColumn(name = "fk_user"),
			inverseJoinColumns = @JoinColumn(name = "fk_comment"))
	@ToString.Exclude
	private Set<Comment> comments;


	@PrePersist
	public void setupFields() {
		this.created = LocalDate.now();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		User user = (User) o;
		return getId() != null && Objects.equals(getId(), user.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
