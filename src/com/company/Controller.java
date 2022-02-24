package com.company;
/**
 * @author Dmitry
 * @version 1.0.0
 */

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

/**Control class of Application handle user input*/
public class Controller {

    /**
     * Global reference to Main stage
     */
    public static Stage mainStage;

    /**FXML components*/
    @FXML public ProgressBar p0;
    @FXML public ProgressBar p1;
    @FXML public ProgressBar p2;
    @FXML public ProgressBar p3;
    @FXML public ProgressBar p4;
    @FXML public ProgressBar p5;
    @FXML public Button downloadButton;
    @FXML public Button browseButton;
    @FXML public TextField URLTextField;
    @FXML public TextField SavePathTextField;

    /**
     * Reserve server path
     */
    public static String defaultServerPath;

    /**
     * Reserve local path
     */
    public static String defaultLocalPath;

    /**
     * Method for get List ProgressBar in stage
     * @return ArrayList<ProgressBar>
     */
    public ArrayList<ProgressBar> getBars(){
        return new ArrayList<>(Arrays.asList(p0, p1, p2, p3, p4, p5));
    }

    /**
     * Method reset all variables in begin position stage
     */
    public void start(){
        resetProgressBar();
        defaultServerPath = Loader.properties.getProperty("URLPath");
        //defaultServerPath = "http://212.183.159.230/5MB.zip";
        defaultLocalPath = Loader.properties.getProperty("LocalPath");
        //defaultLocalPath = "C:\\Users\\kondi\\Desktop\\JavaTest\\20MB.zip";

        URLTextField.setText(defaultServerPath);
        SavePathTextField.setText(defaultLocalPath);

    }

    /**
     * Method reset progress in ProgressBars
     */
    public void resetProgressBar(){
        p0.progressProperty().set(0);
        p1.progressProperty().set(0);
        p2.progressProperty().set(0);
        p3.progressProperty().set(0);
        p4.progressProperty().set(0);
        p5.progressProperty().set(0);
    }


    /**
     * Handler Action click BrowseButton is finding localPath
     */
    @FXML
    private void onBrowseButtonClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(mainStage);
        if(file != null && file.exists() && file.isDirectory()){
            Client.localPath = file.getPath() + "\\5mb.zip";
            SavePathTextField.setText(Client.localPath);
        }else{
            Client.localPath = SavePathTextField.getText();
        }
        System.out.println("Browse");
        MyLogger.logger.log(Level.INFO, "Browse");

    }

    /**
     * Handler Action click DownloadButton start download
     */
    @FXML
    private void onDownloadButtonClick(){

        MyLogger.logger.log(Level.INFO, "Начало загрузки");
        downloadButton.setDisable(true);
        browseButton.setDisable(true);

        int threadSize=6;
        CountDownLatch latch = new CountDownLatch(threadSize);
        Client client = new Client(threadSize, URLTextField.getText(), Client.localPath, latch, this);

        long startTime = System.currentTimeMillis();
        client.executeDownLoad();
        long endTime = System.currentTimeMillis();
        System.out.println ("Конец всех загрузок, общее затраченное время" + (endTime-startTime) / 1000 + "s");
    }
}







