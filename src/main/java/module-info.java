module Editora.LibreFox {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports br.edu.ufersa.LibreFox;
    opens br.edu.ufersa.LibreFox to javafx.fxml, javafx.graphics;

    exports br.edu.ufersa.LibreFox.Controllers;
    opens br.edu.ufersa.LibreFox.Controllers to javafx.fxml;

    exports br.edu.ufersa.LibreFox.Model.entities;
    opens br.edu.ufersa.LibreFox.Model.entities to javafx.fxml;
}