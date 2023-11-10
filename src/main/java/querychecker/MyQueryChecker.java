package querychecker;

import observer.notifications.MistakeNotification;
import org.json.JSONObject;
import querychecker.mistakes.Mistake;
import querychecker.mistakes.MistakeFactory;
import querychecker.mistakes.Mistakes;

import java.util.*;

public class MyQueryChecker implements QueryChecker {
    private Map<Mistakes, Mistake> mistakes;
    MistakeFactory mistakeFactory;

    public MyQueryChecker() {
        mistakeFactory = new MistakeFactory();
        mistakes = new HashMap<>();
        loadMistakes();
    }

    private void loadMistakes() {
        String jsonString = new Scanner(getClass().getResourceAsStream("/config/querychecker.json"), "UTF-8").useDelimiter("\\A").next();
        JSONObject jsonObject = new JSONObject(jsonString);
        for (Mistakes m : Mistakes.values()) {
            JSONObject jsonMistake;
            try {
                jsonMistake = jsonObject.getJSONObject(m.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            String name = jsonMistake.getString("name");
            String description = jsonMistake.getString("description");
            String recommendation = jsonMistake.getString("recommendation");
            Mistake mistake = mistakeFactory.createMistake(m, name, description, recommendation);
            mistakes.put(m, mistake);
        }
    }

    private Mistake getMistake(Mistakes mistake) {
        return mistakes.get(mistake);
    }

    @Override
    public List<MistakeNotification> checkQuery(String query) {
        List<MistakeNotification> stack = new ArrayList<>();
        for (Mistakes m : Mistakes.values()) {
            Mistake mistake = getMistake(m);
            List<MistakeNotification> notifications = mistake.check(query);
            if (notifications != null)
                stack.addAll(notifications);
        }
        return stack;
    }
}
