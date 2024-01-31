package utility;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class SendResponse {
    public static void sendResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes().length);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        OutputStream os = exchange.getResponseBody();
        os.write(jsonResponse.getBytes());
        os.close();
    }
}
