package com.fanfan.robot.presenter.ipersenter;

import android.graphics.Bitmap;

import com.fanfan.robot.app.common.presenter.BasePresenter;
import com.fanfan.robot.app.common.presenter.BaseView;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.detectFace.Face;

/**
 * Created by zhangyuanyuan on 2017/9/18.
 */

public abstract class IDetectFacePresenter implements BasePresenter {


    private IDetectFaceView mBaseView;

    public IDetectFacePresenter(IDetectFaceView baseView) {
        mBaseView = baseView;
    }

    public abstract void detectFace(Bitmap bitmap);

    public abstract void setFaceDetect();

    public interface IDetectFaceView extends BaseView {

        void onError(BaseEvent event);

        void onError(int code, String msg);

        void showConfirm(Face face, Bitmap bitmap);
    }

}
