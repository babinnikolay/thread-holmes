package ru.hukola.threadholmes.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.*;

public class MainApplication extends Application {

    ExecutorService backgroundExec = Executors.newCachedThreadPool();
    @Override
    public void start(Stage stage) throws IOException {
        Button cancelButton = new Button();
        cancelButton.setText("Cancel");
        cancelButton.setTranslateX(60);
        cancelButton.setTranslateY(60);

        Button startButton = new Button();
        startButton.setText("Start");
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                class CancelListener implements EventHandler<ActionEvent> {
                    BackgroundTask<?> task;
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        if (task != null) {
                            System.out.println("stop sleeping");
                            task.cancel(true);
                        }
                    }
                }
                final CancelListener listener = new CancelListener();
                listener.task = new BackgroundTask<Void>() {
                    @Override
                    protected Void compute() throws Exception {
                        System.out.println("Sleep 2");
                        Thread.sleep(2000);
                        System.out.println("Stop sleeping");
                        return null;
                    }

                    @Override
                    protected void onCompletion(Void result, Throwable exception, boolean cancelled) {
                        cancelButton.removeEventHandler(new EventType<>("onAction"), listener);
                    }

                    @Override
                    public void run() {
                        try {
                            this.compute();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public boolean cancel(boolean mayInterruptIfRunning) {
                        return false;
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }

                    @Override
                    public boolean isDone() {
                        return false;
                    }

                    @Override
                    public Void get() throws InterruptedException, ExecutionException {
                        return null;
                    }

                    @Override
                    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                        return null;
                    }
                };

                cancelButton.addEventHandler(new EventType<>("onAction"), listener);
                backgroundExec.execute(listener.task);
            }

        });
        StackPane root = new StackPane();
        root.getChildren().add(startButton);
        root.getChildren().add(cancelButton);
        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}