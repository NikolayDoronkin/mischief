package inc.mischief.mischief.endpoint;

import inc.mischief.mischief.configuration.jwt.JwtTokenProvider;
import inc.mischief.mischief.model.request.user.LoginRequest;
import inc.mischief.mischief.model.response.user.LoginResponse;
import inc.mischief.mischief.repositories.UserRepository;
import inc.mischief.mischief.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

import static inc.mischief.mischief.configuration.exception.ExceptionMessage.USER_NOT_FOUND_WITH_LOGIN;

@RestController
@RequiredArgsConstructor
@Tag(name = "Login", description = "Эндпоинт для входа и прочего (пинг сервера)")
public class LoginEndpoint {

	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;

	private final CommentService commentService;

	@PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
		var expectedUser = Optional.ofNullable(userRepository.findByLogin(loginRequest.login()))
				.orElseThrow(
						() -> new EntityNotFoundException(USER_NOT_FOUND_WITH_LOGIN.formatted(loginRequest.login())));

		if (!JwtTokenProvider.passwordEncoder().matches(loginRequest.password(), expectedUser.getPassword())) {
			throw new PersistenceException("Passwords aren't equals. Retry");
		}

		var token = jwtTokenProvider.createToken(expectedUser.getFirstName(),
				Collections.singletonList(expectedUser.getUserRole()));

		return ResponseEntity.ok(new LoginResponse(token));
	}

	@GetMapping("/ping")
	public String getHelloWorld() {
		return "Hello world!";
	}
}
