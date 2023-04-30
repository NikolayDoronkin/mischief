package inc.mischief.mischief.repositories;

import inc.mischief.mischief.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
	List<Ticket> findAllByRelatedProjectId(UUID projectId);
	Ticket findTicketByRelatedProjectIdAndId(UUID projectId, UUID ticketId);

	@Query(value = """
			select max(ticket.number) from ticket
			    join project p on ticket.fk_related_project = p.id
			where p.id = ?1
			""", nativeQuery = true)
	Integer findMaxNumberFromProject(UUID projectId);
}
