package br.com.biblioteca.controller;

import br.com.biblioteca.exception.ErroAoMostrarTelaException;
import br.com.biblioteca.model.livro.LivroResponse;
import br.com.biblioteca.service.LivroService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;

public class LivrosController {
    private final LivroService livroService = new LivroService();
    private final int LIMITE = 12;
    private LivroCardController livroCardController;
    private String termoPesquisa = "";
    private MenuLateralController menuLateralController;

    @FXML
    private Pagination paginacao;

    @FXML
    private TextField caixaDePesquisa;

    @FXML
    private VBox container;

    @FXML
    private void initialize() {}

    private String token;

    public void setupLivroController(String token, MenuLateralController menuLateralController) {
        this.token = token;
        this.menuLateralController = menuLateralController;
        mostrarLivros();
    }

    private void mostrarLivros(){
        paginacao.setPageFactory(indexNumber ->  {
            VBox livrosContainer = new VBox();
            livrosContainer.setSpacing(10);
            carregarLivros(livrosContainer, indexNumber);
            return livrosContainer;
        });
    }

    private void carregarLivros(VBox livrosContainer, int index) {
        if (termoPesquisa.isBlank()) {
            livroService.listar(index, LIMITE)
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            if (response.isEmpty()){
                                mostrarMensagem("A lista de livros está vazia.");
                                return;
                            }
                            paginacao.setPageCount(response.getTotalPages());
                            preencherCards(livrosContainer, response.getContent());
                        });
                    })
                    .exceptionally(this::tratarErro);
        } else {
            livroService.buscarPorTermo(termoPesquisa, index, LIMITE)
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            if (response.isEmpty()){
                                mostrarMensagem("Não foram encontrados resultados para essa pesquisa.");
                                return;
                            }
                            paginacao.setPageCount(response.getTotalPages());
                            preencherCards(livrosContainer, response.getContent());
                        });
                    })
                    .exceptionally(this::tratarErro);
        }
    }

    @FXML
    private void pesquisar() {
        termoPesquisa = caixaDePesquisa.getText().trim();
        paginacao.setCurrentPageIndex(0);
        mostrarLivros();
    }

    private void mostrarMensagem(String mensagem){
        Label texto = new Label(mensagem);
        texto.setPadding(new Insets(10));
        texto.getStyleClass().add("texto-cinza");
        texto.setStyle("-fx-font-size: 15px;");
        HBox caixa =  new HBox();
        caixa.getChildren().add(texto);
        container.getChildren().add(caixa);

        paginacao.setVisible(false);
        paginacao.setManaged(false);
    }

    public void preencherCards(VBox livrosContainer, List<LivroResponse> livros) {
        for (LivroResponse livroResponse : livros) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/livro-card.fxml"));
                Parent card = loader.load();
                livroCardController = loader.getController();
                livroCardController.setupCard(token, menuLateralController, livroResponse);
                livrosContainer.getChildren().add(card);
                card.getStyleClass().add("card-livro");
            }
            catch (IOException e){
                e.printStackTrace();
                throw new ErroAoMostrarTelaException("Não foi possível mostrar card.");
            }
        }
    }

    private Void tratarErro(Throwable error) {
        error.printStackTrace();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Ocorreu um erro ao carregar livros. Tente novamente mais tarde");
            alert.showAndWait();
        });
        return null;
    }
}
