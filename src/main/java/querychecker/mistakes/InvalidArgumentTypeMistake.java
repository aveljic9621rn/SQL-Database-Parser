package querychecker.mistakes;

import observer.notifications.MistakeNotification;

import java.util.ArrayList;
import java.util.List;

public class InvalidArgumentTypeMistake extends Mistake {
    @Override
    public List<MistakeNotification> check(String query) {
        return new ArrayList<>();
    }

    public InvalidArgumentTypeMistake(String name, String description, String recommendation) {
        super(name, description, recommendation);
    }
}
