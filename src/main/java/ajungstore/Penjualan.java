package ajungstore;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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

        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("contentBox");
        contentBox.setSpacing(10);

        VBox contentHeaderBox = new VBox();
        contentHeaderBox.getStyleClass().add("contentHeader");

        Label contentHeaderTitle = new Label("Dashboard Penjualan");
        contentHeaderTitle.getStyleClass().add("contentHeaderTitle");

        Label contentHeaderDescription = new Label("Pengelolaan daftar penjualan Toko Ajung");
        contentHeaderDescription.getStyleClass().add("contentHeaderDescription");

        contentHeaderBox.getChildren().addAll(contentHeaderTitle, contentHeaderDescription);

        HBox quickStats = new HBox();
        quickStats.getStyleClass().add("quickStats");
        quickStats.setSpacing(10);

        VBox statPenjualan = new VBox();
        statPenjualan.setAlignment(Pos.CENTER);
        statPenjualan.getStyleClass().add("statPenjualan");
        HBox.setHgrow(statPenjualan, Priority.ALWAYS); // <-- Mengatur lebar VBox

        Label statPenjualanHeader = new Label("Penjualan");
        statPenjualanHeader.getStyleClass().add("statPenjualanHeader");

        Label statPenjualanContent = new Label("10");
        statPenjualanContent.getStyleClass().add("statPenjualanContent");

        statPenjualan.getChildren().addAll(statPenjualanHeader, statPenjualanContent);

        VBox statHutang = new VBox();
        statHutang.setAlignment(Pos.CENTER);
        statHutang.getStyleClass().add("statHutang");
        HBox.setHgrow(statHutang, Priority.ALWAYS); // <-- Mengatur lebar VBox

        Label statHutangHeader = new Label("Hutang");
        statHutangHeader.getStyleClass().add("statHutangHeader");

        Label statHutangContent = new Label("10");
        statHutangContent.getStyleClass().add("statHutangContent");

        statHutang.getChildren().addAll(statHutangHeader, statHutangContent);

        VBox statPiutang = new VBox();
        statPiutang.setAlignment(Pos.CENTER);
        statPiutang.getStyleClass().add("statPiutang");
        HBox.setHgrow(statPiutang, Priority.ALWAYS); // <-- Mengatur lebar VBox

        Label statPiutangHeader = new Label("Piutang");
        statPiutangHeader.getStyleClass().add("statPiutangHeader");

        Label statPiutangContent = new Label("10");
        statPiutangContent.getStyleClass().add("statPiutangContent");

        statPiutang.getChildren().addAll(statPiutangHeader, statPiutangContent);

        quickStats.getChildren().setAll(statPenjualan, statHutang, statPiutang);


        VBox tableBox = new VBox();
        tableBox.setSpacing(10);
        Button buttonCreate = new Button("+ Penjualan");
        buttonCreate.getStyleClass().add("buttonCreate");
        buttonCreate.setAlignment(Pos.CENTER);
        buttonCreate.setTextFill(Color.WHITE);
        buttonCreate.setMinWidth(150);

        buttonCreate.setOnAction(e -> {
            try {
                create(indexState);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        TableView table = new TableView();
        // tambahkan kolom-kolom untuk tabel penjualan (no, nama customer, status, action)
        TableColumn colNo = new TableColumn("No");
        TableColumn colNamaCustomer = new TableColumn("Nama Customer");
        TableColumn colStatus = new TableColumn("Status");
        TableColumn colAction = new TableColumn("Action");

        // Mengatur lebar kolom dengan persentase dari lebar total tabel
        colNo.prefWidthProperty().bind(table.widthProperty().multiply(0.1)); // 10% dari lebar tabel
        colNamaCustomer.prefWidthProperty().bind(table.widthProperty().multiply(0.4)); // 40% dari lebar tabel
        colStatus.prefWidthProperty().bind(table.widthProperty().multiply(0.3)); // 30% dari lebar tabel
        colAction.prefWidthProperty().bind(table.widthProperty().multiply(0.2)); // 20% dari lebar tabel

        table.getColumns().addAll(colNo, colNamaCustomer, colStatus, colAction);

        tableBox.getChildren().addAll(buttonCreate, table);

        contentBox.getChildren().addAll(contentHeaderBox, quickStats, tableBox);

        borderPane.setLeft(sidebar);
        borderPane.setCenter(contentBox);


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

        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("contentBox");
        contentBox.setSpacing(30);

        VBox contentHeaderBox = new VBox();
        contentHeaderBox.getStyleClass().add("contentHeader");

        Label contentHeaderTitle = new Label("Tambah Penjualan");
        contentHeaderTitle.getStyleClass().add("contentHeaderTitle");

        Label contentHeaderDescription = new Label("Menambah penjualan yang terjadi di Toko Ajung");
        contentHeaderDescription.getStyleClass().add("contentHeaderDescription");

        contentHeaderBox.getChildren().addAll(contentHeaderTitle, contentHeaderDescription);

        VBox formBox = new VBox();
        formBox.getStyleClass().add("formBox");
        formBox.setSpacing(20);

        VBox primaryForm = new VBox();
        primaryForm.setSpacing(30);
        primaryForm.getStyleClass().add("primaryForm");

        HBox nomorFakturField = new HBox();
        nomorFakturField.setSpacing(60);
        nomorFakturField.setAlignment(Pos.CENTER_LEFT);
        Label nomorFakturLabel = new Label("No Faktur");
        nomorFakturLabel.getStyleClass().add("nomorFakturLabel");
        TextField nomorFakturInput = new TextField();
        nomorFakturInput.getStyleClass().add("nomorFakturInput");
        HBox.setHgrow(nomorFakturInput, Priority.ALWAYS);
        nomorFakturField.getChildren().addAll(nomorFakturLabel, nomorFakturInput);

        HBox namaPelangganField = new HBox();
        namaPelangganField.setSpacing(15);
        namaPelangganField.setAlignment(Pos.CENTER_LEFT);
        Label namaPelangganLabel = new Label("Nama Pelanggan");
        namaPelangganLabel.getStyleClass().add("namaPelangganLabel");
        namaPelangganLabel.setMinWidth(Region.USE_PREF_SIZE); // Menentukan lebar minimum agar tidak terpotong
        ComboBox<String> namaPelangganInput = new ComboBox<>();
        namaPelangganInput.getItems().addAll("Andi", "Budi", "Budiman Andi");
        namaPelangganInput.getStyleClass().add("namaPelangganInput");
        HBox.setHgrow(namaPelangganInput, Priority.ALWAYS);
        namaPelangganInput.prefWidthProperty().bind(primaryForm.widthProperty().subtract(120)); // 60 adalah spacing dari nomorFakturField
        namaPelangganField.getChildren().addAll(namaPelangganLabel, namaPelangganInput);

        HBox tanggalField = new HBox();
        tanggalField.setSpacing(75);
        tanggalField.setAlignment(Pos.CENTER_LEFT);
        Label tanggalLabel = new Label("Tanggal");
        tanggalLabel.getStyleClass().add("tanggalLabel");
        tanggalLabel.setMinWidth(Region.USE_PREF_SIZE);
        DatePicker tanggalInput = new DatePicker();
        tanggalInput.getStyleClass().add("tanggalInput");
        HBox.setHgrow(tanggalInput, Priority.ALWAYS);
        tanggalInput.prefWidthProperty().bind(primaryForm.widthProperty().subtract(120)); // 60 adalah spacing dari tanggalField
        tanggalField.getChildren().addAll(tanggalLabel, tanggalInput);


        primaryForm.getChildren().addAll(nomorFakturField, namaPelangganField, tanggalField);

        VBox secondaryForm = new VBox();
        secondaryForm.getStyleClass().add("secondaryForm");

        formBox.getChildren().addAll(primaryForm, secondaryForm);

        contentBox.getChildren().addAll(contentHeaderBox, formBox);

        borderPane.setLeft(sidebar);
        borderPane.setCenter(contentBox);


        Scene scene = new Scene(borderPane, 800, 600);
        createStage.setScene(scene);
        createStage.setFullScreen(true);
        createStage.setTitle("AjungStore - Create Penjualan");
        createStage.show();
    }

    public void edit(Stage ediStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/editPenjualan.css").toExternalForm();
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

        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("contentBox");
        contentBox.setSpacing(10);

        VBox contentHeaderBox = new VBox();
        contentHeaderBox.getStyleClass().add("contentHeader");

        Label contentHeaderTitle = new Label("Edit Penjualan");
        contentHeaderTitle.getStyleClass().add("contentHeaderTitle");

        Label contentHeaderDescription = new Label("Mengedit penjualan yang terjadi di Toko Ajung");
        contentHeaderDescription.getStyleClass().add("contentHeaderDescription");

        contentHeaderBox.getChildren().addAll(contentHeaderTitle, contentHeaderDescription);

        contentBox.getChildren().addAll(contentHeaderBox);

        borderPane.setLeft(sidebar);
        borderPane.setCenter(contentBox);

        Scene scene = new Scene(borderPane, 800, 600);
        ediStage.setScene(scene);
        ediStage.setFullScreen(true);
        ediStage.setTitle("AjungStore - Edit Penjualan");
        ediStage.show();
    }
}

