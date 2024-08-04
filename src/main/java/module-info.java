module oracle.com.c1apiautomation {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires jdk.compiler;



    opens oracle.com.c1apiautomation to javafx.fxml;
    exports oracle.com.c1apiautomation;
    exports oracle.com.c1apiautomation.model;
    opens oracle.com.c1apiautomation.model to javafx.fxml;
    exports oracle.com.c1apiautomation.helpers;
    opens oracle.com.c1apiautomation.helpers to javafx.fxml;
    exports oracle.com.c1apiautomation.controllers;
    opens oracle.com.c1apiautomation.controllers to javafx.fxml;


}