package ajungstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class Penjualan {
    private double totalPenjualan = 0.0;
    private int totalKuantitas = 0;
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private List<SalesDetailService> daftarDetailTransaksi = new ArrayList<>();

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String convertToInteger(String value) {
        // Hapus karakter non-digit dari string
        String cleanedValue = value.replaceAll("[^\\d]", "");

        // Konversi string menjadi integer
        String result = cleanedValue;
        return result;
    }

    public int storeSales(SalesService salesService) {
        String insertSalesSQL = "INSERT INTO sales (customerId, userId, transactionDate, status, numberFactur, totalQuantity, totalSales, totalPayment) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement preparedStatement = connection.prepareStatement(insertSalesSQL,
                        Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, salesService.getCustomerId());
            preparedStatement.setInt(2, salesService.getUserId());
            preparedStatement.setDate(3, java.sql.Date.valueOf(salesService.getTransactionDate()));
            preparedStatement.setString(4, salesService.getStatus());
            preparedStatement.setString(5, salesService.getNumberFactur());
            preparedStatement.setInt(6, salesService.getTotalQuantity());
            preparedStatement.setDouble(7, salesService.getTotalSales());
            preparedStatement.setDouble(8, salesService.getTotalPayment());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the generated sale ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; //

    }

    public int editSales(SalesService salesService) {
        String updateSalesSQL = "UPDATE sales SET customerId = ?, userId = ?, transactionDate = ?, status = ?, numberFactur = ?, totalQuantity = ?, totalSales = ?, totalPayment = ? WHERE id = ?";
        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement preparedStatement = connection.prepareStatement(updateSalesSQL)) {

            preparedStatement.setInt(1, salesService.getCustomerId());
            preparedStatement.setInt(2, salesService.getUserId());
            preparedStatement.setDate(3, java.sql.Date.valueOf(salesService.getTransactionDate()));
            preparedStatement.setString(4, salesService.getStatus());
            preparedStatement.setString(5, salesService.getNumberFactur());
            preparedStatement.setInt(6, salesService.getTotalQuantity());
            preparedStatement.setDouble(7, salesService.getTotalSales());
            preparedStatement.setDouble(8, salesService.getTotalPayment());
            preparedStatement.setInt(9, salesService.getSaleId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Rows updated: " + affectedRows);
                return salesService.getSaleId(); // Return the updated sale ID
            } else {
                System.out.println("No rows updated.");
            }
        } catch (SQLException e) {
            System.err.println("Error editing sales: " + e.getMessage());
            e.printStackTrace();
        }
        return 0; // Return 0 if no update or an error occurred
    }

    public void storeSalesDetail(int salesId) {
        try (Connection connection = Dbconnect.getConnect()) {
            String sqlDelete = "DELETE FROM sales_details WHERE salesId = ?";
            String sqlStore = "INSERT INTO sales_details (salesId, productId, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement statementDelete = connection.prepareStatement(sqlDelete);
            statementDelete.setInt(1, salesId);
            statementDelete.executeUpdate();

            PreparedStatement statementStore = connection.prepareStatement(sqlStore);
            for (SalesDetailService detail : daftarDetailTransaksi) {
                statementStore.setInt(1, salesId);
                statementStore.setInt(2, detail.getProductId());
                statementStore.setDouble(3, detail.getPrice());
                statementStore.setInt(4, detail.getQuantity());

                statementStore.addBatch();
            }

            statementStore.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSalesRecord(int salesId) {
        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement deleteDetailsStmt = connection
                        .prepareStatement("DELETE FROM sales_details WHERE salesId = ?");
                PreparedStatement deleteSalesStmt = connection.prepareStatement("DELETE FROM sales WHERE id = ?")) {

            // Start a transaction
            connection.setAutoCommit(false);

            // Delete from sales_details first
            deleteDetailsStmt.setInt(1, salesId);
            deleteDetailsStmt.executeUpdate();

            // Delete from sales
            deleteSalesStmt.setInt(1, salesId);
            deleteSalesStmt.executeUpdate();

            // Commit the transaction
            connection.commit();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void index(Stage indexStage) throws Exception {
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
        Pelanggan pelanggan = new Pelanggan();
        Button navPenjualan = new Button("Penjualan");
        navPenjualan.getStyleClass().add("navPenjualan");
        navPenjualan.setOnAction(e -> {
            try {
                index(indexStage);
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
        HBox.setHgrow(statPenjualan, Priority.ALWAYS);

        Label statPenjualanHeader = new Label("Penjualan");
        statPenjualanHeader.getStyleClass().add("statPenjualanHeader");

        Label statPenjualanContent = new Label("10"); // Anda bisa mengganti ini dengan data dari database
        statPenjualanContent.getStyleClass().add("statPenjualanContent");

        statPenjualan.getChildren().addAll(statPenjualanHeader, statPenjualanContent);

        VBox statPiutang = new VBox();
        statPiutang.setAlignment(Pos.CENTER);
        statPiutang.getStyleClass().add("statPiutang");
        HBox.setHgrow(statPiutang, Priority.ALWAYS);

        Label statPiutangHeader = new Label("Piutang");
        statPiutangHeader.getStyleClass().add("statPiutangHeader");

        Label statPiutangContent = new Label("10"); // Anda bisa mengganti ini dengan data dari database
        statPiutangContent.getStyleClass().add("statPiutangContent");

        statPiutang.getChildren().addAll(statPiutangHeader, statPiutangContent);

        quickStats.getChildren().setAll(statPenjualan, statPiutang);

        VBox tableBox = new VBox();
        tableBox.setSpacing(10);

        HBox filterBox = new HBox();
        filterBox.setSpacing(10);
        DatePicker startDate = new DatePicker();
        Label untilLabel = new Label("s/d");
        DatePicker endDate = new DatePicker();
        filterBox.getChildren().addAll(startDate, untilLabel, endDate);

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);

        Button buttonCreate = new Button("+ Penjualan");
        buttonCreate.getStyleClass().add("buttonCreate");
        buttonCreate.setAlignment(Pos.CENTER);
        buttonCreate.setTextFill(Color.WHITE);
        buttonCreate.setMinWidth(150);

        Button buttonCetakLaporan = new Button("Cetak");
        buttonCetakLaporan.getStyleClass().add("buttonCetakLaporan");
        buttonCetakLaporan.setAlignment(Pos.CENTER);
        buttonCetakLaporan.setTextFill(Color.WHITE);
        buttonCetakLaporan.setMinWidth(150);

        buttonBox.getChildren().setAll(buttonCreate, buttonCetakLaporan);

        buttonCreate.setOnAction(e -> {
            try {
                create(indexStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        TableView<ObservableList<String>> table = new TableView<>();

        TableColumn<ObservableList<String>, String> colNo = new TableColumn<>("No");
        TableColumn<ObservableList<String>, String> colTanggalTransaksi = new TableColumn<>("Tanggal");
        TableColumn<ObservableList<String>, String> colNamaCustomer = new TableColumn<>("Nama Customer");
        TableColumn<ObservableList<String>, String> colStatus = new TableColumn<>("Status");
        TableColumn<ObservableList<String>, String> colAction = new TableColumn<>("Action");

        colNo.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        colTanggalTransaksi.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        colNamaCustomer.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        colStatus.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        colAction.prefWidthProperty().bind(table.widthProperty().multiply(0.2));

        colNo.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(0)));
        colTanggalTransaksi.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(1)));
        colNamaCustomer.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(2)));
        colStatus.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(3)));
        colAction.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(""));

        table.getColumns().addAll(colNo, colTanggalTransaksi, colNamaCustomer, colStatus, colAction);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT sales.id, sales.transactionDate, customers.name, sales.status FROM sales LEFT JOIN customers ON sales.customerId = customers.id")) {

            ResultSet resultSet = statement.executeQuery();

            int no = 1;
            while (resultSet.next()) {
                ObservableList<String> rowData = FXCollections.observableArrayList();
                rowData.add(String.valueOf(no++));
                LocalDate transactionDate = resultSet.getDate("transactionDate").toLocalDate();
                String formattedDate = transactionDate.format(formatter);
                rowData.add(formattedDate);
                rowData.add(resultSet.getString("name"));
                rowData.add(resultSet.getString("status"));
                rowData.add(resultSet.getString("id"));
                data.add(rowData);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        colAction.setCellFactory(param -> new TableCell<ObservableList<String>, String>() {
            final Button editButton = new Button("Edit");
            final Button deleteButton = new Button("Hapus");

            {
                editButton.setOnAction(event -> {
                    try {
                        ObservableList<String> rowData = getTableView().getItems().get(getIndex());
                        int salesId = Integer.parseInt(rowData.get(4));
                        edit(salesId, indexStage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                deleteButton.setOnAction(event -> {
                    ObservableList<String> rowData = getTableView().getItems().get(getIndex());
                    int salesId = Integer.parseInt(rowData.get(4));

                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Konfirmasi Penghapusan");
                    alert.setHeaderText(null);
                    alert.setContentText("Apakah Anda yakin ingin menghapus penjualan ini?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        deleteSalesRecord(salesId);
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

        tableBox.getChildren().addAll(filterBox, buttonBox, table);

        contentBox.getChildren().addAll(contentHeaderBox, quickStats, tableBox);

        borderPane.setLeft(sidebar);
        borderPane.setCenter(contentBox);

        Scene scene = new Scene(borderPane, 800, 600);
        indexStage.setScene(scene);
        indexStage.show();
    }

    public void create(Stage createStage) throws Exception {
        SalesService salesService = new SalesService();
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
        Barang barang = new Barang();
        Pelanggan pelanggan = new Pelanggan();
        Button navPenjualan = new Button("Penjualan");
        navPenjualan.getStyleClass().add("navPenjualan");
        navPenjualan.setOnAction(e -> {
            try {
                index(createStage);
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
        nomorFakturInput.setDisable(true);
        nomorFakturInput.setText(salesService.getNewNumberFactur());
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
        CustomerService customerService = new CustomerService();
        List<String> customerNames = customerService.getAllCustomerNames();
        namaPelangganInput.getItems().addAll(customerNames);
        namaPelangganInput.getStyleClass().add("namaPelangganInput");
        HBox.setHgrow(namaPelangganInput, Priority.ALWAYS);
        namaPelangganInput.prefWidthProperty().bind(primaryForm.widthProperty().subtract(120)); // 60 adalah spacing
                                                                                                // dari nomorFakturField
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
        tanggalInput.prefWidthProperty().bind(primaryForm.widthProperty().subtract(120)); // 60 adalah spacing dari
                                                                                          // tanggalField
        tanggalField.getChildren().addAll(tanggalLabel, tanggalInput);

        primaryForm.getChildren().addAll(nomorFakturField, namaPelangganField, tanggalField);

        VBox secondaryForm = new VBox();
        secondaryForm.setSpacing(20);
        secondaryForm.getStyleClass().add("secondaryForm");

        VBox secondaryFormHeader = new VBox();
        secondaryFormHeader.setAlignment(Pos.CENTER);
        Label secondaryFormTitle = new Label("Detail Transaksi");
        secondaryFormTitle.getStyleClass().add("secondaryFormTitle");
        secondaryFormHeader.getChildren().setAll(secondaryFormTitle);

        GridPane secondaryFormGrid = new GridPane();
        secondaryFormGrid.setHgap(10);
        secondaryFormGrid.setVgap(10);

        Button tambahDetailTransaksiButton = new Button("Tambah Barang");
        tambahDetailTransaksiButton.getStyleClass().add("tambahDetailTransaksiButton");
        tambahDetailTransaksiButton.setTextFill(Color.WHITE);

        HBox totalField = new HBox();
        totalField.setSpacing(100);
        totalField.setAlignment(Pos.CENTER_LEFT);
        Label totalLabel = new Label("Total");
        totalLabel.getStyleClass().add("totalLabel");
        TextField totalInput = new TextField();
        totalInput.setEditable(false);
        totalInput.getStyleClass().add("totalInput");
        totalInput.setText(currencyFormat.format(totalPenjualan));
        totalField.getChildren().addAll(totalLabel, totalInput);

        addSecondaryFormField(secondaryFormGrid, 0, total -> totalInput.setText(currencyFormat.format(total)));
        tambahDetailTransaksiButton.setOnAction(e -> addSecondaryFormField(secondaryFormGrid,
                secondaryFormGrid.getRowCount(), total -> totalInput.setText(currencyFormat.format(total))));

        HBox totalBayarField = new HBox();
        totalBayarField.setSpacing(60);
        totalBayarField.setAlignment(Pos.CENTER_LEFT);
        Label totalBayarLabel = new Label("Total Bayar");
        totalBayarLabel.getStyleClass().add("totalBayarLabel");
        TextField totalBayarInput = new TextField();
        totalBayarInput.getStyleClass().add("totalBayarInput");
        // HBox.setHgrow(totalBayarInput, Priority.ALWAYS);
        totalBayarInput.textProperty().addListener((observable, oldValue, newValue) -> {
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
                    totalBayarInput.setText(formatted);
                    // Pindahkan kursor ke akhir teks
                    totalBayarInput.end();
                } catch (NumberFormatException e) {
                    totalBayarInput.setText(oldValue); // Kembalikan ke nilai lama jika parsing gagal
                }
            }
        });
        totalBayarField.getChildren().addAll(totalBayarLabel, totalBayarInput);

        secondaryForm.getChildren().addAll(secondaryFormHeader, secondaryFormGrid, tambahDetailTransaksiButton,
                totalField, totalBayarField);

        formBox.getChildren().addAll(primaryForm, secondaryForm);

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
            if (nomorFakturInput.getText().isEmpty()) {
                showAlert("Nomor Faktur harus diisi");
                return;
            }

            if (namaPelangganInput.getValue() == null || namaPelangganInput.getValue().isEmpty()) {
                showAlert("Nama Pelanggan harus dipilih");
                return;
            }

            if (tanggalInput.getValue() == null) {
                showAlert("Tanggal harus diisi");
                return;
            }

            if (totalBayarInput.getText().isEmpty()
                    || Double.parseDouble(convertToInteger(totalBayarInput.getText())) < 0) {
                showAlert("Total Bayar harus diisi dan lebih besar atau sama dengan 0");
                return;
            }
            System.out.println("Berhasil menyimpan data barang");
            try {
                salesService.setCustomerId(customerService.getCustomerIdByName(namaPelangganInput.getValue()));
                salesService.setUserId(1); // Asumsikan userId 1 untuk Admin, bisa diubah sesuai konteks
                salesService.setTransactionDate(tanggalInput.getValue());

                String status = totalPenjualan > Double.parseDouble(convertToInteger(totalBayarInput.getText()))
                        ? "BELUM_LUNAS"
                        : "LUNAS";
                salesService.setStatus(status); // Atur status default
                salesService.setNumberFactur(nomorFakturInput.getText());
                salesService.setTotalQuantity(totalKuantitas);
                salesService.setTotalSales(totalPenjualan);

                Double totalPembayaran = Double.parseDouble(convertToInteger(totalBayarInput.getText()));
                salesService.setTotalPayment(totalPembayaran);

                int saleId = storeSales(salesService);

                ProductService productService = new ProductService();
                if (saleId > 0) {
                    storeSalesDetail(saleId);

                    totalKuantitas = 0;
                    totalPenjualan = 0.0;
                    daftarDetailTransaksi = new ArrayList<>();
                    index(createStage);
                } else {
                    System.out.println("Gagal menambah data penjualan");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        contentFooterBox.setAlignment(Pos.CENTER_RIGHT);
        contentFooterBox.getChildren().addAll(backButton, submitButton);

        contentBox.getChildren().addAll(contentHeaderBox, formBox, contentFooterBox);

        borderPane.setLeft(sidebar);
        borderPane.setLeft(sidebar);
        borderPane.setCenter(contentBox);

        ScrollPane scrollPane = new ScrollPane(borderPane); // Tambahkan ScrollPane di sini
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 800, 600);
        createStage.setScene(scene);
        createStage.setFullScreen(true);
        createStage.setTitle("AjungStore - Create Penjualan");
        createStage.show();
    }

    public void edit(int salesId, Stage editStage) throws Exception {
        SalesService salesService = new SalesService();
        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT id, numberFactur, transactionDate, customerId, status, totalSales, totalPayment from sales WHERE id = ?")) {

            statement.setInt(1, salesId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                LocalDate transactionDate = resultSet.getDate("transactionDate").toLocalDate();
                String customerId = resultSet.getString("customerId");
                String numberFactur = resultSet.getString("numberFactur");
                double totalSales = resultSet.getDouble("totalSales");
                double totalPayment = resultSet.getDouble("totalPayment");

                salesService.setTransactionDate(transactionDate);
                salesService.setCustomerId(Integer.valueOf(customerId));
                salesService.setNumberFactur(numberFactur);
                salesService.setTotalSales(totalSales);
                salesService.setTotalPayment(totalPayment);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

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
        Barang barang = new Barang();
        Pelanggan pelanggan = new Pelanggan();
        Button navPenjualan = new Button("Penjualan");
        navPenjualan.getStyleClass().add("navPenjualan");
        navPenjualan.setOnAction(e -> {
            try {
                index(editStage);
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
        nomorFakturInput.setDisable(true);
        nomorFakturInput.setText(salesService.getNumberFactur());
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
        CustomerService customerService = new CustomerService();
        List<String> customerNames = customerService.getAllCustomerNames();
        namaPelangganInput.getItems().addAll(customerNames);
        namaPelangganInput.setValue(customerService.getCustomerNameById(salesService.getCustomerId()));
        namaPelangganInput.getStyleClass().add("namaPelangganInput");
        HBox.setHgrow(namaPelangganInput, Priority.ALWAYS);
        namaPelangganInput.prefWidthProperty().bind(primaryForm.widthProperty().subtract(120)); // 60 adalah spacing
                                                                                                // dari nomorFakturField
        namaPelangganField.getChildren().addAll(namaPelangganLabel, namaPelangganInput);

        HBox tanggalField = new HBox();
        tanggalField.setSpacing(75);
        tanggalField.setAlignment(Pos.CENTER_LEFT);
        Label tanggalLabel = new Label("Tanggal");
        tanggalLabel.getStyleClass().add("tanggalLabel");
        tanggalLabel.setMinWidth(Region.USE_PREF_SIZE);
        DatePicker tanggalInput = new DatePicker();
        tanggalInput.setValue(salesService.getTransactionDate());
        tanggalInput.getStyleClass().add("tanggalInput");
        HBox.setHgrow(tanggalInput, Priority.ALWAYS);
        tanggalInput.prefWidthProperty().bind(primaryForm.widthProperty().subtract(120)); // 60 adalah spacing dari
                                                                                          // tanggalField
        tanggalField.getChildren().addAll(tanggalLabel, tanggalInput);

        primaryForm.getChildren().addAll(nomorFakturField, namaPelangganField, tanggalField);

        VBox secondaryForm = new VBox();
        secondaryForm.setSpacing(20);
        secondaryForm.getStyleClass().add("secondaryForm");

        VBox secondaryFormHeader = new VBox();
        secondaryFormHeader.setAlignment(Pos.CENTER);
        Label secondaryFormTitle = new Label("Detail Transaksi");
        secondaryFormTitle.getStyleClass().add("secondaryFormTitle");
        secondaryFormHeader.getChildren().setAll(secondaryFormTitle);

        GridPane secondaryFormGrid = new GridPane();
        secondaryFormGrid.setHgap(10);
        secondaryFormGrid.setVgap(10);

        Button tambahDetailTransaksiButton = new Button("Tambah Barang");
        tambahDetailTransaksiButton.getStyleClass().add("tambahDetailTransaksiButton");
        tambahDetailTransaksiButton.setTextFill(Color.WHITE);

        HBox totalField = new HBox();
        totalField.setSpacing(100);
        totalField.setAlignment(Pos.CENTER_LEFT);
        Label totalLabel = new Label("Total");
        totalLabel.getStyleClass().add("totalLabel");
        TextField totalInput = new TextField();
        totalPenjualan = salesService.getTotalSales();
        totalInput.setEditable(false);
        totalInput.getStyleClass().add("totalInput");
        totalInput.setText(currencyFormat.format(totalPenjualan));
        totalField.getChildren().addAll(totalLabel, totalInput);

        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement detailStatement = connection.prepareStatement(
                        "SELECT productId, price, quantity from sales_details where salesId = ?")) {

            detailStatement.setInt(1, salesId);
            ResultSet detailResultSet = detailStatement.executeQuery();

            int rowIndex = 0;
            while (detailResultSet.next()) {
                int productId = detailResultSet.getInt("productId");
                double price = detailResultSet.getDouble("price");
                int quantity = detailResultSet.getInt("quantity");
                addSecondaryFormFieldOnEditPage(secondaryFormGrid, rowIndex,
                        total -> totalInput.setText(currencyFormat.format(total)), productId, price, quantity);
                rowIndex++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        tambahDetailTransaksiButton.setOnAction(e -> addSecondaryFormFieldOnEditPage(secondaryFormGrid,
                secondaryFormGrid.getRowCount(), total -> totalInput.setText(currencyFormat.format(total)), 0, 0.0, 0));

        HBox totalBayarField = new HBox();
        totalBayarField.setSpacing(60);
        totalBayarField.setAlignment(Pos.CENTER_LEFT);
        Label totalBayarLabel = new Label("Total Bayar");
        totalBayarLabel.getStyleClass().add("totalBayarLabel");
        TextField totalBayarInput = new TextField();
        totalBayarInput.setText(currencyFormat.format(salesService.getTotalPayment()));
        totalBayarInput.getStyleClass().add("totalBayarInput");
        totalBayarInput.textProperty().addListener((observable, oldValue, newValue) -> {
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
                    totalBayarInput.setText(formatted);
                    // Pindahkan kursor ke akhir teks
                    totalBayarInput.end();
                } catch (NumberFormatException e) {
                    totalBayarInput.setText(oldValue); // Kembalikan ke nilai lama jika parsing gagal
                }
            }
        });
        // HBox.setHgrow(totalBayarInput, Priority.ALWAYS);
        totalBayarField.getChildren().addAll(totalBayarLabel, totalBayarInput);

        secondaryForm.getChildren().addAll(secondaryFormHeader, secondaryFormGrid, tambahDetailTransaksiButton,
                totalField, totalBayarField);

        formBox.getChildren().addAll(primaryForm, secondaryForm);

        HBox contentFooterBox = new HBox();
        contentFooterBox.setSpacing(20);

        Button backButton = new Button("Kembali");
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction(e -> {
            try {
                totalPenjualan = 0.0;
                index(editStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button submitButton = new Button("Simpan");
        submitButton.getStyleClass().add("submitButton");
        submitButton.setTextFill(Color.WHITE);
        submitButton.setOnAction(e -> {
            if (nomorFakturInput.getText().isEmpty()) {
                showAlert("Nomor Faktur harus diisi");
                return;
            }

            if (namaPelangganInput.getValue() == null || namaPelangganInput.getValue().isEmpty()) {
                showAlert("Nama Pelanggan harus dipilih");
                return;
            }

            if (tanggalInput.getValue() == null) {
                showAlert("Tanggal harus diisi");
                return;
            }

            if (totalBayarInput.getText().isEmpty()
                    || Double.parseDouble(convertToInteger(totalBayarInput.getText())) < 0) {
                showAlert("Total Bayar harus diisi dan lebih besar atau sama dengan 0");
                return;
            }
            System.out.println("Berhasil menyimpan data barang");
            try {
                salesService.setIdSale(salesId);
                salesService.setCustomerId(customerService.getCustomerIdByName(namaPelangganInput.getValue()));
                salesService.setUserId(1); // Asumsikan userId 1 untuk Admin, bisa diubah sesuai konteks
                salesService.setTransactionDate(tanggalInput.getValue());

                String status = totalPenjualan > Double.parseDouble(convertToInteger(totalBayarInput.getText()))
                        ? "BELUM_LUNAS"
                        : "LUNAS";
                salesService.setStatus(status); // Atur status default
                salesService.setNumberFactur(nomorFakturInput.getText());
                salesService.setTotalQuantity(totalKuantitas);
                salesService.setTotalSales(totalPenjualan);

                Double totalPembayaran = totalPenjualan > Double
                        .parseDouble(convertToInteger(totalBayarInput.getText()))
                                ? Double.parseDouble(convertToInteger(totalBayarInput.getText()))
                                : totalPenjualan;
                salesService.setTotalPayment(totalPembayaran);

                int saleId = editSales(salesService);

                if (saleId > 0) {
                    storeSalesDetail(saleId);

                    totalKuantitas = 0;
                    totalPenjualan = 0.0;
                    daftarDetailTransaksi = new ArrayList<>();
                    index(editStage);
                } else {
                    System.out.println("Gagal menambah data penjualan");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        totalPenjualan = salesService.getTotalSales();
        totalInput.setText(currencyFormat.format(totalPenjualan));

        contentFooterBox.setAlignment(Pos.CENTER_RIGHT);
        contentFooterBox.getChildren().addAll(backButton, submitButton);

        contentBox.getChildren().addAll(contentHeaderBox, formBox, contentFooterBox);

        borderPane.setLeft(sidebar);
        borderPane.setCenter(contentBox);
        ScrollPane scrollPane = new ScrollPane(borderPane); // Tambahkan ScrollPane di sini
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 800, 600);
        editStage.setScene(scene);
        editStage.setFullScreen(true);
        editStage.setTitle("AjungStore - Edit Penjualan");
        editStage.show();
    }

    private void addSecondaryFormField(GridPane grid, int rowIndex, Consumer<Double> totalUpdater) {
        ProductService productService = new ProductService();
        VBox namaBarangField = new VBox();
        namaBarangField.setSpacing(10);
        Label namaBarangLabel = new Label("Nama Barang");
        namaBarangLabel.getStyleClass().add("namaBarangLabel");
        ComboBox<String> namaBarangInput = new ComboBox<>();
        namaBarangInput.setMinWidth(600);
        namaBarangInput.setMinHeight(20);
        List<String> productNames = productService.getAllProductName();
        namaBarangInput.getItems().addAll(productNames);
        namaBarangInput.getStyleClass().add("namaBarangInput");
        namaBarangField.getChildren().addAll(namaBarangLabel, namaBarangInput);

        VBox hargaSatuanField = new VBox();
        hargaSatuanField.setSpacing(10);
        Label hargaSatuanLabel = new Label("Harga Satuan");
        hargaSatuanLabel.getStyleClass().add("hargaSatuanLabel");
        TextField hargaSatuanInput = new TextField();
        hargaSatuanInput.setMinWidth(200);
        hargaSatuanInput.setMinHeight(20);
        hargaSatuanInput.getStyleClass().add("hargaSatuanInput");
        hargaSatuanField.getChildren().addAll(hargaSatuanLabel, hargaSatuanInput);
        hargaSatuanInput.setEditable(false); // Menonaktifkan input

        // Tambahkan listener ke ComboBox untuk mengupdate harga satuan
        namaBarangInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hargaSatuanInput.setText(currencyFormat.format(productService.getProductPrice(newValue)));
            }
        });

        VBox kuantitasField = new VBox();
        kuantitasField.setSpacing(10);
        Label kuantitasLabel = new Label("Kuantitas");
        kuantitasLabel.getStyleClass().add("kuantitasLabel");
        TextField kuantitasInput = new TextField();
        kuantitasInput.setMinWidth(100);
        kuantitasInput.setMinHeight(20);
        kuantitasInput.getStyleClass().add("kuantitasInput");

        // UnaryOperator untuk memfilter input agar hanya angka yang diterima
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };

        // Membuat TextFormatter dengan IntegerStringConverter untuk mengonversi ke
        // Integer
        TextFormatter<Integer> textFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, filter);
        kuantitasInput.setTextFormatter(textFormatter);

        kuantitasField.getChildren().addAll(kuantitasLabel, kuantitasInput);

        VBox subtotalField = new VBox();
        subtotalField.setSpacing(10);
        Label subtotalLabel = new Label("Subtotal");
        subtotalLabel.getStyleClass().add("subtotalLabel");
        TextField subtotalInput = new TextField();
        subtotalInput.setMinWidth(150);
        subtotalInput.setMinHeight(20);
        subtotalInput.setEditable(false);
        subtotalInput.getStyleClass().add("subtotalInput");
        subtotalField.getChildren().addAll(subtotalLabel, subtotalInput);

        // Tambahkan listener ke TextField kuantitasInput
        kuantitasInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    int kuantitas = Integer.parseInt(newValue);
                    double hargaSatuan = currencyFormat.parse(hargaSatuanInput.getText()).doubleValue();
                    double subtotal = kuantitas * hargaSatuan;

                    // Cari detail transaksi yang sudah ada untuk produk yang sama
                    Optional<SalesDetailService> existingDetail = daftarDetailTransaksi.stream()
                            .filter(detail -> detail.getProductId() == productService
                                    .getProductIdByName(namaBarangInput.getValue()))
                            .findFirst();

                    if (existingDetail.isPresent()) {
                        // Update kuantitas dan subtotal dari detail transaksi yang sudah ada
                        SalesDetailService detail = existingDetail.get();
                        double oldSubtotal = detail.getQuantity() * hargaSatuan;
                        detail.setQuantity(kuantitas);
                        detail.setPrice(hargaSatuan);
                        double diff = subtotal - oldSubtotal;
                        totalPenjualan += diff;
                        totalKuantitas += kuantitas;
                    } else {
                        // Tambahkan detail transaksi baru ke daftarDetailTransaksi
                        SalesDetailService detail = new SalesDetailService();
                        detail.setProductId(productService.getProductIdByName(namaBarangInput.getValue()));
                        detail.setPrice(hargaSatuan);
                        detail.setQuantity(kuantitas);
                        daftarDetailTransaksi.add(detail);
                        totalPenjualan += subtotal;
                        totalKuantitas += kuantitas;
                    }

                    subtotalInput.setText(currencyFormat.format(subtotal));
                    totalUpdater.accept(totalPenjualan);
                } catch (ParseException e) {
                    subtotalInput.setText("0,00");
                }
            } else {
                subtotalInput.setText("");
            }
        });

        VBox hapusDetailButtonField = new VBox();
        hapusDetailButtonField.setSpacing(10);
        Label hapusDetailButtonEmptyLabel = new Label(" ");
        Button hapusDetailButton = new Button("-");
        hapusDetailButton.getStyleClass().add("hapusDetailButton");
        hapusDetailButton.setAlignment(Pos.CENTER);
        hapusDetailButtonField.getChildren().addAll(hapusDetailButtonEmptyLabel, hapusDetailButton);
        hapusDetailButton.setOnAction(e -> {
            // Cari detail transaksi yang sesuai untuk produk yang akan dihapus
            int productIdToDelete = productService.getProductIdByName(namaBarangInput.getValue());
            Optional<SalesDetailService> detailToDelete = daftarDetailTransaksi.stream()
                    .filter(detail -> detail.getProductId() == productIdToDelete)
                    .findFirst();

            if (detailToDelete.isPresent()) {
                // Hapus detail transaksi dari daftarDetailTransaksi
                SalesDetailService detail = detailToDelete.get();
                double subtotalToDelete = detail.getQuantity() * detail.getPrice();
                totalPenjualan -= subtotalToDelete;
                totalKuantitas -= detail.getQuantity();
                daftarDetailTransaksi.remove(detail);
                totalUpdater.accept(totalPenjualan); // Update nilai totalPenjualan di form utama
            }

            // Remove the row from the grid
            grid.getChildren().removeAll(namaBarangField, hargaSatuanField, kuantitasField, subtotalField,
                    hapusDetailButtonField);
        });

        grid.addRow(rowIndex, namaBarangField, hargaSatuanField, kuantitasField, subtotalField, hapusDetailButtonField);
    }

    private void addSecondaryFormFieldOnEditPage(GridPane grid, int rowIndex, Consumer<Double> totalUpdater,
            int productId, double price, int quantity) {

        ProductService productService = new ProductService();
        VBox namaBarangField = new VBox();
        namaBarangField.setSpacing(10);
        Label namaBarangLabel = new Label("Nama Barang");
        namaBarangLabel.getStyleClass().add("namaBarangLabel");
        ComboBox<String> namaBarangInput = new ComboBox<>();
        namaBarangInput.setMinWidth(600);
        namaBarangInput.setMinHeight(20);
        List<String> productNames = productService.getAllProductName();
        namaBarangInput.getItems().addAll(productNames);
        namaBarangInput.getStyleClass().add("namaBarangInput");
        namaBarangField.getChildren().addAll(namaBarangLabel, namaBarangInput);

        VBox hargaSatuanField = new VBox();
        hargaSatuanField.setSpacing(10);
        Label hargaSatuanLabel = new Label("Harga Satuan");
        hargaSatuanLabel.getStyleClass().add("hargaSatuanLabel");
        TextField hargaSatuanInput = new TextField();
        hargaSatuanInput.setMinWidth(200);
        hargaSatuanInput.setMinHeight(20);
        hargaSatuanInput.getStyleClass().add("hargaSatuanInput");
        hargaSatuanField.getChildren().addAll(hargaSatuanLabel, hargaSatuanInput);
        hargaSatuanInput.setEditable(false); // Menonaktifkan input

        // Tambahkan listener ke ComboBox untuk mengupdate harga satuan
        namaBarangInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hargaSatuanInput.setText(currencyFormat.format(productService.getProductPrice(newValue)));
            }
        });

        VBox kuantitasField = new VBox();
        kuantitasField.setSpacing(10);
        Label kuantitasLabel = new Label("Kuantitas");
        kuantitasLabel.getStyleClass().add("kuantitasLabel");
        TextField kuantitasInput = new TextField();
        kuantitasInput.setMinWidth(100);
        kuantitasInput.setMinHeight(20);
        kuantitasInput.getStyleClass().add("kuantitasInput");

        // UnaryOperator untuk memfilter input agar hanya angka yang diterima
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };

        // Membuat TextFormatter dengan IntegerStringConverter untuk mengonversi ke
        // Integer
        TextFormatter<Integer> textFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, filter);
        kuantitasInput.setTextFormatter(textFormatter);

        kuantitasField.getChildren().addAll(kuantitasLabel, kuantitasInput);

        VBox subtotalField = new VBox();
        subtotalField.setSpacing(10);
        Label subtotalLabel = new Label("Subtotal");
        subtotalLabel.getStyleClass().add("subtotalLabel");
        TextField subtotalInput = new TextField();
        subtotalInput.setMinWidth(150);
        subtotalInput.setMinHeight(20);
        subtotalInput.setEditable(false);
        subtotalInput.getStyleClass().add("subtotalInput");
        subtotalField.getChildren().addAll(subtotalLabel, subtotalInput);

        // Tambahkan listener ke TextField kuantitasInput
        kuantitasInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    int kuantitas = Integer.parseInt(newValue);
                    double hargaSatuan = currencyFormat.parse(hargaSatuanInput.getText()).doubleValue();
                    double subtotal = kuantitas * hargaSatuan;

                    // Cari detail transaksi yang sudah ada untuk produk yang sama
                    Optional<SalesDetailService> existingDetail = daftarDetailTransaksi.stream()
                            .filter(detail -> detail.getProductId() == productService
                                    .getProductIdByName(namaBarangInput.getValue()))
                            .findFirst();

                    if (existingDetail.isPresent()) {
                        // Update kuantitas dan subtotal dari detail transaksi yang sudah ada
                        SalesDetailService detail = existingDetail.get();
                        double oldSubtotal = detail.getQuantity() * hargaSatuan;
                        detail.setQuantity(kuantitas);
                        detail.setPrice(hargaSatuan);
                        double diff = subtotal - oldSubtotal;
                        totalPenjualan += diff;
                        totalKuantitas += kuantitas;
                    } else {
                        // Tambahkan detail transaksi baru ke daftarDetailTransaksi
                        SalesDetailService detail = new SalesDetailService();
                        detail.setProductId(productService.getProductIdByName(namaBarangInput.getValue()));
                        detail.setPrice(hargaSatuan);
                        detail.setQuantity(kuantitas);
                        daftarDetailTransaksi.add(detail);
                        totalPenjualan += subtotal;
                        totalKuantitas += kuantitas;
                    }

                    subtotalInput.setText(currencyFormat.format(subtotal));
                    totalUpdater.accept(totalPenjualan);
                } catch (ParseException e) {
                    subtotalInput.setText("0,00");
                }
            } else {
                subtotalInput.setText("");
            }
        });

        VBox hapusDetailButtonField = new VBox();
        hapusDetailButtonField.setSpacing(10);
        Label hapusDetailButtonEmptyLabel = new Label(" ");
        Button hapusDetailButton = new Button("-");
        hapusDetailButton.getStyleClass().add("hapusDetailButton");
        hapusDetailButton.setAlignment(Pos.CENTER);
        hapusDetailButtonField.getChildren().addAll(hapusDetailButtonEmptyLabel, hapusDetailButton);
        hapusDetailButton.setOnAction(e -> {
            // Cari detail transaksi yang sesuai untuk produk yang akan dihapus
            int productIdToDelete = productService.getProductIdByName(namaBarangInput.getValue());
            Optional<SalesDetailService> detailToDelete = daftarDetailTransaksi.stream()
                    .filter(detail -> detail.getProductId() == productIdToDelete)
                    .findFirst();

            if (detailToDelete.isPresent()) {
                // Hapus detail transaksi dari daftarDetailTransaksi
                SalesDetailService detail = detailToDelete.get();
                double subtotalToDelete = detail.getQuantity() * detail.getPrice();
                totalPenjualan -= subtotalToDelete;
                totalKuantitas -= detail.getQuantity();
                daftarDetailTransaksi.remove(detail);
                totalUpdater.accept(totalPenjualan); // Update nilai totalPenjualan di form utama
            }

            // Remove the row from the grid
            grid.getChildren().removeAll(namaBarangField, hargaSatuanField, kuantitasField, subtotalField,
                    hapusDetailButtonField);
        });

        if (productId != 0 && price != 0.0 && quantity != 0) {
            namaBarangInput.setValue(productService.getProductNameById(productId));
            hargaSatuanInput.setText(currencyFormat.format(price));
            kuantitasInput.setText(String.valueOf(quantity));
            subtotalInput.setText(currencyFormat.format(quantity * price));
        }

        grid.addRow(rowIndex, namaBarangField, hargaSatuanField, kuantitasField, subtotalField, hapusDetailButtonField);
    }

}
