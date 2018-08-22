package com.fanfan.robot.listener.base.synthesizer.cloud;

import android.content.Context;
import android.media.AudioManager;

import com.fanfan.novel.utils.FucUtil;
import com.fanfan.novel.utils.youdao.TranslateLanguage;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.listener.base.synthesizer.ISynthListener;
import com.fanfan.robot.listener.base.synthesizer.TtsEventAdapter;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;
import com.youdao.sdk.ydonlinetranslate.TranslateErrorCode;
import com.youdao.sdk.ydonlinetranslate.TranslateListener;
import com.youdao.sdk.ydtranslate.Translate;

public class MySynthesizer {

    private static boolean isInited = false;

    private Context mContext;

    private SpeechSynthesizer mTts;

    private SynthesizerListener mListener;

    public MySynthesizer(Context context, ISynthListener synthListener) {
        this(context, new TtsEventAdapter(synthListener));
    }

    public MySynthesizer(Context context, SynthesizerListener recognizerListener) {
        if (isInited) {
            throw new RuntimeException("还未调用release()，请勿新建一个新类");
        }
        isInited = true;
        this.mContext = context;
        this.mListener = recognizerListener;
        initTts();
    }

    public void initTts() {

        if (mTts == null) {
            mTts = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
                @Override
                public void onInit(int code) {
                    if (code != ErrorCode.SUCCESS) {
                        Print.e("初始化失败，错误码：" + code);
                    }
                }
            });
        }
    }


    public void onResume() {
        buildTts();
    }

    public void onPause() {
        stop();
    }

    public void speak(String answer) {
        if (mTts != null) {

            mTts.startSpeaking(answer, mListener);
        }
    }

    public void stop() {
        if (mTts != null && isSpeaking()) {
            mTts.stopSpeaking();
        }
    }

    public void pause() {
        if (mTts != null) {
            mTts.pauseSpeaking();
        }
    }

    public void resume() {
        if (mTts != null) {
            mTts.resumeSpeaking();
        }
    }

    public void destroy() {
        if (mTts != null) {
            mTts.destroy();
        }
        mListener = null;
    }

    public void release() {
        if (mTts == null) {
            return;
        }
        stop();
        destroy();
        isInited = false;
    }

    public boolean isSpeaking() {
        if (mTts == null)
            return false;
        return mTts.isSpeaking();
    }

    private void buildTts() {
        if (mTts == null) {
            initTts();
        }
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);

        mTts.setParameter(ResourceUtil.TTS_RES_PATH, FucUtil.getResTtsPath(mContext, RobotInfo.getInstance().getTtsLocalTalker()));
        mTts.setParameter(SpeechConstant.VOICE_NAME, RobotInfo.getInstance().getTtsLineTalker());
        mTts.setParameter(SpeechConstant.SPEED, String.valueOf(RobotInfo.getInstance().getLineSpeed()));
        mTts.setParameter(SpeechConstant.PITCH, "50");
        mTts.setParameter(SpeechConstant.VOLUME, String.valueOf(RobotInfo.getInstance().getLineVolume()));
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "" + AudioManager.STREAM_MUSIC);
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Constants.PROJECT_PATH + "/msc/tts.wav");
        //开启VAD
        mTts.setParameter(SpeechConstant.VAD_ENABLE, "1");
        //会话最长时间
        mTts.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "100");

        mTts.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
    }

}
