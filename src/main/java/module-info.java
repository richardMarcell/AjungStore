module ajungstore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;

    opens ajungstore to javafx.fxml;

    exports ajungstore;
}
