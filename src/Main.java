import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final String WEB_ROOT = "web";

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
        System.out.println("Сервер запущен удачно!");
        return server;
    }



    private static void initRoutes(HttpServer server) {
        server.createContext("/apps/profile", Main::handleProfile);
        server.createContext("/apps/", Main::handleApps);

        server.createContext("/files/", Main::handleFile);

        server.createContext("/css/", Main::handleFile);
        server.createContext("/images/", Main::handleFile);
        server.createContext("/bg/", Main::handleFile);

        server.createContext("/", Main::handleRoot);
    }


    private static void handleRoot(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(200, 0);
        try (PrintWriter writer = getWriterFrom(exchange)) {
            writer.printf("Добро пожаловать на главную страницу!%n");
            writer.printf("Путь запроса: %s%n", exchange.getRequestURI());
            writer.printf("Метод: %s%n", exchange.getRequestMethod());
            writer.flush();
        }
    }

    private static void handleApps(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(200, 0);
        try (PrintWriter writer = getWriterFrom(exchange)) {
            writer.printf("Раздел приложений!%n");
            writer.printf("Путь запроса: %s%n", exchange.getRequestURI());
            writer.printf("Метод: %s%n", exchange.getRequestMethod());
            writer.flush();
        }
    }

    private static void handleProfile(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(200, 0);
        try (PrintWriter writer = getWriterFrom(exchange)) {
            writer.printf("Личный кабинет пользователя!%n");
            writer.printf("Путь запроса: %s%n", exchange.getRequestURI());
            writer.printf("Метод: %s%n", exchange.getRequestMethod());
            writer.flush();
        }
    }


    private static void handleFile(HttpExchange exchange) throws IOException {

        String requestPath = exchange.getRequestURI().getPath();

        String relativePath = requestPath
                .replaceFirst("^/files/", "")
                .replaceFirst("^/", "");

        Path filePath = Paths.get(WEB_ROOT, relativePath);

        System.out.printf("Запрос файла: %s -> %s%n", requestPath, filePath);

        if (Files.notExists(filePath)) {
            sendNotFound(exchange, relativePath);
            return;
        }

        String contentType = detectContentType(relativePath);

        byte[] fileBytes = Files.readAllBytes(filePath);

        exchange.getResponseHeaders().add("Content-Type", contentType);
        exchange.sendResponseHeaders(200, fileBytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(fileBytes);
        }
    }
    private static String detectContentType(String fileName) {
        if (fileName.endsWith(".html"))                          return "text/html; charset=utf-8";
        else if (fileName.endsWith(".css"))                      return "text/css; charset=utf-8";
        else if (fileName.endsWith(".js"))                       return "application/javascript";
        else if (fileName.endsWith(".png"))                      return "image/png";
        else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        else if (fileName.endsWith(".gif"))                      return "image/gif";
        else if (fileName.endsWith(".svg"))                      return "image/svg+xml";
        else if (fileName.endsWith(".ico"))                      return "image/x-icon";
        else                                                     return "text/plain; charset=utf-8";
    }
    private static void sendNotFound(HttpExchange exchange, String fileName) throws IOException {
        String message = "404: Файл не найден -> " + fileName;
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(404, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
        System.out.println("404: " + fileName);
    }



    private static PrintWriter getWriterFrom(HttpExchange exchange) {
        OutputStream output = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(output, false, charset);
    }
}