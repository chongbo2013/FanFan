package com.fanfan.robot.view.camera.state;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.fanfan.robot.view.camera.CameraInterface;

/**
 * Created by Administrator on 2018/3/14/014.
 */

public interface State {

    void start(SurfaceHolder holder, float screenProp);

    void stop();

    void foucs(float x, float y, CameraInterface.FocusCallback callback);

    void swtich(SurfaceHolder holder, float screenProp);

    void restart();

    void capture();

    void record(Surface surface, float screenProp);

    void stopRecord(boolean isShort, long time);

    void cancle(SurfaceHolder holder, float screenProp);

    void confirm();

    void zoom(float zoom, int type);

    void flash(String mode);
}