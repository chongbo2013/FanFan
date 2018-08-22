package com.fanfan.robot.listener.base.recog.lexicon;

import com.seabreeze.log.Print;

public class MyHotLexiconListener implements HotLexiconListener {
    @Override
    public void onCloudLexiconUpdatedSuccess() {
        Print.i("在线热词上传成功");
    }

    @Override
    public void onLocalLexiconUpdatedSuccess() {
        Print.i("本地热词上传成功");
    }

    @Override
    public void onLexiconUpdatedError(int errorCode, String errorDescription) {
        Print.i("词典更新失败,错误码 ：" + errorCode + " 错误详情 : " + errorDescription);
    }
}
