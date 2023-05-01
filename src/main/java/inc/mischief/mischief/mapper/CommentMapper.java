package inc.mischief.mischief.mapper;

import inc.mischief.mischief.domain.Comment;
import inc.mischief.mischief.model.request.comment.CreateCommentRequest;
import inc.mischief.mischief.model.request.comment.UpdateCommentRequest;
import inc.mischief.mischief.model.response.comment.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		uses = TicketMapper.class)
public interface CommentMapper {
	Comment convert(CreateCommentRequest request);
	Comment convert(UpdateCommentRequest request);
	CommentResponse convert(Comment request);
	List<CommentResponse> convert(List<Comment> users);
	void update(@MappingTarget Comment updatedComment, Comment commentFromRequest);
}
