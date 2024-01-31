import com.sun.net.httpserver.HttpServer;
import controller.search.*;
import controller.user.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleHttpServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticHandler());
        server.createContext("/user/join", new UserRegisterController());
        server.createContext("/user/login", new UserLoginController());
        server.createContext("/user/logout", new UserLogoutController());
        server.createContext("/user/leave", new UserLeaveController());
        server.createContext("/user/recover", new UserRecoverController());

        server.createContext("/data/search", new SearchController());
        server.createContext("/data/save_data", new SaveDataController());
        server.createContext("/data/load_data", new LoadDataController());
        server.createContext("/data/load_fri", new LoadFriDataController());
        server.createContext("/data/load_hot", new LoadHotController());
        server.createContext("/data/load_acc", new LoadAccController());
        server.createContext("/data/load_log", new LoadLogController());


        server.start();
        System.out.println("Server listening on port " + port);
    }
}

