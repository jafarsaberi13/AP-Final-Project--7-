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
class ClientConnection implements Runnable {
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;
    /**
     * Constructs a new ClientConnection instance.
     *
     * @param clientSocket The socket connection to the client.
     */
    public ClientConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    /**
     * The main method for processing client messages and handling client interaction.
     */
    @Override
    public void run() {
        try (
                InputStream inputStream = clientSocket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                OutputStream outputStream = clientSocket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream, true)
        ) {
            this.writer = printWriter;
            this.reader = bufferedReader;

            // Read and broadcast messages from the client
            String clientMessage;
            while ((clientMessage = bufferedReader.readLine()) != null) {
                try {
                    // Parse the incoming message as JSON directly
                    JSONObject jsonMessage = (JSONObject) new JSONParser().parse(clientMessage);

                    // Broadcast the parsed message to all other clients
                    MessagingServer.sendToAllClients(jsonMessage, this);
                } catch (org.json.simple.parser.ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Connection error with client: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
    /**
     * Closes the client connection and broadcasts a disconnect message to other clients.
     */
    private void closeConnection() {
        try {
            System.out.println(username + " has left the chat!");

            // Broadcast the user's exit message
            JSONObject exitMessage = new JSONObject();
            exitMessage.put("action", "user_left");
            exitMessage.put("username", username);
            MessagingServer.sendToAllClients(exitMessage, this);

            MessagingServer.removeClientConnection(this);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Sends a JSON message to the connected client.
     *
     * @param jsonMessage The JSON-formatted message to send.
     */
    void sendJsonMessage(JSONObject jsonMessage) {
        writer.println(jsonMessage.toJSONString());
    }
}
