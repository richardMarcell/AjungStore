package ajungstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
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

public class Pelanggan {
    private void saveCustomer(CustomerService customerService) {
        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO customers (name, phoneNumber, address) VALUES (?, ?, ?)")) {
            statement.setString(1, customerService.getName());
            statement.setString(2, customerService.getPhoneNumber());
            statement.setString(3, customerService.getAddress());
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteCustomerRecord(int customerId) {
        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement deleteCustomersStmt = connection
                        .prepareStatement("DELETE FROM customers WHERE id = ?")) {

            // Start a transaction
            connection.setAutoCommit(false);
            // Delete from sales_details first
            // Delete from sales
            deleteCustomersStmt.setInt(1, customerId);
            deleteCustomersStmt.executeUpdate();

            // Commit the transaction
            connection.commit();

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (PreparedStatement statement = connection.prepareStatement("SELECT name, phoneNumber FROM customers");
                    ResultSet resultSet = statement.executeQuery()) {

                int index = 1;
                while (resultSet.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(String.valueOf(index++));
                    row.add(resultSet.getString("name"));
                    row.add(resultSet.getString("phoneNumber"));
                    data.add(row);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean showDeleteConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this sales record?");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    private CustomerService loadCustomerData(int customerId) {
        CustomerService customerService = new CustomerService();
        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE id = ?")) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                customerService.setName(resultSet.getString("name"));
                customerService.setPhoneNumber(resultSet.getString("phoneNumber"));
                customerService.setAddress(resultSet.getString("address"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return customerService;
    }

    // Metode untuk memperbarui data pelanggan
    private void updateCustomer(CustomerService customerService, int customerId) {
        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE customers SET name = ?, phoneNumber = ?, address = ? WHERE id = ?")) {
            statement.setString(1, customerService.getName());
            statement.setString(2, customerService.getPhoneNumber());
            statement.setString(3, customerService.getAddress());
            statement.setInt(4, customerId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void index(Stage indexStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/indexPelanggan.css").toExternalForm();
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

        Barang barang = new Barang();
        Penjualan penjualan = new Penjualan();
        Piutang piutang = new Piutang();
        
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
                barang.index(indexStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        Button navPelanggan = new Button("Pelanggan");
        navPelanggan.getStyleClass().add("navPelanggan");
        navPelanggan.setOnAction(e -> {
            try {
                index(indexStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button navPiutang = new Button("Piutang");
        navPiutang.getStyleClass().add("navPiutang");
        navPiutang.setOnAction(e -> {
            try {
                piutang.index(indexStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        sidebar.getChildren().addAll(navPenjualan, navBarang, navPelanggan, navPiutang);

        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("contentBox");
        contentBox.setSpacing(10);

        VBox contentHeaderBox = new VBox();
        contentHeaderBox.getStyleClass().add("contentHeader");

        Label contentHeaderTitle = new Label("Dashboard Pelanggan");
        contentHeaderTitle.getStyleClass().add("contentHeaderTitle");

        Label contentHeaderDescription = new Label("Pengelolaan daftar pelanggan Toko Ajung");
        contentHeaderDescription.getStyleClass().add("contentHeaderDescription");

        contentHeaderBox.getChildren().addAll(contentHeaderTitle, contentHeaderDescription);

        VBox tableBox = new VBox();
        tableBox.setSpacing(10);
        Button buttonCreate = new Button("+ Pelanggan");
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
        TableColumn<ObservableList<String>, String> colNamaCustomer = new TableColumn<>("Nama");
        TableColumn<ObservableList<String>, String> colStatus = new TableColumn<>("Nomor Telepon");
        TableColumn<ObservableList<String>, String> colAction = new TableColumn<>("Action");

        colNo.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        colNamaCustomer.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
        colStatus.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        colAction.prefWidthProperty().bind(table.widthProperty().multiply(0.2));

        colNo.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(0)));
        colNamaCustomer.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(1)));
        colStatus.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(2)));
        colAction.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(""));

        table.getColumns().addAll(colNo, colNamaCustomer, colStatus, colAction);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        // Ambil data dari database
        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT id, name, phoneNumber FROM customers");
                ResultSet resultSet = statement.executeQuery()) {

            int index = 1;
            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(index++));
                row.add(resultSet.getString("name"));
                row.add(resultSet.getString("phoneNumber"));
                row.add(resultSet.getString("id"));
                data.add(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        colAction.setCellFactory(param -> new TableCell<ObservableList<String>, String>() {
            final Button editButton = new Button("Edit");
            final Button deleteButton = new Button("Hapus");

            {
                // Handle edit button action
                editButton.setOnAction(event -> {
                    ObservableList<String> rowData = getTableView().getItems().get(getIndex());
                    int customerId = Integer.parseInt(rowData.get(3)); // Assuming the ID is in the fourth column

                    try {
                        edit(indexStage, customerId);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                // Handle delete button action
                deleteButton.setOnAction(event -> {
                    ObservableList<String> rowData = getTableView().getItems().get(getIndex());
                    int id = Integer.parseInt(rowData.get(3)); // Assuming the ID is in the first column

                    // Show confirmation dialog
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Konfirmasi Penghapusan");
                    alert.setHeaderText(null);
                    alert.setContentText("Apakah Anda yakin ingin menghapus barang ini?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        deleteCustomerRecord(id);
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
        indexStage.setTitle("AjungStore - Index Pelanggan");
        indexStage.show();
    }

    public void create(Stage createStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/createPelanggan.css").toExternalForm();
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
        Barang barang = new Barang();
        Penjualan penjualan = new Penjualan();
        Piutang piutang = new Piutang();
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
                barang.index(createStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        Button navPelanggan = new Button("Pelanggan");
        navPelanggan.getStyleClass().add("navPelanggan");
        navPelanggan.setOnAction(e -> {
            try {
                index(createStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        

        Button navPiutang = new Button("Piutang");
        navPiutang.getStyleClass().add("navPiutang");
        navPiutang.setOnAction(e -> {
            try {
                piutang.index(createStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        sidebar.getChildren().addAll(navPenjualan, navBarang, navPelanggan, navPiutang);

        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("contentBox");
        contentBox.setSpacing(30);

        VBox contentHeaderBox = new VBox();
        contentHeaderBox.getStyleClass().add("contentHeader");

        Label contentHeaderTitle = new Label("Tambah Pelanggan");
        contentHeaderTitle.getStyleClass().add("contentHeaderTitle");

        Label contentHeaderDescription = new Label("Menambah pelanggan di Toko Ajung");
        contentHeaderDescription.getStyleClass().add("contentHeaderDescription");

        contentHeaderBox.getChildren().addAll(contentHeaderTitle, contentHeaderDescription);

        VBox formBox = new VBox();
        formBox.getStyleClass().add("formBox");
        formBox.setSpacing(20);

        VBox primaryForm = new VBox();
        primaryForm.setSpacing(30);
        primaryForm.getStyleClass().add("primaryForm");

        HBox namaField = new HBox();
        namaField.setSpacing(60);
        namaField.setAlignment(Pos.CENTER_LEFT);
        Label namaLabel = new Label("Nama");
        namaLabel.getStyleClass().add("namaLabel");
        TextField namaInput = new TextField();
        namaInput.getStyleClass().add("namaInput");
        HBox.setHgrow(namaInput, Priority.ALWAYS);
        namaField.getChildren().addAll(namaLabel, namaInput);

        HBox nomorTeleponField = new HBox();
        nomorTeleponField.setSpacing(60);
        nomorTeleponField.setAlignment(Pos.CENTER_LEFT);
        Label nomorTeleponLabel = new Label("Telepon");
        nomorTeleponLabel.getStyleClass().add("nomorTeleponLabel");
        TextField nomorTeleponInput = new TextField();
        nomorTeleponInput.getStyleClass().add("nomorTeleponInput");
        HBox.setHgrow(nomorTeleponInput, Priority.ALWAYS);
        nomorTeleponField.getChildren().addAll(nomorTeleponLabel, nomorTeleponInput);

        HBox alamatField = new HBox();
        alamatField.setSpacing(60);
        alamatField.setAlignment(Pos.CENTER_LEFT);
        Label alamatLabel = new Label("Alamat");
        alamatLabel.getStyleClass().add("alamatLabel");
        TextField alamatInput = new TextField();
        alamatInput.getStyleClass().add("alamatInput");
        HBox.setHgrow(alamatInput, Priority.ALWAYS);
        alamatField.getChildren().addAll(alamatLabel, alamatInput);

        primaryForm.getChildren().addAll(namaField, nomorTeleponField, alamatField);

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
            String nama = namaInput.getText();
            String nomorTelepon = nomorTeleponInput.getText();
            String alamat = alamatInput.getText();

            if (nama.isEmpty() || nomorTelepon.isEmpty() || alamat.isEmpty()) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Nama Pelanggan, Nomor telepon dan alamat tidak boleh kosong.");
                alert.showAndWait();
            } else {
                CustomerService customerService = new CustomerService();
                customerService.setName(namaInput.getText());
                customerService.setPhoneNumber(nomorTeleponInput.getText());
                customerService.setAddress(alamatInput.getText());

                saveCustomer(customerService);
                System.out.println("Berhasil menyimpan data pelanggan");
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
        createStage.setTitle("AjungStore - Create Pelanggan");
        createStage.show();
    }

    public void edit(Stage editStage, int customerId) throws Exception {
        CustomerService customer = loadCustomerData(customerId);

        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/editPelanggan.css").toExternalForm();
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
        Barang barang = new Barang();
        Penjualan penjualan = new Penjualan();
        Piutang piutang = new Piutang();
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
                barang.index(editStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        Button navPelanggan = new Button("Pelanggan");
        navPelanggan.getStyleClass().add("navPelanggan");
        navPelanggan.setOnAction(e -> {
            try {
                index(editStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button navPiutang = new Button("Piutang");
        navPiutang.getStyleClass().add("navPiutang");
        navPiutang.setOnAction(e -> {
            try {
                piutang.index(editStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        sidebar.getChildren().addAll(navPenjualan, navBarang, navPelanggan, navPiutang);

        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("contentBox");
        contentBox.setSpacing(30);

        VBox contentHeaderBox = new VBox();
        contentHeaderBox.getStyleClass().add("contentHeader");

        Label contentHeaderTitle = new Label("Edit Pelanggan");
        contentHeaderTitle.getStyleClass().add("contentHeaderTitle");

        Label contentHeaderDescription = new Label("Mengedit pelanggan di Toko Ajung");
        contentHeaderDescription.getStyleClass().add("contentHeaderDescription");

        contentHeaderBox.getChildren().addAll(contentHeaderTitle, contentHeaderDescription);

        VBox formBox = new VBox();
        formBox.getStyleClass().add("formBox");
        formBox.setSpacing(20);

        VBox primaryForm = new VBox();
        primaryForm.setSpacing(30);
        primaryForm.getStyleClass().add("primaryForm");

        HBox namaField = new HBox();
        namaField.setSpacing(60);
        namaField.setAlignment(Pos.CENTER_LEFT);
        Label namaLabel = new Label("Nama");
        namaLabel.getStyleClass().add("namaLabel");
        TextField namaInput = new TextField();
        namaInput.getStyleClass().add("namaInput");
        namaInput.setText(customer.getName());
        HBox.setHgrow(namaInput, Priority.ALWAYS);
        namaField.getChildren().addAll(namaLabel, namaInput);

        HBox nomorTeleponField = new HBox();
        nomorTeleponField.setSpacing(60);
        nomorTeleponField.setAlignment(Pos.CENTER_LEFT);
        Label nomorTeleponLabel = new Label("Telepon");
        nomorTeleponLabel.getStyleClass().add("nomorTeleponLabel");
        TextField nomorTeleponInput = new TextField();
        nomorTeleponInput.getStyleClass().add("nomorTeleponInput");
        nomorTeleponInput.setText(customer.getPhoneNumber());
        HBox.setHgrow(nomorTeleponInput, Priority.ALWAYS);
        nomorTeleponField.getChildren().addAll(nomorTeleponLabel, nomorTeleponInput);

        HBox alamatField = new HBox();
        alamatField.setSpacing(60);
        alamatField.setAlignment(Pos.CENTER_LEFT);
        Label alamatLabel = new Label("Alamat");
        alamatLabel.getStyleClass().add("alamatLabel");
        TextField alamatInput = new TextField();
        alamatInput.getStyleClass().add("alamatInput");
        alamatInput.setText(customer.getAddress());
        HBox.setHgrow(alamatInput, Priority.ALWAYS);
        alamatField.getChildren().addAll(alamatLabel, alamatInput);

        primaryForm.getChildren().addAll(namaField, nomorTeleponField, alamatField);

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
            if (namaInput.getText().isEmpty() || nomorTeleponInput.getText().isEmpty()
                    || alamatInput.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Nama, Nomor Telepon, dan Alamat harus diisi.");
                alert.showAndWait();
            } else {
                System.out.println("Berhasil menyimpan data pelanggan");
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
        editStage.setTitle("AjungStore - Edit Pelanggan");
        editStage.show();
    }

}
