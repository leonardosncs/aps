package gui;


import application.Main;
import biblioteca.entidades.Emprestimo;
import biblioteca.entidades.ExemplarLivro;
import biblioteca.entidades.Livro;
import biblioteca.entidades.Usuario;
import biblioteca.repositorio.*;
import gui.util.Alerts;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CadastrarExemplarController implements Initializable {

	@FXML
	private TextField inputCodLivro;
	
	@FXML
	private TableView<ExemplarLivro> tabelaExemplares;
  
	@FXML
  private TableColumn<ExemplarLivro, String> codExemplarCol;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		codExemplarCol.setCellValueFactory(f -> new ReadOnlyStringWrapper(Integer.toString(f.getValue().getId())));
	}

	public void onBtnRemoverSelecionado() {
		RepositorioExemplarLivro repoExemplar = Main.getGerenciadorRepositorio().getRepositorio(RepositorioExemplarLivro.class);

		ExemplarLivro exemplarSelecionado = tabelaExemplares.getSelectionModel().getSelectedItem();

		if (exemplarSelecionado == null) {
			Alerts.showAlert("Aviso", null, "Nenhum exemplar selecionado",
				Alert.AlertType.WARNING);
			return;
		}

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirme");
		alert.setHeaderText(null);
		alert.setContentText(String.format("Deseja remover o exemplar de código %d?", exemplarSelecionado.getId()));
		alert.getButtonTypes().clear();
		alert.getButtonTypes().add(ButtonType.YES);
		alert.getButtonTypes().add(ButtonType.NO);

		Optional<ButtonType> respostaOpt = alert.showAndWait();
		if (!respostaOpt.isPresent() || respostaOpt.get() == ButtonType.NO) {
			return;
		}

		try {
			repoExemplar.deletarPeloId(exemplarSelecionado.getId());

			Alerts.showAlert("Successo", null, "Exemplar removido com sucesso!",
				Alert.AlertType.INFORMATION);

			// Atualiza lista de exemplares
			tabelaExemplares.getItems().remove(tabelaExemplares.getSelectionModel().getSelectedIndex());
		} catch(Exception ex) {
			ex.printStackTrace();
			Alerts.showAlert("Erro", "Ocorreu um erro ao adicionar o exemplar:", ex.getMessage(), Alert.AlertType.ERROR);
		}
	}

	public void onBtnAdicionar() {
		if (!validaCampos()) {
			return;
		}

		RepositorioExemplarLivro repoExemplar = Main.getGerenciadorRepositorio().getRepositorio(RepositorioExemplarLivro.class);
		RepositorioLivro repoLivro = Main.getGerenciadorRepositorio().getRepositorio(RepositorioLivro.class);

		Livro livro = repoLivro.buscarPeloId(Integer.parseInt(inputCodLivro.getText()));

		if (livro == null) {
			Alerts.showAlert("Erro", null, "Não existe um livro com o código: " + inputCodLivro.getText(),
				Alert.AlertType.ERROR);
			return;
		}

		ExemplarLivro exemplar = new ExemplarLivro();
		exemplar.setDisponivel(true);
		exemplar.setLivro(livro);

		try {
			repoExemplar.cadastrar(exemplar);

			Alerts.showAlert("Successo", null, "Novo exemplar adicionado. Código: " + exemplar.getId(),
				Alert.AlertType.INFORMATION);

			// Atualiza lista de exemplares
			tabelaExemplares.setItems(FXCollections.observableList(repoExemplar.buscarExemplares(livro)));
		} catch(Exception ex) {
			ex.printStackTrace();
			Alerts.showAlert("Erro", "Ocorreu um erro ao adicionar o exemplar:", ex.getMessage(), Alert.AlertType.ERROR);
		}
	}

	public void onBtnBuscar() {
		if (!validaCampos()) {
			return;
		}

		GerenciadorRepositorio repos = Main.getGerenciadorRepositorio();
		RepositorioExemplarLivro repoExemplar = repos.getRepositorio(RepositorioExemplarLivro.class);
		RepositorioLivro repoLivro = repos.getRepositorio(RepositorioLivro.class);

		Livro livro = repoLivro.buscarPeloId(Integer.parseInt(inputCodLivro.getText()));

		if (livro == null) {
			Alerts.showAlert("Erro", null, "Não existe um livro com o código: " + inputCodLivro.getText(),
				Alert.AlertType.ERROR);
			return;
		}

		List<ExemplarLivro> exemplares = repoExemplar.buscarExemplares(livro);

		if (exemplares.size() == 0) {
			Alerts.showAlert("Aviso", null, "O livro informado não possui exemplares cadastrados.",
				Alert.AlertType.INFORMATION);
			return;
		}

		tabelaExemplares.setItems(FXCollections.observableList(exemplares));
	}

	private boolean validaCampos() {
		if (inputCodLivro.getText().isEmpty()) {
			Alerts.showAlert("Aviso", null, "Preencha o campo Código do Livro!", Alert.AlertType.WARNING);
			return false;
		}

		try {
			Integer.parseInt(inputCodLivro.getText());
		} catch (NumberFormatException ex) {
			Alerts.showAlert("Aviso", null, "O código digitado não é um número!", Alert.AlertType.WARNING);
			return false;
		}

		return true;
	}
}
