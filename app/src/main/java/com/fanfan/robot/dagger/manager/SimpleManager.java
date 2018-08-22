//package com.fanfan.robot.dagger.manager;
//
//import com.fanfan.robot.model.SerialBean;
//import com.fanfan.robot.presenter.LocalSoundPresenter;
//import com.fanfan.robot.presenter.SerialPresenter;
//
//import javax.inject.Inject;
//
///**
// * Created by Administrator on 2018/3/8/008.
// */
//
//public class SimpleManager {
//
//
//    @Inject
//    public SimpleManager(LocalSoundPresenter localSoundPresenter, SerialPresenter serialPresenter) {
//        this.mSoundPresenter = localSoundPresenter;
//        this.mSerialPresenter = serialPresenter;
//    }
//
//    public void onDataReceiverd(SerialBean serialBean) {
//        mSerialPresenter.onDataReceiverd(serialBean);
//    }
//
//    public void receiveMotion(int type, String motion) {
//        mSerialPresenter.receiveMotion(type, motion);
//    }
//
//    public void doAnswer(String messageContent) {
//        mSoundPresenter.doAnswer(messageContent);
//    }
//
//    public void onCompleted() {
//        mSoundPresenter.onCompleted();
//    }
//
//    public void stopVoice() {
//        mSoundPresenter.stopEvery();
//    }
//}
