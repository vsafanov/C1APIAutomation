module oracle.com.c1apiautomation {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires jdk.compiler;
    requires java.prefs;
    requires java.net.http;
    requires org.jetbrains.annotations;
    requires java.xml.crypto;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires jdk.jshell;


    opens oracle.com.c1apiautomation to javafx.fxml;
    exports oracle.com.c1apiautomation;
    exports oracle.com.c1apiautomation.model;
    opens oracle.com.c1apiautomation.model to javafx.fxml;
    exports oracle.com.c1apiautomation.helpers;
    opens oracle.com.c1apiautomation.helpers to javafx.fxml;
    exports oracle.com.c1apiautomation.controllers;
    opens oracle.com.c1apiautomation.controllers to javafx.fxml;
    exports oracle.com.c1apiautomation.controls to javafx.fxml; // Export the package
    opens oracle.com.c1apiautomation.controls to javafx.fxml;


}