package inc.mischief.mischief.endpoint;

import inc.mischief.mischief.mapper.CommentMapper;
import inc.mischief.mischief.model.request.comment.CreateCommentRequest;
import inc.mischief.mischief.model.request.comment.UpdateCommentRequest;
import inc.mischief.mischief.model.response.comment.CommentResponse;
import inc.mischief.mischief.model.response.project.ProjectResponse;
import inc.mischief.mischief.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Comment", description = "Эндпоинты для работы с комментариями")
public class CommentEndpoint {

	private final CommentMapper commentMapper;
	private final CommentService commentService;

	@Operation(summary = "Получить все комментарии по заданию")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ProjectResponse.class))})})
	@GetMapping("/fromTicket/{ticketId}")
	public ResponseEntity<List<CommentResponse>> getCommentsFromProject(@PathVariable UUID ticketId) {
		return new ResponseEntity<>(commentMapper.convert(commentService.getCommentsFromTicket(ticketId)), HttpStatus.OK);
	}

	@Operation(summary = "Создать комментарий к задаче")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ProjectResponse.class))})})
	@PostMapping("/create")
	public ResponseEntity<CommentResponse> create(@Validated @RequestBody CreateCommentRequest request) {
		return new ResponseEntity<>(
				commentMapper.convert(commentService.create(commentMapper.convert(request))), HttpStatus.CREATED);
	}

	@Operation(summary = "Обновить комментарий к задаче")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ProjectResponse.class))})})
	@PostMapping("/update")
	public ResponseEntity<CommentResponse> update(@Validated @RequestBody UpdateCommentRequest request) {
		return new ResponseEntity<>(
				commentMapper.convert(commentService.update(commentMapper.convert(request))), HttpStatus.CREATED);
	}

	@Operation(summary = "Удалить комментарий")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = ProjectResponse.class))})})
	@PostMapping("/{commentId}/delete")
	public void delete(@PathVariable UUID commentId) {
		commentService.delete(commentId);
	}
}
