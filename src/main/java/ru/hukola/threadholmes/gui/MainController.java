package ru.hukola.threadholmes.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainController {
    @FXML
    private Label statusText;

    private final String FILE_NANE = "src/main/resources/big.txt";

    public void onStartButtonClick(ActionEvent actionEvent) {
        statusText.setText("download text");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Path big = Paths.get(FILE_NANE);
                try {
                    Files.createFile(big);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}