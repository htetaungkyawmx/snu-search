package utility;

import com.google.gson.Gson;
import model.Response;

public class JsonResponseHelper {
    public static String createResponse(String message, String userId) {
        Response response = new Response(message, userId);
        Gson gson = new Gson();
        return gson.toJson(response);
    }
}
