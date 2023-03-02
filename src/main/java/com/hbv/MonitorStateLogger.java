package com.hbv;

class MonitorStateLogger implements Runnable {
    int count;
    public void run(){
        MyLogger.info("tick ..."+ count++);
    }
}