package com.saberi.collabocanvas;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
/**
 * The {@code drawingServer} class is responsible for managing a collaborative drawing server.
 * It handles client connections, broadcasting messages, and managing online users.
 */
public class drawingServer {
    private static Set<ClientHandler> clientHandlers = new HashSet<>();
    static Set<String> onlineUsers = new HashSet<>();  // Track the online users by their names

    /**
     * Starts the drawing server and listens for incoming client connections on port 7777.
     */
    public static void startDrawingServer() {
        try (ServerSocket serverSocket = new ServerSocket(7777)) {
            System.out.println("Drawing Server started on port 7777");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected!");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Error in server:");
            e.printStackTrace();
        }
    }
    /**
     * Broadcasts a JSON message to all connected clients except the sender.
     *
     * @param message the JSON message to broadcast.
     * @param sender  the client sending the message.
     */
    public static void broadcast(JSONObject message, ClientHandler sender) {
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }
    /**
     * Removes a client handler from the set of connected clients.
     *
     * @param clientHandler the client handler to remove.
     */
    public static void removeClient(ClientHandler clientHandler) {
        synchronized (clientHandlers) {
            clientHandlers.remove(clientHandler);
        }
    }
}
/**
 * Handles client connections and communication.
 */

}
