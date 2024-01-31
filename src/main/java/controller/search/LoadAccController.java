package controller.search;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.User;
import service.DataStore;
import utility.JsonResponseHelper;
import utility.SendResponse;
import utility.UserLogManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static utility.ParseQuery.parseQuery;
import static utility.UserLogManager.prettyPrint;

public class LoadAccController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> queryParams = parseQuery(exchange.getRequestURI().getQuery());

            String userId = queryParams.get("user");

            if (userId != null) {
                List<User> userList = DataStore.getAllUsers();

                JsonArray jsonArray = mapToJsonArray(userList);

                // Create a JSON response containing the search terms
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.add("Account List", jsonArray);

                UserLogManager.write(userId, exchange);

                // Send the JSON response to the client
                SendResponse.sendResponse(exchange, 200, prettyPrint(jsonResponse));
            } else {
                SendResponse.sendResponse(exchange, 400, JsonResponseHelper.createResponse("Bad Request", userId));
            }
        }
    }

    private JsonArray mapToJsonArray(List<User> userList) {
        JsonArray jsonArray = userList.stream()
                .map(user -> {
                    JsonObject userJson = new JsonObject();
                    userJson.addProperty("id", user.getId());
                    userJson.addProperty("password", user.getPassword());
                    userJson.addProperty("isActive", user.isActive());
                    return userJson;
                })
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
        return jsonArray;
    }
}
