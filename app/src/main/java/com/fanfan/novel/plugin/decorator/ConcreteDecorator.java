package com.fanfan.novel.plugin.decorator;

/**
 * Created by android on 2018/2/6.
 */

public class ConcreteDecorator extends Decoratar {

    public ConcreteDecorator(Component component) {
        super(component);
    }

    @Override
    public void operation() {
        super.operation();//调用原有的业务方法
        addedBehavior();//调用新增的业务方法
    }

    private void addedBehavior() {

    }
}
