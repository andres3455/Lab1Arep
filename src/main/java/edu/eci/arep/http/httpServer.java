package edu.eci.arep.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class httpServer {
    private static final int PORT = 35000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running on port " + PORT);

            while (true) { 
                Socket clientSocket = serverSocket.accept();
                RequestHandler.handle(clientSocket);
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
