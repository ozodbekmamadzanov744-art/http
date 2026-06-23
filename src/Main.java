import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {
        try {
            HttpServer server = makeServer();
            initRoutes(server);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(host, 9889);
        System.out.printf("Запускаем сервер по адресу http://%s:%s/%n",
                address.getHostName(), address.getPort());
        HttpServer server = HttpServer.create(address, 50);
        System.out.println("Удачно!");
        return server;
    }
    private static void initRoutes(HttpServer server) {
        server.createContext("/", Main::handleRoot);
        server.createContext("/apps/", Main::handleApps);
        server.createContext("/apps/profile", Main::handleProfile);
    }

    private static void handleRoot(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, 0);

            try (PrintWriter writer = getWriterFrom(exchange)) {
                writer.printf("Добро пожаловать на главную страницу!%n");
                writer.printf("Путь запроса: %s%n", exchange.getRequestURI());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleApps(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, 0);

            try (PrintWriter writer = getWriterFrom(exchange)) {
                writer.printf("Раздел приложений!%n");
                writer.printf("Путь запроса: %s%n", exchange.getRequestURI());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleProfile(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, 0);

            try (PrintWriter writer = getWriterFrom(exchange)) {
                writer.printf("Личный кабинет пользователя!%n");
                writer.printf("Путь запроса: %s%n", exchange.getRequestURI());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange) {
        OutputStream output = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(output, false, charset);
    }
}