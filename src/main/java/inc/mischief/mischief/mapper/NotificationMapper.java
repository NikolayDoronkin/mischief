package inc.mischief.mischief.mapper;

import inc.mischief.mischief.domain.Comment;
import inc.mischief.mischief.domain.Notification;
import inc.mischief.mischief.model.response.notification.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {
//	Notification convert(CreateCommentRequest request);
//	Notification convert(UpdateCommentRequest request);
//	CommentResponse convert(Comment request);
	List<NotificationResponse> convert(List<Notification> users);
//	void update(@MappingTarget Comment updatedComment, Comment commentFromRequest);
}
