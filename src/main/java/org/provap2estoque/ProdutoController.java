package org.provap2estoque;

import org.DAO.DaoProduto;
import org.model.Produto;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    private final DaoProduto daoProduto = new DaoProduto();

    @FXML
    public void initialize() {
        System.out.println("=== INITIALIZE CHAMADO ===");

        // Configurar as colunas usando lambda (mais robusto)
        colID.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        colProduto.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));

        colQuantidade.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantidade()).asObject());

        colPreco.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPreco()).asObject());

        tabelaProdutos.setItems(listaProdutos);

        // Carregar produtos do banco de dados
        carregarProdutos();

        System.out.println("=== FIM INITIALIZE ===");
    }

    /**
     * Carrega os produtos do banco de dados usando JPA
     */
    private void carregarProdutos() {
        try {
            listaProdutos.clear();
            listaProdutos.addAll(daoProduto.listarTodos());
            System.out.println("✅ " + listaProdutos.size() + " produtos carregados do banco");
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar produtos: " + e.getMessage());
            mostrarAlerta("Erro", "Erro ao carregar produtos do banco de dados!", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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
        quantidadeField.setText("0");

        TextField precoField = new TextField();
        precoField.setPromptText("Preço");
        precoField.setText("0.00");

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Quantidade:"), 0, 1);
        grid.add(quantidadeField, 1, 1);
        grid.add(new Label("Preço:"), 0, 2);
        grid.add(precoField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Focar no campo nome
        nomeField.requestFocus();

        // Processar o resultado
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String nome = nomeField.getText().trim();
                int quantidade = Integer.parseInt(quantidadeField.getText().trim());
                double preco = Double.parseDouble(precoField.getText().trim().replace(",", "."));

                if (nome.isEmpty()) {
                    mostrarAlerta("Erro", "O nome do produto não pode estar vazio!", Alert.AlertType.ERROR);
                    return;
                }

                if (quantidade < 0) {
                    mostrarAlerta("Erro", "A quantidade não pode ser negativa!", Alert.AlertType.ERROR);
                    return;
                }

                if (preco < 0) {
                    mostrarAlerta("Erro", "O preço não pode ser negativo!", Alert.AlertType.ERROR);
                    return;
                }

                // Criar o produto (ID será gerado automaticamente pelo JPA)
                Produto novoProduto = new Produto(0, nome, quantidade, preco);

                // Salvar no banco usando JPA
                daoProduto.cadastrar(novoProduto);

                // Recarregar a tabela
                carregarProdutos();

                System.out.println("✅ Produto adicionado: " + nome);
                mostrarAlerta("Sucesso", "Produto adicionado com sucesso!", Alert.AlertType.INFORMATION);

            } catch (NumberFormatException e) {
                mostrarAlerta("Erro", "Quantidade deve ser um número inteiro e Preço deve ser um número válido!\nUse ponto (.) como separador decimal.", Alert.AlertType.ERROR);
            } catch (Exception e) {
                System.err.println("❌ Erro ao adicionar produto: " + e.getMessage());
                mostrarAlerta("Erro", "Erro ao adicionar produto: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExcluirProduto() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();

        if (selecionado != null) {
            // Confirmar exclusão
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText("Deseja realmente excluir o produto?");
            confirmacao.setContentText("Produto: " + selecionado.getNome() + "\nID: " + selecionado.getId());

            Optional<ButtonType> resultado = confirmacao.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    // Remover do banco usando JPA
                    daoProduto.remover(selecionado);

                    // Recarregar a tabela
                    carregarProdutos();

                    System.out.println("✅ Produto excluído: " + selecionado.getNome());
                    mostrarAlerta("Sucesso", "Produto excluído com sucesso!", Alert.AlertType.INFORMATION);

                } catch (Exception e) {
                    System.err.println("❌ Erro ao excluir produto: " + e.getMessage());
                    mostrarAlerta("Erro", "Erro ao excluir produto: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        } else {
            mostrarAlerta("Aviso", "Nenhum produto selecionado!", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleAtualizarProduto() {
        try {
            carregarProdutos();
            System.out.println("✅ Tabela atualizada");
            mostrarAlerta("Sucesso", "Tabela atualizada com sucesso!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar: " + e.getMessage());
            mostrarAlerta("Erro", "Erro ao atualizar tabela!", Alert.AlertType.ERROR);
        }
    }

    /**
     * Método auxiliar para mostrar alertas
     */
    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(mensagem);
        alert.showAndWait();
    }
}