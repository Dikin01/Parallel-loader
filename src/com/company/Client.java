package com.company;
/**
 * @author Dmitry
 * @version 1.0.0
 */
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

/**Model class of Application start download and handle it*/
public class Client {
    /**
     * Thread counter
     */
    private int threadCount;
    /**
     * Server path
     */
    public static String serverPath;
    /**
     * Local path
     */
    public static String localPath;
    /**
     * Semaphore for synchronize
     */
    private CountDownLatch latch;

    /**
     * Controller reference
     */
    private Controller controller;

    /**
     * List ProgressBar in stage
     */
    private ArrayList<ProgressBar> bars = new ArrayList<>();

    /**
     * Client constructor
     * @param threadCount
     * @param serverPath
     * @param localPath
     * @param latch
     * @param controller
     */
    public Client(int threadCount, String serverPath, String localPath,
                  CountDownLatch latch, Controller controller) {
        this.threadCount = threadCount;
        this.serverPath = serverPath;
        this.localPath = localPath;
        this.latch = latch;
        this.bars = controller.getBars();
        this.controller = controller;
    }

    /**
     * Method start download
     */
    public void executeDownLoad() {

        try {
            URL url = new URL(controller.URLTextField.getText());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();
            if (code == 200) {
                // Длина данных, возвращаемых сервером, фактически равна длине файла в байтах
                int length = conn.getContentLength();
                System.out.println("Общая длина файла:" + length + "Байт (B)");
                RandomAccessFile raf = new RandomAccessFile(localPath, "rwd");
                // Указать длину создаваемого файла
                raf.setLength(length);
                raf.close();
                // Разделить файл
                int blockSize = length / threadCount;
                for (int threadId = 1; threadId <= threadCount; threadId++) {
                    // Начальная позиция первого потока загрузки
                    int startIndex = (threadId - 1) * blockSize;
                    int endIndex = startIndex + blockSize - 1;
                    if (threadId == threadCount) {
                        // Длина последней загрузки темы немного больше
                        endIndex = length;
                    }
                    System.out.println("Thread" + threadId + "Download:" + startIndex + "Byte ~" + endIndex + "Byte");
                    Download(threadId, startIndex, endIndex, bars.get(threadId-1),controller);
                }
            }
            else{
                HandlerDownloadException();
            }

        } catch (Exception e) {
            HandlerDownloadException();
            e.printStackTrace();
        }
    }

    /**
     * Method handle unsuccessful connection
     */
    public void HandlerDownloadException(){
        MyLogger.logger.log(Level.WARNING, "Не удалось подключиться по URL");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Не удалось подключиться по URL");
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        });
        System.out.println("Не удалось подключиться по URL");
        controller.downloadButton.setDisable(false);
        controller.browseButton.setDisable(false);
    }

    /**
     * Method execute download in Task
     * @param threadId number thread
     * @param startIndex start number byte
     * @param endIndex end number byte
     * @param bar ProgressBar
     * @param controller Reference to controller for enable buttons
     */
    public void Download(int threadId, int startIndex, int endIndex, ProgressBar bar, Controller controller) {

        Task task = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {

                try {
                    System.out.println("Thread" + threadId + "Загрузка ...");
                    URL url = new URL(serverPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    // Запросить сервер загрузить указанное местоположение части файла
                    conn.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
                    conn.setConnectTimeout(5000);
                    int code = conn.getResponseCode();

                    System.out.println("Thread " + threadId + "Запрос кода возврата =" + code);

                    InputStream is = conn.getInputStream(); // Возвращаем ресурс
                    RandomAccessFile raf = new RandomAccessFile(localPath, "rwd");
                    // С чего начать запись при случайной записи файлов
                    raf.seek(startIndex); // Найти файл

                    int len = 0;
                    int progress = 0;
                    byte[] buffer = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        if (isCancelled()) {
                            break;
                        }
                        raf.write(buffer, 0, len);
                        progress += len;
                        updateProgress(progress, endIndex-startIndex);
                    }
                    is.close();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
                latch.countDown();
                System.out.println("Thread " + threadId + " закончил загрузку");

                if(latch.getCount() == 0){
                    controller.downloadButton.setDisable(false);
                    controller.browseButton.setDisable(false);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Загрузка завершена");
                            alert.setHeaderText(null);
                            alert.showAndWait();


                        }
                    });
                    System.out.println("Загрузка завершена");
                    MyLogger.logger.log(Level.INFO, "Успешная загрузка");
                }
                return null;
            }
        };

        bar.progressProperty().bind(task.progressProperty());
        Thread th = new Thread(task);
        th.setDaemon(false);
        th.start();
    }
}

