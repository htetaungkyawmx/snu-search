package utility;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.time.LocalDateTime;

public class UserLogManager {
    private static final String LOG_FILE_PATH = "src/main/resources/static/users_log.json";

    public static void write(String userId, HttpExchange exchange) throws IOException {
        String timestamp = String.valueOf(LocalDateTime.now());
        String request = "[req] [ " + timestamp + " ] http://localhost:" + exchange.getLocalAddress().getPort() + exchange.getRequestURI().toString();
        // Load existing JSON data from the file
        JsonObject jsonData = loadJsonData();

        // Find or create the user object
        JsonArray usersArray = jsonData.getAsJsonArray("users");
        JsonObject userObject = findOrCreateUser(usersArray, userId);

        // Add the search term to the user's searchTerms
        JsonArray searchTermsArray = userObject.getAsJsonArray("logs");
        searchTermsArray.add(request);

        // Write the updated JSON data back to the file
        writeJsonData(jsonData);
    }

    private static JsonObject loadJsonData() throws IOException {
        Gson gson = new Gson();
        File dataFile = new File(LOG_FILE_PATH);

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


    private static JsonObject findOrCreateUser(JsonArray usersArray, String userId) {
        for (int i = 0; i < usersArray.size(); i++) {
            JsonObject userObject = usersArray.get(i).getAsJsonObject();
            String id = userObject.get("userId").getAsString();
            if (id.equals(userId)) {
                return userObject;
            }
        }

        // If the user doesn't exist, create a new user object
        JsonObject newUser = new JsonObject();
        newUser.addProperty("userId", userId);
        newUser.add("logs", new JsonArray());
        usersArray.add(newUser);
        return newUser;
    }

    private static void writeJsonData(JsonObject jsonData) throws IOException {
        Gson gson = new Gson();
        try (OutputStream outputStream = new FileOutputStream(LOG_FILE_PATH);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            gson.toJson(jsonData, writer);
        }
    }

    public static JsonArray fetchUserLogs() {
        File dataFile = new File(LOG_FILE_PATH);
        try (FileInputStream fileInputStream = new FileInputStream(dataFile);
             InputStreamReader reader = new InputStreamReader(fileInputStream)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonObject jsonData = jsonElement.getAsJsonObject();
            JsonArray usersArray = jsonData.getAsJsonArray("users");

            // Using Java Stream to find the user by ID
            return usersArray.asList().stream()
                    .map(userElement -> userElement.getAsJsonObject().getAsJsonArray("logs"))
                    .collect(JsonArray::new, JsonArray::addAll, JsonArray::addAll);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // User not found or no log
    }

    // A utility method to pretty-print JSON
    public static String prettyPrint(JsonObject jsonObject) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }
}

