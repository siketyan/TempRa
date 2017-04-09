package io.github.siketyan.monitor.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String INFO = "INFO";
    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";
    
    private static void log(String type, String text) {
        System.out.println(
            "[" + new SimpleDateFormat("HH:mm:ss").format(new Date())
                + " " + type + "]: " + text
        );
    }
    
    public static void info(String text) {
        log(INFO, text);
    }
    
    public static void success(String text) {
        log(SUCCESS, text);
    }
    
    public static void error(String text) {
        log(ERROR, text);
    }
}