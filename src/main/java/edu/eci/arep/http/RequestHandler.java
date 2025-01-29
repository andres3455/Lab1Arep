package edu.eci.arep.http;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
public class RequestHandler {

    private static final String BASE_DIRECTORY = "src/main/public";

    public static void handle(Socket clientSocket) {
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             PrintWriter writer = new PrintWriter(output)) {

            // Leer la primera línea de la solicitud HTTP
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            System.out.println("Request: " + requestLine);

            // Parsear la línea de solicitud
            String[] parts = requestLine.split(" ");
            if (parts.length < 3 || !parts[0].equals("GET")) {
                sendResponse(writer, 400, "Bad Request", "Unsupported HTTP method.");
                return;
            }

            String path = parts[1];
            if (path.equals("/")) {
                path = "/index.html";
            }

            // Manejo de rutas específicas (API REST)
            if (path.startsWith("/api/weather")) {
                Map<String, String> queryParams = parseQueryParams(path);
                String city = queryParams.getOrDefault("city,", "Bogota");
                
                Random random = new Random();
                int temperature = random.nextInt(15) + 10;
                int humidity = random.nextInt(50) + 50;

                String [] conditions = {"Soleado", "Nublado", "Lluvioso", "Tormenta"};
                String condition = conditions[random.nextInt(conditions.length)];

                String jsonResponse = String.format("{\"city\": \"%s\", \"temperature\": %d, \"humidity\": %d, \"condition\": \"%s\"}", city, temperature, humidity, condition);
                sendResponse(writer, 200, "OK", jsonResponse);

            } else {
                File file = new File(BASE_DIRECTORY + path);
                if (file.exists() && file.isFile()) {
                    sendFileResponse(writer, output, file);
                } else {
                    sendResponse(writer, 404, "Not Found", "Archivo no encontrado.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        }
    }

    private static void sendResponse(PrintWriter writer, int statusCode, String statusMessage, String body) {
        writer.printf("HTTP/1.1 %d %s\r\n", statusCode, statusMessage);
        writer.println("Content-Type: text/plain");
        writer.println("Content-Length: " + body.length());
        writer.println();
        writer.println(body);
        writer.flush();
    }

    private static void sendFileResponse(PrintWriter writer, OutputStream output, File file) throws IOException {
        String contentType = Files.probeContentType(file.toPath());
        long contentLength = file.length();

        writer.printf("HTTP/1.1 200 OK\r\n");
        writer.println("Content-Type: " + contentType);
        writer.println("Content-Length: " + contentLength);
        writer.println();
        writer.flush();

        // Enviar el contenido del archivo
        try (FileInputStream fileInput = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInput.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
        }
    }

    private static Map<String, String> parseQueryParams(String path) {
        Map<String, String> queryParams = new HashMap<>();
        if (path.contains("?")) {
            String[] parts = path.split("\\?");
            if (parts.length > 1) {
                String query = parts[1];
                for (String param : query.split("&")) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        queryParams.put(key, value);
                    }
                }
            }
        }
        return queryParams;
    }
    
}
