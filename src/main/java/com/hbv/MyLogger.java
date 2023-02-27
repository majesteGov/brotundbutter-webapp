package com.hbv;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger {
    public synchronized static void info(String msg) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,msg);

    }
}
