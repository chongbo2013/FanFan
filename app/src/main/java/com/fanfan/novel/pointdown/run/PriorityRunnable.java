package com.fanfan.novel.pointdown.run;

public class PriorityRunnable implements Runnable {

    private Runnable obj;

    public PriorityRunnable(Runnable obj) {
        this.obj = obj;
    }

    @Override
    public void run() {
        this.obj.run();
    }
}
