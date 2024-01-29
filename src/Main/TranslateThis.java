package Main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TranslateThis extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load UI from FXML
            Parent root = FXMLLoader.load(this.getClass().getResource("/UI/TranslateThis_UI.fxml"));
            
            primaryStage.setTitle("Translate This");
            
            Image icon = new Image("/image/icon.png");
            primaryStage.getIcons().add(icon);
            
            Scene scene = new Scene(root);          
            primaryStage.setScene(scene);
            primaryStage.show();

            // You can now use 'engine' to execute JavaScript scripts

        } catch (IOException ex) {
            Logger.getLogger(TranslateThis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
