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
class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private String userName;
    /**
     * Constructs a new {@code ClientHandler} for the specified socket.
     *
     * @param socket the socket for client communication.
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    /**
     * The main execution method for the client handler.
     * Manages communication with the client and broadcasts messages.
     */
    @Override
    public void run() {
        try (
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true)
        ) {
            this.out = writer;
            JSONParser parser = new JSONParser();
            String jsonString;

            // Ask the client for their username
            out.println("Enter your username:");
            this.userName = reader.readLine();

            // Add the user to the list of online users and send the update to all clients
            synchronized (drawingServer.onlineUsers) {
                drawingServer.onlineUsers.add(userName);
            }

            while ((jsonString = reader.readLine()) != null) {
                try {
                    JSONObject obj = (JSONObject) parser.parse(jsonString);
                    System.out.println("Received: " + obj.toJSONString());

                    // Check if the request is a save action
                    if ("save".equals(obj.get("action"))) {
                        saveCanvasData(obj);
                    } else {
                        // Broadcast the drawing action to other clients
                        drawingServer.broadcast(obj, this);
                    }
                } catch (ParseException e) {
                    System.out.println("Invalid JSON received:");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Connection error:");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    /**
     * Closes the connection and cleans up resources.
     */
    private void closeConnection() {
        try {
            // Remove the client from the online users list when they disconnect
            if (userName != null) {
                synchronized (drawingServer.onlineUsers) {
                    drawingServer.onlineUsers.remove(userName);
                }
            }
            //drawingServer.sendOnlineUsers();  // Send the updated list of online users

            drawingServer.removeClient(this);
            socket.close();
            System.out.println("Client disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Sends a JSON message to the connected client.
     *
     * @param message the JSON message to send.
     */
    public void sendMessage(JSONObject message) {
        out.println(message.toJSONString());
    }
    /**
     * Saves the canvas data to a JSON file.
     *
     * @param obj the JSON object containing canvas data.
     */
    private void saveCanvasData(JSONObject obj) {
        try {
            // Extract file name and shapes data from the JSON object
            String fileName = (String) obj.get("fileName");
            JSONArray shapesArray = (JSONArray) obj.get("shapes");
            String filePath = "E:\\Canva\\savedFiles\\" + fileName + ".json"; // Full path

            // Create or overwrite the file
            File file = null;
            try {
                file = new File(filePath);
                if (file.createNewFile()) {
                    System.out.println("file created");
                } else {
                    System.out.println("existed");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try (FileWriter fileWriter = new FileWriter(file)) {
                JSONObject canvasData = new JSONObject();
                canvasData.put("shapes", shapesArray);

                // Write the JSON data to the file
                fileWriter.write(canvasData.toJSONString());
                fileWriter.flush();
            }

            System.out.println("Canvas data saved to " + file.getAbsolutePath());
            // Optionally send a confirmation message back to the client
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("message", "Canvas saved as " + fileName + ".json");
            sendMessage(response);
        } catch (IOException e) {
            System.out.println("Error saving canvas data:");
            e.printStackTrace();

            // Send an error message back to the client
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to save canvas data.");
            sendMessage(errorResponse);
        }
    }
}
