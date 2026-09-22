package br.com.biblioteca.controller;

import br.com.biblioteca.exception.FalhaNaDevolucaoException;
import br.com.biblioteca.model.emprestimo.EmprestimoResponse;
import br.com.biblioteca.model.livro.LivroResponse;
import br.com.biblioteca.service.EmprestimoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;


public class EmprestimoCardController {
    EmprestimoResponse emprestimo =  new EmprestimoResponse();
    EmprestimoService emprestimoService = new EmprestimoService();

    @FXML
    private Label tituloLabel;
    @FXML
    private Label dataEntregaLabel;
    @FXML
    private Label dataEmprestimoLabel;
    @FXML
    private Label autorLabel;
    @FXML
    private Button devolverButton;
    @FXML
    private Button renovarButton;
    @FXML
    private GridPane emprestimoGrid;

    private String token;

    public void setupCard(String token, EmprestimoResponse emprestimo){
        this.emprestimo = emprestimo;
        this.token = token;

        LivroResponse livro = emprestimo.getLivro();
        tituloLabel.setText(livro.getTitulo());
        autorLabel.setText(livro.getAutor());
        dataEntregaLabel.setText(emprestimo.getDataDevolucao().toString());
        dataEmprestimoLabel.setText(emprestimo.getDataEmprestimo().toString());

        if (!emprestimo.getAtivo()){
            devolverButton.setDisable(true);
            devolverButton.setManaged(false);

            renovarButton.setDisable(true);
            renovarButton.setManaged(false);
        }
    }

    public void renovar() {
        emprestimoService.renovar(token, emprestimo.getId())
                .thenAccept(novaDataEntrega -> {
                    Platform.runLater(() -> {
                        dataEntregaLabel.setText(String.valueOf(novaDataEntrega));
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Sucesso");
                        alert.setContentText("Você renovou o seu emprestimo de " + this.tituloLabel.getText() + "\nNova data de entrega: " + this.dataEntregaLabel.getText());
                        alert.getDialogPane().setPrefWidth(500);
                        alert.getDialogPane().setPrefHeight(250);
                        alert.showAndWait();
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Ocorreu um erro ao renovar este emprestimo.\n" + e.getCause().getMessage());
                        alert.getDialogPane().setPrefWidth(500);
                        alert.getDialogPane().setPrefHeight(250);
                        alert.showAndWait();
                    });
                    return null;
                });
    }

    public void devolver(){
        emprestimoService.devolver(token, emprestimo.getId())
                .thenAccept(response -> {
                    Platform.runLater(this::tornarDevolvido);
                })
                .exceptionally(e -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Ocorreu um erro ao realizar a devolução, tente novamente mais tarde.");
                    alert.showAndWait();
                    e.printStackTrace();
                    throw new FalhaNaDevolucaoException("Erro ao realizar devolução.");
                });

        tornarDevolvido();
    }

    private void tornarDevolvido() //Essa função serve para remover os botões de renovar e devolver, uma vez que não é possível realizar nenhuma dessas ações após a devolução.
    {
        renovarButton.setVisible(false);
        renovarButton.setManaged(false);

        devolverButton.setVisible(false);
        devolverButton.setManaged(false);

        Label texto = new Label();
        texto.setText("Devolução registrada.");
        texto.setFont(new Font(12));
        emprestimoGrid.add(texto, 4, 1);
    }
}
