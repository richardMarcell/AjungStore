package ajungstore;


import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Barang {
    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private void refreshTableData(TableView<ObservableList<String>> table) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM products")) {
            ResultSet resultSet = statement.executeQuery();

            int no = 1;
            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(no++)); // Adding "No" value
                row.add(resultSet.getString("name"));
                row.add(resultSet.getString("price"));
                row.add(resultSet.getString("id")); // Adding the actual ID (hidden from view)
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.setItems(data);
    }

    private void saveData(String name, String price) {

        String query = "INSERT INTO products(name, price) VALUES (?, ?)";
        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, price);
            int rowsInserted = statement.executeUpdate();


            if (rowsInserted > 0) {
                System.out.println("Data berhasil disimpan.");
            } else {
                System.out.println("Gagal menyimpan data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteData(String id) {
        String query = "DELETE FROM products WHERE id = ?";


        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            int rowsDeleted = statement.executeUpdate();


            if (rowsDeleted > 0) {
                System.out.println("Data berhasil dihapus.");
            } else {
                System.out.println("Gagal menghapus data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // edit Data
    private ObservableList<String> getDataById(String id) {
        ObservableList<String> row = FXCollections.observableArrayList();
        String query = "SELECT * FROM products WHERE id = ?";

        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                row.add(resultSet.getString("name"));
                row.add(resultSet.getString("price"));
                row.add(resultSet.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return row;
    }

    private void updateData (String id, String name, String price) {
        

        String query = "UPDATE products SET name = ?, price = ? where id = ? ";

        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, price);
            statement.setString(3, id);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Data berhasil diperbarui.");
            } else {
                System.out.println("Gagal memperbarui data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String convertToInteger(String value) {
        // Hapus karakter non-digit dari string
        String cleanedValue = value.replaceAll("[^\\d]", "");

        // Konversi string menjadi integer
        String result = cleanedValue;
        
        return result;}

    public void index(Stage indexStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/indexBarang.css").toExternalForm();
        borderPane.getStylesheets().add(css);


        GridPane header = new GridPane();
        header.setMinHeight(80);
        header.getStyleClass().add("header");


        Label appName = new Label("AjungStore");
        appName.setTextFill(Color.RED);
        appName.getStyleClass().add("appName");


        Label welcome = new Label("Hai, Admin");
        welcome.getStyleClass().add("welcome");


        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        header.getColumnConstraints().add(column1);


        header.setAlignment(Pos.CENTER);
        header.add(appName, 0, 0);
        header.add(welcome, 1, 0);
        borderPane.setTop(header);


        VBox sidebar = new VBox();
        sidebar.setMinWidth(200);
        sidebar.getStyleClass().add("sidebar");


        Pelanggan pelanggan = new Pelanggan();
        Penjualan penjualan = new Penjualan();


        Button navPenjualan = new Button("Penjualan");
        navPenjualan.getStyleClass().add("navPenjualan");
        navPenjualan.setOnAction(e -> {
            try {
                penjualan.index(indexStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        Button navBarang = new Button("Barang");
        navBarang.getStyleClass().add("navBarang");
        navBarang.setOnAction(e -> {
            try {
                index(indexStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        Button navPelanggan = new Button("Pelanggan");
        navPelanggan.getStyleClass().add("navPelanggan");
        navPelanggan.setOnAction(e -> {
            try {
                pelanggan.index(indexStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        sidebar.getChildren().addAll(navPenjualan, navBarang, navPelanggan);


        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("contentBox");
        contentBox.setSpacing(10);


        VBox contentHeaderBox = new VBox();
        contentHeaderBox.getStyleClass().add("contentHeader");


        Label contentHeaderTitle = new Label("Dashboard Barang");
        contentHeaderTitle.getStyleClass().add("contentHeaderTitle");


        Label contentHeaderDescription = new Label("Pengelolaan daftar barang Toko Ajung");
        contentHeaderDescription.getStyleClass().add("contentHeaderDescription");


        contentHeaderBox.getChildren().addAll(contentHeaderTitle, contentHeaderDescription);


        VBox tableBox = new VBox();
        tableBox.setSpacing(10);


        Button buttonCreate = new Button("+ Barang");
        buttonCreate.getStyleClass().add("buttonCreate");
        buttonCreate.setAlignment(Pos.CENTER);
        buttonCreate.setTextFill(Color.WHITE);
        buttonCreate.setMinWidth(150);


        buttonCreate.setOnAction(e -> {
            try {
                create(indexStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        TableView<ObservableList<String>> table = new TableView<>();


        TableColumn<ObservableList<String>, String> colNo = new TableColumn<>("No");
        TableColumn<ObservableList<String>, String> colNamaBarang = new TableColumn<>("Nama Barang");
        TableColumn<ObservableList<String>, String> colHarga = new TableColumn<>("Harga");
        TableColumn<ObservableList<String>, String> colAction = new TableColumn<>("Action");


        colNo.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        colNamaBarang.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
        colHarga.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        colAction.prefWidthProperty().bind(table.widthProperty().multiply(0.2));


        colNo.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(0)));
        colNamaBarang.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(1)));
        colHarga.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(2)));
        colAction.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(""));


        table.getColumns().addAll(colNo, colNamaBarang, colHarga, colAction);


        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();


        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM products")) {


            ResultSet resultSet = statement.executeQuery();


            int no = 1;
            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(no++));
                row.add(resultSet.getString("name"));
                row.add(formatter.format(Double.valueOf(resultSet.getString("price"))));
                row.add(resultSet.getString("id"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.setItems(data);

        colAction.setCellFactory(param -> new TableCell<ObservableList<String>, String>() {
            final Button editButton = new Button("Edit");
            final Button deleteButton = new Button("Hapus");
            final HBox actionButtons = new HBox(editButton, deleteButton);
       
            {
                // Handle edit button action
                actionButtons.setSpacing(10);
                editButton.setOnAction(event -> {
                    ObservableList<String> rowData = getTableView().getItems().get(getIndex());
                    String id = rowData.get(3);
                    try {
                        edit(indexStage, id);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
       
                // Handle delete button action
                deleteButton.setOnAction(event -> {
                    ObservableList<String> rowData = getTableView().getItems().get(getIndex());
                    String id = rowData.get(3); // Assuming the ID is in the first column
       
                    // Show confirmation dialog
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Konfirmasi Penghapusan");
                    alert.setHeaderText(null);
                    alert.setContentText("Apakah Anda yakin ingin menghapus barang ini?");
                   
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        deleteData(id);
                        getTableView().getItems().remove(rowData); // Remove from table view
                    }
                });
            }
       
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
       
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(editButton, deleteButton);
                    buttons.setSpacing(5);
                    setGraphic(buttons);
                }
            }
        });
       
        table.setItems(data);
        tableBox.getChildren().addAll(buttonCreate, table);


        contentBox.getChildren().addAll(contentHeaderBox, tableBox);


        borderPane.setLeft(sidebar);
        borderPane.setCenter(contentBox);


        Scene scene = new Scene(borderPane, 800, 600);
        indexStage.setScene(scene);
        indexStage.setFullScreen(true);
        indexStage.setTitle("AjungStore - Index Barang");
        indexStage.show();
    }


    public void create(Stage createStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/createBarang.css").toExternalForm();
        borderPane.getStylesheets().add(css);
   
        GridPane header = new GridPane();
        header.setMinHeight(80);
        header.getStyleClass().add("header");
   
        Label appName = new Label("AjungStore");
        appName.setTextFill(Color.RED);
        appName.getStyleClass().add("appName");
   
        Label welcome = new Label("Hai, Admin");
        welcome.getStyleClass().add("welcome");
   
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        header.getColumnConstraints().add(column1);
   
        header.setAlignment(Pos.CENTER);
        header.add(appName, 0, 0);
        header.add(welcome, 1, 0);
        borderPane.setTop(header);
   
        VBox sidebar = new VBox();
        sidebar.setMinWidth(200);
        sidebar.getStyleClass().add("sidebar");
   
        Pelanggan pelanggan = new Pelanggan();
        Penjualan penjualan = new Penjualan();
   
        Button navPenjualan = new Button("Penjualan");
        navPenjualan.getStyleClass().add("navPenjualan");
        navPenjualan.setOnAction(e -> {
            try {
                penjualan.index(createStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
   
        Button navBarang = new Button("Barang");
        navBarang.getStyleClass().add("navBarang");
        navBarang.setOnAction(e -> {
            try {
                index(createStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
   
        Button navPelanggan = new Button("Pelanggan");
        navPelanggan.getStyleClass().add("navPelanggan");
        navPelanggan.setOnAction(e -> {
            try {
                pelanggan.index(createStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
   
        sidebar.getChildren().addAll(navPenjualan, navBarang, navPelanggan);
   
        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("contentBox");
        contentBox.setSpacing(30);
   
        VBox contentHeaderBox = new VBox();
        contentHeaderBox.getStyleClass().add("contentHeader");
   
        Label contentHeaderTitle = new Label("Tambah Barang");
        contentHeaderTitle.getStyleClass().add("contentHeaderTitle");
   
        Label contentHeaderDescription = new Label("Menambah barang yang ada di Toko Ajung");
        contentHeaderDescription.getStyleClass().add("contentHeaderDescription");
   
        contentHeaderBox.getChildren().addAll(contentHeaderTitle, contentHeaderDescription);
   
        VBox formBox = new VBox();
        formBox.getStyleClass().add("formBox");
        formBox.setSpacing(20);
   
        VBox primaryForm = new VBox();
        primaryForm.setSpacing(30);
        primaryForm.getStyleClass().add("primaryForm");
   
        HBox namaBarangField = new HBox();
        namaBarangField.setSpacing(60);
        namaBarangField.setAlignment(Pos.CENTER_LEFT);
        Label namaBarangLabel = new Label("Nama Barang");
        namaBarangLabel.getStyleClass().add("namaBarangLabel");
        TextField namaBarangInput = new TextField();
        namaBarangInput.getStyleClass().add("namaBarangInput");
        HBox.setHgrow(namaBarangInput, Priority.ALWAYS);
        namaBarangField.getChildren().addAll(namaBarangLabel, namaBarangInput);
   
        HBox hargaSatuanField = new HBox();
        hargaSatuanField.setSpacing(60);
        hargaSatuanField.setAlignment(Pos.CENTER_LEFT);
        Label hargaSatuanLabel = new Label("Harga Satuan");
        hargaSatuanLabel.getStyleClass().add("hargaSatuanLabel");
        TextField hargaSatuanInput = new TextField();
        hargaSatuanInput.getStyleClass().add("hargaSatuanInput");
        HBox.setHgrow(hargaSatuanInput, Priority.ALWAYS);
        hargaSatuanField.getChildren().addAll(hargaSatuanLabel, hargaSatuanInput);

        hargaSatuanInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    // Hapus semua karakter non-digit sebelum parsing
                    String cleanString = newValue.replaceAll("[^\\d]", "");
                    // Parsing string menjadi angka
                    long parsed = Long.parseLong(cleanString);
                    // Format angka menjadi format mata uang
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    formatter.setMaximumFractionDigits(0); // Tidak menampilkan desimal
                    String formatted = formatter.format(parsed);
                    // Set nilai yang terformat ke dalam text field
                    hargaSatuanInput.setText(formatted);
                    // Pindahkan kursor ke akhir teks
                    hargaSatuanInput.end();
                } catch (NumberFormatException e) {
                    hargaSatuanInput.setText(oldValue); // Kembalikan ke nilai lama jika parsing gagal
                }
            }
        });
   
        primaryForm.getChildren().addAll(namaBarangField, hargaSatuanField);
   
        formBox.getChildren().addAll(primaryForm);
   
        HBox contentFooterBox = new HBox();
        contentFooterBox.setSpacing(20);
   
        Button backButton = new Button("Kembali");
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction(e -> {
            try {
                index(createStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
   
        Button submitButton = new Button("Simpan");
        submitButton.getStyleClass().add("submitButton");
        submitButton.setTextFill(Color.WHITE);
        submitButton.setOnAction(e -> {
            String namaBarang = namaBarangInput.getText();
            String hargaSatuan = hargaSatuanInput.getText();
   
            if (namaBarang.isEmpty() || hargaSatuan.isEmpty()) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Nama Barang dan Harga Satuan tidak boleh kosong.");
                alert.showAndWait();
            } else {
                String HargaSatuan = convertToInteger(hargaSatuan);
                saveData(namaBarang, HargaSatuan);
                try {
                    index(createStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
   
        contentFooterBox.setAlignment(Pos.CENTER_RIGHT);
        contentFooterBox.getChildren().addAll(backButton, submitButton);
   
        contentBox.getChildren().addAll(contentHeaderBox, formBox, contentFooterBox);
   
        borderPane.setLeft(sidebar);
        borderPane.setCenter(contentBox);
   
        Scene scene = new Scene(borderPane, 800, 600);
        createStage.setScene(scene);
        createStage.setFullScreen(true);
        createStage.setTitle("AjungStore - Create Barang");
        createStage.show();

  
    }
   
    public void edit(Stage editStage, String id) throws Exception {
        ObservableList<String> data = getDataById(id);

        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/createBarang.css").toExternalForm();
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
        Pelanggan pelanggan = new Pelanggan();
        Penjualan penjualan = new Penjualan();
        Button navPenjualan = new Button("Penjualan");
        navPenjualan.getStyleClass().add("navPenjualan");
        navPenjualan.setOnAction(e -> {
            try {
                penjualan.index(editStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        Button navBarang = new Button("Barang");
        navBarang.getStyleClass().add("navBarang");
        navBarang.setOnAction(e -> {
            try {
                index(editStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        Button navPelanggan = new Button("Pelanggan");
        navPelanggan.getStyleClass().add("navPelanggan");
        navPelanggan.setOnAction(e -> {
            try {
                pelanggan.index(editStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        sidebar.getChildren().addAll(navPenjualan, navBarang, navPelanggan);


        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("contentBox");
        contentBox.setSpacing(30);


        VBox contentHeaderBox = new VBox();
        contentHeaderBox.getStyleClass().add("contentHeader");


        Label contentHeaderTitle = new Label("Edit Barang");
        contentHeaderTitle.getStyleClass().add("contentHeaderTitle");


        Label contentHeaderDescription = new Label("Mengedit barang yang ada di Toko Ajung");
        contentHeaderDescription.getStyleClass().add("contentHeaderDescription");


        contentHeaderBox.getChildren().addAll(contentHeaderTitle, contentHeaderDescription);


        VBox formBox = new VBox();
        formBox.getStyleClass().add("formBox");
        formBox.setSpacing(20);


        VBox primaryForm = new VBox();
        primaryForm.setSpacing(30);
        primaryForm.getStyleClass().add("primaryForm");


        HBox namaBarangField = new HBox();
        namaBarangField.setSpacing(60);
        namaBarangField.setAlignment(Pos.CENTER_LEFT);
        Label namaBarangLabel = new Label("Nama Barang");
        namaBarangLabel.getStyleClass().add("namaBarangLabel");
        TextField namaBarangInput = new TextField(data.get(0));
        namaBarangInput.getStyleClass().add("namaBarangInput");
        HBox.setHgrow(namaBarangInput, Priority.ALWAYS);
        namaBarangField.getChildren().addAll(namaBarangLabel, namaBarangInput);


        HBox hargaSatuanField = new HBox();
        hargaSatuanField.setSpacing(60);
        hargaSatuanField.setAlignment(Pos.CENTER_LEFT);
        Label hargaSatuanLabel = new Label("Harga Satuan");
        hargaSatuanLabel.getStyleClass().add("hargaSatuanLabel");
        TextField hargaSatuanInput = new TextField(formatter.format(Double.valueOf(data.get(1))));
        hargaSatuanInput.getStyleClass().add("hargaSatuanInput");
        HBox.setHgrow(hargaSatuanInput, Priority.ALWAYS);
        hargaSatuanField.getChildren().addAll(hargaSatuanLabel, hargaSatuanInput);

        hargaSatuanInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    // Hapus semua karakter non-digit sebelum parsing
                    String cleanString = newValue.replaceAll("[^\\d]", "");
                    // Parsing string menjadi angka
                    long parsed = Long.parseLong(cleanString);
                    // Format angka menjadi format mata uang
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    formatter.setMaximumFractionDigits(0); // Tidak menampilkan desimal
                    String formatted = formatter.format(parsed);
                    // Set nilai yang terformat ke dalam text field
                    hargaSatuanInput.setText(formatted);
                    // Pindahkan kursor ke akhir teks
                    hargaSatuanInput.end();
                } catch (NumberFormatException e) {
                    hargaSatuanInput.setText(oldValue); // Kembalikan ke nilai lama jika parsing gagal
                }
            }
        });

        primaryForm.getChildren().addAll(namaBarangField, hargaSatuanField);


        formBox.getChildren().addAll(primaryForm);


        HBox contentFooterBox = new HBox();
        contentFooterBox.setSpacing(20);


        Button backButton = new Button("Kembali");
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction(e -> {
            try {
                index(editStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        Button submitButton = new Button("Simpan");
        submitButton.getStyleClass().add("submitButton");
        submitButton.setTextFill(Color.WHITE);
        submitButton.setOnAction(e -> {
            String namaBarang = namaBarangInput.getText();
            String harga = hargaSatuanInput.getText();
        
            if (!namaBarang.equals(data.get(0)) || !harga.equals(formatter.format(Double.valueOf(data.get(1))))) {
                if (namaBarang.isEmpty() || harga.isEmpty()) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Peringatan");
                    alert.setHeaderText(null);
                    alert.setContentText("Nama Barang atau Harga Satuan tidak boleh kosong.");
                    alert.showAndWait();
                } else {
                    String HargaSatuan = convertToInteger(harga);
                    updateData(id, namaBarang, HargaSatuan);
                    try {
                        index(editStage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                // Jika tidak ada perubahan, maka tidak melakukan update data
                try {
                    index(editStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        contentFooterBox.setAlignment(Pos.CENTER_RIGHT);
        contentFooterBox.getChildren().addAll(backButton, submitButton);


        contentBox.getChildren().addAll(contentHeaderBox, formBox, contentFooterBox);


        borderPane.setLeft(sidebar);
        borderPane.setCenter(contentBox);


        Scene scene = new Scene(borderPane, 800, 600);
        editStage.setScene(scene);
        editStage.setFullScreen(true);
        editStage.setTitle("AjungStore - Edit Barang");
        editStage.show();

    }}