package inc.mischief.mischief.endpoint;

import inc.mischief.mischief.configuration.jwt.JwtUser;
import inc.mischief.mischief.mapper.TicketMapper;
import inc.mischief.mischief.model.request.ticket.CreateTicketRequest;
import inc.mischief.mischief.model.request.ticket.UpdateTicketRequest;
import inc.mischief.mischief.model.response.ticket.TicketResponse;
import inc.mischief.mischief.service.TicketService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Ticket", description = "Эндпоинты для работы с задачами")
public class TicketEndpoint {

	private final TicketMapper ticketMapper;

	private final UserService userService;
	private final TicketService ticketService;

	@Operation(summary = "Получение всех заданий из проекта")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = TicketResponse.class))})})
	@GetMapping("/project/{projectId}/ticket")
	public ResponseEntity<List<TicketResponse>> getTicketsFromProject(@AuthenticationPrincipal JwtUser currentUser,
																	  @PathVariable UUID projectId) {
		return new ResponseEntity<>(
				ticketMapper.convert(ticketService.findTicketsFromProject(projectId)),
				HttpStatus.CREATED);
	}

	@Operation(summary = "Получение всех заданий из проекта")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = TicketResponse.class))})})
	@GetMapping("/project/{projectId}/ticket/{ticketId}")
	public ResponseEntity<TicketResponse> getTicketFromProjectById(@AuthenticationPrincipal JwtUser currentUser,
																		 @PathVariable UUID projectId,
																		 @PathVariable UUID ticketId
	) {
		return new ResponseEntity<>(
				ticketMapper.convert(ticketService.findTicketFromProjectById(projectId, ticketId)),
				HttpStatus.CREATED);
	}

	@Operation(summary = "Создать задачу")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User created",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = TicketResponse.class))})})
	@PostMapping("/project/{projectId}/ticket/create")
	public ResponseEntity<TicketResponse> create(@AuthenticationPrincipal JwtUser currentUser,
												 @PathVariable UUID projectId,
												 @Valid @RequestBody CreateTicketRequest request) {

		var accessedUsers = userService.findByIds(request.getAccessedUserIds());
		var listeners = userService.findByIds(request.getListenerIds());

		var ticket = ticketMapper.convert(request);
		ticket.setAccessableUsers(accessedUsers);
		ticket.setListeners(listeners);

		return new ResponseEntity<>(
				ticketMapper.convert(ticketService.create(ticket, currentUser.getUser())),
				HttpStatus.CREATED);
	}

	@Operation(summary = "Обновить задачу")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User created",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = TicketResponse.class))})})
	@PostMapping("/ticket/update")
	public ResponseEntity<TicketResponse> update(@Valid @RequestBody UpdateTicketRequest request) {
		return new ResponseEntity<>(
				ticketMapper.convert(ticketService.update(ticketMapper.convert(request))),
				HttpStatus.ACCEPTED);
	}

	@Operation(summary = "Удалить задачу")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ResponseEntity.BodyBuilder.class))})})
	@DeleteMapping("/ticket/delete/{id}")
	public void delete(@PathVariable UUID id) {
		ticketService.delete(id);
	}
}
