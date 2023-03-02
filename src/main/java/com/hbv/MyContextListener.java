package com.hbv;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRequestListener;

import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyContextListener implements ServletContextListener, ServletRequestListener {

    ScheduledThreadPoolExecutor executor;
    static int leasedConnections;

    public void contentInitialized(ServletContextEvent sce){
        ServletContext ctx = sce.getServletContext();

        MyLogger.info("initialized");
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(new MonitorStateLogger(),0,10, TimeUnit.SECONDS);
    }
    public void contextDestroyed(ServletContextEvent sce){
        ServletContext ctx = sce.getServletContext();
        MyLogger.info("cancel Timer");
        executor.shutdownNow();
    }
    public void requestInitialized(ServletContextEvent sce){
        MyLogger.info("request initialized");
    }
    public void requestDestroy(ServletContextEvent sce){
        MyLogger.info("request destroyed");
    }
    public synchronized static void incrementLeasedConnections(){
        leasedConnections ++;
    }
    public synchronized static void decementLeasedConnections(){
        leasedConnections --;
    }
}
