package com.fanfan.robot.presenter;

import android.graphics.Bitmap;

import com.fanfan.robot.model.FaceAuth;
import com.fanfan.novel.utils.bitmap.BitmapUtils;
import com.fanfan.robot.presenter.ipersenter.IFaceRegisterPresenter;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.face.bean.AddFace;
import com.fanfan.youtu.api.face.bean.Newperson;
import com.fanfan.youtu.api.face.bean.detectFace.DetectFace;
import com.fanfan.youtu.api.face.event.AddPersonEvent;
import com.fanfan.youtu.api.face.event.DetectFaceEvent;
import com.fanfan.youtu.api.face.event.NewPersonEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by android on 2018/1/9.
 */

public class FaceRegisterPresenter extends IFaceRegisterPresenter {

    private IFaceRegView mFaceRegView;

    private Youtucode youtucode;

    private String authId;

    private int cutRatio = 4;

    private boolean isnewPerson;
    private boolean isAddface;

    private boolean isDetect;

    public FaceRegisterPresenter(IFaceRegView baseView, String authId) {
        super(baseView);
        mFaceRegView = baseView;
        this.authId = authId;
        youtucode = Youtucode.getSingleInstance();
        isAddface = true;
    }

    @Override
    public void start() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void finish() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void newPerson(Bitmap bitmap) {
        if (isnewPerson)
            return;
        isnewPerson = true;
        //创建个人信息
        Bitmap replicaBitmap = Bitmap.createBitmap(bitmap);
        Bitmap copyBitmap = BitmapUtils.ImageCrop(replicaBitmap, cutRatio, cutRatio, true);
        String currentTimeStr = String.valueOf(System.currentTimeMillis());
        Print.e("personId foundPerson : " + currentTimeStr);
        youtucode.newPerson(copyBitmap, currentTimeStr, authId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(NewPersonEvent event) {
        if (event.isOk()) {
            Newperson newperson = event.getBean();
            Print.e(newperson);
            if (newperson.getErrorcode() == 0) {
                String person_id = newperson.getPerson_id();
                //提示
                mFaceRegView.newpersonSuccess(person_id);
            } else {
                //错误时，isnewPerson置为false，curCount现在时0，保持不变
                isnewPerson = false;
                mFaceRegView.onError(newperson.getErrorcode(), newperson.getErrormsg());
            }
        } else {
            isnewPerson = false;
            mFaceRegView.onError(event);
        }
    }

    @Override
    public void uploadFace(FaceAuth faceAuth, Bitmap bitmap) {
        if (faceAuth == null)
            return;
        if (isAddface)
            return;
        isAddface = true;

        youtucode.addFace(bitmap, faceAuth.getPersonId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(AddPersonEvent event) {
        if (event.isOk()) {
            AddFace addFace = event.getBean();
            Print.e(addFace);
            if (addFace.getErrorcode() == 0) {
                List<Integer> integerList = addFace.getRet_codes();
                int number = 0;
                for (int i = 0; i < integerList.size(); i++) {
                    if (integerList.get(i) == 0) {
                        number++;
                    }
                }
                mFaceRegView.uploadBitmapFinish(number);
            } else {
                mFaceRegView.onError(addFace.getErrorcode(), addFace.getErrormsg());
            }
        } else {
            mFaceRegView.onError(event);
        }
    }


    @Override
    public void setAddface() {
        isAddface = false;
    }

    @Override
    public void detectFace(Bitmap bitmap) {
        if (isDetect)
            return;
        isDetect = true;
        youtucode.detectFace(bitmap, 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(DetectFaceEvent event) {
        if (event.isOk()) {
            DetectFace detectFace = event.getBean();
            Print.e(detectFace);
            if (detectFace.getErrorcode() == 0) {

            } else {
                mFaceRegView.onError(detectFace.getErrorcode(), detectFace.getErrormsg());
                isDetect = false;
            }
        } else {
            mFaceRegView.onError(event);
            isDetect = false;
        }
    }

}
