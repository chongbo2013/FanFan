package com.fanfan.novel.plugin.adapterObject;

/**
 * Created by android on 2018/2/5.
 */

public class MyClass {

    public static void main(){
        Adapter adapter = new Adapter(new Adaptee());
        adapter.sampleOperation1();
        adapter.sampleOperation2();
    }
}
