package inc.mischief.mischief.service;

import inc.mischief.mischief.domain.Ticket;
import inc.mischief.mischief.domain.User;
import inc.mischief.mischief.domain.enumeration.ticket.TicketStatus;
import inc.mischief.mischief.mapper.TicketMapper;
import inc.mischief.mischief.repositories.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;

    private final JdbcTemplate jdbcTemplate;

    public Ticket findTicketFromProjectById(UUID projectId, UUID ticketId) {
        return ticketRepository.findTicketByRelatedProjectIdAndId(projectId, ticketId);
    }

    public PageImpl<Ticket> findTicketsFromProject(UUID projectId, Pageable pageable, String searchFilter) {

        var uuids = jdbcTemplate.queryForList(
                """
                        select t.id from ticket t
                            join project p on t.fk_related_project = p.id
                            %s
                        where t.fk_related_project = '%s'
                        %s
                        """
                        .formatted(StringUtils.isNotBlank(searchFilter) ? "join \"user\" u on u.id = t.fk_assignee" : "",
                                projectId.toString(),
                                StringUtils.isNotBlank(searchFilter)
                                ? """
                                    and (
                                  p.short_name ilike '%1$s' or
                                  t.title ilike '%1$s' or
                                  t.priority_name ilike '%1$s' or
                                  t.type ilike '%1$s' or
                                  t.status ilike '%1$s' or
                                  u.first_name ilike '%1$s' or
                                  u.last_name ilike '%1$s'
                                )
                                """.formatted("%" + searchFilter + "%") : ""),
                UUID.class);

        return ticketRepository.findAllByIdIn(uuids, pageable);
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
            createdTicket.setStarted(LocalDateTime.now());
        }

        var savedTicket = ticketRepository.save(createdTicket);

        return ticketRepository.save(updateListeners(savedTicket));
    }

    private Ticket updateListeners(Ticket savedTicket) {
        var newListeners = Stream.of(savedTicket.getReporter(), savedTicket.getAssignee(), savedTicket.getReviewer())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        var updatedListeners = new HashSet<>(savedTicket.getListeners());
        updatedListeners.addAll(newListeners);
        savedTicket.setListeners(updatedListeners);

        return savedTicket;
    }

    public Ticket update(Ticket updatedTicket) {

        var ticket = ticketRepository.findById(updatedTicket.getId()).orElseThrow(EntityNotFoundException::new);

        if (updatedTicket.getStatus() != ticket.getStatus()) {
            var availableStatuses = ticket.getStatus().getAvailableStatuses()
                    .stream()
                    .map(TicketStatus::valueOf)
                    .collect(Collectors.toSet());

            if (!availableStatuses.contains(updatedTicket.getStatus())) {
                throw new IllegalArgumentException("That status is not available: %s".formatted(updatedTicket.getStatus()));
            }
        }

        checkDuration(ticket, updatedTicket);

        ticketMapper.update(ticket, updatedTicket);

        return ticketRepository.save(ticket);
    }

    private void checkDuration(Ticket ticket, Ticket updatedTicket) {
        var currentStatus = ticket.getStatus();
        var nextStatus = updatedTicket.getStatus();

        if (currentStatus == TicketStatus.IN_PROGRESS && nextStatus == TicketStatus.DONE) {
            ticket.setFinished(LocalDateTime.now());
            ticket.setDuration(Duration.between(ticket.getStarted(), ticket.getFinished()).toDays());
        } else if (currentStatus != TicketStatus.IN_PROGRESS && nextStatus == TicketStatus.IN_PROGRESS) {
            ticket.setStarted(Optional.ofNullable(ticket.getStarted()).orElse(LocalDateTime.now()));
            ticket.setDuration(ticket.getDuration() + Duration.between(ticket.getStarted(), LocalDateTime.now()).toDays());
        }
    }

    @Transactional
    public void delete(UUID id) {
        ticketRepository.deleteById(id);
    }
}
