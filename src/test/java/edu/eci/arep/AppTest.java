package edu.eci.arep;

import java.net.Socket;

import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;


class AppTest {
    
    private static final String HOST = "localhost";
    private static final int PORT = 35000;

    @Test
    void testHandleValidGetRequest() {
        try (Socket socket = new Socket(HOST, PORT);
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true);
             InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            
            writer.println("GET /index.html HTTP/1.1");
            writer.println("Host: " + HOST);
            writer.println();
            
            String responseLine = reader.readLine();
            assertNotNull(responseLine, "Response should not be null");
            assertTrue(responseLine.contains("200 OK"), "Expected 200 OK but got: " + responseLine);
            
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testHandleInvalidMethod() {
        try (Socket socket = new Socket(HOST, PORT);
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true);
             InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            
            writer.println("POST /index.html HTTP/1.1");
            writer.println("Host: " + HOST);
            writer.println();
            
            String responseLine = reader.readLine();
            assertNotNull(responseLine, "Response should not be null");
            assertTrue(responseLine.contains("400 Bad Request"), "Expected 400 Bad Request but got: " + responseLine);
            
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testHandleMissingResource() {
        try (Socket socket = new Socket(HOST, PORT);
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true);
             InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            
            writer.println("GET /nonexistent.html HTTP/1.1");
            writer.println("Host: " + HOST);
            writer.println();
            
            String responseLine = reader.readLine();
            assertNotNull(responseLine, "Response should not be null");
            assertTrue(responseLine.contains("404 Not Found"), "Expected 404 Not Found but got: " + responseLine);
            
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}