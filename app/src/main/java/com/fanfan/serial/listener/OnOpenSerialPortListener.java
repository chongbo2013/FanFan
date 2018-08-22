package com.fanfan.serial.listener;

import java.io.File;

public interface OnOpenSerialPortListener {

    void onSuccess(File device, int i);

    void onFail(File device, Status status);

    enum Status {
        NO_READ_WRITE_PERMISSION,
        OPEN_FAIL
    }
}
