package com.fanfan.robot.view.camera.state;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.fanfan.robot.view.camera.CameraInterface;
import com.fanfan.robot.view.camera.CameraView;

/**
 * Created by Administrator on 2018/3/14/014.
 */

public class CameraMachine implements State {

    private Context mContext;

    private CameraView mView;

    private State mState;

    private State previewState;       //浏览状态(空闲)
    private State borrowPictureState; //浏览图片
    private State borrowVideoState;   //浏览视频

    public CameraMachine(Context context, CameraView view, CameraInterface.CameraOpenOverCallback callback) {
        mContext = context;
        mView = view;
        previewState = new PreviewState(this);
        borrowPictureState = new BorrowPictureState(this);
        borrowVideoState = new BorrowVideoState(this);
        mState = previewState;
    }

    public CameraView getView() {
        return mView;
    }

    public Context getContext() {
        return mContext;
    }

    public void setState(State state) {
        mState = state;
    }

    /**
     * 获取浏览图片状态
     *
     * @return
     */
    public State getBorrowPictureState() {
        return borrowPictureState;
    }

    /**
     * 获取浏览视频状态
     *
     * @return
     */
    public State getBorrowVideoState() {
        return borrowVideoState;
    }

    /**
     * 获取空闲状态
     *
     * @return
     */
    public State getPreviewState() {
        return previewState;
    }

    @Override
    public void start(SurfaceHolder holder, float screenProp) {
        mState.start(holder, screenProp);
    }

    @Override
    public void stop() {
        mState.stop();
    }

    @Override
    public void foucs(float x, float y, CameraInterface.FocusCallback callback) {
        mState.foucs(x, y, callback);
    }

    @Override
    public void swtich(SurfaceHolder holder, float screenProp) {
        mState.swtich(holder, screenProp);
    }

    @Override
    public void restart() {
        mState.restart();
    }

    @Override
    public void capture() {
        mState.capture();
    }

    @Override
    public void record(Surface surface, float screenProp) {
        mState.record(surface, screenProp);
    }

    @Override
    public void stopRecord(boolean isShort, long time) {
        mState.stopRecord(isShort, time);
    }

    @Override
    public void cancle(SurfaceHolder holder, float screenProp) {
        mState.cancle(holder, screenProp);
    }

    @Override
    public void confirm() {
        mState.confirm();
    }

    @Override
    public void zoom(float zoom, int type) {
        mState.zoom(zoom, type);
    }

    @Override
    public void flash(String mode) {
        mState.flash(mode);
    }
}
