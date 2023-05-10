package inc.mischief.mischief.repositories;

import inc.mischief.mischief.domain.User;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	User findByLogin(String login);
	User findByLoginAndIdIsNot(String login, UUID id);
	User findByFirstName(String firstName);
	List<User> findByDeletedIsNullOrderByLastName();
	PageImpl<User> findByIdIn(Collection<UUID> ids, Pageable pageable);
}
