package com.fanfan.robot.presenter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.presenter.ipersenter.ICameraPresenter;
import com.fanfan.novel.utils.bitmap.BitmapUtils;
import com.fanfan.novel.utils.camera.CameraUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.fanfan.robot.app.common.Constants.unusual;

/**
 * Created by zhangyuanyuan on 2017/9/18.
 */

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CameraPresenter extends ICameraPresenter implements
        Camera.PreviewCallback,
        Camera.PictureCallback,
        Camera.ShutterCallback,
        Camera.FaceDetectionListener {

    private ICameraView mCameraView;

    private SurfaceHolder mHolder;
    private Camera mCamera;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private int previewWidth = 640;
    private int previewHeight = 480;
    private int pictureWidth = 640;
    private int pictureHeight = 480;

    private boolean isPreviewing = false;
    private Matrix mScaleMatrix = new Matrix();

    private int orientionOfCamera;
    private int orientionOfPhoto;
    private int orientionOfFace;

    private boolean isFirst;
    private boolean isFaceFirst;

    private boolean isCameraFaceDetection;

    public CameraPresenter(ICameraView baseView, SurfaceHolder surfaceHolder) {
        super(baseView);
        mCameraView = baseView;
        mHolder = surfaceHolder;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
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
    @Override
    public void openCamera() {
        if (null != mCamera) {
            return;
        }
        if (Camera.getNumberOfCameras() == 1) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        try {
            mCamera = Camera.open(mCameraId);
            mCamera.setFaceDetectionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            closeCamera();
            return;
        }
    }

    @Override
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
                    orientionOfCamera = CameraUtils.getInstance().getCameraDisplayOrientation((Activity) mCameraView.getContext(), mCameraId);
                } else {
                    // TODO: 2017/12/21  robot
                    orientionOfCamera = 90;
                }

                mCamera.setDisplayOrientation(orientionOfCamera);
                mCamera.startPreview();
                mCamera.setPreviewCallback(this);

                try {
                    if(mHolder != null) {
                        mCamera.setPreviewDisplay(mHolder);
                    }
                    mCamera.startPreview();//开启预览
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (parameters.getMaxNumDetectedFaces() > 1) {
                    mCamera.startFaceDetection();
                    isCameraFaceDetection = true;
                }
                isPreviewing = true;
                mCameraView.previewFinish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void changeCamera() {
        if (Camera.getNumberOfCameras() == 1) {
            Log.e("", "只有后置摄像头，不能切换");
            return;
        }
        closeCamera();
        setCameraId();
        openCamera();
        doStartPreview();
    }

    @Override
    public void setMatrix(int width, int height) {
        mScaleMatrix.setScale(width / (float) previewHeight, height / (float) previewWidth);
    }

    @Override
    public void cameraAutoFocus() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {//自动对焦
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    mCameraView.autoFocusSuccess();
                }
            }
        });
    }

    @Override
    public void cameraTakePicture() {
//        isPreviewing = false;
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(this, null, this);
        }
    }

    @Override
    public int getCameraId() {
        return mCameraId;
    }

    @Override
    public int getOrientionOfCamera() {
        if (!isFaceFirst) {
            orientionOfFace = orientionOfCamera;
            isFaceFirst = true;
        }
        return orientionOfFace;
    }

    @Override
    public void pictureTakenFinsih() {
        mCamera.startPreview();
        isPreviewing = true;
    }

    @Override
    public boolean cameraFaceDetection() {
        return isCameraFaceDetection;
    }

    private void setCameraId() {
        if (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
    }


    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Camera.Size size = camera.getParameters().getPreviewSize();
        YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, size.width, size.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, baos);
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
        if (!isFirst) {
            orientionOfPhoto = orientionOfCamera;
            isFirst = true;
        }
        if (faceNumber > 0) {
            if (orientionOfPhoto == orientionOfCamera) {
                mCameraView.tranBitmap(faceBitmap, faceNumber);
            }
        } else {
            mCameraView.noFace();
        }

        copyBitmap.recycle();
        faceBitmap.recycle();
        previewBitmap.recycle();
    }


    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        Bitmap saveBitmap = null;
        if (null != bytes) {
            mCamera.stopPreview();
            isPreviewing = false;

            Bitmap previewBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(0.0f, previewBitmap.getWidth() / 2, previewBitmap.getHeight() / 2);
            if (unusual) {
                matrix.setRotate(-orientionOfCamera);
            } else {
                // TODO: 2017/12/21  robot
                matrix.setRotate(orientionOfCamera);
            }
            saveBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, previewBitmap.getWidth(), previewBitmap.getHeight(), matrix, true);

        }
        if (null != saveBitmap) {
            long saveTime = System.currentTimeMillis();
            boolean save = BitmapUtils.saveBitmapToFile(saveBitmap, Constants.PICTURETAKEN, saveTime + ".jpg");
            if (save) {
                mCameraView.pictureTakenSuccess(Constants.PROJECT_PATH + Constants.PICTURETAKEN + File.separator + saveTime + ".jpg");
            } else {
                mCameraView.pictureTakenFail();
            }
        }
    }


    @Override
    public void onShutter() {

    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        if (!unusual) {
            mCameraView.setCameraFaces(faces);
        }
    }
}
