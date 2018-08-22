package com.fanfan.robot.listener.base.recog.local;

import android.content.Context;
import android.speech.RecognizerIntent;

import com.fanfan.novel.utils.FucUtil;
import com.fanfan.novel.utils.grammer.GrammerUtils;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.listener.base.recog.IRecogListener;
import com.fanfan.robot.listener.base.recog.RecogEventAdapter;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;

import java.io.File;

public class MyRecognizerLocal {

    private Context mContext;

    private SpeechRecognizer mIat;

    private RecognizerListener mListener;

    public MyRecognizerLocal(Context context, IRecogListener recogListener) {
        this(context, new RecogEventAdapter(recogListener));
    }

    public MyRecognizerLocal(Context context, RecognizerListener recognizerListener) {
        this.mContext = context;
        this.mListener = recognizerListener;
        initIat();
    }

    private void initIat() {
        if (mIat == null) {
            mIat = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
                @Override
                public void onInit(int code) {
                    if (code != ErrorCode.SUCCESS) {
                        Print.e("初始化失败，错误码：" + code);
                    }
                    Print.e("local initIat success");
                }
            });
        }
    }


    public void onStart() {
//        start();
    }

    public void onResume() {
        start();
    }

    public void onPause() {
        stop();
    }


    public void start() {
        setIatparameter();
        mIat.startListening(mListener);
    }

    public void stop() {
        if (mIat != null) {
            mIat.startListening(null);
            mIat.stopListening();
        }
    }

    public void release() {
        if (mIat == null) {
            return;
        }
        mListener = null;
//        mIat.cancel();
//        mIat.destroy();
    }

    private void setIatparameter() {

        if (mIat == null) {
            return;
        }
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);

        //麦克风阵列开启远场识别
        mIat.setParameter(SpeechConstant.DOMAIN, "fariat");
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT_AUE, "speex-wb;10");

        mIat.setParameter(ResourceUtil.ASR_RES_PATH, FucUtil.getResAsrPath(mContext));
        mIat.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
        mIat.setParameter(SpeechConstant.LOCAL_GRAMMAR, GrammerUtils.LOCAL_GRAMMAR_NAME);
        mIat.setParameter(SpeechConstant.MIXED_THRESHOLD, GrammerUtils.THRESHOLD + "");

        mIat.setParameter(SpeechConstant.RESULT_TYPE, GrammerUtils.RESULT_TYPE);

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "9000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, GrammerUtils.AUDIO_FORMAT);
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Constants.GRM_PATH + File.separator + "iat.wav");
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }


}
