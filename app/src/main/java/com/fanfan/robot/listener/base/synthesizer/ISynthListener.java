package com.fanfan.robot.listener.base.synthesizer;

public interface ISynthListener {


    void onSpeakBegin();

    void onBufferProgress(int percent);

    void onSpeakPaused();

    void onSpeakResumed();

    void onSpeakProgress(int percent);

    void onCompleted();

    void onSpeakError(int errorCode, String errorDescription);

}
