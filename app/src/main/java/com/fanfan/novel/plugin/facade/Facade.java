package com.fanfan.novel.plugin.facade;

/**
 * Created by android on 2018/2/6.
 */

public class Facade {

    private ModuleA moduleA = null;
    private ModuleB moduleB = null;
    private ModuleC moduleC = null;

    private static Facade facade = null;

    private Facade() {
        moduleA = new ModuleA();
        moduleB = new ModuleB();
        moduleC = new ModuleC();
    }

    public static Facade getInstance() {
        if (facade == null) {
            facade = new Facade();
        }
        return facade;
    }

    public void testOperation() {
        moduleA.testFuncA();
        moduleB.testFuncB();
        moduleC.testFuncC();
    }
}
