package com.fanfan.robot.model.local;

import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/12/5.
 */

public class Ws {

    private int bg;
    private String slot;
    private List<Cw> cw;

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getBg() {
        return bg;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getSlot() {
        return slot;
    }

    public void setCw(List<Cw> cw) {
        this.cw = cw;
    }

    public List<Cw> getCw() {
        return cw;
    }

    @Override
    public String toString() {
        return "Ws{" +
                "slot='" + slot + '\'' +
                ", cw=" + cw +
                '}';
    }
}
