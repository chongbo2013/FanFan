package com.fanfan.robot.listener.base.synthesizer;

import com.fanfan.robot.R;
import com.fanfan.robot.app.NovelApp;
import com.seabreeze.log.Print;

public class EarListener implements ISynthListener {

    protected static final String TAG = "EarListener ";

    @Override
    public void onSpeakBegin() {

        Print.i(TAG + "开始说话");
    }

    @Override
    public void onBufferProgress(int percent) {

        String format = String.format(NovelApp.getInstance().getString((R.string.tts_toast_format_buffer)), percent);
        Print.i(format);
    }

    @Override
    public void onSpeakPaused() {

        Print.i(TAG + "暂停播放");
    }

    @Override
    public void onSpeakResumed() {

        Print.i(TAG + "继续播放");
    }

    @Override
    public void onSpeakProgress(int percent) {

        String format = String.format(NovelApp.getInstance().getString((R.string.tts_toast_format_speak)), percent);
        Print.i(TAG + format);
    }

    @Override
    public void onCompleted() {

        Print.i(TAG + "播放完成");
    }

    @Override
    public void onSpeakError(int errorCode, String errorDescription) {

        Print.i(TAG + "播放error speechError ： " + errorCode + " , message : " + errorDescription);
    }
}
