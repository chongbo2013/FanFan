package com.fanfan.robot.listener.base;

import android.os.Bundle;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by dell on 2017/9/20.
 */

public class TtsListener implements SynthesizerListener {

    public TtsListener(SynListener synListener) {
        this.synListener = synListener;
    }

    @Override
    public void onSpeakBegin() {
        if (synListener != null) {
//            synListener.onSpeakBegin();
        }
    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {
        if (speechError == null) {
            if (synListener != null) {
                synListener.onCompleted();
            }
        }
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }

    private SynListener synListener;

    public interface SynListener {
        void onCompleted();

        void onSpeakBegin();
    }
}
