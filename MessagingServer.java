package com.saberi.collabocanvas;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.*;
/**
 * The MessagingServer class implements a multi-client chat server.
 * Clients can connect to the server, send messages, and receive messages broadcasted to all connected clients.
 */
public class MessagingServer {
    private static Set<ClientConnection> activeConnections = Collections.synchronizedSet(new HashSet<>());

    public static void startchatServer() {
        int serverPort = 1111;
        System.out.println("********** WELCOME TO THE CHAT SERVER! **********");

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Server is running on port " + serverPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected!");
                ClientConnection clientConnection = new ClientConnection(clientSocket);
                activeConnections.add(clientConnection);
                new Thread(clientConnection).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Sends a JSON message to all connected clients except the sender.
     *
     * @param jsonMessage The JSON-formatted message to broadcast.
     * @param sender      The client connection that sent the message (excluded from receiving it).
     */
    static void sendToAllClients(JSONObject jsonMessage, ClientConnection sender) {
        synchronized (activeConnections) {
            for (ClientConnection client : activeConnections) {
                if (client != sender) {
                    client.sendJsonMessage(jsonMessage);
                }
            }
        }
    }
    /**
     * Removes a client connection from the set of active connections.
     *
     * @param clientConnection The client connection to remove.
     */
    static void removeClientConnection(ClientConnection clientConnection) {
        synchronized (activeConnections) {
            activeConnections.remove(clientConnection);
        }
    }
}
/**
 * The ClientConnection class handles the interaction between the server and a connected client.
 * It processes incoming messages from the client and broadcasts them to other clients.
 */

}
