package br.com.biblioteca.controller;

import br.com.biblioteca.model.usuario.UsuarioResponse;
import br.com.biblioteca.service.EmprestimoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class TelaInicialController {
    UsuarioResponse usuario;
    EmprestimoService emprestimoService = new EmprestimoService();

    @FXML
    private Label contagem_emprestimos;
    @FXML
    private Label contagem_devolucoes;

    public void setTela(String token, UsuarioResponse usuario) {
        this.usuario = usuario;

        emprestimoService.calcularNumAtivos(token, usuario.getId())
                .thenAccept(response -> {
                    Platform.runLater(() -> contagem_emprestimos.setText(response));
                })
                .exceptionally(erro -> {
                    Platform.runLater(() -> {
                        erro.printStackTrace();
                        contagem_emprestimos.setText("0");
                        contagem_devolucoes.setText("0");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Ocorreu um erro ao mostrar as contagens.");
                        alert.setContentText(erro.getCause().getMessage());
                        alert.showAndWait();
                    });
                    return null;
                });

        emprestimoService.calcularNumDevolucoes(token, usuario.getId())
                .thenAccept(response -> {
                    Platform.runLater(() ->  contagem_devolucoes.setText(response));
                })
                .exceptionally(erro -> {
                    Platform.runLater(() -> {
                        erro.printStackTrace();
                        contagem_emprestimos.setText("0");
                        contagem_devolucoes.setText("0");

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Ocorreu um erro ao mostrar as contagens.");
                        alert.setContentText(erro.getCause().getMessage());
                        alert.showAndWait();
                    });
                    return null;
                });

    }
}
