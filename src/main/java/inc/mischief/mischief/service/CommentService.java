package inc.mischief.mischief.service;

import inc.mischief.mischief.domain.Comment;
import inc.mischief.mischief.domain.Notification;
import inc.mischief.mischief.mapper.CommentMapper;
import inc.mischief.mischief.repositories.CommentRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final UserService userService;
	private final TicketService ticketService;
	private final ProjectService projectService;
	private final NotificationService notificationService;

	private final CommentMapper commentMapper;
	private final CommentRepository commentRepository;

	public List<Comment> getCommentsFromTicket(UUID ticketId) {
		return commentRepository.findAllByRelatedTicketId(ticketId);
	}

	@Transactional
	public Comment create(Comment comment) {
		var relatedTicket = ticketService.findTicketFromProjectById(comment.getRelatedTicket().getRelatedProjectId(), comment.getRelatedTicket().getId());
		comment.setRelatedTicket(ticketService.findTicketFromProjectById(relatedTicket.getRelatedProjectId(), relatedTicket.getId()));
		commentRepository.save(comment);

		var author = userService.findById(comment.getAuthorId());
		var relatedProject = projectService.findById(relatedTicket.getRelatedProjectId());
		var notification = Notification.builder()
				.template("%s %s оставил комментарий в %s-%s"
						.formatted(
								author.getFirstName(),
								author.getLastName(),
								relatedProject.getShortName(),
								relatedTicket.getNumber()))
				.receiver(new HashSet<>(relatedTicket.getListeners()))
				.author(author)
				.build();
		notification.setRelatedTicket(relatedTicket);

		notificationService.create(notification);

		return comment;
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
