package br.com.biblioteca.controller;

import br.com.biblioteca.exception.SemExemplaresDisponiveisException;
import br.com.biblioteca.model.emprestimo.EmprestimoResponse;
import br.com.biblioteca.model.livro.LivroResponse;
import br.com.biblioteca.model.usuario.UsuarioResponse;
import br.com.biblioteca.service.EmprestimoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PagEmprestimoController {
    private LivroResponse livroResponse;
    private UsuarioResponse usuario;
    private final EmprestimoService emprestimoService = new EmprestimoService();
    private EmprestimoResponse emprestimo;

    @FXML
    private Label tituloLivro;
    @FXML
    private Label autorLivro;
    @FXML
    private Button emprestarButton;
    @FXML
    private VBox pagina;

    private String token;

    public void setLivro(LivroResponse livroResponse) {
        this.livroResponse = livroResponse;
        tituloLivro.setText(livroResponse.getTitulo());
        autorLivro.setText(livroResponse.getAutor());
    }

    public void setUsuario(String token, UsuarioResponse usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    @FXML
    private void pedirEmprestado() {
        emprestimoService.emprestar(token, livroResponse.getId(), usuario.getId())
                .thenAccept(response -> {
                    emprestimo = response;
                    Platform.runLater(() -> {
                        mostrarDetalhes(emprestimo);
                    });
                })
                .exceptionally(error -> {
                    if (error.getCause() instanceof SemExemplaresDisponiveisException) {
                        error.printStackTrace();
                        alertarErro(error.getCause().getMessage());
                    }
                    else {
                        error.printStackTrace();
                        System.out.println(error.getCause().getMessage());
                        alertarErro(error.getCause().getMessage());
                    }
                    return null;
                });
    }

    private void mostrarDetalhes(EmprestimoResponse emprestimo) {

        //Definir valores
        Label exemplarId = new Label();
        Label dataEmprestimo = new Label();
        Label dataEntrega = new Label();
        Label mensagem = new Label("Emprestimo concluído!");
        exemplarId.setText("ID do exemplar: " + emprestimo.getExemplarId());
        dataEmprestimo.setText("Data de emprestimo: " + emprestimo.getDataEmprestimo());
        dataEntrega.setText("Data de devolução: " + emprestimo.getDataDevolucao());

        exemplarId.getStyleClass().add("texto-branco");
        dataEmprestimo.getStyleClass().add("texto-branco");
        dataEntrega.getStyleClass().add("texto-branco");
        mensagem.getStyleClass().add("texto-branco");

        //Mostrar caixa com os dados do emprestimo
        VBox infoEmprestimoVBox = new VBox();
        infoEmprestimoVBox.getStyleClass().add("faixa-info-emprestimo");
        infoEmprestimoVBox.setPadding(new Insets(10));
        infoEmprestimoVBox.getChildren().add(mensagem);
        infoEmprestimoVBox.getChildren().add(exemplarId);
        infoEmprestimoVBox.getChildren().add(dataEmprestimo);
        infoEmprestimoVBox.getChildren().add(dataEntrega);

        pagina.getChildren().add(infoEmprestimoVBox);

        //Esconder o botão de emprestar
        emprestarButton.setVisible(false);
        emprestarButton.setManaged(false);
    }

    private void alertarErro(String contexto){
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Falha ao realizar emprestimo");
            alert.setContentText(contexto);
            alert.showAndWait();
        });
    }
}
