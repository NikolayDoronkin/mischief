package inc.mischief.mischief.endpoint;

import inc.mischief.mischief.configuration.jwt.JwtUser;
import inc.mischief.mischief.mapper.NotificationMapper;
import inc.mischief.mischief.model.response.notification.NotificationResponse;
import inc.mischief.mischief.service.NotificationService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Notification", description = "Эндпоинты для работы с нотификациями")
public class NotificationEndpoint {

	private final NotificationMapper notificationMapper;
	private final NotificationService notificationService;

	@Operation(summary = "Получить все уведомления для текущего пользователя")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = NotificationResponse.class))})})
	@GetMapping("/forUser")
	public ResponseEntity<List<NotificationResponse>> getNotificationsForUser(@AuthenticationPrincipal JwtUser user) {
		return new ResponseEntity<>(
				notificationMapper.convert(notificationService.getAllNotificationsForUser(user.getUser())),
				HttpStatus.OK);
	}

	@Operation(summary = "Отметить уведомление как просмотренное")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = NotificationResponse.class))})})
	@PostMapping("/setViewed")
	public void setViewedNotifications(@RequestBody List<UUID> notificationIds) {
		notificationService.setViewed(notificationIds);
	}
}
