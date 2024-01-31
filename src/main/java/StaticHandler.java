import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

class StaticHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Get the requested URI
        String uri = exchange.getRequestURI().toString();

        // If the request is for the root ("/"), serve index.html
        if (uri.equals("/")) {
            uri = "/static/index.html";
        }

        // Load the requested file
        File file = new File("src/main/resources" + uri);

        if (file.exists() && !file.isDirectory()) {
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            // Set the response content type based on the file extension
            String contentType = "text/plain";
            if (uri.endsWith(".html")) {
                contentType = "text/html";
            } else if (uri.endsWith(".css")) {
                contentType = "text/css";
            } else if (uri.endsWith(".js")) {
                contentType = "application/javascript";
            }

            // Set response headers
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);

            // Write the file content to the response
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        } else {
            // File not found, return a 404 error
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}