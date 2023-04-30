package inc.mischief.mischief.service;

import inc.mischief.mischief.domain.Project;
import inc.mischief.mischief.domain.User;
import inc.mischief.mischief.mapper.ProjectMapper;
import inc.mischief.mischief.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectDeletedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectMapper projectMapper;
	private final ProjectRepository projectRepository;

	public Project findById(UUID projectId) {
		return projectRepository.findById(projectId)
				.orElseThrow(EntityNotFoundException::new);
	}

	public List<Project> findAllForUserWithAccess(UUID userId) {
		return projectRepository.findAllByIdIn(projectRepository.findAllForUserByAccess(userId));
	}

	@Transactional
	public Project create(Project createdProject, User creator) {
		createdProject.setCreator(creator);

		return projectRepository.save(createdProject);
	}

	public Project update(Project updatedProject) {

		var project = projectRepository.findById(updatedProject.getId()).orElseThrow(EntityNotFoundException::new);

		projectMapper.update(project, updatedProject);

		return projectRepository.save(project);
	}

	@Transactional
	public void delete(UUID id) {
		projectRepository.findById(id)
				.ifPresentOrElse(project -> {
					if (Objects.nonNull(project.getDeleted()))
						throw new ObjectDeletedException(
								"Project was already deleted!",
								project.getId(),
								project.toString());

					project.setDeleted(LocalDate.now());
				}, () -> {
					throw new EntityNotFoundException();
				});
	}
}
