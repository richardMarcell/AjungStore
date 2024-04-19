package ajungstore;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Login extends Application {

    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/login.css").toExternalForm();
        borderPane.getStylesheets().add(css);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT); // Mengatur alignment ke tengah
        vbox.setSpacing(10);
        vbox.setMaxWidth(400);

        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Selamat Datang Kembali");
        titleLabel.getStyleClass().add("title");
        titleLabel.setAlignment(Pos.CENTER);

        Label titleDescription = new Label("Silahkan Login Ke Akun Anda");
        titleDescription.getStyleClass().add("titleDescription");
        titleDescription.setAlignment(Pos.CENTER);

        header.getChildren().addAll(titleLabel, titleDescription);

        Label usernameLabel = new Label("Username:");
        usernameLabel.setAlignment(Pos.CENTER_LEFT);
        TextField usernameField = new TextField();
        usernameField.setPrefHeight(50);


        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefHeight(50);

        Button loginButton = new Button("Login");
        loginButton.setMinWidth(400);
        loginButton.prefHeight(40);
        loginButton.setOnAction(e -> {
            // Logic for login
        });

        vbox.getChildren().addAll(header, usernameLabel, usernameField, passwordLabel, passwordField, loginButton);

        borderPane.setCenter(vbox);

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("AjungStore - Login");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}