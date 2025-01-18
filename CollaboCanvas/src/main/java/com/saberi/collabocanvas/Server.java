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
    public static void handleClient(Socket clientSocket, Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            String jsonString = in.readLine();
            System.out.println("Recieved: " + jsonString);
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(jsonString);

            String type = (String) obj.get("Type");
            if (type.equals("REGISTER")) {
                String userName = (String) obj.get("Username");
                String email = (String) obj.get("Email");
                String password = (String) obj.get("Password");
                System.out.println(" type " + type);
                System.out.println(" user name" + userName);
                System.out.println("email  " + email);
                System.out.println("password " + password);
            }
            else {
                String userName = (String) obj.get("Username");
                String password = (String) obj.get("Password");
                System.out.println(" type " + type);
                System.out.println(" user name" + userName);
                System.out.println("password " + password);
            }


            int result = check(obj);
            if ((result == 0) && (obj.get("Type").equals("REGISTER"))) {
                saveDataToFile(obj); // save the data in json file
            }
            System.out.println("result = " + result);
            out.writeUTF("" + result);
            //saveToFile(jsonString); // save data to json file
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }
    /**
     * Saves a JSON object to the data file.
     *
     * @param jsonObject The JSON object to save.
     */
    public static void saveDataToFile(JSONObject jsonObject) {
        try {
            FileWriter fileWriter = new FileWriter("DataFile.json", true);
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.write(System.lineSeparator());
            fileWriter.close();
        } catch (IOException ioException)  {
            ioException.printStackTrace();
        }
    }
}