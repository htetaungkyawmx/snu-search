package controller.search;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utility.JsonResponseHelper;
import utility.SendResponse;
import utility.UserLogManager;

import java.io.*;
import java.util.Map;

import static utility.ParseQuery.parseQuery;

public class SaveDataController implements HttpHandler {
    private static final String DATA_FILE_PATH = "src/main/resources/static/search_data.json";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> queryParams = parseQuery(exchange.getRequestURI().getQuery());

            String searchTerm = queryParams.get("q");
            String userId = queryParams.get("user");

            if (searchTerm != null && userId != null) {
                // Load existing JSON data from the file
                JsonObject jsonData = loadJsonData();

                // Find or create the user object
                JsonArray usersArray = jsonData.getAsJsonArray("users");
                JsonObject userObject = findOrCreateUser(usersArray, userId);

                // Add the search term to the user's searchTerms
                JsonArray searchTermsArray = userObject.getAsJsonArray("searchTerms");
                searchTermsArray.add(searchTerm);

                // Write the updated JSON data back to the file
                writeJsonData(jsonData);

                UserLogManager.write(userId, exchange);

                System.out.println("Saved search term for user " + userId + ": " + searchTerm);

                SendResponse.sendResponse(exchange, 200, JsonResponseHelper.createResponse("Search term saved successfully.", userId));
            } else {
                SendResponse.sendResponse(exchange, 400, JsonResponseHelper.createResponse("Bad Request", userId));
            }
        }
    }


    private JsonObject loadJsonData() throws IOException {
        Gson gson = new Gson();
        File dataFile = new File(DATA_FILE_PATH);

        if (dataFile.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(dataFile);
                 InputStreamReader reader = new InputStreamReader(fileInputStream)) {
                return gson.fromJson(reader, JsonObject.class);
            }
        } else {
            // If the file doesn't exist, create a new JSON object
            JsonObject jsonData = new JsonObject();
            jsonData.add("users", new JsonArray());
            return jsonData;
        }
    }


    private JsonObject findOrCreateUser(JsonArray usersArray, String userId) {
        for (int i = 0; i < usersArray.size(); i++) {
            JsonObject userObject = usersArray.get(i).getAsJsonObject();
            String id = userObject.get("id").getAsString();
            if (id.equals(userId)) {
                return userObject;
            }
        }

        // If the user doesn't exist, create a new user object
        JsonObject newUser = new JsonObject();
        newUser.addProperty("id", userId);
        newUser.add("searchTerms", new JsonArray());
        usersArray.add(newUser);
        return newUser;
    }

    private void writeJsonData(JsonObject jsonData) throws IOException {
        Gson gson = new Gson();
        try (OutputStream outputStream = new FileOutputStream(DATA_FILE_PATH);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            gson.toJson(jsonData, writer);
        }
    }
}
