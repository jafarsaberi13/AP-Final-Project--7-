package com.saberi.collabocanvas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;

public class logiN extends Application {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8888;

