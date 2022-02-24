package com.company;
/**
 * @author Dmitry
 * @version 1.0.0
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.logging.Level;

/**View class of Application, show interface*/
public class Main extends Application {

    /**Method launch Application
     * @param args Args line
     * */
    public static void main (String[] args) {
        Loader loader = new Loader();
        MyLogger logger = new MyLogger(new File("logs.log"));
        MyLogger.logger.log(Level.INFO, "Start program");
        launch(args);


    }

    /**Method paint view
     * @param stage start stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Parallel download");
        stage.setScene(scene);
        stage.setResizable(false);
        Controller.mainStage = stage;
        stage.show();

        Controller controller = fxmlLoader.getController();
        controller.start();

    }
}
