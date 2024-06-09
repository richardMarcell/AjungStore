package ajungstore;

import java.time.LocalDate;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;

public class Hutang {
    private String namaPelanggan;
    private LocalDate tanggalPelanggan;
    private double totalHutang;

    public Hutang(String namaPelanggan, LocalDate tanggalPelanggan, double totalHutang) {
        this.namaPelanggan = namaPelanggan;
        this.tanggalPelanggan = tanggalPelanggan;
        this.totalHutang = totalHutang;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public LocalDate getTanggalPelanggan() {
        return tanggalPelanggan;
    }

    public void setTanggalPelanggan(LocalDate tanggalPelanggan) {
        this.tanggalPelanggan = tanggalPelanggan;
    }

    public double getTotalHutang() {
        return totalHutang;
    }

    public void setTotalHutang(double totalHutang) {
        this.totalHutang = totalHutang;
    }

    public void index(Stage indexStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/indexHutang.css").toExternalForm();
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

        Button navHutang = new Button("Hutang");
        navHutang.getStyleClass().add("navHutang");
        navHutang.setOnAction(e -> {
            try {
                index(indexStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public class HutangPage {
        private Stage stage;
        private ObservableList<Hutang> hutangList;
    
        public HutangPage(Stage stage) {
            this.stage = stage;
            hutangList = FXCollections.observableArrayList();
        }
    
        public void show() {
            TableView<Hutang> table = new TableView<>();
    
            TableColumn<Hutang, String> namaPelangganColumn = new TableColumn<>("Nama Pelanggan");
            namaPelangganColumn.setCellValueFactory(new PropertyValueFactory<>("namaPelanggan"));
    
            TableColumn<Hutang, LocalDate> tanggalPelangganColumn = new TableColumn<>("Tanggal Pelanggan");
            tanggalPelangganColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalPelanggan"));
    
            TableColumn<Hutang, Double> totalHutangColumn = new TableColumn<>("Total Hutang");
            totalHutangColumn.setCellValueFactory(new PropertyValueFactory<>("totalHutang"));
    
            table.getColumns().addAll(namaPelangganColumn, tanggalPelangganColumn, totalHutangColumn);
            table.setItems(hutangList);
    
            Button logButton = new Button("Tampilkan Log Pelunasan Hutang");
            logButton.setOnAction(e -> tampilkanLogPelunasanHutang());
    
            VBox vbox = new VBox(table, logButton);
            Scene scene = new Scene(vbox, 800, 600);
    
            stage.setScene(scene);
            stage.setTitle("Halaman Hutang");
            stage.show();
        }
    
        private void tampilkanLogPelunasanHutang() {
            // Implementasi untuk menampilkan log pelunasan hutang
            System.out.println("Log pelunasan hutang ditampilkan.");
        }
    
        public void addHutang(Hutang hutang) {
            hutangList.add(hutang);
        }
    }
}


