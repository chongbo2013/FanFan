package com.fanfan.serial.thread;

import java.io.IOException;
import java.io.InputStream;


public abstract class SerialPortReadThread extends Thread {

    public abstract void onDataReceived(String absolute, int baudRate, byte[] bytes);

    private String mAbsolute;
    private int iBaudRate;

    private InputStream mInputStream;
    private byte[] mReadBuffer;

    public SerialPortReadThread(String absolute, int baudRate, InputStream inputStream) {
        mAbsolute = absolute;
        iBaudRate = baudRate;
        mInputStream = inputStream;
        mReadBuffer = new byte[1024];
    }

    @Override
    public void run() {
        super.run();

        while (!isInterrupted()) {
            try {
                if (null == mInputStream) {
                    return;
                }
                int size = mInputStream.read(mReadBuffer);

                if (size > 0) {
                    byte[] readBytes = new byte[size];

                    System.arraycopy(mReadBuffer, 0, readBytes, 0, size);

                    onDataReceived(mAbsolute, iBaudRate, readBytes);
                }
                try {
                    Thread.sleep(50);//延时50ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * 关闭线程 释放资源
     */
    public void release() {
        interrupt();

        if (null != mInputStream) {
            try {
                mInputStream.close();
                mInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
