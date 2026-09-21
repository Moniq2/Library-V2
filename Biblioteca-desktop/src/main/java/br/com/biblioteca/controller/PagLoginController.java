package br.com.biblioteca.controller;

import br.com.biblioteca.exception.CredenciaisInvalidasException;
import br.com.biblioteca.exception.EmailInvalidoException;
import br.com.biblioteca.model.usuario.UsuarioLoginRequest;
import br.com.biblioteca.model.usuario.UsuarioResponse;
import br.com.biblioteca.security.Session;
import br.com.biblioteca.service.UsuarioService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class PagLoginController {
    private MenuLateralController menuLateralController;
    private final UsuarioService usuarioService;
    private Session session;

    @FXML
    private TextField campoEmail;
    @FXML
    private TextField campoSenha;
    @FXML
    private Label mensagemLabel;

    @FXML
    private void initialize(){
    }

    public PagLoginController() {
        usuarioService = new UsuarioService();
        menuLateralController = new MenuLateralController();
        session = new Session();
    }

    public void setCampos(String email, String senha){
        this.campoEmail.setText(email);
        this.campoSenha.setText(senha);
    }

    @FXML
    private void abrirPagCadastro(Event event){
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/fxml/pag-cadastro.fxml"),
                    "FXML não encontrado: /fxml/pag-cadastro.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Scene cenaAtual = stage.getScene();
            Scene scene = new Scene(root, cenaAtual.getWidth(), cenaAtual.getHeight());

            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void abrirTelaInicial(Event event){
        try{
            Session sessao = new Session();
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/fxml/menu-lateral.fxml"),
                    "FXML não encontrado: /fxml/menu-lateral.fxml"));

            Parent root = loader.load();
            menuLateralController = loader.getController();
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

            Scene cenaAtual = stage.getScene();
            Scene scene = new Scene(root, cenaAtual.getWidth(), cenaAtual.getHeight());

            stage.setScene(scene);
            stage.setTitle("Biblioteca");
            stage.setMaximized(true);

            usuarioService.buscarUsuarioAtual(session.getToken())
                    .thenAccept(usuario -> {
                        Platform.runLater(() -> {
                            menuLateralController.setUsuario(session, usuario);
                            menuLateralController.iniciar();
                            stage.show();
                        });
                    })
                    .exceptionally(erro -> {
                        erro.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Erro ao abrir tela inicial");
                        alert.setContentText(erro.getCause().getMessage());
                        return null;
                    });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void logar(Event event){
        String email = campoEmail.getText();
        String senha = campoSenha.getText();

        if (email.trim().isEmpty() || senha.trim().isEmpty()){
            mensagemLabel.setText("Por favor, preencha todos os campos para prosseguir.");
            mensagemLabel.setOpacity(1.0);
            return;
        }

        UsuarioLoginRequest usuarioLoginRequest = new UsuarioLoginRequest(email, senha);
            usuarioService.logar(usuarioLoginRequest)
                    .thenAccept(usuario -> {
                        Platform.runLater(() -> {
                            session.setToken(usuario.getToken());
                            abrirTelaInicial(event);
                        });
                    })
                    .exceptionally(error -> {
                        Platform.runLater(()-> {
                            if (error.getCause() instanceof CredenciaisInvalidasException){
                                mensagemLabel.setText("Senha ou email incorretos.");
                                mensagemLabel.setOpacity(1.0);
                            }
                            else if (error.getCause() instanceof EmailInvalidoException) {
                                mensagemLabel.setText("Por favor, digite um email válido!");
                                mensagemLabel.setOpacity(1.0);
                            }
                            else {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Erro!");
                                alert.setContentText("Ocorreu um erro ao realizar o login.\n" + error.getCause().getMessage());
                                alert.showAndWait();
                            }
                        });
                        System.out.println(error.getMessage());
                        return null;
                    });
    }

    public void mostrarLogout(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Logout");
        alert.setContentText("Você saiu da sua conta");
        alert.show();
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> alert.close());
        pause.play();
    }
}

