package controller.search;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utility.JsonResponseHelper;
import utility.SendResponse;
import utility.UserLogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import static utility.ParseQuery.parseQuery;
import static utility.UserLogManager.prettyPrint;

public class LoadFriDataController implements HttpHandler {
    private static final String DATA_FILE_PATH = "src/main/resources/static/search_data.json";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> queryParams = parseQuery(exchange.getRequestURI().getQuery());

            String friUserId = queryParams.get("q");
            String userId = queryParams.get("user");

            if (userId != null) {
                // Fetch the saved search terms based on the Fri's User ID
                JsonArray searchTermsArray = fetchUserSearchTerms(friUserId);

                if (searchTermsArray != null) {
                    // Create a JSON response containing the search terms
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.add("searchTerms", searchTermsArray);

                    UserLogManager.write(userId, exchange);

                    // Send the JSON response to the client
                    SendResponse.sendResponse(exchange, 200, prettyPrint(jsonResponse));
                } else {
                    SendResponse.sendResponse(exchange, 404, JsonResponseHelper.createResponse("User not found.", userId));
                }
            } else {
                SendResponse.sendResponse(exchange, 400, JsonResponseHelper.createResponse("Bad Request", userId));
            }
        }
    }

    private JsonArray fetchUserSearchTerms(String userId) {
        File dataFile = new File(DATA_FILE_PATH);
        try (FileInputStream fileInputStream = new FileInputStream(dataFile);
             InputStreamReader reader = new InputStreamReader(fileInputStream)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonObject jsonData = jsonElement.getAsJsonObject();
            JsonArray usersArray = jsonData.getAsJsonArray("users");

            // Using Java Stream to find the user by ID
            return usersArray
                    .asList()
                    .stream()
                    .filter(userElement -> userId.equals(userElement.getAsJsonObject().get("id").getAsString()))
                    .findFirst()
                    .map(userElement -> userElement.getAsJsonObject().getAsJsonArray("searchTerms"))
                    .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // User not found or no search terms
    }
}
