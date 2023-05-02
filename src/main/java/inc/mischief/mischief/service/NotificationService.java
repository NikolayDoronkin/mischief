package inc.mischief.mischief.service;

import inc.mischief.mischief.domain.Notification;
import inc.mischief.mischief.domain.User;
import inc.mischief.mischief.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public List<Notification> getAllNotificationsForUser(User user) {
		var notificationsByReceiver = notificationRepository.findNotificationsByReceiver(user.getId());

		return notificationRepository.findAllByIdIn(notificationsByReceiver);
	}

	@Transactional
	public void setViewed(List<UUID> notificationIds) {
		notificationRepository.setViewed(notificationIds, LocalDate.now());
	}

	@Transactional
	public Notification create(Notification notification) {
		return notificationRepository.save(notification);
	}
}
