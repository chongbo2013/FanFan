package com.fanfan.serial.listener;

public interface OnSerialPortDataListener {

    /**
     * 数据接收
     *
     * @param bytes 接收到的数据
     */
    void onDataReceived(String absolute , int baudRate, byte[] bytes);

    /**
     * 数据发送
     *
     * @param bytes 发送的数据
     */
    void onDataSent(String absolute, int baudRate, byte[] bytes);
}
