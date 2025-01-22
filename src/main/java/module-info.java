module com.sbaltsas.assignments.networksiichat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;


    opens com.sbaltsas.assignments.networksiichat to javafx.fxml;
    exports com.sbaltsas.assignments.networksiichat;
}