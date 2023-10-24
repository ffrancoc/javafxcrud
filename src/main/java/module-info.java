module com.github.ffrancoc.crudapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mariadb.jdbc;
    requires java.base;

    opens com.github.ffrancoc.crudapp to javafx.fxml;
    opens com.github.ffrancoc.crudapp.models to javafx.base;
    exports com.github.ffrancoc.crudapp;
}
