package com.fanfan.robot.model;

/**
 * Created by android on 2017/12/26.
 */

public class SerialBean {

    private String absolute;
    private int baudRate;
    private String motion;

    public String getAbsolute() {
        return absolute;
    }

    public void setAbsolute(String absolute) {
        this.absolute = absolute;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public String getMotion() {
        return motion;
    }

    public void setMotion(String motion) {
        this.motion = motion;
    }

    @Override
    public String toString() {
        return "SerialBean{" +
                "absolute='" + absolute + '\'' +
                ", baudRate=" + baudRate +
                ", motion='" + motion + '\'' +
                '}';
    }
}
