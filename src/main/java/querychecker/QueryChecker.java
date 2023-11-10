package querychecker;

import observer.notifications.MistakeNotification;

import java.util.List;

public interface QueryChecker {
    List<MistakeNotification> checkQuery(String query);
}
