package com.company;

/**
 * @author Dmitry
 * @version 1.0.0
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.*;

/**Class for writing logs*/
public class MyLogger {

    /**
     * Reference logger
     */
    public static Logger logger = Logger.getLogger( MyLogger.class.getName() );

    /**
     * Filehandler whith path
     */
    private static FileHandler handler;

    /**
     * Constructor logger
     * @param file
     */
    public MyLogger(File file){

        try {
            handler = new FileHandler(file.getAbsolutePath(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);

    }


}
