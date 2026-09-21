package br.com.biblioteca.controller;
import br.com.biblioteca.model.livro.LivroResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class LivroCardController {
    private LivroResponse livroResponse;
    private MenuLateralController menuLateralController = new MenuLateralController();

    @FXML
    private Label tituloLabel;

    @FXML
    private Label autorLabel;

    @FXML
    private Label codigoLabel;

    @FXML
    private Label anoLabel;

    private String token;

    public void setupCard(String token, MenuLateralController menulateralController, LivroResponse livro) {
        this.token = token;
        this.menuLateralController = menulateralController;
        setLivro(livro);
    }

    public void setLivro(LivroResponse livroResponse) {
        tituloLabel.setText(livroResponse.getTitulo());
        autorLabel.setText(livroResponse.getAutor());
        codigoLabel.setText(String.valueOf(livroResponse.getId()));
        anoLabel.setText(String.valueOf(livroResponse.getDataPublicacao()));
        this.livroResponse = livroResponse;
    }

    @FXML
    public void abrirTelaEmprestimo() {
        try {
            PagEmprestimoController pagController = menuLateralController.carregarTela("/fxml/pag-emprestimo.fxml");
            pagController.setLivro(this.livroResponse);
            pagController.setUsuario(token, menuLateralController.getUsuario());
        }
        catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Falha na requisição");
            alert.setContentText("Não foi possível realizar emprestimo.");
            alert.showAndWait();
        }
    }
}
