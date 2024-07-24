module oracle.com.c1apiautomation {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens oracle.com.c1apiautomation to javafx.fxml;
    exports oracle.com.c1apiautomation;
}