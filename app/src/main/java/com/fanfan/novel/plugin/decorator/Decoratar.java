package com.fanfan.novel.plugin.decorator;

/**
 * Created by android on 2018/2/6.
 */

public class Decoratar implements Component {

    //维持一个对抽象构建对象的引用
    private Component component;

    //注入一个抽象构建类型的对象
    public Decoratar(Component component){
        this.component = component;
    }

    @Override
    public void operation() {
        component.operation();
    }
}
