package utility;

import java.util.HashMap;
import java.util.Map;

public class ParseQuery {
     public static Map<String, String> parseQuery(String query) {
        Map<String, String> parameters = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return parameters;
    }
}
