
package paintball;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Paintball extends Application{
private MainController mainController;
    
    public static void main(String[] args) {
        
        launch(args);
        
    }
    @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
    Parent root = fxmlLoader.load();
    this.mainController = fxmlLoader.getController();
    Scene scene = new Scene(root);
    String css = Paintball.class.getResource("/style.css").toExternalForm();
    scene.getStylesheets().add(css);
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.setTitle("Paintball");
    primaryStage.show();
  }

  @Override
  public void stop() throws Exception {
   mainController.closeDbConnection();
   super.stop();
  }
    
}
