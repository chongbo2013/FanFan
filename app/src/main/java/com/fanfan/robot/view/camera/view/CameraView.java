package com.fanfan.robot.view.camera.view;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/3/14/014.
 */

public interface CameraView {

    void resetState(int type);

    void confirmState(int type);

    void showPicture(Bitmap bitmap, boolean isVertical);

    void playVideo(Bitmap firstFrame, String url);

    void stopVideo();

    void setTip(String tip);

    void startPreviewCallback();

    boolean handlerFoucs(float x, float y);
}
