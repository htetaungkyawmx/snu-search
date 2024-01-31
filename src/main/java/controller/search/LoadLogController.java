package controller.search;

import com.google.gson.*;
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

public class LoadLogController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> queryParams = parseQuery(exchange.getRequestURI().getQuery());

            String userId = queryParams.get("user");

            if (userId != null) {
                // Fetch all user logs
                JsonArray searchTermsArray = UserLogManager.fetchUserLogs();

                // Create a JSON response containing the logs
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.add("users", searchTermsArray);

                // Send the JSON response to the client
                SendResponse.sendResponse(exchange, 200, prettyPrint(jsonResponse));

            } else {
                SendResponse.sendResponse(exchange, 400, JsonResponseHelper.createResponse("Bad Request", userId));
            }
        }
    }


}
