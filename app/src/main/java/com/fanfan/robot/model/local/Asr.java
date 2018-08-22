package com.fanfan.robot.model.local;

import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/12/5.
 */

public class Asr {

    private int sn;
    private boolean ls;
    private int bg;
    private int ed;
    private List<Ws> ws;
    private int sc;

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getSn() {
        return sn;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public boolean getLs() {
        return ls;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getBg() {
        return bg;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public int getEd() {
        return ed;
    }

    public void setWs(List<Ws> ws) {
        this.ws = ws;
    }

    public List<Ws> getWs() {
        return ws;
    }

    public void setSc(int sc) {
        this.sc = sc;
    }

    public int getSc() {
        return sc;
    }


    @Override
    public String toString() {
        return "Asr{" +
                "sn=" + sn +
                ", ls=" + ls +
                ", bg=" + bg +
                ", ed=" + ed +
                ", ws=" + ws +
                ", sc=" + sc +
                '}';
    }
}
