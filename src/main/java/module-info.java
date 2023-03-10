module ru.hukola.threadholmes {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.hukola.threadholmes to javafx.fxml;
    exports ru.hukola.threadholmes;
    exports ru.hukola.threadholmes.gui;
    opens ru.hukola.threadholmes.gui to javafx.fxml;
}