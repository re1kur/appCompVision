module practice {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires opencv;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.commons.io;
    requires org.apache.commons.imaging;
    opens org.example;
    opens org.example.controllers to javafx.fxml;
}