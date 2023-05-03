package inc.mischief.mischief.service;

import inc.mischief.mischief.domain.Project;
import inc.mischief.mischief.domain.Ticket;
import inc.mischief.mischief.domain.User;
import inc.mischief.mischief.domain.enumeration.ticket.TicketStatus;
import inc.mischief.mischief.mapper.ProjectMapper;
import inc.mischief.mischief.mapper.TicketMapper;
import inc.mischief.mischief.mapper.UserMapper;
import inc.mischief.mischief.repositories.ProjectRepository;
import inc.mischief.mischief.repositories.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectDeletedException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final UserMapper userMapper;
	private final TicketMapper ticketMapper;
	private final ProjectMapper projectMapper;

	private final ProjectRepository projectRepository;
	private final TicketRepository ticketRepository;

	private final UserService userService;
	private final TicketService ticketService;

	public HashMap<String, Object> getProjectDashboard(User user, UUID projectId) {
		var result = new HashMap<String, Object>();

//		TOTAL TICKETS FROM PROJECT
		var ticketsFromProject = ticketService.findTicketsFromProject(projectId);

//		Карточки
//		---------------------------------------------------------------------------------------------------
		var tickets = ticketsFromProject.stream()
				.filter(ticket -> Objects.equals(ticket.getAssigneeId(), user.getId()))
				.toList();
		var done = tickets.stream()
				.filter(ticket -> ticket.getStatus() == TicketStatus.DONE)
				.toList();
		var inProgress = tickets.stream()
				.filter(ticket -> ticket.getStatus() == TicketStatus.IN_PROGRESS)
				.toList();
		var onReview = tickets.stream()
				.filter(ticket -> ticket.getStatus() == TicketStatus.ON_REVIEW)
				.toList();
//		Карточки
//		---------------------------------------------------------------------------------------------------

		var lastUpdatedTickets = tickets.stream()
				.sorted((o1, o2) -> {
					if(Objects.isNull(o1.getUpdated()) || Objects.isNull(o2.getUpdated())) {
						if (o1.getCreated().isEqual(o2.getCreated())) return 0;

						return o1.getCreated().isBefore(o2.getCreated())
								? 1
								: -1;
					}
					if (o1.getUpdated().isEqual(o2.getUpdated())) return 0;

					return o1.getUpdated().isBefore(o2.getUpdated())
							? 1
							: -1;
				})
				.limit(5)
				.toList();
//		--------------------------------------------------------------------------------------------------
		result.put("totalTicketsFromProject", ticketsFromProject.size());
		result.put("done", done.size());
		result.put("inProgress", inProgress.size());
		result.put("onReview", onReview.size());

		result.put("lastUpdatedTickets", ticketMapper.convert(ticketsFromProject));

		return result;
	}

	public HashMap<String, Object> getDashboard(User user) {
//		Карточки
//		---------------------------------------------------------------------------------------------------
//		TOTAL PROJECTS
		var userProjects = projectRepository.findAllByUsersContains(user);

		var userProjectIds = userProjects.stream()
				.map(Project::getId)
				.toList();

//		TOTAL TICKETS FROM PROJECTS
		var ticketsFromProjects = ticketRepository.findAllByRelatedProjectIdIn(userProjectIds)
				.stream()
				.limit(5)
				.toList();

		//		TOTAL TICKETS FROM PROJECTS IN RPOGRESS
		var inProgress = ticketsFromProjects.stream()
				.filter(ticket -> ticket.getStatus() == TicketStatus.IN_PROGRESS)
				.toList();

//		TOTAL TICKETS FROM PROJECTS IN DONE
		var done = ticketsFromProjects.stream()
				.filter(ticket -> ticket.getStatus() == TicketStatus.DONE)
				.toList();

//		Остальное
//		-----------------------------------------------------------------------------------------------------
		List<Ticket> lastModifiedTickets = ticketRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "updated")))
				.stream()
				.filter(ticket -> Objects.equals(ticket.getAssigneeId(), user.getId()))
				.limit(5)
				.toList();

		List<Project> lastModifiedProjects = lastModifiedTickets.stream()
				.map(Ticket::getRelatedProject)
				.toList();
//		-----------------------------------------------------------------------------------------------------
		var result = new HashMap<String, Object>();

		result.put("totalProjects", userProjects.size());
		result.put("totalTicketsFromProjects", ticketsFromProjects.size());
		result.put("totalTicketsFromProjectsInProgress", inProgress.size());
		result.put("totalTicketsFromProjectsInDone", done.size());

		result.put("lastModifiedProjects", projectMapper.convert(lastModifiedProjects));
		result.put("lastModifiedTickets", ticketMapper.convert(lastModifiedTickets));

		return result;
	}

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

	public List<Map<String, Object>> getStatistics(UUID projectId) {
		var resultStatistics = new ArrayList<Map<String, Object>>();

		Map<User, Set<Ticket>> userTickets = ticketService.findTicketsFromProject(projectId)
				.stream()
				.filter(ticket -> ticket.getStatus() == TicketStatus.DONE)
				.collect(Collectors.groupingBy(Ticket::getAssignee, Collectors.toSet()));

		userTickets.forEach((user, tickets) -> {
			var prioritySummary = takeSummaryByField(tickets, ticket -> ticket.getPriorityName().getPriority());
			var difficultySummary = takeSummaryByField(tickets, Ticket::getDifficulty);
			var trackedTimeSummary = takeSummaryByField(tickets, Ticket::getDuration);

			var performance = trackedTimeSummary != 0
					? (double) (prioritySummary * difficultySummary / trackedTimeSummary)
					: 0;

			resultStatistics.add(Map.of(
					"user", userMapper.convert(user),
					"performance", performance
			));
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
