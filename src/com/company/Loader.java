package com.company;
import java.io.*;
import java.util.Properties;

/**
 * @author Dmitry
 */

/**
 * Load initial property at the start
 */
public class Loader {

    /**
     * Name file with property
     */
    public static String fileNameSettings = "settings.properties";

    /**
     * Property
     */
    public static Properties properties;

    /**
     * Constructor loader
     */
    public Loader(){

        //Создаем объект Properties и загружаем в него данные из файла.
        properties = new Properties();

        try {
            properties.load(new FileReader(fileNameSettings));
            MyLogger.logger.info("Properties loaded");
        }
        catch (IOException e) {
            MyLogger.logger.fine(e.getMessage());
        }

    }
}

