package inc.mischief.mischief.service;

import inc.mischief.mischief.domain.User;
import inc.mischief.mischief.domain.enumeration.UserRole;
import inc.mischief.mischief.mapper.UserMapper;
import inc.mischief.mischief.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectDeletedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserMapper userMapper;
	private final UserRepository userRepository;

	public List<User> findAll() {
		return userRepository.findByDeletedIsNullOrderByLastName()
				.stream()
				.filter(user -> user.getUserRole() != UserRole.SYSTEM)
				.toList();
	}

	public Set<User> findByIds(Collection<UUID> ids) {
		return userRepository.findByIdIn(ids);
	}

	public User findById(UUID id) {
		return userRepository.findByIdIn(Collections.singleton(id))
				.stream()
				.findFirst()
				.orElseThrow(EntityExistsException::new);
	}

	public User create(User createdUser) {

		checkExisting(userRepository.findByLogin(createdUser.getLogin()), "User with this login already exists!");

		if (createdUser.getUserRole() == null) {
			createdUser.setUserRole(UserRole.USER);
		}

		return userRepository.save(createdUser);
	}

	public User update(User updatedUser) {
		checkExisting(userRepository.findByLogin(updatedUser.getLogin()),
				"User with this login already exists!");

		var user = userRepository.findById(updatedUser.getId()).orElseThrow(EntityNotFoundException::new);

		userMapper.update(user, updatedUser);

		return userRepository.save(user);
	}

	@Transactional
	public void delete(UUID id) {
		userRepository.findById(id)
				.ifPresentOrElse(user -> {
					if (Objects.nonNull(user.getDeleted()))
						throw new ObjectDeletedException(
								"User was already deleted!",
								user.getId(),
								user.toString());

					user.setDeleted(LocalDate.now());
				}, () -> {
					throw new EntityNotFoundException();
				});
	}

	private void checkExisting(User user, String message) {
		Optional.ofNullable(user).ifPresent(foundedUser -> { throw new EntityExistsException(message); });
	}
}
