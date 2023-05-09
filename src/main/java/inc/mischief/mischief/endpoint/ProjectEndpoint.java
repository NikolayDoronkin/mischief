package inc.mischief.mischief.endpoint;

import inc.mischief.mischief.configuration.jwt.JwtUser;
import inc.mischief.mischief.mapper.ProjectMapper;
import inc.mischief.mischief.mapper.UserMapper;
import inc.mischief.mischief.model.request.project.CreateProjectRequest;
import inc.mischief.mischief.model.request.project.UpdateProjectRequest;
import inc.mischief.mischief.model.response.project.ProjectResponse;
import inc.mischief.mischief.model.response.user.UserResponse;
import inc.mischief.mischief.service.ProjectService;
import inc.mischief.mischief.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Project", description = "Эндпоинты для работы с проектами")
public class ProjectEndpoint {

	private final UserMapper userMapper;
	private final ProjectMapper projectMapper;

	private final UserService userService;
	private final ProjectService projectService;

	@Operation(summary = "Получить статистику по проекту")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ProjectResponse.class))})})
	@GetMapping("/{id}/statistics")
	public ResponseEntity<List<Map<String, Object>>> getStatistics(@AuthenticationPrincipal JwtUser currentUser,
															 @PathVariable UUID id) {
		return new ResponseEntity<>(projectService.getStatistics(id), HttpStatus.OK);
	}

	@Operation(summary = "Получить всех участников проекта")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ProjectResponse.class))})})
	@GetMapping("/{id}/members")
	public ResponseEntity<Collection<UserResponse>> getMembers(@AuthenticationPrincipal JwtUser currentUser,
															   @PathVariable UUID id) {
		return new ResponseEntity<>(userMapper.convert(projectService.getMembersFromProject(id)), HttpStatus.OK);
	}

	@Operation(summary = "Создать клиента")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ProjectResponse.class))})})
	@GetMapping("/forUser/{id}")
	public ResponseEntity<ProjectResponse> findAccessedProjectsForUser(@AuthenticationPrincipal JwtUser currentUser,
																	   @PathVariable UUID id) {
		return new ResponseEntity<>(
				projectMapper.convert(projectService.findById(id)),
				HttpStatus.OK);
	}

	@Operation(summary = "Поиск проекта по идентификатору")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ProjectResponse.class))})})
	@GetMapping("/{id}")
	public ResponseEntity<ProjectResponse> findById(@AuthenticationPrincipal JwtUser currentUser,
													@PathVariable UUID id) {
		return new ResponseEntity<>(
				projectMapper.convert(projectService.findById(id)),
				HttpStatus.OK);
	}

	@Operation(summary = "Найти все доступные проекты для пользователя")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ProjectResponse.class))})})
	@GetMapping("/findByCreatorId/{id}")
	public ResponseEntity<Page<ProjectResponse>> findByCreatorId(@AuthenticationPrincipal JwtUser currentUser,
																 @PathVariable UUID id, @ParameterObject Pageable pageable) {
		return new ResponseEntity<>(
				projectMapper.convert(projectService.findAllForUserWithAccess(id, pageable)),
				HttpStatus.OK);
	}

	@Operation(summary = "Создать проект")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User created",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = UserResponse.class))})})
	@PostMapping("/create")
	public ResponseEntity<ProjectResponse> create(@AuthenticationPrincipal JwtUser currentUser,
												  @Valid @RequestBody CreateProjectRequest request) {
		var convert = projectMapper.convert(request);
		convert.setUsers(userService.findByIds(request.getAccessedUserIds()));
		return new ResponseEntity<>(
				projectMapper.convert(projectService.create(convert, currentUser.getUser())),
				HttpStatus.CREATED);
	}

	@Operation(summary = "Обновить проект")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User created",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = UserResponse.class))})})
	@PostMapping("/update")
	public ResponseEntity<ProjectResponse> update(@Valid @RequestBody UpdateProjectRequest request) {
		return new ResponseEntity<>(
				projectMapper.convert(projectService.update(projectMapper.convert(request))),
				HttpStatus.ACCEPTED);
	}

	@Operation(summary = "Удалить клиента")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ResponseEntity.BodyBuilder.class))})})
	@DeleteMapping("/delete/{id}")
	public void delete(@PathVariable UUID id) {
		projectService.delete(id);
	}

	@Operation(summary = "Удалить участника из проекта")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ResponseEntity.BodyBuilder.class))})})
	@PostMapping("/delete/{projectId}/delete/{userId}")
	public void delete(@PathVariable UUID projectId, @PathVariable UUID userId) {
		projectService.deleteUserFromProject(projectId, userId);
	}

	@Operation(summary = "Получить статистику по проектам")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ProjectResponse.class))})})
	@GetMapping("/getDashboard")
	public ResponseEntity<Map<String, Object>> getDashboard(@AuthenticationPrincipal JwtUser user) {
		return new ResponseEntity<>(projectService.getDashboard(user.getUser()), HttpStatus.OK);
	}

	@Operation(summary = "Получить статистику по проекту")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ProjectResponse.class))})})
	@GetMapping("/{projectId}/getDashboardForProject")
	public ResponseEntity<Map<String, Object>> getDashboardForProject(@PathVariable UUID projectId,
																	  @AuthenticationPrincipal JwtUser user) {
		return new ResponseEntity<>(projectService.getProjectDashboard(user.getUser(), projectId), HttpStatus.OK);
	}
}
