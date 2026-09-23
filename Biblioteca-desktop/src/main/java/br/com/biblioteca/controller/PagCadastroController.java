package br.com.biblioteca.controller;

import br.com.biblioteca.exception.EmailInvalidoException;
import br.com.biblioteca.model.usuario.UsuarioRequest;
import br.com.biblioteca.service.UsuarioService;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

import static br.com.biblioteca.model.common.TipoUsuario.ALUNO;
import static br.com.biblioteca.model.common.TipoUsuario.PROFESSOR;


public class PagCadastroController {
    UsuarioService usuarioService;

    public PagCadastroController() {
        this.usuarioService = new UsuarioService();
    }

    @FXML
    private TextField campoEmail;
    @FXML
    private TextField campoNome;
    @FXML
    private TextField campoSenha;
    @FXML
    private RadioButton alunoCheckbox;
    @FXML
    private RadioButton professorCheckbox;
    @FXML
    private Label mensagemLabel;

    @FXML
    private void initialize() {
        ToggleGroup tipoUsuarioGroup = new ToggleGroup();
        alunoCheckbox.setToggleGroup(tipoUsuarioGroup);
        professorCheckbox.setToggleGroup(tipoUsuarioGroup);
    }

    ///verificar usuario
    @FXML
    private void cadastrar(Event event){
        String email = campoEmail.getText();
        String nome = campoNome.getText();
        String senha = campoSenha.getText();

        if (nome == null || nome.trim().isEmpty()){
            mensagemLabel.setText("Por favor digite um nome.");
            mensagemLabel.setOpacity(1);
            return;
        }

        if (email == null || email.trim().isEmpty()){
            mensagemLabel.setText("Por favor insira um e-mail.");
            mensagemLabel.setOpacity(1);
            return;
        }

        if (senha == null || senha.trim().isEmpty()){
            mensagemLabel.setText("Por favor digite uma senha.");
            mensagemLabel.setOpacity(1);
            return;
        }

        if (senha.length() < 8) {
            mensagemLabel.setText("A senha deve ter no mínimo 8 caracteres.");
            mensagemLabel.setOpacity(1);
            return;
        }

        UsuarioRequest usuarioRequest;

        if (alunoCheckbox.isSelected()) {
            usuarioRequest = new UsuarioRequest(nome, email, senha, ALUNO);
        } else if (professorCheckbox.isSelected()) {
            usuarioRequest = new UsuarioRequest(nome, email, senha, PROFESSOR);
        } else {
            mensagemLabel.setText("Por favor selecione uma opção.");
            mensagemLabel.setOpacity(1);
            return;
        }
        try {
            usuarioService.cadastrar(usuarioRequest);
        }
        catch (Exception e) {
            if (e instanceof EmailInvalidoException) {
                mensagemLabel.setText("Digite um email válido!");
                return;
            }
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Ocorreu um erro ao realizar o cadastro.\n" + e.getCause().getMessage());
            alert.showAndWait();
            return;
        }
        mensagemLabel.setText("Cadastrado com sucesso!");
        abrirTelaLogin(event);
    }

    @FXML
    private void abrirTelaLogin(Event event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-page.fxml"));
            Parent root = loader.load();

            PagLoginController loginController = loader.getController();
            loginController.setCampos(this.campoEmail.getText(), this.campoSenha.getText());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene cenaAtual = stage.getScene();
            Scene scene = new Scene(root, cenaAtual.getWidth(), cenaAtual.getHeight());

            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("LIBRARY");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
