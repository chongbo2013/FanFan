package com.fanfan.robot.view.camera;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.ImageView;

import com.fanfan.novel.utils.camera.view.AngleUtil;
import com.fanfan.novel.utils.camera.CameraUtils;
import com.fanfan.novel.utils.camera.view.DeviceUtil;
import com.fanfan.novel.utils.system.FileUtil;
import com.fanfan.novel.utils.system.ScreenUtil;
import com.fanfan.novel.utils.permiss.CheckPermission;
import com.seabreeze.log.Print;
import com.uuzuche.lib_zxing.camera.AutoFocusCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Bitmap.createBitmap;

/**
 * Created by Administrator on 2018/3/14/014.
 */

public class CameraInterface implements Camera.PreviewCallback {

    public static final int TYPE_RECORDER = 0x090;  //视频录制
    public static final int TYPE_CAPTURE = 0x091;   //拍照

    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;

    private int CAMERA_FRONT_POSITION = -1;     //前置
    private int CAMERA_POST_POSITION = -1;      //后置
    private int SELECTED_CAMERA = -1;

    private SurfaceHolder mHolder = null;
    private float screenProp = -1.0f;

    private boolean isRecorder = false;     //true  视频录制中
    private MediaRecorder mediaRecorder;
    private String mVideoFileName;
    private String mSaveVideoPath;
    private String mVideoFileAbsPath;
    private Bitmap videoFirstFrame = null;

    private CameraView.ErrorListener mErrorLisenter;

    private ImageView mSwitchView;          //转换摄像头按钮
    private ImageView mFlashLamp;           //闪光灯按钮

    private int previewWidth;
    private int previewHeight;

    private int angle = 0;
    private int cameraAngle = 90;//摄像头角度   默认为90度
    private int rotation = 0;
    private byte[] firstFrameData;

    private int nowScaleRate = 0;
    private int recordScleRate = 0;

    //视频质量
    private int mediaQuality = CameraView.MEDIA_QUALITY_MIDDLE;
    private SensorManager sm = null;

    private int nowAngle;

    private int handlerTime = 0;

    private volatile static CameraInterface mCameraInterface;

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null)
            synchronized (CameraInterface.class) {
                if (mCameraInterface == null)
                    mCameraInterface = new CameraInterface();
            }
        return mCameraInterface;
    }

    private CameraInterface() {
        findAvailableCameras();
        SELECTED_CAMERA = CAMERA_POST_POSITION;
        mSaveVideoPath = "";
    }

    private void findAvailableCameras() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, info);
            switch (info.facing) {
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    CAMERA_FRONT_POSITION = info.facing;
                    break;
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    CAMERA_POST_POSITION = info.facing;
                    break;
            }
        }

    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
                return;
            }
            float[] values = event.values;
            angle = AngleUtil.getSensorAngle(values[0], values[1]);
            rotationAnimation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 切换摄像头icon跟随手机角度进行旋转
     */
    private void rotationAnimation() {
        if (mSwitchView == null) {
            return;
        }
        if (rotation != angle) {
            int startRotaion = 0;
            int endRotation = 0;
            switch (rotation) {
                case 0:
                    startRotaion = 0;
                    switch (angle) {
                        case 90:
                            endRotation = -90;
                            break;
                        case 270:
                            endRotation = 90;
                            break;
                    }
                    break;
                case 90:
                    startRotaion = -90;
                    switch (angle) {
                        case 0:
                            endRotation = 0;
                            break;
                        case 180:
                            endRotation = -180;
                            break;
                    }
                    break;
                case 180:
                    startRotaion = 180;
                    switch (angle) {
                        case 90:
                            endRotation = 270;
                            break;
                        case 270:
                            endRotation = 90;
                            break;
                    }
                    break;
                case 270:
                    startRotaion = 90;
                    switch (angle) {
                        case 0:
                            endRotation = 0;
                            break;
                        case 180:
                            endRotation = 180;
                            break;
                    }
                    break;
            }
            ObjectAnimator animC = ObjectAnimator.ofFloat(mSwitchView, "rotation", startRotaion, endRotation);
            ObjectAnimator animF = ObjectAnimator.ofFloat(mFlashLamp, "rotation", startRotaion, endRotation);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(animC, animF);
            set.setDuration(500);
            set.start();
            rotation = angle;
        }
    }


    /**
     * 设置自定义按钮
     *
     * @param switchView
     * @param flashLamp
     */
    public void setSwitchView(ImageView switchView, ImageView flashLamp) {
        mSwitchView = switchView;
        mFlashLamp = flashLamp;
        if (switchView != null) {
            cameraAngle = CameraUtils.getInstance().getCameraDisplayOrientation(mSwitchView.getContext(), SELECTED_CAMERA);
        }
    }

    /**
     * 设置视频保存位置
     *
     * @param saveVideoPath
     */
    public void setSaveVideoPath(String saveVideoPath) {
        mSaveVideoPath = saveVideoPath;
        File file = new File(saveVideoPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 设置相机聚焦
     *
     * @param zoom
     * @param type
     */
    public void setZoom(float zoom, int type) {
        if (mCamera == null) {
            return;
        }
        if (mParams == null) {
            mParams = mCamera.getParameters();
        }
        if (!mParams.isZoomSupported() || !mParams.isSmoothZoomSupported()) {
            return;
        }
        switch (type) {
            case TYPE_RECORDER:
                if (!isRecorder) {
                    return;
                }
                if (zoom >= 0) {
                    //每移动50个像素缩放一个级别
                    int scaleRate = (int) (zoom / 50);
                    // 获取最大像素
                    if (scaleRate <= mParams.getMaxZoom() && scaleRate >= nowScaleRate && recordScleRate != scaleRate) {
                        //设置相机焦距，其参数是一个整型的参数，该参数的范围是0到Camera.getParameters().getMaxZoom()。
                        mParams.setZoom(scaleRate);
                        mCamera.setParameters(mParams);
                        recordScleRate = scaleRate;
                    }
                }
                break;
            case TYPE_CAPTURE:
                if (isRecorder) {
                    return;
                }
                //每移动50个像素缩放一个级别
                int scaleRate = (int) (zoom / 50);
                if (scaleRate < mParams.getMaxZoom()) {
                    nowScaleRate += scaleRate;
                    if (nowScaleRate < 0) {
                        nowScaleRate = 0;
                    } else if (nowScaleRate > mParams.getMaxZoom()) {
                        nowScaleRate = mParams.getMaxZoom();
                    }
                    mParams.setZoom(nowScaleRate);
                    mCamera.setParameters(mParams);
                }
                break;
        }
    }

    /**
     * 设置录制视频质量
     *
     * @param quality
     */
    public void setMediaQuality(int quality) {
        this.mediaQuality = quality;
    }

    /**
     * 设置闪光灯类型
     *
     * @param flashMode
     */
    public void setFlashMode(String flashMode) {
        if (mCamera == null)
            return;
        Camera.Parameters params = mCamera.getParameters();
        //用于设置闪光灯的类型，其参数是一个字符型参数，位于Parameters类中，以FLASH_MODE_开头。
        params.setFlashMode(flashMode);
        mCamera.setParameters(params);
    }

    /**
     * 启动相机
     *
     * @param callback
     */
    public void doOpenCamera(CameraOpenOverCallback callback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (!CheckPermission.isCameraUseable(SELECTED_CAMERA) && this.mErrorLisenter != null) {
                mErrorLisenter.onError();
                return;
            }
        }
        if (mCamera == null) {
            openCamera(SELECTED_CAMERA);
        }
        callback.cameraHasOpened();
    }

    /**
     * 启动相机
     *
     * @param cameraId
     */
    private synchronized void openCamera(int cameraId) {
        try {
            mCamera = Camera.open(cameraId);
        } catch (Exception e) {
            if (mErrorLisenter != null) {
                mErrorLisenter.onError();
            }
        }
    }

    /**
     * 切换摄像头
     *
     * @param holder
     * @param screenProp
     */
    public synchronized void switchCamera(SurfaceHolder holder, float screenProp) {
        if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
            SELECTED_CAMERA = CAMERA_FRONT_POSITION;
        } else {
            SELECTED_CAMERA = CAMERA_POST_POSITION;
        }
        doDestroyCamera();
        openCamera(SELECTED_CAMERA);
        if (Build.VERSION.SDK_INT > 17 && mCamera != null) {
            //实现拍照的时候开启或者关闭快门音
            mCamera.enableShutterSound(false);
        }
        doStartPreview(holder, screenProp);
    }

    /**
     * 相机启动预览
     *
     * @param holder
     * @param screenProp
     */
    public void doStartPreview(SurfaceHolder holder, float screenProp) {
        if (isPreviewing) {
            Print.e("doStartPreview isPreviewing");
        }
        if (holder == null) {
            return;
        }
        if (this.screenProp < 0) {
            this.screenProp = screenProp;
        }
        mHolder = holder;
        if (mCamera != null) {
            try {

                mParams = mCamera.getParameters();
                Camera.Size previewSize = CameraUtils.getInstance().getPreviewSize(mParams.getSupportedPreviewSizes(), 1000, screenProp);
                Camera.Size pictureSize = CameraUtils.getInstance().getPictureSize(mParams.getSupportedPictureSizes(), 1200, screenProp);

                mParams.setPreviewSize(previewSize.width, previewSize.height);
                mParams.setPictureSize(pictureSize.width, pictureSize.height);

                previewWidth = previewSize.width;
                previewHeight = previewSize.height;

                //获取受支持的对焦模式。
                boolean isFocusModeAuto = CameraUtils.getInstance().isSupportedFocusMode(mParams.getSupportedFocusModes(), Camera.Parameters.FOCUS_MODE_AUTO);
                if (isFocusModeAuto) {
                    mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }

                //获取受支持的图片格式。
                boolean isJPEG = CameraUtils.getInstance().isSupportedPictureFormats(mParams.getSupportedPictureFormats(), ImageFormat.JPEG);
                if (isJPEG) {
                    //设置图片的图像格式。
                    mParams.setPictureFormat(ImageFormat.JPEG);
                    //捕获的图像集的JPEG质量。
                    mParams.setJpegQuality(100);
                }

                mCamera.setParameters(mParams);

                mParams = mCamera.getParameters();
                mCamera.setPreviewDisplay(holder);  //SurfaceView
                //浏览角度
                mCamera.setDisplayOrientation(cameraAngle);

                mCamera.setPreviewCallback(this);
                mCamera.startPreview();
                isPreviewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 相机停止预览
     */
    public void doStopPreview() {
        if (null != mCamera) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                //这句要在stopPreview后执行，不然会卡顿或者花屏
                mCamera.setPreviewDisplay(null);
                isPreviewing = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 销毁相机
     */
    public void doDestroyCamera() {
        mErrorLisenter = null;
        if (null != mCamera) {
            try {
                mCamera.setPreviewCallback(null);
                mSwitchView = null;
                mFlashLamp = null;
                mCamera.stopPreview();
                mCamera.setPreviewDisplay(null);
                mHolder = null;
                isPreviewing = false;
                mCamera.release();
                mCamera = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void takePicture(final TakePictureCallback callback) {
        if (mCamera == null) {
            return;
        }
        switch (cameraAngle) {
            case 90:
                nowAngle = Math.abs(angle + cameraAngle) % 360;
                break;
            case 270:
                nowAngle = Math.abs(cameraAngle - angle);
                break;
        }
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                    matrix.setRotate(nowAngle);
                } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                    matrix.setRotate(360 - nowAngle);
                    matrix.postScale(-1, 1);
                }
                bitmap = createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (callback != null) {
                    if (nowAngle == 90 || nowAngle == 270) {
                        callback.captureResult(bitmap, true);
                    } else {
                        callback.captureResult(bitmap, false);
                    }
                }
            }
        });
    }

    public void startRecord(Surface surface, float screenProp) {
        mCamera.setPreviewCallback(null);
        int nowAngle = (angle + 90) % 360;
        //获取第一帧图片
        Camera.Parameters parameters = mCamera.getParameters();

        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;

        YuvImage yuvImage = new YuvImage(firstFrameData, parameters.getPreviewFormat(), width, height, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 50, out);
        byte[] bytes = out.toByteArray();
        videoFirstFrame = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Matrix matrix = new Matrix();
        if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
            matrix.setRotate(nowAngle);
        } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
            matrix.setRotate(270);
        }
        videoFirstFrame = createBitmap(videoFirstFrame, 0, 0, videoFirstFrame.getWidth(), videoFirstFrame.getHeight(), matrix, true);
        if (isRecorder) {
            return;
        }

        //打开相机—–调用 Camera.open()
        if (mCamera == null) {
            openCamera(SELECTED_CAMERA);
        }

        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }

        if (mParams == null) {
            mParams = mCamera.getParameters();
        }

        List<String> focusModes = mParams.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(mParams);
        //解锁Camera调用 Camera.unlock().
        mCamera.unlock();
        //重启MediaRecorder使其处于空闲状态。调用这个方法后，你需要重新配置一次该对象，就像刚创建时一样。
        mediaRecorder.reset();
        //设置用于视频录制的照相机。 使用该方法在预览和采集模式间快速切换，而无需销毁camera对象。unlock()方法应该在该方法前被调用。该方法必须在prepare()前调用。
        mediaRecorder.setCamera(mCamera);
        //设置要用于录制的视频源。
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //设置要记录的声音源。 如果未调用该方法，输出文件将不会包含音频轨道。声音源需要在设置录制参数或编码器之前指定。只在setOutputFormat()。方法前调用。
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置录制过程中输出文件的格式。
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //设置要用于录制的视频编码器。
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置要用于录制的音频编码器。设置使用的音频的编码器。如果该方法被调用，输出文件将不包含音频轨道。在setOutputFormat()之后，prepare()之前调用该方法。
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        Camera.Size videoSize;
        if (mParams.getSupportedVideoSizes() == null) {
            videoSize = CameraUtils.getInstance().getPreviewSize(mParams.getSupportedPreviewSizes(), 600, screenProp);
        } else {
            videoSize = CameraUtils.getInstance().getPreviewSize(mParams.getSupportedVideoSizes(), 600, screenProp);
        }

        //设置要捕捉的视频的宽度和高度。
        if (videoSize.width == videoSize.height) {
            mediaRecorder.setVideoSize(previewWidth, previewHeight);
        } else {
            mediaRecorder.setVideoSize(videoSize.width, videoSize.height);
        }

        //设置输出视频播放的方向提示
        if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
            //手机预览倒立的处理
            if (cameraAngle == 270) {
                //横屏
                if (nowAngle == 0) {

                    mediaRecorder.setOrientationHint(180);
                } else if (nowAngle == 270) {
                    mediaRecorder.setOrientationHint(270);
                } else {
                    mediaRecorder.setOrientationHint(90);
                }
            } else {
                if (nowAngle == 90) {
                    mediaRecorder.setOrientationHint(270);
                } else if (nowAngle == 270) {
                    mediaRecorder.setOrientationHint(90);
                } else {
                    mediaRecorder.setOrientationHint(nowAngle);
                }
            }
        } else {
            mediaRecorder.setOrientationHint(nowAngle);
        }

        //设置录制的视频编码比特率。
        if (DeviceUtil.isHuaWeiRongyao()) {
            mediaRecorder.setVideoEncodingBitRate(4 * 100000);
        } else {
            mediaRecorder.setVideoEncodingBitRate(mediaQuality);
        }

        //设置Surface以显示录制媒体（视频）的预览。
        mediaRecorder.setPreviewDisplay(surface);

        mVideoFileName = "video_" + System.currentTimeMillis() + ".mp4";
        if (mSaveVideoPath.equals("")) {
            mSaveVideoPath = Environment.getExternalStorageDirectory().getPath();
        }

        mVideoFileAbsPath = mSaveVideoPath + File.separator + mVideoFileName;
        //设置要生成的输出文件的路径。
        mediaRecorder.setOutputFile(mVideoFileAbsPath);
        try {
            //准备记录器开始捕捉和编码数据。该方法必须在设置期望捕捉的音频和视频源、编码方式、文件格式等之后，和start()方法前被调用。
            mediaRecorder.prepare();
            //开始捕获数据并将其编码到setOutputFile（）指定的文件中。
            mediaRecorder.start();
            isRecorder = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord(boolean isShort, StopRecordCallback callback) {
        if (!isRecorder) {
            return;
        }
        if (mediaRecorder != null) {
            //录制时发生错误时注册要调用的回调。
            mediaRecorder.setOnErrorListener(null);
            //记录时发生信息事件时注册要调用的回调。
            mediaRecorder.setOnInfoListener(null);
            //设置Surface以显示录制媒体（视频）的预览
            mediaRecorder.setPreviewDisplay(null);

            try {
                //停止录制。
                mediaRecorder.stop();
            } catch (RuntimeException e) {
                e.printStackTrace();
                mediaRecorder = null;
                mediaRecorder = new MediaRecorder();
            } finally {
                if (mediaRecorder != null) {
                    // 释放和该MediaRecorder对象关联的资源。
                    mediaRecorder.release();
                }
                mediaRecorder = null;
                isRecorder = false;
            }

            if (isShort) {
                if (FileUtil.deleteFile(mVideoFileAbsPath)) {
                    callback.recordResult(null, null);
                }
                return;
            }
            doStopPreview();
            String fileName = mSaveVideoPath + File.separator + mVideoFileName;
            callback.recordResult(fileName, videoFirstFrame);
        }

    }

    public void handleFocus(final Context context, final float x, final float y, final FocusCallback callback) {
        if (mCamera == null) {
            return;
        }

        Camera.Parameters params = mCamera.getParameters();
        Rect focusRect = calculateTapArea(x, y, 1f, context);

        //取消先前调用自动对焦
        mCamera.cancelAutoFocus();
        //camera没有聚焦功能,params.getMaxNumFocusAreas() == 0
        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            params.setFocusAreas(focusAreas);
        } else {
            callback.focusSuccess();
            return;
        }

        final String currentFocusMode = params.getFocusMode();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(params);
        mCamera.autoFocus(new AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success || handlerTime > 10) {
                    Camera.Parameters params = camera.getParameters();
                    params.setFocusMode(currentFocusMode);
                    camera.setParameters(params);
                    handlerTime = 0;
                    callback.focusSuccess();
                } else {
                    handlerTime++;
                    handleFocus(context, x, y, callback);
                }
            }
        });
    }

    private Rect calculateTapArea(float x, float y, float coefficient, Context context) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / ScreenUtil.getScreenWidth(context) * 2000 - 1000);
        int centerY = (int) (y / ScreenUtil.getScreenHeight(context) * 2000 - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public void registerSensorManager(Context context) {
        if (sm == null) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        sm.registerListener(sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterSensorManager(Context context) {
        if (sm == null) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        sm.unregisterListener(sensorEventListener);
    }

    public void isPreview(boolean res) {
        this.isPreviewing = res;
    }

    public void setErrorLinsenter(CameraView.ErrorListener linsenter) {
        mErrorLisenter = linsenter;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        firstFrameData = data;
    }

    //*****//*****//*****//*****//*****//*****//*****//*****//*****//
    public interface FocusCallback {
        void focusSuccess();

    }

    public interface CameraOpenOverCallback {
        void cameraHasOpened();

    }

    public interface TakePictureCallback {
        void captureResult(Bitmap bitmap, boolean isVertical);

    }

    public interface StopRecordCallback {
        void recordResult(String url, Bitmap firstFrame);

    }
}
