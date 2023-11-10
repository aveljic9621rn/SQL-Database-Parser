package utils.pretty;

import utils.Constants;

public class Prettifier implements IPrettifier {
    public String prettify(String query) {
        if (query.equals("")) {
            return "";
        }

        String[] split = query.trim().split("\\s+");
        StringBuilder stringBuilder = new StringBuilder();

        for (String word : split) {
            boolean foundKeyword = false;
            boolean foundNewRow = false;
            String keyword = "";
            for (String cur : Constants.KEYWORDS) {
                if (word.equalsIgnoreCase(cur)) {
                    keyword = cur;
                    foundKeyword = true;
                    break;
                }
            }

            for (String[] group : Constants.KEYWORD_GROUPS) {
                if (foundNewRow) {
                    break;
                }

                for (String item : group) {
                    if (word.equalsIgnoreCase(item)) {
                        foundNewRow = true;
                        break;
                    }
                }
            }

            if (foundKeyword) {
                word = "<span style = \"text-transform: uppercase; color: '#FF00FF';\">" + keyword + "</span>";
                if (foundNewRow) {
                    word = "<br>" + word;
                }
            } else if (!(word.startsWith("\"") && word.endsWith("\"") || (word.startsWith("'") && word.endsWith("'")))) {
                word = word.toLowerCase();
            }

            stringBuilder.append(word).append(" ");
        }
        if (stringBuilder.charAt(0) == '<') {
            stringBuilder.delete(0, 4);
        }

        return stringBuilder.toString().trim();
    }
}
