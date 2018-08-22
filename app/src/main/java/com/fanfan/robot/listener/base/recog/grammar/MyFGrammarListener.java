package com.fanfan.robot.listener.base.recog.grammar;

import com.seabreeze.log.Print;

public class MyFGrammarListener implements FGrammarListener {

    @Override
    public void onGrammarBuildSuccess() {
        Print.i("完成构建");
    }

    @Override
    public void onCloudGrammarBuildSuccess() {
        Print.i("在线语法构建成功");
    }

    @Override
    public void onLocalGrammarBuildSuccess() {
        Print.i("本地语法构建成功");
    }

    @Override
    public void onGrammarBuildError(int errorCode, String errorDescription) {

        Print.i("构建语法失败,错误码 ：" + errorCode + " 错误详情 : " + errorDescription);
    }
}
