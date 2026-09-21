package br.com.biblioteca.controller;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import br.com.biblioteca.model.usuario.UsuarioResponse;
import br.com.biblioteca.security.Session;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MenuLateralController {
    @FXML
    private StackPane conteudo;
    @FXML
    private Label nomeUsuario;

    private UsuarioResponse usuario;
    private Session session;

    public void setUsuario(Session session, UsuarioResponse usuario) {
        this.usuario = usuario;
        this.session = session;
        nomeUsuario.setText(usuario.getNome());
    }

    public UsuarioResponse getUsuario(){ return usuario; };

    public <T> T carregarTela(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent tela = loader.load();
            conteudo.getChildren().setAll(tela);
            return loader.getController();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void iniciar() {
        TelaInicialController telaInicialController = carregarTela("/fxml/home.fxml");
        telaInicialController.setTela(session.getToken(), usuario);
    }

    @FXML
    private void abrirLivros() {
        LivrosController controller = carregarTela("/fxml/livros.fxml");
        controller.setupLivroController(session.getToken(), this);
    }

    @FXML
    private void abrirEmprestimos() {
        EmprestimosController controller = carregarTela("/fxml/emprestimos.fxml");
        controller.setUsuario(session.getToken(), usuario);
    }

    @FXML
    private void initialize(){}

    @FXML
    private void sair(Event event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sair");
        alert.setHeaderText(null);
        alert.setContentText("Tem certeza que desejar sair da sua conta?");
        ButtonType sim = new ButtonType("Sim");
        ButtonType nao = new ButtonType("Não");
        alert.getButtonTypes().setAll(sim, nao);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == sim) {
            session.logout();
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                        getClass().getResource("/fxml/login-page.fxml"),
                        "FXML não encontrado: /fxml/login-page.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene cenaAtual = stage.getScene();
                Scene scene = new Scene(root, cenaAtual.getWidth(), cenaAtual.getHeight());

                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
                PagLoginController loginController = loader.getController();
                loginController.mostrarLogout();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
