package queryparser;

import java.util.Arrays;
import java.util.Locale;

public class Util {
    public static Pair<String, String> readWordOrQuotes(String query, Character[] terminators, Character[] quotes) {
        String tableName;
        query = query.trim();
        String finalQuery1 = query;
        boolean quoted = Arrays.stream(quotes).map(e -> finalQuery1.charAt(0) == e).reduce(false, (p, n) -> p || n);

        if (quoted) {
            char quote = query.charAt(0);
            query = query.substring(1);
            int other = query.indexOf(quote);
            if (other == -1) return null;
            tableName = query.substring(0, other);
            query = query.substring(tableName.length() + 1).trim();
        } else {
            String finalQuery = query;
            int firstTerminatorIndex = Arrays.stream(terminators)
                    .map(e -> !finalQuery.contains(String.valueOf(e)) ? finalQuery.length() : finalQuery.indexOf(String.valueOf(e)))
                    .reduce(finalQuery.length(), (t, n) -> Math.min(n, t));
            tableName = query.substring(0, firstTerminatorIndex).toLowerCase(Locale.ROOT);
            query = query.substring(tableName.length()).trim();
        }
        return new Pair<>(tableName, query);
    }
}
