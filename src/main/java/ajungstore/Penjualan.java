package ajungstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

    private boolean showDeleteConfirmationDialog() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Penghapusan");
        alert.setHeaderText(null);
        alert.setContentText("Apakah Anda yakin ingin menghapus data penjualan ini?");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
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

        VBox statHutang = new VBox();
        statHutang.setAlignment(Pos.CENTER);
        statHutang.getStyleClass().add("statHutang");
        HBox.setHgrow(statHutang, Priority.ALWAYS);

        Label statHutangHeader = new Label("Hutang");
        statHutangHeader.getStyleClass().add("statHutangHeader");

        Label statHutangContent = new Label("10"); // Anda bisa mengganti ini dengan data dari database
        statHutangContent.getStyleClass().add("statHutangContent");

        statHutang.getChildren().addAll(statHutangHeader, statHutangContent);

        VBox statPiutang = new VBox();
        statPiutang.setAlignment(Pos.CENTER);
        statPiutang.getStyleClass().add("statPiutang");
        HBox.setHgrow(statPiutang, Priority.ALWAYS);

        Label statPiutangHeader = new Label("Piutang");
        statPiutangHeader.getStyleClass().add("statPiutangHeader");

        Label statPiutangContent = new Label("10"); // Anda bisa mengganti ini dengan data dari database
        statPiutangContent.getStyleClass().add("statPiutangContent");

        statPiutang.getChildren().addAll(statPiutangHeader, statPiutangContent);

        quickStats.getChildren().setAll(statPenjualan, statHutang, statPiutang);

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
        TableColumn<ObservableList<String>, String> colNamaCustomer = new TableColumn<>("Nama Customer");
        TableColumn<ObservableList<String>, String> colStatus = new TableColumn<>("Status");
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

        try (Connection connection = Dbconnect.getConnect();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT sales.id, customers.name, sales.status FROM sales LEFT JOIN customers ON sales.customerId = customers.id")) {

            ResultSet resultSet = statement.executeQuery();

            int no = 1;
            while (resultSet.next()) {
                ObservableList<String> rowData = FXCollections.observableArrayList();
                rowData.add(String.valueOf(no++));
                rowData.add(resultSet.getString("name"));
                rowData.add(resultSet.getString("status"));
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
                        edit(indexStage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                deleteButton.setOnAction(event -> {
                    if (showDeleteConfirmationDialog()) {
                        ObservableList<String> rowData = getTableView().getItems().get(getIndex());
                        int salesId = Integer.parseInt(rowData.get(0)); // Assuming the sales ID is stored in the first
                                                                        // column
                        deleteSalesRecord(salesId);

                        // Refresh the table data after deletion
                        getTableView().getItems().remove(rowData);
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

        // Set up the initial rows for the secondary form
        addSecondaryFormField(secondaryFormGrid, 0);

        Button tambahDetailTransaksiButton = new Button("Tambah Barang");
        tambahDetailTransaksiButton.getStyleClass().add("tambahDetailTransaksiButton");
        tambahDetailTransaksiButton.setTextFill(Color.WHITE);
        tambahDetailTransaksiButton
                .setOnAction(e -> addSecondaryFormField(secondaryFormGrid, secondaryFormGrid.getRowCount()));

        HBox totalField = new HBox();
        totalField.setSpacing(100);
        totalField.setAlignment(Pos.CENTER_LEFT);
        Label totalLabel = new Label("Total");
        totalLabel.getStyleClass().add("totalLabel");
        TextField totalInput = new TextField();
        totalInput.getStyleClass().add("totalInput");
        // HBox.setHgrow(totalInput, Priority.ALWAYS);
        totalField.getChildren().addAll(totalLabel, totalInput);

        HBox totalBayarField = new HBox();
        totalBayarField.setSpacing(60);
        totalBayarField.setAlignment(Pos.CENTER_LEFT);
        Label totalBayarLabel = new Label("Total Bayar");
        totalBayarLabel.getStyleClass().add("totalBayarLabel");
        TextField totalBayarInput = new TextField();
        totalBayarInput.getStyleClass().add("totalBayarInput");
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
                index(createStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button submitButton = new Button("Simpan");
        submitButton.getStyleClass().add("submitButton");
        submitButton.setTextFill(Color.WHITE);
        submitButton.setOnAction(e -> {
            System.out.println("Berhasil menyimpan data barang");
            try {
                index(createStage);
            } catch (Exception ex) {
                ex.printStackTrace();
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
        createStage.setTitle("AjungStore - Create Penjualan");
        createStage.show();
    }

    public void edit(Stage editStage) throws Exception {
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

        Label contentHeaderTitle = new Label("Edit Penjualan");
        contentHeaderTitle.getStyleClass().add("contentHeaderTitle");

        Label contentHeaderDescription = new Label("Mengedit penjualan yang terjadi di Toko Ajung");
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

        // Set up the initial rows for the secondary form
        addSecondaryFormField(secondaryFormGrid, 0);

        Button tambahDetailTransaksiButton = new Button("Tambah Barang");
        tambahDetailTransaksiButton.getStyleClass().add("tambahDetailTransaksiButton");
        tambahDetailTransaksiButton.setTextFill(Color.WHITE);
        tambahDetailTransaksiButton
                .setOnAction(e -> addSecondaryFormField(secondaryFormGrid, secondaryFormGrid.getRowCount()));

        HBox totalField = new HBox();
        totalField.setSpacing(100);
        totalField.setAlignment(Pos.CENTER_LEFT);
        Label totalLabel = new Label("Total");
        totalLabel.getStyleClass().add("totalLabel");
        TextField totalInput = new TextField();
        totalInput.getStyleClass().add("totalInput");
        // HBox.setHgrow(totalInput, Priority.ALWAYS);
        totalField.getChildren().addAll(totalLabel, totalInput);

        HBox totalBayarField = new HBox();
        totalBayarField.setSpacing(60);
        totalBayarField.setAlignment(Pos.CENTER_LEFT);
        Label totalBayarLabel = new Label("Total Bayar");
        totalBayarLabel.getStyleClass().add("totalBayarLabel");
        TextField totalBayarInput = new TextField();
        totalBayarInput.getStyleClass().add("totalBayarInput");
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
                index(editStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button submitButton = new Button("Simpan");
        submitButton.getStyleClass().add("submitButton");
        submitButton.setTextFill(Color.WHITE);
        submitButton.setOnAction(e -> {
            System.out.println("Berhasil menyimpan data barang");
            try {
                index(editStage);
            } catch (Exception ex) {
                ex.printStackTrace();
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
        editStage.setTitle("AjungStore - Edit Penjualan");
        editStage.show();
    }

    private void addSecondaryFormField(GridPane grid, int rowIndex) {
        VBox namaBarangField = new VBox();
        namaBarangField.setSpacing(10);
        Label namaBarangLabel = new Label("Nama Barang");
        namaBarangLabel.getStyleClass().add("namaBarangLabel");
        ComboBox<String> namaBarangInput = new ComboBox<>();
        namaBarangInput.setMinWidth(600);
        namaBarangInput.setMinHeight(20);
        namaBarangInput.getItems().addAll("Pepsodent", "Rinso", "Blueband");
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

        VBox kuantitasField = new VBox();
        kuantitasField.setSpacing(10);
        Label kuantitasLabel = new Label("Kuantitas");
        kuantitasLabel.getStyleClass().add("kuantitasLabel");
        TextField kuantitasInput = new TextField();
        kuantitasInput.setMinWidth(100);
        kuantitasInput.setMinHeight(20);
        kuantitasInput.getStyleClass().add("kuantitasInput");
        kuantitasField.getChildren().addAll(kuantitasLabel, kuantitasInput);

        VBox subtotalField = new VBox();
        subtotalField.setSpacing(10);
        Label subtotalLabel = new Label("Subtotal");
        subtotalLabel.getStyleClass().add("subtotalLabel");
        TextField subtotalInput = new TextField();
        subtotalInput.setMinWidth(150);
        subtotalInput.setMinHeight(20);
        subtotalInput.getStyleClass().add("subtotalInput");
        subtotalField.getChildren().addAll(subtotalLabel, subtotalInput);

        VBox hapusDetailButtonField = new VBox();
        hapusDetailButtonField.setSpacing(10);
        Label hapusDetailButtonEmptyLabel = new Label(" ");
        Button hapusDetailButton = new Button("-");
        hapusDetailButton.getStyleClass().add("hapusDetailButton");
        hapusDetailButton.setAlignment(Pos.CENTER);
        hapusDetailButtonField.getChildren().addAll(hapusDetailButtonEmptyLabel, hapusDetailButton);
        hapusDetailButton.setOnAction(e -> {
            // Remove the row from the grid
            grid.getChildren().removeAll(namaBarangField, hargaSatuanField, kuantitasField, subtotalField,
                    hapusDetailButtonField);
        });

        grid.addRow(rowIndex, namaBarangField, hargaSatuanField, kuantitasField, subtotalField, hapusDetailButtonField);
    }

}
