package ajungstore;

import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Penjualan {
    public void index(Stage indexState) throws Exception {
        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/indexPenjualan.css").toExternalForm();
        borderPane.getStylesheets().add(css);

        GridPane header = new GridPane();
        header.setMinHeight(80);
        header.getStyleClass().add("header");

        Label appName = new Label("AjungStore");
        appName.setTextFill(Color.RED);
        appName.getStyleClass().add("appName");

        Label welcome = new Label("Hai, Admin");
        welcome.getStyleClass().add("welcome");

        // Set the first column to expand to take the remaining space
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        header.getColumnConstraints().add(column1);

        header.setAlignment(Pos.CENTER);

        header.add(appName, 0, 0); // Add AjungStore to the first column, first row
        header.add(welcome, 1, 0); // Add Hai, Admin to the second column, first row

        borderPane.setTop(header);

        VBox sidebar = new VBox();
        sidebar.setMinWidth(200);
        sidebar.getStyleClass().add("sidebar");

        // Buat item navigasi
        Label navPenjualan = new Label("Penjualan");
        navPenjualan.getStyleClass().add("navPenjualan");
        Label navBarang = new Label("Barang");
        navBarang.getStyleClass().add("navBarang");
        Label navPelanggan = new Label("Pelanggan");
        navPelanggan.getStyleClass().add("navPelanggan");

        sidebar.getChildren().addAll(navPenjualan, navBarang, navPelanggan);

        borderPane.setLeft(sidebar);

        Scene scene = new Scene(borderPane, 800, 600);
        indexState.setScene(scene);
        indexState.setFullScreen(true);
        indexState.setTitle("AjungStore - Index Penjualan");
        indexState.show();
    }

    public void create(Stage createStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/createPenjualan.css").toExternalForm();
        borderPane.getStylesheets().add(css);

        GridPane header = new GridPane();
        header.setMinHeight(80);
        header.getStyleClass().add("header");

        Label appName = new Label("AjungStore");
        appName.setTextFill(Color.RED);
        appName.getStyleClass().add("appName");

        Label welcome = new Label("Hai, Admin");
        welcome.getStyleClass().add("welcome");

        // Set the first column to expand to take the remaining space
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        header.getColumnConstraints().add(column1);

        header.setAlignment(Pos.CENTER);

        header.add(appName, 0, 0); // Add AjungStore to the first column, first row
        header.add(welcome, 1, 0); // Add Hai, Admin to the second column, first row

        borderPane.setTop(header);

        VBox sidebar = new VBox();
        sidebar.setMinWidth(200);
        sidebar.getStyleClass().add("sidebar");

        // Buat item navigasi
        Label navPenjualan = new Label("Penjualan");
        navPenjualan.getStyleClass().add("navPenjualan");
        Label navBarang = new Label("Barang");
        navBarang.getStyleClass().add("navBarang");
        Label navPelanggan = new Label("Pelanggan");
        navPelanggan.getStyleClass().add("navPelanggan");

        sidebar.getChildren().addAll(navPenjualan, navBarang, navPelanggan);

        borderPane.setLeft(sidebar);

        Scene scene = new Scene(borderPane, 800, 600);
        createStage.setScene(scene);
        createStage.setFullScreen(true);
        createStage.setTitle("AjungStore - Create Penjualan");
        createStage.show();
    }
}

