module ajungstore {
    requires javafx.controls;
    requires javafx.fxml;

    opens ajungstore to javafx.fxml;
    exports ajungstore;
}
