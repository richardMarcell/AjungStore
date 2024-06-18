package ajungstore;

import java.io.File;
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

import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

public class Piutang {

    public static String convertToInteger(String value) {
        // Hapus karakter non-digit dari string
        String cleanedValue = value.replaceAll("[^\\d]", "");

        // Konversi string menjadi integer
        String result = cleanedValue;
        return result;
    }

    private double convertCurrencyStringToDouble(String currency) {
        try {
            // Remove Rp, dots and replace comma with dot
            String cleanString = currency.replace("Rp", "").replace(".", "").replace(",", ".");
            return Double.parseDouble(cleanString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private double totalPiutang = 0.0;
    private int totalKuantitas = 0;
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private String generateReportHTML(LocalDate startDate, LocalDate endDate,
            ObservableList<ObservableList<String>> data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String start = startDate.format(formatter);
        String end = endDate.format(formatter);

        // Start building the HTML content
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
                .append("body { font-family: Arial, sans-serif; padding-right: 80px;}")
                .append("table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }")
                .append("th, td { border: 1px solid #dddddd; padding: 8px; text-align: left; font-size: 12px; }")
                .append("th { background-color: #f2f2f2; }")
                .append("tr:nth-child(even) { background-color: #f9f9f9; }")
                .append("h1, h2, h3 { text-align: center; }")
                .append("</style></head><body>");

        // Report header
        html.append("<h1 style='font-size: 12px;'>Laporan Piutang Toko Ajung</h1>");
        html.append("<h2 style='font-size: 10px;'>Periode ").append(start).append(" s/d ").append(end).append("</h2>");

        // Table headers
        html.append("<table>")
                .append("<tr>")
                .append("<th>No</th>")
                .append("<th>Tanggal</th>")
                .append("<th>Nama Pelanggan</th>")
                .append("<th>Status</th>")
                .append("<th>Total Piutang</th>")
                .append("</tr>");

        // Table data rows
        double totalPiutang = 0;
        int columnTotalPiutangIndex = 4; // Indeks kolom untuk total piutang
        for (ObservableList<String> row : data) {
            html.append("<tr>");
            for (int i = 0; i < row.size(); i++) {
                html.append("<td>").append(row.get(i)).append("</td>");
                if (i == columnTotalPiutangIndex) {
                    totalPiutang += convertCurrencyStringToDouble(row.get(i));
                }
            }
            html.append("</tr>");
        }

        // Total piutang footer
        html.append("</table>");
        html.append("<h3 style='text-align:left; font-size:12px;'>Total Piutang: ")
                .append(currencyFormat.format(totalPiutang)).append("</h3>");

        // End of HTML content
        html.append("</body></html>");

        return html.toString();
    }

    private void printReport(Stage stage, LocalDate startDate, LocalDate endDate,
            ObservableList<ObservableList<String>> data) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        String reportHTML = generateReportHTML(startDate, endDate, data);
        webEngine.loadContent(reportHTML);

        webEngine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                PrinterJob job = PrinterJob.createPrinterJob();
                if (job != null) {
                    // Configure job settings
                    job.getJobSettings().setJobName("Laporan Piutang Toko Ajung");

                    // Set page layout to landscape
                    PageLayout pageLayout = job.getPrinter().createPageLayout(Paper.A4, PageOrientation.LANDSCAPE,
                            Printer.MarginType.DEFAULT);
                    job.getJobSettings().setPageLayout(pageLayout);

                    boolean success = job.printPage(webView);
                    if (success) {
                        job.endJob();
                    }
                }
            }
        });
    }

    public void index(Stage indexStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        String css = this.getClass().getResource("styles/indexPiutang.css").toExternalForm();
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
                pelanggan.index(indexStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button navPiutang = new Button("Piutang");
        navPiutang.getStyleClass().add("navPiutang");
        navPiutang.setOnAction(e -> {
            try {
                index(indexStage);
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

        Label contentHeaderTitle = new Label("Daftar Piutang");
        contentHeaderTitle.getStyleClass().add("contentHeaderTitle");

        Label contentHeaderDescription = new Label("Daftar Piutang yang Dimiliki Toko Ajung");
        contentHeaderDescription.getStyleClass().add("contentHeaderDescription");

        contentHeaderBox.getChildren().addAll(contentHeaderTitle, contentHeaderDescription);

        VBox tableBox = new VBox();
        tableBox.setSpacing(10);

        HBox filterBox = new HBox();
        filterBox.setSpacing(10);

        DatePicker startDate = new DatePicker(LocalDate.now());
        Label untilLabel = new Label("s/d");
        DatePicker endDate = new DatePicker(LocalDate.now());
        Button filterbutton = new Button("Filter");
        filterBox.getChildren().addAll(startDate, untilLabel, endDate, filterbutton);

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);

        Button buttonCetakLaporan = new Button("Cetak");
        buttonCetakLaporan.getStyleClass().add("buttonCetakLaporan");
        buttonCetakLaporan.setAlignment(Pos.CENTER);
        buttonCetakLaporan.setTextFill(Color.WHITE);
        buttonCetakLaporan.setMinWidth(150);

        buttonBox.getChildren().setAll(buttonCetakLaporan);

        TableView<ObservableList<String>> table = new TableView<>();

        TableColumn<ObservableList<String>, String> colNo = new TableColumn<>("No");
        TableColumn<ObservableList<String>, String> colTanggalTransaksi = new TableColumn<>("Tanggal");
        TableColumn<ObservableList<String>, String> colNamaCustomer = new TableColumn<>("Nama Customer");
        TableColumn<ObservableList<String>, String> colStatus = new TableColumn<>("Status");
        TableColumn<ObservableList<String>, String> colTotalPiutang = new TableColumn<>("Total Piutang");

        colNo.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        colTanggalTransaksi.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        colNamaCustomer.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        colStatus.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        colTotalPiutang.prefWidthProperty().bind(table.widthProperty().multiply(0.2));

        colNo.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(0)));
        colTanggalTransaksi.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(1)));
        colNamaCustomer.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(2)));
        colStatus.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(3)));
        colTotalPiutang.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(4)));

        table.getColumns().addAll(colNo, colTanggalTransaksi, colNamaCustomer, colStatus, colTotalPiutang);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        filterbutton.setOnAction(e -> {
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();

            data.clear(); // Clear existing data

            try (Connection connection = Dbconnect.getConnect();
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT sales.id, sales.transactionDate, customers.name, sales.status, sales.totalSales, sales.totalPayment "
                                    +
                                    "FROM sales " +
                                    "LEFT JOIN customers ON sales.customerId = customers.id " +
                                    "WHERE sales.transactionDate BETWEEN ? AND ? " +
                                    "AND sales.isHadDebtBefore = TRUE")) { // Only fetch records that are not fully paid

                statement.setDate(1, java.sql.Date.valueOf(start));
                statement.setDate(2, java.sql.Date.valueOf(end));

                ResultSet resultSet = statement.executeQuery();

                int no = 1;
                while (resultSet.next()) {
                    ObservableList<String> rowData = FXCollections.observableArrayList();
                    rowData.add(String.valueOf(no++));
                    LocalDate transactionDate = resultSet.getDate("transactionDate").toLocalDate();
                    String formattedDate = transactionDate.format(formatter);
                    rowData.add(formattedDate);
                    rowData.add(resultSet.getString("name") != null ? resultSet.getString("name") : "Cash");
                    rowData.add(resultSet.getString("status"));
                    double totalSales = resultSet.getDouble("totalSales");
                    double totalPayment = resultSet.getDouble("totalPayment");
                    double totalPiutang = totalSales - totalPayment;
                    rowData.add(currencyFormat.format(totalPiutang));
                    data.add(rowData);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        table.setItems(data);

        buttonCetakLaporan.setOnAction(e -> {
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();

            // Ensure data is loaded for the selected date range
            filterbutton.fire();

            printReport(indexStage, start, end, data);
        });

        tableBox.getChildren().setAll(filterBox, buttonBox, table);

        contentBox.getChildren().setAll(contentHeaderBox, tableBox);

        borderPane.setLeft(sidebar);
        borderPane.setCenter(contentBox);

        Scene scene = new Scene(borderPane, 1200, 800);
        indexStage.setTitle("Dashboard Piutang");
        indexStage.setScene(scene);
        indexStage.show();

        // Trigger filter button click on load to display initial data
        filterbutton.fire();
    }
}