package com.fanfan.robot.presenter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;

import com.fanfan.novel.utils.bitmap.BitmapUtils;
import com.fanfan.robot.presenter.ipersenter.IDetectFacePresenter;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.face.bean.detectFace.DetectFace;
import com.fanfan.youtu.api.face.bean.detectFace.Face;
import com.fanfan.youtu.api.face.event.DetectFaceEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class DetectFacePresenter extends IDetectFacePresenter {

    private IDetectFaceView mDetectView;

    private Youtucode youtucode;

    private Handler handler = new Handler();

    private boolean isFaceDetect;

    private Bitmap copyBitmap;

    public DetectFacePresenter(IDetectFaceView baseView) {
        super(baseView);
        mDetectView = baseView;

        youtucode = Youtucode.getSingleInstance();

    }

    @Override
    public void detectFace(Bitmap bitmap) {
        if (isFaceDetect)
            return;

        Print.e("从云端获取人脸信息详情 ... ");
        isFaceDetect = true;
        copyBitmap = Bitmap.createBitmap(bitmap);
        youtucode.detectFace(copyBitmap, 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(DetectFaceEvent event) {
        if (event.isOk()) {
            DetectFace detectFace = event.getBean();
            Print.e(detectFace);
            if (detectFace.getErrorcode() == 0) {
                List<Face> faces = detectFace.getFace();
                if (faces != null && faces.size() == 1) {
                    Face face = faces.get(0);
                    copyBitmap = BitmapUtils.cropBitmap(copyBitmap, detectFace.getImage_width(), detectFace.getImage_height(),
                            face.getX(), face.getY(), face.getWidth(), face.getHeight());
                    mDetectView.showConfirm(face, copyBitmap);
                } else {
                    setFaceDetect();
                }
                setFaceDetect();
                mDetectView.onError(detectFace.getErrorcode(), detectFace.getErrormsg());
            }else{
                setFaceDetect();
            }
        } else {
            setFaceDetect();
            mDetectView.onError(event);
        }

    }

    @Override
    public void setFaceDetect() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                copyBitmap = null;
                isFaceDetect = false;
            }
        }, 500);
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
