package com.fanfan.robot.presenter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.fanfan.novel.utils.bitmap.BitmapUtils;
import com.fanfan.robot.presenter.ipersenter.IFaceVerifPresenter;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.face.bean.FaceCompare;
import com.fanfan.youtu.api.face.event.FaceCompareEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

/**
 * Created by android on 2017/12/22.
 */

public class FaceVerifPresenter extends IFaceVerifPresenter {

    private IFaceverifView mFaceverifView;

    private Youtucode youtucode;

    private boolean isFaceCompare;

    private int cutRatio;

    public FaceVerifPresenter(IFaceverifView baseView) {
        super(baseView);
        mFaceverifView = baseView;
        youtucode = Youtucode.getSingleInstance();
        cutRatio = 4;
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
    public void faceCompare(Bitmap bitmapA, Bitmap bitmapB) {
        if (isFaceCompare)
            return;

        Print.e("摄像头图像与身份证头像比较中 ... ");
        isFaceCompare = true;

        Bitmap replicaBitmap = Bitmap.createBitmap(bitmapA);
        Bitmap copyBitmap = BitmapUtils.ImageCrop(replicaBitmap, cutRatio, cutRatio, true);

        mFaceverifView.showMsg("正在进行人脸比对，请稍后");
        try {
            mFaceverifView.faceCompare(copyBitmap);
            youtucode.faceCompare(copyBitmap, bitmapB);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(FaceCompareEvent event) {
        if (event.isOk()) {
            FaceCompare faceCompare = event.getBean();
            Print.e(faceCompare);
            if (faceCompare.getErrorcode() == 0) {
                if (faceCompare.getSimilarity() > 70) {
                    mFaceverifView.compareSuccess();
                } else {
                    mFaceverifView.similarityLow(faceCompare.getSimilarity());
                    isFaceCompare = false;
                }
            } else {
                isFaceCompare = false;
                mFaceverifView.onError(faceCompare.getErrorcode(), faceCompare.getErrormsg());
            }
        } else {
            isFaceCompare = false;
            mFaceverifView.onError(event);
        }

    }

}
