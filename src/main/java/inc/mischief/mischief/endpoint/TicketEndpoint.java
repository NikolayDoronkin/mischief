package inc.mischief.mischief.endpoint;

import inc.mischief.mischief.configuration.jwt.JwtUser;
import inc.mischief.mischief.domain.enumeration.ticket.TicketPriority;
import inc.mischief.mischief.domain.enumeration.ticket.TicketStatus;
import inc.mischief.mischief.domain.enumeration.ticket.TicketType;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
	public ResponseEntity<PageImpl<TicketResponse>> getTicketsFromProject(@AuthenticationPrincipal JwtUser currentUser,
																		  @PathVariable UUID projectId,
																		  @RequestParam(required = false) String searchFilter,
																		  @ParameterObject Pageable pageable) {
		return new ResponseEntity<>(
				ticketMapper.convert(ticketService.findTicketsFromProject(projectId, pageable, searchFilter)),
				HttpStatus.CREATED);
	}

	@Operation(summary = "Получение дочерних задач задания")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = TicketResponse.class))})})
	@GetMapping("/ticket/{ticketId}/child")
	public ResponseEntity<List<TicketResponse>> getChildTickets(@AuthenticationPrincipal JwtUser currentUser,
																@PathVariable UUID ticketId
	) {
		return new ResponseEntity<>(
				ticketMapper.convert(ticketService.findChildTickets(ticketId)),
				HttpStatus.OK);
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

		var accessedUsers = userService.findByIds(request.getAccessedUserIds(), Pageable.unpaged());
		var listeners = userService.findByIds(request.getListenerIds(), Pageable.unpaged());

		var ticket = ticketMapper.convert(request);
		ticket.setAccessableUsers(accessedUsers.toSet());
		ticket.setListeners(listeners.toSet());

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

	@Operation(summary = "Получение всех типов задач")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = TicketResponse.class))})})
	@GetMapping("/ticket/getTicketTypes")
	public ResponseEntity<TicketType[]> getTicketTypes(@AuthenticationPrincipal JwtUser currentUser) {
		return new ResponseEntity<>(TicketType.values(), HttpStatus.OK);
	}

	@Operation(summary = "Получение всех статусов задач")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = TicketResponse.class))})})
	@GetMapping("/ticket/getTicketStatuses")
	public ResponseEntity<TicketStatus[]> getTicketStatuses(@AuthenticationPrincipal JwtUser currentUser) {
		return new ResponseEntity<>(TicketStatus.values(), HttpStatus.OK);
	}

	@Operation(summary = "Получение всех приоритетов задач")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = TicketResponse.class))})})
	@GetMapping("/ticket/getTicketPriorities")
	public ResponseEntity<TicketPriority[]> getTicketPriorities(@AuthenticationPrincipal JwtUser currentUser) {
		return new ResponseEntity<>(TicketPriority.values(), HttpStatus.OK);
	}
}
