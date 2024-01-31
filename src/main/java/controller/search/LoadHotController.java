package controller.search;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utility.JsonResponseHelper;
import utility.SendResponse;
import utility.UserLogManager;

import static utility.ParseQuery.parseQuery;
import static utility.UserLogManager.prettyPrint;

public class LoadHotController implements HttpHandler {
    private static final String DATA_FILE_PATH = "src/main/resources/static/search_data.json";
    private static final int TOP_N = 10; // Number of top search terms to retrieve

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> queryParams = parseQuery(exchange.getRequestURI().getQuery());

            String userId = queryParams.get("user");

            // Load existing JSON data from the file
            JsonObject jsonData = loadJsonData();

            if (jsonData != null) {
                // Get all search terms from all users
                List<String> allSearchTerms = getAllSearchTerms(jsonData);

                if (!allSearchTerms.isEmpty()) {
                    // Count the frequency of each search term
                    Map<String, Long> termFrequencyMap = countSearchTermFrequency(allSearchTerms);

                    // Sort the search terms by frequency
                    List<Map.Entry<String, Long>> sortedTerms = sortSearchTermsByFrequency(termFrequencyMap);

                    // Get the top search terms (hot search terms)
                    List<String> hotSearchTerms = getTopSearchTerms(sortedTerms);

                    // Create a JSON response containing the hot search terms
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.add("hotSearchTerms", convertToJsonArray(hotSearchTerms));

                    UserLogManager.write(userId, exchange);

                    // Send the JSON response to the client
                    SendResponse.sendResponse(exchange, 200, prettyPrint(jsonResponse));
                } else {
                    SendResponse.sendResponse(exchange, 404, JsonResponseHelper.createResponse("No search terms found", userId));
                }
            }
        }
    }

    private JsonObject loadJsonData() {
        File dataFile = new File(DATA_FILE_PATH);
        if (dataFile.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(dataFile);
                 InputStreamReader reader = new InputStreamReader(fileInputStream)) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                if (jsonElement.isJsonObject()) {
                    return jsonElement.getAsJsonObject();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<String> getAllSearchTerms(JsonObject jsonData) {
        JsonArray usersArray = jsonData.getAsJsonArray("users");
        List<String> allSearchTerms = new ArrayList<>();

        for (JsonElement userElement : usersArray) {
            JsonObject userObject = userElement.getAsJsonObject();
            JsonArray searchTermsArray = userObject.getAsJsonArray("searchTerms");

            for (JsonElement termElement : searchTermsArray) {
                allSearchTerms.add(termElement.getAsString());
            }
        }

        return allSearchTerms;
    }

    private Map<String, Long> countSearchTermFrequency(List<String> searchTerms) {
        return searchTerms.stream()
                .collect(Collectors.groupingBy(term -> term, Collectors.counting()));
    }

    private List<Map.Entry<String, Long>> sortSearchTermsByFrequency(Map<String, Long> termFrequencyMap) {
        return termFrequencyMap.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(Collectors.toList());
    }

    private List<String> getTopSearchTerms(List<Map.Entry<String, Long>> sortedTerms) {
        return sortedTerms.stream()
                .map(Map.Entry::getKey)
                .limit(TOP_N) // Get the top N search terms
                .collect(Collectors.toList());
    }

    private JsonArray convertToJsonArray(List<String> strings) {
        JsonArray jsonArray = new JsonArray();
        strings.forEach(jsonArray::add);
        return jsonArray;
    }
}
