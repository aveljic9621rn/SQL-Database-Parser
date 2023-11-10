package querychecker.mistakes;

import observer.notifications.MistakeNotification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StatementOrderWrongMistake extends Mistake {
    @Override
    public List<MistakeNotification> check(String query) {
        query = query.trim();
        List<MistakeNotification> list = new ArrayList<>();
        if (!query.toUpperCase().contains("SELECT")) {
            return list;
        }

        ArrayList<Integer> positions = new ArrayList<>();
        int count = 0;
        int selectPos = query.toUpperCase().indexOf("SELECT");
        if (selectPos != -1) {
            positions.add(selectPos);
        }
        if (selectPos > 0) {
            list.add(new MistakeNotification(getName(),
                    String.format(getDescription(), "Select upit mora da pocinje sa select"),
                    String.format(getRecommendation(), "Proverite redosled kljucnih reci")));
            return list;
        }
        int fromPos = query.toUpperCase().indexOf(" FROM");
        if (fromPos != -1) {
            positions.add(fromPos);
        }
        int joinPos = query.toUpperCase().indexOf(" JOIN");
        if (joinPos != -1) {
            positions.add(joinPos);
        }
        int usingPos = query.toUpperCase().indexOf(" USING");
        if (usingPos != -1) {
            positions.add(usingPos);
        }
        int onPos = query.toUpperCase().indexOf(" ON ");
        if (onPos != -1) {
            positions.add(onPos);
        }
        int wherePos = query.toUpperCase().indexOf(" WHERE");
        if (wherePos != -1) {
            positions.add(wherePos);
        }
        int groupPos = query.toUpperCase().indexOf(" GROUP BY");
        if (groupPos != -1) {
            positions.add(groupPos);
        }
        int havingPos = query.toUpperCase().indexOf(" HAVING");
        if (havingPos != -1) {
            positions.add(havingPos);
        }
        int orderPos = query.toUpperCase().indexOf(" ORDER BY");
        if (orderPos != -1) {
            positions.add(orderPos);
        }

        ArrayList<Integer> sortedPositions = new ArrayList<>(positions);
        sortedPositions.sort(Integer::compareTo);
        for (int i = 0; i < positions.size(); i++) {
            if (!Objects.equals(sortedPositions.get(i), positions.get(i))) {
                list.add(new MistakeNotification(getName(),
                        String.format(getDescription(), "Nije dobar redosled argumenata u select upitu"),
                        String.format(getRecommendation(), "Proverite redosled kljucnih reci")));
                break;
            }
        }

        return list;
    }

    public StatementOrderWrongMistake(String name, String description, String recommendation) {
        super(name, description, recommendation);
    }
}
