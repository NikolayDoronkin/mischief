package inc.mischief.mischief.service;

import inc.mischief.mischief.domain.Project;
import inc.mischief.mischief.domain.Ticket;
import inc.mischief.mischief.domain.User;
import inc.mischief.mischief.domain.enumeration.ticket.TicketStatus;
import inc.mischief.mischief.mapper.ProjectMapper;
import inc.mischief.mischief.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectDeletedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectMapper projectMapper;
	private final ProjectRepository projectRepository;

	private final UserService userService;
	private final TicketService ticketService;

	@Transactional
	public void deleteUserFromProject(UUID projectId, UUID userId) {
		var project = projectRepository.findById(projectId)
				.orElseThrow(EntityNotFoundException::new);

		var users = project.getUsers();

		users.stream()
				.filter(user -> Objects.equals(user.getId(), userId))
				.findFirst()
				.ifPresent(users::remove);
	}

	public Collection<User> getMembersFromProject(UUID projectId) {
		return userService.findByIds(projectRepository.findUserIds(projectId));
	}

	public Map<User, Double> getStatistics(UUID projectId) {
		var resultStatistics = new HashMap<User, Double>();

		Map<User, Set<Ticket>> userTickets = ticketService.findTicketsFromProject(projectId)
				.stream()
				.filter(ticket -> ticket.getStatus() == TicketStatus.DONE)
				.collect(Collectors.groupingBy(Ticket::getAssignee, Collectors.toSet()));

		userTickets.forEach((user, tickets) -> {
			var prioritySummary = takeSummaryByField(tickets, ticket -> ticket.getPriorityName().getPriority());
			var difficultySummary = takeSummaryByField(tickets, Ticket::getDifficulty);
			var trackedTimeSummary = takeSummaryByField(tickets, Ticket::getDuration);

			var perfomance = (double) (prioritySummary * difficultySummary / trackedTimeSummary);
			resultStatistics.put(user, perfomance);
		});

		return resultStatistics;
	}

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

	private long takeSummaryByField(Set<Ticket> tickets, ToLongFunction<Ticket> method) {
		return tickets.stream()
				.mapToLong(method)
				.sum();
	}
}
