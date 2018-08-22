package com.fanfan.robot.model.local;

import android.support.annotation.NonNull;

/**
 * Created by zhangyuanyuan on 2017/12/5.
 */

public class Cw implements Comparable<Cw> {

    private int id;
    private int sc;
    private int gm;
    private String w;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setSc(int sc) {
        this.sc = sc;
    }

    public int getSc() {
        return sc;
    }

    public void setGm(int gm) {
        this.gm = gm;
    }

    public int getGm() {
        return gm;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getW() {
        return w;
    }

    @Override
    public int compareTo(@NonNull Cw o) {
        return o.getW().compareTo(this.getW());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cw) {
            Cw cw = (Cw) obj;
            return cw.w.equals(this.getW());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return w.hashCode();
    }

    @Override
    public String toString() {
        return "Cw{" +
                "sc=" + sc +
                ", w='" + w + '\'' +
                '}';
    }
}
