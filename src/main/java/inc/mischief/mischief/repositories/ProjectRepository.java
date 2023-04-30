package inc.mischief.mischief.repositories;

import inc.mischief.mischief.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
	Project findProjectByName(String name);

	@Query(value = """
			select id from project
				join user_m2m_project um2mp on project.id = um2mp.fk_project
			where um2mp.fk_user = ?1
			""", nativeQuery = true)
	List<UUID> findAllForUserByAccess(UUID userId);
	List<Project> findAllByIdIn(Collection<UUID> projectIds);
}
