package com.fanfan.robot.presenter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;

import com.fanfan.robot.model.FaceAuth;
import com.fanfan.novel.utils.bitmap.BitmapUtils;
import com.fanfan.robot.presenter.ipersenter.IFaceCheckinPresenter;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.face.bean.FaceIdentify;
import com.fanfan.youtu.api.face.bean.GetInfo;
import com.fanfan.youtu.api.face.bean.detectFace.DetectFace;
import com.fanfan.youtu.api.face.bean.detectFace.Face;
import com.fanfan.youtu.api.face.event.DetectFaceEvent;
import com.fanfan.youtu.api.face.event.FaceIdentifyEvent;
import com.fanfan.youtu.api.face.event.GetInfoEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android on 2018/1/10.
 */

public class FaceCheckinPresenter extends IFaceCheckinPresenter {

    private ICheckinView mCheckinView;

    private Youtucode youtucode;

    private Handler handler = new Handler();

    private boolean isFaceIdentify;

    private Bitmap copyBitmap;
    private FaceAuth faceAuth;
    private Face mFace;

    private int detectCount;

    public FaceCheckinPresenter(ICheckinView baseView) {
        super(baseView);
        mCheckinView = baseView;

        youtucode = Youtucode.getSingleInstance();

        detectCount = 0;
    }

    @Override
    public Bitmap bitmapSaturation(Bitmap baseBitmap) {
        Bitmap copyBitmap = Bitmap.createBitmap(baseBitmap.getWidth(), baseBitmap.getHeight(), baseBitmap.getConfig());
        ColorMatrix mImageViewMatrix = new ColorMatrix();
        ColorMatrix mBaoheMatrix = new ColorMatrix();
        float sat = (float) 0.0;
        mBaoheMatrix.setSaturation(sat);
        mImageViewMatrix.postConcat(mBaoheMatrix);
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(mImageViewMatrix);//再把该mImageViewMatrix作为参数传入来实例化ColorMatrixColorFilter
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);//并把该过滤器设置给画笔
        Canvas canvas = new Canvas(copyBitmap);//将画纸固定在画布上
        canvas.drawBitmap(baseBitmap, new Matrix(), paint);//传如baseBitmap表示按照原图样式开始绘制，将得到是复制后的图片
        canvas.drawBitmap(baseBitmap, new Matrix(), paint);//传如baseBitmap表示按照原图样式开始绘制，将得到是复制后的图片
        return copyBitmap;
    }

    @Override
    public void setFaceIdentify() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                copyBitmap = null;
                faceAuth = null;
                mFace = null;
                detectCount = 0;
                isFaceIdentify = false;
            }
        }, 500);
    }

    @Override
    public void faceIdentifyFace(Bitmap bitmap) {
        if (isFaceIdentify)
            return;

        Print.e("从云端获取人脸信息详情 ... ");
        isFaceIdentify = true;
        copyBitmap = Bitmap.createBitmap(bitmap);
        youtucode.faceIdentify(copyBitmap);
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(FaceIdentifyEvent event) {
        if (event.isOk()) {
            FaceIdentify faceIdentify = event.getBean();
            Print.e(faceIdentify);
            if (faceIdentify.getErrorcode() == 0) {

                compareFace(faceIdentify);
            } else {
                setFaceIdentify();
                mCheckinView.onError(faceIdentify.getErrorcode(), faceIdentify.getErrormsg());
            }
        } else {
            setFaceIdentify();
            mCheckinView.onError(event);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void compareFace(FaceIdentify faceIdentify) {
        Print.e("云端获取成功后取得相似度最佳的一个");
        ArrayMap<FaceIdentify.IdentifyItem, Integer> countMap = new ArrayMap<>();

        ArrayList<FaceIdentify.IdentifyItem> identifyItems = faceIdentify.getCandidates();
        if (identifyItems != null && identifyItems.size() > 0) {
            for (int i = 0; i < identifyItems.size(); i++) {
                FaceIdentify.IdentifyItem identifyItem = identifyItems.get(i);

                if (countMap.containsKey(identifyItem)) {
                    countMap.put(identifyItem, countMap.get(identifyItem) + 1);
                } else {
                    countMap.put(identifyItem, 1);
                }
            }

            ArrayMap<Integer, List<FaceIdentify.IdentifyItem>> resultMap = new ArrayMap<>();
            List<Integer> tempList = new ArrayList<Integer>();

            Iterator iterator = countMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<FaceIdentify.IdentifyItem, Integer> entry = (Map.Entry<FaceIdentify.IdentifyItem, Integer>) iterator.next();

                FaceIdentify.IdentifyItem key = entry.getKey();
                int value = entry.getValue();

                if (resultMap.containsKey(value)) {
                    List list = resultMap.get(value);
                    list.add(key);
                } else {
                    List<FaceIdentify.IdentifyItem> list = new ArrayList<>();
                    list.add(key);
                    resultMap.put(value, list);
                    tempList.add(value);
                }
            }
            //对多个人脸进行排序
            Collections.sort(tempList);

            int size = tempList.size();
            List<FaceIdentify.IdentifyItem> list = resultMap.get(tempList.get(size - 1));
            //防止人脸都是 1 时，取辨识度最大
            Collections.sort(list);
            FaceIdentify.IdentifyItem identifyItem = list.get(0);

            if (identifyItem.getConfidence() >= 70) {
                String person = identifyItem.getPerson_id();
                mCheckinView.compareFaceAuth(person);
            } else {
                mCheckinView.confidenceLow(identifyItem);
                setFaceIdentify();
            }
        } else {
            mCheckinView.identifyNoFace();
            setFaceIdentify();
        }
    }

    @Override
    public void getPersonInfo(String person) {
        youtucode.getInfo(person);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(GetInfoEvent event) {
        if (event.isOk()) {
            GetInfo getInfo = event.getBean();
            Print.e(getInfo);
            if (getInfo.getErrorcode() == 0) {
                mCheckinView.fromCloud(getInfo);
            } else {
                setFaceIdentify();
                mCheckinView.onError(getInfo.getErrorcode(), getInfo.getErrormsg());
            }
        } else {
            setFaceIdentify();
            mCheckinView.onError(event);
        }
    }

    @Override
    public void setFaceAuth(FaceAuth faceAuth) {
        this.faceAuth = faceAuth;
    }

    @Override
    public void detectFace() {
        detectCount++;
        if (detectCount > 2) {
            detectCount = 0;
            setFaceIdentify();
            return;
        }
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
                    this.mFace = face;
                    copyBitmap = BitmapUtils.cropBitmap(copyBitmap, detectFace.getImage_width(), detectFace.getImage_height(),
                            face.getX(), face.getY(), face.getWidth(), face.getHeight());
                    mCheckinView.showConfirm(copyBitmap, faceAuth);
                } else {
                    detectFace();
                }
            } else {
                mCheckinView.onError(detectFace.getErrorcode(), detectFace.getErrormsg());
                detectFace();
            }
        } else {
            mCheckinView.onError(event);
            detectFace();
        }
    }

    @Override
    public void confirmChinkIn() {
        mCheckinView.confirmChinkIn(copyBitmap, faceAuth.getAuthId(), mFace);
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
