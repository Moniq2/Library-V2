package br.com.biblioteca.controller;

import br.com.biblioteca.exception.ErroAoMostrarTelaException;
import br.com.biblioteca.model.emprestimo.EmprestimoResponse;
import br.com.biblioteca.model.usuario.UsuarioResponse;
import br.com.biblioteca.service.EmprestimoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;

public class EmprestimosController {
    UsuarioResponse usuario;
    EmprestimoService emprestimoService =  new EmprestimoService();
    EmprestimoCardController emprestimoCardController = new EmprestimoCardController();

    int LIMITE = 10;

    @FXML
    private Pagination paginacao;
    @FXML
    private VBox container;
    @FXML
    private void initialize(){}

    private String token;

    public void setUsuario(String token, UsuarioResponse usuario){
        this.usuario = usuario;
        this.token = token;
        mostrarEmprestimos();
    }

    private void mostrarEmprestimos(){
        paginacao.setPageFactory(pageIndex -> {
                VBox emprestimosVbox = new VBox();
                emprestimosVbox.setSpacing(10);
                carregarEmprestimos(emprestimosVbox, pageIndex);
                
                return emprestimosVbox;
        });
    }

    private void carregarEmprestimos(VBox emprestimosVBox, int pageIndex){
        emprestimoService.listar(token, usuario.getId(), pageIndex, LIMITE)
                .thenAccept(response -> {
                    List<EmprestimoResponse> emprestimos = response.getContent();
                    Platform.runLater(() -> {
                        preencherCards(emprestimosVBox, emprestimos);
                        paginacao.setPageCount(response.getTotalPages());
                    });
                })
                .exceptionally(error -> {
                    error.printStackTrace();
                    System.out.println(error.getMessage());
                    mostrarErro(emprestimosVBox);
                    return null;
                });
    }

    private void preencherCards(VBox emprestimosVbox, List<EmprestimoResponse> emprestimos) {
        
        if (emprestimos.isEmpty()) {
            Label mensagem = new Label("Você não tem nenhum emprestimo cadastrado até agora.");
            mensagem.getStyleClass().add("texto-cinza");
            mensagem.setStyle("-fx-font-size: 15px;");
            HBox caixa =  new HBox();
            caixa.getChildren().add(mensagem);
            container.getChildren().add(caixa);

            paginacao.setVisible(false);
            paginacao.setManaged(false);
            return;
        }

        for (EmprestimoResponse emprestimo : emprestimos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/emprestimo-card.fxml"));
                Parent card = loader.load();
                emprestimoCardController = loader.getController();
                emprestimoCardController.setupCard(token, emprestimo);
                emprestimosVbox.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ErroAoMostrarTelaException("Não foi possível mostrar card.");
            }
        }
    }

    private void mostrarErro(VBox emprestimosVbox){
        Platform.runLater(()-> {
            Alert mensagem = new Alert(Alert.AlertType.ERROR);
            mensagem.setTitle("Erro");
            mensagem.setHeaderText("Ocorreu um erro.");
            mensagem.setContentText("Falha ao carregar emprestimos. Tente novamente mais tarde.");
            emprestimosVbox.getChildren().clear();
        });
    }
}
