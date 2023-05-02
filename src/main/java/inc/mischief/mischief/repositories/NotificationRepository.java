package inc.mischief.mischief.repositories;

import inc.mischief.mischief.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

	@Query(value = """
			select n.id from notification n
				join user_m2m_notification um2mn on n.id = um2mn.fk_notification
			where um2mn.fk_user = ?1 and n.viewed is null
				""", nativeQuery = true)
	List<UUID> findNotificationsByReceiver(UUID userId);

	List<Notification> findAllByIdIn(List<UUID> ids);

	@Modifying
	@Query("update Notification notification set notification.viewed = :currentDate where notification.id in :notificationIds")
	void setViewed(@Param("notificationIds") List<UUID> notificationIds, @Param("currentDate") LocalDate currentDate);
}
