package com.fanfan.robot.presenter.ipersenter;

import android.graphics.Bitmap;

import com.fanfan.robot.app.common.presenter.BasePresenter;
import com.fanfan.robot.app.common.presenter.BaseView;
import com.fanfan.robot.model.FaceAuth;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.FaceIdentify;
import com.fanfan.youtu.api.face.bean.GetInfo;
import com.fanfan.youtu.api.face.bean.detectFace.Face;

/**
 * Created by android on 2018/1/10.
 */

public abstract class IFaceCheckinPresenter implements BasePresenter {

    private ICheckinView mBaseView;

    public IFaceCheckinPresenter(ICheckinView baseView) {
        mBaseView = baseView;
    }

    public abstract Bitmap bitmapSaturation(Bitmap baseBitmap);

    public abstract void setFaceIdentify();

    public abstract void faceIdentifyFace(Bitmap bitmap);

    public abstract void compareFace(FaceIdentify faceIdentify);

    public abstract void getPersonInfo(String person);

    public abstract void setFaceAuth(FaceAuth faceAuth);

    public abstract void detectFace();

    public abstract void confirmChinkIn();

    public interface ICheckinView extends BaseView {

        void onError(BaseEvent event);

        void onError(int code, String msg);

        void compareFaceAuth(String person);

        void identifyNoFace();

        void confidenceLow(FaceIdentify.IdentifyItem identifyItem);

        void showConfirm(Bitmap circleBitmap, FaceAuth faceAuth);

        void confirmChinkIn(Bitmap bitmap, String authId, Face face);

        void isToday();

        void fromCloud(GetInfo getInfo);
    }

}
