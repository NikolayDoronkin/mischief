package inc.mischief.mischief.repositories;

import inc.mischief.mischief.domain.Project;
import inc.mischief.mischief.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
	Project findProjectByName(String name);

	List<Project> findAllByUsersContains(User user);

	@Query(value = """
			select id from project
				join user_m2m_project um2mp on project.id = um2mp.fk_project
			where um2mp.fk_user = ?1
			""", nativeQuery = true)
	List<UUID> findAllForUserByAccess(UUID userId);

	PageImpl<Project> findAllByIdIn(Collection<UUID> projectIds, Pageable pageable);

	@Query(value = """
			select um2mp.fk_user from user_m2m_project um2mp
			where um2mp.fk_project = ?1
			""", nativeQuery = true)
	List<UUID> findUserIds(UUID projectId);
}
