package com.fanfan.robot.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.fanfan.novel.utils.camera.CameraUtils;
import com.fanfan.robot.other.event.FaceEvent;
import com.fanfan.youtu.utils.UUIDGenerator;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.fanfan.robot.app.common.Constants.unusual;

/**
 * Created by android on 2018/2/26.
 */

public class CameraSerivice extends Service implements SurfaceHolder.Callback, Camera.PreviewCallback {

    //camera
    private Camera mCamera;

    private SurfaceHolder mHolder;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private int previewWidth = 640;
    private int previewHeight = 480;
    private int pictureWidth = 640;
    private int pictureHeight = 480;

    private boolean isPreviewing = false;

    private int orientionOfCamera;

    private WindowManager windowManager;
    private WindowManager.LayoutParams wmParams;
    private LinearLayout linearLayout;

    private boolean isSending;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();

        // 设置悬浮窗体属性
        // 1.得到WindoeManager对象：
        windowManager = (WindowManager) getApplicationContext().getSystemService("window");
        // 2.得到WindowManager.LayoutParams对象，为后续设置相关参数做准备：
        wmParams = new WindowManager.LayoutParams();
        // 3.设置相关的窗口布局参数，要实现悬浮窗口效果，要需要设置的参数有
        // 3.1设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 3.2设置图片格式，效果为背景透明 //wmParams.format = PixelFormat.RGBA_8888;
        wmParams.format = 1;
        // 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 4.// 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 5. 调整悬浮窗口至中间
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER;
        // 6. 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        // 7.将需要加到悬浮窗口中的View加入到窗口中了：
        // 如果view没有被加入到某个父组件中，则加入WindowManager中

        SurfaceView surfaceView = new SurfaceView(this);
        mHolder = surfaceView.getHolder(); // 获得SurfaceHolder对象

        WindowManager.LayoutParams params_sur = new WindowManager.LayoutParams();
        params_sur.width = 1;
        params_sur.height = 1;
        params_sur.alpha = 255;
        surfaceView.setLayoutParams(params_sur);

        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // surface.getHolder().setFixedSize(800, 1024);
        mHolder.addCallback(this); // 为SurfaceView添加状态监听

        linearLayout = new LinearLayout(this);
        WindowManager.LayoutParams params_rel = new WindowManager.LayoutParams();
        params_rel.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params_rel.height = WindowManager.LayoutParams.WRAP_CONTENT;
        linearLayout.setLayoutParams(params_rel);
        linearLayout.addView(surfaceView);
        windowManager.addView(linearLayout, wmParams); // 创建View
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new CameraBinder();
    }

    @Override
    public void onDestroy() {
        isSending = false;
        closeCamera();
        windowManager.removeView(linearLayout);
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
        doStartPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        closeCamera();
    }

    public void closeCamera() {
        if (null != mCamera) {
            mCamera.stopFaceDetection();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.stopFaceDetection();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void openCamera() {
        if (null != mCamera) {
            return;
        }
        if (Camera.getNumberOfCameras() == 1) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        try {
            mCamera = Camera.open(mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
            closeCamera();
            return;
        }
    }

    public void doStartPreview() {
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
                List<Camera.Size> pictures = parameters.getSupportedPictureSizes();
                //图片质量
                if (pictures.size() > 1) {
                    Iterator<Camera.Size> itor = pictures.iterator();
                    while (itor.hasNext()) {
                        Camera.Size curPicture = itor.next();
                        Log.e("curPicture", "w : " + curPicture.width + " , h : " + curPicture.height);
                    }
                }
                pictureWidth = pictures.get(0).width;
                pictureHeight = pictures.get(0).height;
                parameters.setPictureSize(pictureWidth, pictureHeight);

                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes.contains("continuous-video")) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                parameters.setPreviewFormat(ImageFormat.NV21);

                List<Camera.Size> previews = parameters.getSupportedPreviewSizes();
                // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
                if (previews.size() > 1) {
                    Iterator<Camera.Size> itor = previews.iterator();
                    while (itor.hasNext()) {
                        Camera.Size curPreview = itor.next();
                        Log.e("curPreview", "w : " + curPreview.width + " , h : " + curPreview.height);
                    }
                }
//                previewWidth = previews.get(0).width;
//                previewHeight = previews.get(0).height;
                parameters.setPreviewSize(previewWidth, previewHeight);

                parameters.set("jpeg-quality", 85);//设置照片质量

                mCamera.setParameters(parameters);

                if (unusual) {
                    // 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
                    orientionOfCamera = CameraUtils.getInstance().getCameraDisplayOrientation(this, mCameraId);
                } else {
                    // TODO: 2017/12/21  robot
                    orientionOfCamera = 90;
                }

                mCamera.setDisplayOrientation(orientionOfCamera);
                mCamera.startPreview();
                mCamera.setPreviewCallback(this);

                try {
//                    mCamera.setPreviewTexture(new SurfaceTexture(0));
                    mCamera.setPreviewDisplay(mHolder);
                    mCamera.startPreview();//开启预览
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (parameters.getMaxNumDetectedFaces() > 1) {
                    mCamera.startFaceDetection();
                }
                isPreviewing = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Camera.Size size = camera.getParameters().getPreviewSize();
        YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, size.width, size.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new android.graphics.Rect(0, 0, size.width, size.height), 80, baos);
        byte[] byteArray = baos.toByteArray();

        Bitmap previewBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        int width = previewBitmap.getWidth();
        int height = previewBitmap.getHeight();

        Matrix matrix = new Matrix();

        FaceDetector detector = null;
        Bitmap faceBitmap = null;

        detector = new FaceDetector(previewBitmap.getWidth(), previewBitmap.getHeight(), 10);
        int oriention = orientionOfCamera;
        if (unusual) {
            oriention = 360 - orientionOfCamera;
        }
        if (oriention == 360) {
            oriention = 0;
        }
        switch (oriention) {
            case 0:
                detector = new FaceDetector(width, height, 10);
                matrix.postRotate(0.0f, width / 2, height / 2);
                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
                break;
            case 90:
                detector = new FaceDetector(height, width, 1);
                matrix.postRotate(-270.0f, height / 2, width / 2);
                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
                break;
            case 180:
                detector = new FaceDetector(width, height, 1);
                matrix.postRotate(-180.0f, width / 2, height / 2);
                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
                break;
            case 270:
                detector = new FaceDetector(height, width, 1);
                matrix.postRotate(-90.0f, height / 2, width / 2);
                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
                break;
        }

        Bitmap copyBitmap = faceBitmap.copy(Bitmap.Config.RGB_565, true);

        FaceDetector.Face[] faces = new FaceDetector.Face[10];
        int faceNumber = detector.findFaces(copyBitmap, faces);
        if (faceNumber > 0) {
            if (!isSending) {
                isSending = true;
                String uuid = UUIDGenerator.getUUID();
                FaceEvent event = new FaceEvent(uuid);
                EventBus.getDefault().post(event.setEvent(200, faceNumber));
            }
        } else {
            Print.i("no face");
        }

        copyBitmap.recycle();
        faceBitmap.recycle();
        previewBitmap.recycle();
    }

    public class CameraBinder extends Binder {

        public CameraSerivice getService() {
            return CameraSerivice.this;
        }
    }

}
