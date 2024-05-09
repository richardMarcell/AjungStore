module ajungstore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens ajungstore to javafx.fxml;
    exports ajungstore;
}
