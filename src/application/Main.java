package application;

import biblioteca.repositorio.GerenciadorRepositorio;
import biblioteca.repositorio.sql.GerenciadorRepositorioSQL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
  
  private static Scene mainScene;
  private static GerenciadorRepositorio gerenciadorRepositorio;

  // TODO: carregar de um arquivo?
  public static String urlBanco = "jdbc:postgresql://localhost:5432/biblioteca";
  public static String senhaBanco = "aps";
  public static String usuarioBanco = "123";
  
  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
      ScrollPane scrollPane = loader.load();

      scrollPane.setFitToHeight(true);
      scrollPane.setFitToWidth(true);

      mainScene = new Scene(scrollPane);
      primaryStage.setWidth(700);
      primaryStage.setHeight(500);
      primaryStage.setResizable(false);
      primaryStage.setScene(mainScene);
      primaryStage.setTitle("Biblioteca IES");
      primaryStage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }

    inicializarGerenciadorRepositorio();
  }

  private void inicializarGerenciadorRepositorio() {
    // TODO: quando a implementação em arquivo foi feita, terá que ter algo
    // para selecionar entre SQL e arquivo...
    // Por agora, apenas a implementação em SQL existe.
    gerenciadorRepositorio = new GerenciadorRepositorioSQL(urlBanco, usuarioBanco, senhaBanco);

    try {
      gerenciadorRepositorio.inicializar();
    } catch (Exception ex) {
      ex.printStackTrace();

      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("Não foi possível inicializar os repositórios.");
      alert.setContentText(ex.getMessage());

      alert.showAndWait();

      // Finaliza a aplicação
      System.exit(1);
    }
  }
  
  public static GerenciadorRepositorio getGerenciadorRepositorio() {
    return gerenciadorRepositorio;
  }
  
  public static Scene getMainScene() {
    return mainScene;
  }
  
  public static void main(String[] args) {
    launch(args);
  }
}
