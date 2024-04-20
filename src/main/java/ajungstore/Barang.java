package ajungstore;

import java.util.Arrays;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Stage;

public class Barang {
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

        // Menambahkan kolom-kolom untuk tabel penjualan (no, nama customer, status, action)
        TableColumn<ObservableList<String>, String> colNo = new TableColumn<>("No");
        TableColumn<ObservableList<String>, String> colNamaCustomer = new TableColumn<>("Nama Barang");
        TableColumn<ObservableList<String>, String> colStatus = new TableColumn<>("Harga");
        TableColumn<ObservableList<String>, String> colAction = new TableColumn<>("Action");

        // Mengatur lebar kolom dengan persentase dari lebar total tabel
        colNo.prefWidthProperty().bind(table.widthProperty().multiply(0.1)); // 10% dari lebar tabel
        colNamaCustomer.prefWidthProperty().bind(table.widthProperty().multiply(0.4)); // 40% dari lebar tabel
        colStatus.prefWidthProperty().bind(table.widthProperty().multiply(0.3)); // 30% dari lebar tabel
        colAction.prefWidthProperty().bind(table.widthProperty().multiply(0.2)); // 20% dari lebar tabel

        // Mengatur data kolom dari sumber data
        colNo.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(0)));
        colNamaCustomer.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(1)));
        colStatus.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(2)));
        colAction.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(""));

        table.getColumns().addAll(colNo, colNamaCustomer, colStatus, colAction);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        String[][] barangData = {
            {"1", "Chitato", "Rp13.000"},
            {"2", "Rinso", "Rp14.000"},
            {"3", "Detol", "Rp20.000"},
        };

        for (String[] row : barangData) {
            ObservableList<String> rowData = FXCollections.observableArrayList();
            rowData.addAll(Arrays.asList(row));
            data.add(rowData);
        }

        colAction.setCellFactory(param -> new TableCell<ObservableList<String>, String>() {
            final Button editButton = new Button("Edit");
            final Button deleteButton = new Button("Hapus");

            {
                // Handle edit button action
                editButton.setOnAction(event -> {
                    try {
                       edit(indexStage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                // Handle delete button action
                deleteButton.setOnAction(event -> {
                    System.out.println("Anda telah menghapus barang ini");
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

        // Set the table data
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
        createStage.setTitle("AjungStore - Create Barang");
        createStage.show();
    }


    public void edit(Stage editStage) throws Exception {
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
        editStage.setTitle("AjungStore - Edit Barang");
        editStage.show();
    }


}

