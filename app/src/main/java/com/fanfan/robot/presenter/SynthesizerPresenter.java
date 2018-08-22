//package com.fanfan.robot.presenter;
//
//import android.app.Activity;
//import android.media.AudioManager;
//import android.os.Handler;
//
//import com.fanfan.robot.app.common.Constants;
//import com.fanfan.robot.presenter.ipersenter.ISynthesizerPresenter;
//import com.fanfan.robot.listener.base.TtsListener;
//import com.fanfan.novel.utils.FucUtil;
//import com.fanfan.robot.app.RobotInfo;
//import com.iflytek.cloud.ErrorCode;
//import com.iflytek.cloud.InitListener;
//import com.iflytek.cloud.SpeechConstant;
//import com.iflytek.cloud.SpeechSynthesizer;
//import com.iflytek.cloud.util.ResourceUtil;
//import com.seabreeze.log.Print;
//
//import java.util.Random;
//
///**
// * Created by android on 2018/1/3.
// */
//
//public class SynthesizerPresenter extends ISynthesizerPresenter implements TtsListener.SynListener {
//
//    private ITtsView mTtsView;
//
//    private SpeechSynthesizer mTts;
//    private TtsListener mTtsListener;
//
//    private Handler mHandler = new Handler();
//
//    public SynthesizerPresenter(ITtsView baseView) {
//        super(baseView);
//        this.mTtsView = baseView;
//        mTtsListener = new TtsListener(this);
//    }
//
//    @Override
//    public void start() {
//        initTts();
//    }
//
//    @Override
//    public void finish() {
//        stopTts();
//        if (mTts != null) {
//            mTts.destroy();
//        }
//        mTtsListener = null;
//    }
//
//    @Override
//    public void initTts() {
//
//        if (mTts == null) {
//            mTts = SpeechSynthesizer.createSynthesizer(mTtsView.getContext(), new InitListener() {
//                @Override
//                public void onInit(int code) {
//                    if (code != ErrorCode.SUCCESS) {
//                        Print.e("初始化失败，错误码：" + code);
//                    }
//                }
//            });
//        }
//    }
//
//    @Override
//    public void buildTts() {
//        if (mTts == null) {
//            initTts();
//        }
//        mTts.setParameter(SpeechConstant.PARAMS, null);
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//
//        mTts.setParameter(ResourceUtil.TTS_RES_PATH, FucUtil.getResTtsPath(mTtsView.getContext(), RobotInfo.getInstance().getTtsLocalTalker()));
//        mTts.setParameter(SpeechConstant.VOICE_NAME, RobotInfo.getInstance().getTtsLineTalker());
//        mTts.setParameter(SpeechConstant.SPEED, String.valueOf(RobotInfo.getInstance().getLineSpeed()));
//        mTts.setParameter(SpeechConstant.PITCH, "50");
//        mTts.setParameter(SpeechConstant.VOLUME, String.valueOf(RobotInfo.getInstance().getLineVolume()));
//        mTts.setParameter(SpeechConstant.STREAM_TYPE, "" + AudioManager.STREAM_VOICE_CALL);
//        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Constants.PROJECT_PATH + "/msc/tts.wav");
//        //开启VAD
//        mTts.setParameter(SpeechConstant.VAD_ENABLE, "1");
//        //会话最长时间
//        mTts.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "100");
//
//        mTts.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
//        Print.e("initTts success ...");
//    }
//
//    @Override
//    public void stopTts() {
//        if (mTts.isSpeaking()) {
//            mTts.stopSpeaking();
//        }
//    }
//
//    @Override
//    public void doAnswer(String answer) {
//        mTtsView.stopSound();
//        mTts.startSpeaking(answer, mTtsListener);
//        mTtsView.onSpeakBegin(answer);
//    }
//
//    @Override
//    public void stopHandler() {
////        mHandler.removeCallbacks(runnable);
//    }
//
//    @Override
//    public void stopAll(String wakeUp) {
//        stopTts();
//        doAnswer(wakeUp);
//    }
//
//    @Override
//    public boolean isSpeaking() {
//        if (mTts == null)
//            return false;
//        return mTts.isSpeaking();
//    }
//
//    private String resFoFinal(int id) {
//        String[] arrResult = ((Activity) mTtsView).getResources().getStringArray(id);
//        return arrResult[new Random().nextInt(arrResult.length)];
//    }
//
//    @Override
//    public void onCompleted() {
////        if (isSpeaking()) {
////            return;
////        }
//        mTtsView.onRunable();
////        mHandler.postDelayed(runnable, 1000);
//    }
//
////    Runnable runnable = new Runnable() {
////        @Override
////        public void run() {
////            mTtsView.onRunable();
////        }
////    };
//
//    @Override
//    public void onSpeakBegin() {
//    }
//}
