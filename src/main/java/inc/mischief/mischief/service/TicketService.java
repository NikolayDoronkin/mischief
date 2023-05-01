package inc.mischief.mischief.service;

import inc.mischief.mischief.domain.Ticket;
import inc.mischief.mischief.domain.User;
import inc.mischief.mischief.domain.enumeration.ticket.TicketStatus;
import inc.mischief.mischief.mapper.TicketMapper;
import inc.mischief.mischief.repositories.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketMapper ticketMapper;
	private final TicketRepository ticketRepository;

	public Ticket findTicketFromProjectById(UUID projectId, UUID ticketId) {
		return ticketRepository.findTicketByRelatedProjectIdAndId(projectId, ticketId);
	}

	public List<Ticket> findTicketsFromProject(UUID projectId) {
		return ticketRepository.findAllByRelatedProjectId(projectId);
	}

	public List<Ticket> findChildTickets(UUID parentTicket) {
		return ticketRepository.findByParentTicketId(parentTicket);
	}

	@Transactional
	public Ticket create(Ticket createdTicket, User creator) {
		createdTicket.setReporter(creator);
		int maxNumberFromProject = Optional.ofNullable(ticketRepository.findMaxNumberFromProject(createdTicket.getRelatedProjectId())).orElse(0);
		createdTicket.setNumber(++maxNumberFromProject);

		return ticketRepository.save(createdTicket);
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

		ticketMapper.update(ticket, updatedTicket);

		return ticketRepository.save(ticket);
	}

	@Transactional
	public void delete(UUID id) {
		ticketRepository.deleteById(id);
	}
}
