package inc.mischief.mischief.service;

import inc.mischief.mischief.domain.Comment;
import inc.mischief.mischief.domain.Ticket;
import inc.mischief.mischief.mapper.CommentMapper;
import inc.mischief.mischief.repositories.CommentRepository;
import inc.mischief.mischief.repositories.TicketRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentMapper commentMapper;
	private final TicketService ticketService;
	private final CommentRepository commentRepository;

	public List<Comment> getCommentsFromTicket(UUID ticketId) {
		return commentRepository.findAllByRelatedTicketId(ticketId);
	}

	@Transactional
	public Comment create(Comment comment) {
		var relatedTicket = comment.getRelatedTicket();
		comment.setRelatedTicket(ticketService.findTicketFromProjectById(relatedTicket.getRelatedProjectId(), relatedTicket.getId()));
		return commentRepository.save(comment);
	}

	@Transactional
	public Comment update(Comment updatedComment) {
		var comment = commentRepository.findById(updatedComment.getId())
				.orElseThrow(EntityExistsException::new);

		commentMapper.update(comment, updatedComment);

		return commentRepository.save(updatedComment);
	}

	@Transactional
	public void delete(UUID id) {
		commentRepository.findById(id)
				.ifPresentOrElse(commentRepository::delete, () -> {
					throw new EntityNotFoundException();
				});
	}
}
