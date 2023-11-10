package querychecker.mistakes;

import observer.notifications.MistakeNotification;

import java.util.List;

public abstract class Mistake {
    private final String name;
    private final String description;
    private final String recommendation;

    public abstract List<MistakeNotification> check(String query);
    //public abstract MistakeNotification createNotification(String query);

    public Mistake(String name, String description, String recommendation) {
        this.name = name;
        this.description = description;
        this.recommendation = recommendation;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
