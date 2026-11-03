package com.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hotel.fxml"));
            stage.setTitle("HotelApp - Sistema de reservas");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Esto forzará que el error real se imprima en la terminal
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
