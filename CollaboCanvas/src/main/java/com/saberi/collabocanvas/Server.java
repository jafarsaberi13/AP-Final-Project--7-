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
    /**
     * Validates a JSON object based on its type (REGISTER or LOGIN).
     *
     * @param jsonObject The JSON object to validate.
     * @return {@code 0} if validation is successful, or an error code otherwise.
     */
    public static int check(JSONObject jsonObject) {
        if (((String) jsonObject.get("Type")).equals("REGISTER")) {
            int tmp = checkUserName((String) jsonObject.get("Username"));
            if (tmp == 0) {
                int tmp1 = checkEmail((String) jsonObject.get("Email"));
                if (tmp1 == 0) {
                    int tmp2 = checkPassword((String) jsonObject.get("Password"));
                    if (tmp2 == 0) {
                        return 0;
                    } else {
                        return tmp2;
                    }
                } else {
                    return tmp1;
                }
            } else {
                return tmp;
            }
        } else { // if register be Log in
            return readData(jsonObject);
        }
    }
    /**
     * Reads data from the JSON file and validates the login credentials.
     *
     * @param jsonObject The JSON object containing the login credentials.
     * @return {@code 0} if login is successful, or {@code 1} otherwise.
     */
    public static int readData(JSONObject jsonObject) {
        JSONParser parser = new JSONParser();
        String username = jsonObject.get("Username").toString();
        String password = jsonObject.get("Password").toString();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("DataFile.json"));
            String jsonString = "";
            while ((jsonString = reader.readLine()) != null) {
                JSONObject jsonObject1 = (JSONObject) parser.parse(jsonString);
                if (jsonObject1.get("Username").equals(username)) {
                    if (jsonObject1.get("Password").equals(password)) {
                        return 0;
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return 1;
    }
    public static int checkUserName(String str) {
        String usernameRegex = "^[a-zA-Z][a-zA-Z0-9._]{2,14}$";
        return str.matches(usernameRegex) ? 0 : 1;
    }
    /**
     * Validates the format of an email address.
     *
     * @param email The email to validate.
     * @return {@code 0} if the email is valid, or {@code 2} otherwise.
     */
    public static int checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex) ? 0 : 2;
    }
    /**
     * Validates the format of a password.
     *
     * @param password The password to validate.
     * @return {@code 0} if the password is valid, or {@code 3} otherwise.
     */
    public static int checkPassword(String password) {
        if (password == null) {
            return 3;
        }
        return 0;
    }
}