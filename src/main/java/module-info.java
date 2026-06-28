module Editora.LibreFox {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;

    exports br.edu.ufersa.LibreFox;
    opens br.edu.ufersa.LibreFox to javafx.fxml, javafx.graphics;

    exports br.edu.ufersa.LibreFox.Controller;
    opens br.edu.ufersa.LibreFox.Controller to javafx.fxml;

    exports br.edu.ufersa.LibreFox.Model.entities;
    opens br.edu.ufersa.LibreFox.Model.entities to javafx.fxml;
}