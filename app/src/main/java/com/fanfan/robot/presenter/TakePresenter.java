package com.fanfan.robot.presenter;

import android.graphics.Bitmap;
import android.os.CountDownTimer;

import com.fanfan.robot.presenter.ipersenter.ITakePresenter;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.uploadfile.bean.Filezoo;
import com.fanfan.youtu.api.uploadfile.bean.Message;
import com.fanfan.youtu.api.uploadfile.event.UpfilesZooEvent;
import com.uuzuche.lib_zxing.utils.CodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

/**
 * Created by android on 2018/1/9.
 */

public class TakePresenter extends ITakePresenter {

    private ITakeView mTakeView;

    private Youtucode youtucode;

    private boolean isFirst;
    private CountDownTimer countDownTimer;

    public TakePresenter(ITakeView baseView) {
        super(baseView);
        mTakeView = baseView;

        youtucode = Youtucode.getSingleInstance();
    }


    @Override
    public void startCountDownTimer() {
        if (isFirst) {
            return;
        }

        countDownTimer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long l) {
                String str = String.valueOf(l / 1000);
                mTakeView.onTick(str);
            }

            @Override
            public void onFinish() {
                mTakeView.onFinish();
            }
        };
        countDownTimer.start();
        isFirst = true;
    }

    @Override
    public void stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void deletePhoto(String mSavePath) {
        File file = new File(mSavePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void sharePhoto(String mSavePath) {
        File file = new File(mSavePath);
        if (file.exists()) {
            youtucode.upfilesZoo(mSavePath);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(UpfilesZooEvent event) {
        if (event.isOk()) {
            Filezoo filezoo = event.getBean();

            if (filezoo.getResCode().equals("1")) {
                List<Message> messageList = filezoo.getMessage();
                if (messageList != null && messageList.size() > 0) {
                    Message message = messageList.get(0);
                    mTakeView.uploadSuccess(message.getUrl());
                } else {
                    mTakeView.onError(-1, "messageList null");
                }
            } else {
                mTakeView.onError(Integer.valueOf(filezoo.getResCode()), filezoo.getResMsg());
            }
        } else {
            mTakeView.onError(event);
        }
    }


    @Override
    public Bitmap generatingCode(String url, int width, int height, Bitmap logo) {
        return CodeUtils.createImage(url, width, height, logo);
    }

    @Override
    public void start() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void finish() {
        EventBus.getDefault().unregister(this);
    }

}
