package pro.sky.telegrambot.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notification_tasks")
public class NotificationTask implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private Long chat_id;

    private String notification;

    private LocalDateTime alarmTime;

    public NotificationTask() {
    }

    public NotificationTask(Long chat_id, String notification, LocalDateTime alarmTime) {
        this.chat_id = chat_id;
        this.notification = notification;
        this.alarmTime = alarmTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChat_id() {
        return chat_id;
    }

    public void setChat_id(Long chat_id) {
        this.chat_id = chat_id;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public LocalDateTime getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(LocalDateTime time_to_send) {
        this.alarmTime = time_to_send;
    }
}
