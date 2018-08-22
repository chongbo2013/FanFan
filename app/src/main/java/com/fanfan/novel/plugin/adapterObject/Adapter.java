package com.fanfan.novel.plugin.adapterObject;

/**
 * Created by android on 2018/2/5.
 */

public class Adapter implements Target {

    private Adaptee adaptee;

    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void sampleOperation1() {
        adaptee.sampleOperation1();
    }

    @Override
    public void sampleOperation2() {
        System.out.print("sampleOperation2");
    }
}
