package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final NotificationTaskRepository notificationTaskRepository;

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            SendMessage message;
            Long chatId = update.message().chat().id();
            logger.info("Processing update: {}", update);
            String receivedMessage = update.message().text();
            if (receivedMessage.equals("/start")) {
                message = new SendMessage(chatId, "Привет " + update.message().from().username() + ".");
                telegramBot.execute(message);
                return;
            }
            String regex = "([0-9.:\\s]{16})(\\s)([\\W+]+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(receivedMessage);
            if (matcher.matches()) {
                String date = matcher.group(1);
                String notification = matcher.group(3);
                NotificationTask notificationTask = new NotificationTask(chatId,notification
                        , LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                notificationTaskRepository.save(notificationTask);
            }
            // Process your updates here
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(fixedDelay = 60_000L)
    public void run() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Collection<NotificationTask> tasks = notificationTaskRepository.findNotificationTasksByAlarmTime(now);
        if (!tasks.isEmpty()) {
            tasks.forEach(task -> {
                SendMessage message = new SendMessage(task.getChat_id(), "Напоминание: " + task.getNotification());
                telegramBot.execute(message);
            });
        }
    }

}
