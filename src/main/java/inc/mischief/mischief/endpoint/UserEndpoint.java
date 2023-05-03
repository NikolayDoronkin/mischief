package inc.mischief.mischief.endpoint;

import inc.mischief.mischief.configuration.jwt.JwtUser;
import inc.mischief.mischief.mapper.UserMapper;
import inc.mischief.mischief.model.request.user.CreateUserRequest;
import inc.mischief.mischief.model.request.user.UpdateUserRequest;
import inc.mischief.mischief.model.response.user.UserResponse;
import inc.mischief.mischief.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.openssl.PasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "Эндпоинты для работы с пользователем")
public class UserEndpoint {

	private final UserMapper userMapper;
	private final UserService userService;

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Получить список всех пользователей")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User created",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = UserResponse.class))})})
	@GetMapping("/getAll")
	public ResponseEntity<List<UserResponse>> getAll() {
		return new ResponseEntity<>(userMapper.convert(userService.findAll()), HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Получить пользователя по id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User founded",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = UserResponse.class))})})
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
		return new ResponseEntity<>(userMapper.convert(userService.findById(id)), HttpStatus.OK);
	}

	@Transactional
	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping("/current")
	public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal JwtUser jwtUser) {
		return ResponseEntity.ok(userMapper.convert(jwtUser.getUser()));
	}

	@Operation(summary = "Создать клиента")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User created",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = UserResponse.class))})})
	@PostMapping("/create")
	public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) throws PasswordException {

		if(!Objects.equals(request.getPassword(), request.getRepeatPassword())) {
			throw new PasswordException("Пароли не совпадают. Повторите попытку");
		}

		return new ResponseEntity<>(
				userMapper.convert(userService.create(userMapper.convert(request))),
				HttpStatus.CREATED);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Обновить клиента")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User created",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = UserResponse.class))})})
	@PostMapping("/update")
	public ResponseEntity<UserResponse> update(@Valid @RequestBody UpdateUserRequest request) {
		return new ResponseEntity<>(
				userMapper.convert(userService.update(userMapper.convert(request))),
				HttpStatus.ACCEPTED);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Удалить клиента")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ResponseEntity.BodyBuilder.class))})})
	@DeleteMapping("/delete/{id}")
	public void delete(@PathVariable UUID id) {
		userService.delete(id);
	}
}
