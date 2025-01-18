package com.saberi.collabocanvas;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
/**
 * Represents a server that handles client connections for user registration and login.
 * The server validates client requests, stores data in a JSON file, and ensures proper format
 * for usernames, emails, and passwords.
 */
public class Server {
    private static File jsonDataFile;
    /**
     * Initializes the server, creates a data file if it doesn't exist, and listens for client connections.
     */
    public static void serverConnect() {
        try {
            jsonDataFile =  new File("DataFile.json");

            if (jsonDataFile.createNewFile()) {
                System.out.println("file created");
            } else {
                System.out.println("file exist");
            }
        } catch (IOException ioException) {
            System.out.println("Error");
            ioException.printStackTrace();
        }
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("Server started, waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                // Handle client in a separate thread
                new Thread(() -> handleClient(clientSocket, clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Handles client requests for registration and login.
     *
     * @param clientSocket The socket connected to the client.
     * @param socket       The socket used for communication.
     */
    
}