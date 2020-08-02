package StegoDesktop;

/* Created by: Jeremy Ruth - 04.25.2018

 * This program provides a GUI based method to implement image based stegonography. That is, hiding one image
 * "inside" another. Least Significant Bit (LSB) embedding is used to perform the image hiding.
 *
 * The program also features the new idea of an "image key." This key allows the user to specify an additional 
 * image that is used to scamble the hidden messsage. The receiver must also posess and and use the exact image 
 * in order to reverse the scrambling and retried the hidden message using standard LSB methods. This provides an 
 * additional layer of protection in the form of cryptography.
 * 
 * For additional information and assistance with the user of the compiled program see my GitHub page:
 * https://github.com/Jeremy-Ruth/Stegonography_App
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader guiLoader = new FXMLLoader(getClass().getResource("UserInterface.fxml"));
        Parent root = guiLoader.load();
        Controller appController = guiLoader.getController();

        primaryStage.setTitle("StegoApp Desktop");
        primaryStage.setScene(new Scene(root, 1600, 1000));
        primaryStage.setResizable(false);
        primaryStage.show();
        appController.startUpRoutine();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
