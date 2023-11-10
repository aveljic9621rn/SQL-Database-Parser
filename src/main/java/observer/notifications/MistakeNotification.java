package observer.notifications;

public class MistakeNotification {
    private final String name;
    private final String description;
    private final String recommendation;

    public MistakeNotification(String name, String description, String recommendation) {
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
