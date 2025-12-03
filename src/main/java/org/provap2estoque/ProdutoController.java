package org.provap2estoque;

import org.model.Produto;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.geometry.Insets;

import java.util.Optional;

public class ProdutoController {

    @FXML
    private TableView<Produto> tabelaProdutos;

    @FXML
    private TableColumn<Produto, Integer> colID;
    @FXML
    private TableColumn<Produto, String> colProduto;
    @FXML
    private TableColumn<Produto, Integer> colQuantidade;
    @FXML
    private TableColumn<Produto, Double> colPreco;

    private final ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("=== INITIALIZE CHAMADO ===");

        // Método alternativo e mais robusto usando cellValueFactory com lambda
        colID.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        colProduto.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));

        colQuantidade.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantidade()).asObject());

        colPreco.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPreco()).asObject());

        tabelaProdutos.setItems(listaProdutos);

        System.out.println("Tamanho da lista antes: " + listaProdutos.size());


        System.out.println("Tamanho da lista depois: " + listaProdutos.size());
        System.out.println("Items da tabela: " + tabelaProdutos.getItems().size());

        System.out.println("=== Produtos adicionados ===");
        for (Produto p : listaProdutos) {
            System.out.println("ID: " + p.getId() + " | Nome: " + p.getNome() +
                    " | Qtd: " + p.getQuantidade() + " | Preço: " + p.getPreco());
        }

        System.out.println("=== FIM INITIALIZE ===");
    }

    @FXML
    private void handleAdicionarProduto() {
        System.out.println("handleAdicionarProduto chamado!");

        // Criar o diálogo customizado
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Adicionar Produto");
        dialog.setHeaderText("Preencha os dados do produto:");

        // Criar os campos de entrada
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome do produto");

        TextField quantidadeField = new TextField();
        quantidadeField.setPromptText("Quantidade");

        TextField precoField = new TextField();
        precoField.setPromptText("Preço");

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Quantidade:"), 0, 1);
        grid.add(quantidadeField, 1, 1);
        grid.add(new Label("Preço:"), 0, 2);
        grid.add(precoField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Processar o resultado
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Usuário clicou OK");
            try {
                String nome = nomeField.getText().trim();
                int quantidade = Integer.parseInt(quantidadeField.getText().trim());
                double preco = Double.parseDouble(precoField.getText().trim());

                System.out.println("Dados lidos: " + nome + ", " + quantidade + ", " + preco);

                if (nome.isEmpty()) {
                    mostrarAlerta("Erro", "O nome do produto não pode estar vazio!", Alert.AlertType.ERROR);
                    return;
                }

                int novoId = listaProdutos.size() + 1;
                Produto novoProduto = new Produto(novoId, nome, quantidade, preco);
                listaProdutos.add(novoProduto);

                System.out.println("Produto adicionado! Total de produtos: " + listaProdutos.size());
                System.out.println("Produto: " + novoProduto.getId() + " - " + novoProduto.getNome());

                mostrarAlerta("Sucesso", "Produto adicionado com sucesso!", Alert.AlertType.INFORMATION);

            } catch (NumberFormatException e) {
                System.out.println("Erro ao converter números: " + e.getMessage());
                mostrarAlerta("Erro", "Quantidade deve ser um número inteiro e Preço deve ser um número válido!", Alert.AlertType.ERROR);
            }
        } else {
            System.out.println("Usuário cancelou");
        }
    }

    @FXML
    private void handleExcluirProduto() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            listaProdutos.remove(selecionado);
            mostrarAlerta("Sucesso", "Produto excluído com sucesso!", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Aviso", "Nenhum produto selecionado!", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleAtualizarProduto() {
        tabelaProdutos.refresh();
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(mensagem);
        alert.showAndWait();
    }
}