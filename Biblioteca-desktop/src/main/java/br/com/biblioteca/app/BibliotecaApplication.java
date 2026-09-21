package br.com.biblioteca.app;

import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BibliotecaApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                BibliotecaApplication.class.getResource("/fxml/login-page.fxml"),
                "FXML não encontrado: /fxml/login-page.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("Library");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
