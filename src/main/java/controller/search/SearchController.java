package controller.search;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.hc.client5.http.fluent.Request;
import utility.JsonResponseHelper;
import utility.SendResponse;
import utility.UserLogManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static utility.ParseQuery.parseQuery;

public class SearchController implements HttpHandler {
    private static final String GOOGLE_API_KEY = "AIzaSyDf7W-R5Ojo_QdeqcTR2KOKhbBkce40YwE";
    private static final String SEARCH_ENGINE_ID = "a56b0f37834304686";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
            String userId = params.get("user");


            String query = exchange.getRequestURI().getQuery();
            if (query != null) {
                String[] queryParams = query.split("&");
                String encodedQuery = "";

                for (String param : queryParams) {
                    if (!param.startsWith("user=")) {
                        encodedQuery += URLEncoder.encode(param, StandardCharsets.UTF_8) + "&";
                    }
                }

                // Remove the trailing '&' character
                if (encodedQuery.endsWith("&")) {
                    encodedQuery = encodedQuery.substring(0, encodedQuery.length() - 1);
                }

                // Build the Google CSE API URL
                String apiUrl = String.format(
                        "https://www.googleapis.com/customsearch/v1?key=%s&cx=%s&q=%s",
                        GOOGLE_API_KEY,
                        SEARCH_ENGINE_ID,
                        encodedQuery
                );

                try {
                    // Make a GET request to the Google CSE API
                    String jsonResponse = Request.get(apiUrl).execute().returnContent().asString();

                    UserLogManager.write(userId, exchange);

                    // Send the API response as is to the UI
                    SendResponse.sendResponse(exchange, 200, jsonResponse);
                } catch (Exception e) {
                    // Handle errors, e.g., connection problems, API rate limiting, etc.
                    SendResponse.sendResponse(exchange, 500, JsonResponseHelper.createResponse("Internal Server Error", userId));
                }
            } else {
                SendResponse.sendResponse(exchange, 400, JsonResponseHelper.createResponse("Bad Request", userId));
            }
        }
    }
}

