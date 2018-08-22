package com.fanfan.robot.listener.base.synthesizer;

import android.os.Bundle;

import com.fanfan.robot.R;
import com.fanfan.robot.app.NovelApp;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.seabreeze.log.Print;

public class TtsEventAdapter implements SynthesizerListener {

    private ISynthListener listener;

    public TtsEventAdapter(ISynthListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSpeakBegin() {
        listener.onSpeakBegin();
    }

    /**
     * 合成进度
     *
     * @param percent  缓冲进度
     * @param beginPos
     * @param endPos
     * @param info
     */
    @Override
    public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        listener.onBufferProgress(percent);
    }

    @Override
    public void onSpeakPaused() {
        listener.onSpeakPaused();
    }

    @Override
    public void onSpeakResumed() {
        listener.onSpeakResumed();
    }

    /**
     * 播放进度
     *
     * @param percent  播放进度
     * @param beginPos
     * @param endPos
     */
    @Override
    public void onSpeakProgress(int percent, int beginPos, int endPos) {
        listener.onSpeakProgress(percent);
    }

    @Override
    public void onCompleted(SpeechError speechError) {
        if (speechError == null) {
            listener.onCompleted();
        } else if (speechError != null) {
            listener.onSpeakError(speechError.getErrorCode(), speechError.getErrorDescription());
        }
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }
}
