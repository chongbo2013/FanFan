//package com.fanfan.robot.dagger.manager;
//
//import com.fanfan.robot.app.common.Constants;
//import com.fanfan.robot.model.RobotBean;
//import com.fanfan.robot.model.SerialBean;
//import com.fanfan.robot.presenter.ChatPresenter;
//import com.fanfan.robot.presenter.SerialPresenter;
//import com.fanfan.robot.presenter.SynthesizerPresenter;
//import com.fanfan.robot.service.SerialService;
//import com.fanfan.robot.presenter.LineSoundPresenter;
//
//import javax.inject.Inject;
//
///**
// * Created by Administrator on 2018/3/8/008.
// */
//
//public class MainManager {
//
//    ChatPresenter mChatPresenter;
//    SerialPresenter mSerialPresenter;
//    SynthesizerPresenter mTtsPresenter;
//    LineSoundPresenter mSoundPresenter;
//
//    @Inject
//    public MainManager(ChatPresenter chatPresenter, SerialPresenter serialPresenter,
//                       SynthesizerPresenter synthesizerPresenter,
//                       LineSoundPresenter lineSoundPresenter) {
//        mChatPresenter = chatPresenter;
//        mSerialPresenter = serialPresenter;
//        mTtsPresenter = synthesizerPresenter;
//        mSoundPresenter = lineSoundPresenter;
//    }
//
//    public void onCreate() {
//
//        mSerialPresenter.start();
//        mTtsPresenter.start();
//        mSoundPresenter.start();
//    }
//
//    public void onResume() {
//        mTtsPresenter.buildTts();
//        mChatPresenter.start();
//    }
//
//    public void onPause() {
//        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
//        mTtsPresenter.stopTts();
//        mTtsPresenter.stopHandler();
//        mSoundPresenter.stopVoice();
//
//        mChatPresenter.finish();
//    }
//
//    public void onDestroy() {
//        mTtsPresenter.finish();
//
//        mSoundPresenter.finish();
//    }
//
//    public void callStop() {
//        mTtsPresenter.stopTts();
//        mTtsPresenter.stopHandler();
//        mSoundPresenter.stopVoice();
//    }
//
//    public void stopVoice() {
//        mSoundPresenter.stopVoice();
//    }
//
//    public void receiveMotion(int type, String motion) {
//        mSerialPresenter.receiveMotion(type, motion);
//    }
//
//    public void onCompleted() {
//        mTtsPresenter.onCompleted();
//    }
//
//    public void onlineResult(String unicode) {
//        mSoundPresenter.onlineResult(unicode);
//    }
//
//    public void stopAll(String wakeUp) {
//        mTtsPresenter.stopAll(wakeUp);
//    }
//
//    public void sendCustomMessage(RobotBean robotBean) {
//        mChatPresenter.sendCustomMessage(robotBean);
//    }
//
//    public void onDataReceiverd(SerialBean serialBean) {
//        mSerialPresenter.onDataReceiverd(serialBean);
//    }
//
//    public void doAnswer(String messageContent) {
//
//        mTtsPresenter.doAnswer(messageContent);
//    }
//
//    public void doUrl(String url) {
//
//        mSoundPresenter.playVoice(url);
//    }
//
//    public void stopHandler() {
//        mTtsPresenter.stopHandler();
//    }
//
//    public void sendMessage(String identifier, String question) {
//        mChatPresenter.sendServerMessage(question);
//    }
//
//    public boolean isSpeaking() {
//        return mTtsPresenter.isSpeaking();
//    }
//
//
//}
