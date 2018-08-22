package com.fanfan.novel.plugin;

/**
 * Created by android on 2018/2/3.
 */

public class Dynamic implements IDynamic  {
    @Override
    public void methodWithCallback(Callback callback) {
        Bean bean = new Bean();
        bean.setName("璐宝宝");

        //回调宿主APP的方法
        callback.callback(bean);
    }
}
