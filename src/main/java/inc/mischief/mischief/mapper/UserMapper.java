package inc.mischief.mischief.mapper;

import inc.mischief.mischief.configuration.jwt.JwtTokenProvider;
import inc.mischief.mischief.domain.Ticket;
import inc.mischief.mischief.domain.User;
import inc.mischief.mischief.model.request.user.CreateUserRequest;
import inc.mischief.mischief.model.request.user.UpdateUserRequest;
import inc.mischief.mischief.model.response.ticket.TicketResponse;
import inc.mischief.mischief.model.response.user.UserResponse;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.PageImpl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

	@BeforeMapping
	default void mapPassword(CreateUserRequest request) {
		Optional.ofNullable(request.getPassword())
				.ifPresent(password -> {
					var encodedPassword = JwtTokenProvider.passwordEncoder().encode(password);
					request.setPassword(encodedPassword);
				});
	}

	User convert(CreateUserRequest request);
	User convert(UpdateUserRequest request);
	UserResponse convert(User user);
	List<UserResponse> convert(List<User> users);
	Collection<UserResponse> convert(Collection<User> users);

	default PageImpl<UserResponse> convert(PageImpl<User> users) {
		var responses = users.stream()
				.map(this::convert)
				.toList();
		return new PageImpl<>(responses, users.getPageable(), users.getTotalElements());
	}

	void update(@MappingTarget User updatedUser, User userFromRequest);
}
