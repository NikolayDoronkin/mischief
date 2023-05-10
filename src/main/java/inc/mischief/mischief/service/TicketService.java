package inc.mischief.mischief.service;

import inc.mischief.mischief.domain.Ticket;
import inc.mischief.mischief.domain.User;
import inc.mischief.mischief.domain.enumeration.ticket.TicketStatus;
import inc.mischief.mischief.mapper.TicketMapper;
import inc.mischief.mischief.repositories.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketMapper ticketMapper;
	private final TicketRepository ticketRepository;

	public Ticket findTicketFromProjectById(UUID projectId, UUID ticketId) {
		return ticketRepository.findTicketByRelatedProjectIdAndId(projectId, ticketId);
	}

	public PageImpl<Ticket> findTicketsFromProject(UUID projectId, Pageable pageable) {
		return ticketRepository.findAllByRelatedProjectId(projectId, pageable);
	}

	public List<Ticket> findChildTickets(UUID parentTicket) {
		return ticketRepository.findByParentTicketId(parentTicket);
	}

	@Transactional
	public Ticket create(Ticket createdTicket, User creator) {
		createdTicket.setReporter(creator);
		int maxNumberFromProject = Optional.ofNullable(ticketRepository.findMaxNumberFromProject(createdTicket.getRelatedProjectId())).orElse(0);
		createdTicket.setNumber(++maxNumberFromProject);

		if (createdTicket.getStatus() == TicketStatus.IN_PROGRESS) {
			createdTicket.setStarted(LocalDate.now());
		}

		var savedTicket = ticketRepository.save(createdTicket);

		return ticketRepository.save(updateListeners(savedTicket));
	}

	private Ticket updateListeners(Ticket savedTicket) {
		var newListeners = Stream.of(savedTicket.getReporter(), savedTicket.getAssignee(), savedTicket.getReviewer())
				.filter(Objects::nonNull)
				.toList();
		savedTicket.getListeners().addAll(newListeners);

		return savedTicket;
	}

	public Ticket update(Ticket updatedTicket) {

		var ticket = ticketRepository.findById(updatedTicket.getId()).orElseThrow(EntityNotFoundException::new);

		var availableStatuses = ticket.getStatus().getAvailableStatuses()
				.stream()
				.map(TicketStatus::valueOf)
				.collect(Collectors.toSet());

		if (!availableStatuses.contains(updatedTicket.getStatus())) {
			throw new IllegalArgumentException("That status is not available: %s".formatted(updatedTicket.getStatus()));
		}

		checkDuration(ticket, updatedTicket);

		ticketMapper.update(ticket, updatedTicket);

		return ticketRepository.save(ticket);
	}

	private void checkDuration(Ticket ticket, Ticket updatedTicket) {
		var currentStatus = ticket.getStatus();
		var nextStatus = updatedTicket.getStatus();

		if (currentStatus == TicketStatus.IN_PROGRESS && nextStatus == TicketStatus.DONE) {
			ticket.setFinished(LocalDate.now());
			ticket.setDuration(Duration.between(ticket.getStarted(), ticket.getFinished()).toDays());
		}
		else if (currentStatus != TicketStatus.IN_PROGRESS && nextStatus == TicketStatus.IN_PROGRESS) {
			ticket.setDuration(ticket.getDuration() + Duration.between(ticket.getStarted(), LocalDate.now()).toDays());
		}
	}

	@Transactional
	public void delete(UUID id) {
		ticketRepository.deleteById(id);
	}
}
