package ajungstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class User extends Application {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    private String md5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/login.css").toExternalForm();
        borderPane.getStylesheets().add(css);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT);
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
            String username = usernameField.getText();
            String password = md5(passwordField.getText()); // MD5 hash input password

            try (Connection connection = Dbconnect.getConnect();
                    PreparedStatement statement = connection
                            .prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
                statement.setString(1, username);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Login berhasil
                    System.out.println("Login berhasil!");
                    // Pindah ke halaman penjualan
                    Penjualan penjualan = new Penjualan();
                    try {
                        penjualan.index(primaryStage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // Login gagal
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Login Gagal");
                    alert.setHeaderText(null);
                    alert.setContentText("Username atau password salah!");
                    alert.showAndWait();
                    System.out.println("Login gagal! Username atau password salah.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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
