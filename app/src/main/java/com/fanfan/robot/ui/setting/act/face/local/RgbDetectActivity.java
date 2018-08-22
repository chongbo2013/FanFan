package com.fanfan.robot.ui.setting.act.face.local;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.FaceCropper;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.FaceFilter;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.TexturePreviewView;
import com.baidu.aip.face.camera.CameraView;
import com.baidu.aip.face.camera.ICameraControl;
import com.baidu.aip.manager.FaceLiveness;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.ImageUtils;
import com.baidu.aip.utils.PreferencesUtil;
import com.baidu.idl.facesdk.FaceInfo;
import com.baidu.idl.facesdk.FaceTracker;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.seabreeze.log.Print;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RgbDetectActivity extends BarBaseActivity implements FaceDetectManager.OnFaceDetectListener {

    @BindView(R.id.camera_layout)
    FrameLayout cameraLayout;
    @BindView(R.id.preview_view)
    PreviewView previewView;
    @BindView(R.id.texture_view)
    TextureView textureView;
    @BindView(R.id.tip_tv)
    TextView tipTv;
    @BindView(R.id.detect_duration_tv)
    TextView detectDurationTv;
    @BindView(R.id.rgb_liveness_duration_tv)
    TextView rgbLivenessDurationTv;
    @BindView(R.id.rgb_liveness_score_tv)
    TextView rgbLivenessScoreTv;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, RgbDetectActivity.class);
        intent.putExtra("source", RegActivity.SOURCE_REG);
        context.startActivityForResult(intent, RegActivity.REQUEST_CODE_AUTO_DETECT);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private int source;

    // 用于检测人脸。
    private FaceDetectManager faceDetectManager;


    private RectF rectF = new RectF();

    private Paint paint = new Paint();

    {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(30);
    }

    @Override
    protected boolean whetherNotReturn() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_rgb_detect;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            source = intent.getIntExtra("source", -1);
        }

        faceDetectManager = new FaceDetectManager(getApplicationContext());

        // 从系统相机获取图片帧。
        CameraImageSource cameraImageSource = new CameraImageSource(this);
        // 图片越小检测速度越快，闸机场景640 * 480 可以满足需求。实际预览值可能和该值不同。和相机所支持的预览尺寸有关。
        // 可以通过 camera.getParameters().getSupportedPreviewSizes()查看支持列表。
        cameraImageSource.getCameraControl().setPreferredPreviewSize(640, 480);

        // 设置最小人脸，该值越小，检测距离越远，该值越大，检测性能越好。范围为80-200
        FaceSDKManager.getInstance().getFaceDetector().setMinFaceSize(100);
//        FaceSDKManager.getInstance().getFaceDetector().setNumberOfThreads(4);

        // 设置预览
        cameraImageSource.setPreviewView(previewView);

        // 设置图片源
        faceDetectManager.setImageSource(cameraImageSource);
        faceDetectManager.setUseDetect(true);
        // 传给facesdk的图片高宽不同，将不能正确检出人脸，需要clear前面的trackedFaces
        FaceSDKManager.getInstance().getFaceDetector().clearTrackedFaces();

        textureView.setOpaque(false);
        // 不需要屏幕自动变黑。
        textureView.setKeepScreenOn(true);

        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (isPortrait) {
            previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
            // 相机坚屏模式
            cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_PORTRAIT);
        } else {
            previewView.setScaleType(PreviewView.ScaleType.FIT_HEIGHT);
            // 相机横屏模式
            cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_HORIZONTAL);
        }

        //  选择使用前置摄像头
        cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_FRONT);

        //  选择使用usb摄像头
//        cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_USB);
        // 如果不设置，人脸框会镜像，显示不准
        previewView.getTextureView().setScaleX(-1);

        // 选择使用后置摄像头
//        cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_BACK);
//        previewView.getTextureView().setScaleX(-1);

        faceDetectManager.setOnFaceDetectListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        // 开始检测
        faceDetectManager.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 结束检测。
        faceDetectManager.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        faceDetectManager.stop();
    }


    @Override
    public void onDetectFace(int status, FaceInfo[] infos, ImageFrame imageFrame) {

//        Bitmap bitmap = Bitmap.createBitmap(imageFrame.getArgb(), imageFrame.getWidth(), imageFrame.getHeight(), Bitmap.Config.ARGB_8888);

        if (status == FaceTracker.ErrCode.OK.ordinal() && infos != null) {
            FaceInfo faceInfo = infos[0];

            if (faceInfo.mConf < 0.6) {
                displayTip("人脸置信度太低");
                return;
            }

            float[] headPose = faceInfo.headPose;
            if (Math.abs(headPose[0]) > 20 || Math.abs(headPose[1]) > 20 || Math.abs(headPose[2]) > 20) {
                displayTip("人脸置角度太大，请正对屏幕");
                return;
            }

            int width = imageFrame.getWidth();
            int height = imageFrame.getHeight();
            // 判断人脸大小，若人脸超过屏幕二分一，则提示文案“人脸离手机太近，请调整与手机的距离”；
            // 若人脸小于屏幕三分一，则提示“人脸离手机太远，请调整与手机的距离”
            float ratio = (float) faceInfo.mWidth / (float) height;
            Log.i("liveness_ratio", "ratio=" + ratio);
            if (ratio > 0.6) {
                displayTip("人脸离屏幕太近，请调整与屏幕的距离");
            } else if (ratio < 0.2) {
                displayTip("人脸离屏幕太远，请调整与屏幕的距离");
            } else if (faceInfo.mCenter_x > width * 3 / 4) {
                displayTip("人脸在屏幕中太靠右");
            } else if (faceInfo.mCenter_x < width / 4) {
                displayTip("人脸在屏幕中太靠左");
            } else if (faceInfo.mCenter_y > height * 3 / 4) {
                displayTip("人脸在屏幕中太靠下");
            } else if (faceInfo.mCenter_x < height / 4) {
                displayTip("人脸在屏幕中太靠上");
            }

            int liveType = PreferencesUtil.getInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity.TYPE_NO_LIVENSS);
            if (liveType == LivenessSettingActivity.TYPE_NO_LIVENSS) {
                saveFace(faceInfo, imageFrame);
            } else if (liveType == LivenessSettingActivity.TYPE_RGB_LIVENSS) {
                if (rgbLiveness(imageFrame, faceInfo) > 0.9) {
                    saveFace(faceInfo, imageFrame);
                } else {
                    showToast("rgb活体分数过低");
                }
            }


        } else {
            if (status == FaceTracker.ErrCode.IMG_BLURED.ordinal() ||
                    status == FaceTracker.ErrCode.PITCH_OUT_OF_DOWN_MAX_RANGE.ordinal() ||
                    status == FaceTracker.ErrCode.PITCH_OUT_OF_UP_MAX_RANGE.ordinal() ||
                    status == FaceTracker.ErrCode.YAW_OUT_OF_LEFT_MAX_RANGE.ordinal() ||
                    status == FaceTracker.ErrCode.YAW_OUT_OF_RIGHT_MAX_RANGE.ordinal()) {
                displayTip("请静止平视屏幕");
            } else if (status == FaceTracker.ErrCode.POOR_ILLUMINATION.ordinal()) {
                displayTip("光线太暗，请到更明亮的地方");
            } else if (status == FaceTracker.ErrCode.UNKNOW_TYPE.ordinal() ||
                    status == FaceTracker.ErrCode.NO_FACE_DETECTED.ordinal()) {
                displayTip("未检测到人脸");
            }
        }

        showFrame(imageFrame, infos);
    }


    private float rgbLiveness(ImageFrame imageFrame, FaceInfo faceInfo) {

        long starttime = System.currentTimeMillis();
        final float rgbScore = FaceLiveness.getInstance().rgbLiveness(imageFrame.getArgb(), imageFrame
                .getWidth(), imageFrame.getHeight(), faceInfo.landmarks);
        final long duration = System.currentTimeMillis() - starttime;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rgbLivenessDurationTv.setVisibility(View.VISIBLE);
                rgbLivenessScoreTv.setVisibility(View.VISIBLE);
                rgbLivenessDurationTv.setText("RGB活体耗时：" + duration);
                rgbLivenessScoreTv.setText("RGB活体得分：" + rgbScore);
            }
        });

        return rgbScore;
    }


    private void saveFace(FaceInfo faceInfo, ImageFrame imageFrame) {
        final Bitmap bitmap = FaceCropper.getFace(imageFrame.getArgb(), faceInfo, imageFrame.getWidth());
        if (source == RegActivity.SOURCE_REG) {
            // 注册来源保存到注册人脸目录
            File faceDir = FileUitls.getFaceDirectory();
            if (faceDir != null) {
                String imageName = UUID.randomUUID().toString();
                File file = new File(faceDir, imageName);
                // 压缩人脸图片至300 * 300，减少网络传输时间
                ImageUtils.resize(bitmap, file, 300, 300);
                Intent intent = new Intent();
                intent.putExtra("file_path", file.getAbsolutePath());
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                showToast("注册人脸目录未找到");
            }
        } else {
            try {
                // 其他来源保存到临时目录
                final File file = File.createTempFile(UUID.randomUUID().toString() + "", ".jpg");
                // 人脸识别不需要整张图片。可以对人脸区别进行裁剪。减少流量消耗和，网络传输占用的时间消耗。
                ImageUtils.resize(bitmap, file, 300, 300);
                Intent intent = new Intent();
                intent.putExtra("file_path", file.getAbsolutePath());
                setResult(Activity.RESULT_OK, intent);
                finish();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void displayTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tipTv.setText(tip);
            }
        });
    }

    /**
     * 绘制人脸框。
     */
    private void showFrame(ImageFrame imageFrame, FaceInfo[] faceInfos) {
        Canvas canvas = textureView.lockCanvas();
        if (canvas == null) {
            textureView.unlockCanvasAndPost(canvas);
            return;
        }
        if (faceInfos == null || faceInfos.length == 0) {
            // 清空canvas
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            textureView.unlockCanvasAndPost(canvas);
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        FaceInfo faceInfo = faceInfos[0];

        rectF.set(getFaceRect(faceInfo, imageFrame));

        // 检测图片的坐标和显示的坐标不一样，需要转换。
        previewView.mapFromOriginalRect(rectF);

        float yaw = Math.abs(faceInfo.headPose[0]);
        float patch = Math.abs(faceInfo.headPose[1]);
        float roll = Math.abs(faceInfo.headPose[2]);
        if (yaw > 20 || patch > 20 || roll > 20) {
            // 不符合要求，绘制黄框
            paint.setColor(Color.YELLOW);

            String text = "请正视屏幕";
            float width = paint.measureText(text) + 50;
            float x = rectF.centerX() - width / 2;
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, x + 25, rectF.top - 20, paint);
            paint.setColor(Color.YELLOW);
        } else {
            // 符合检测要求，绘制绿框
            paint.setColor(Color.GREEN);
        }
        paint.setStyle(Paint.Style.STROKE);
        // 绘制框
        canvas.drawRect(rectF, paint);
        textureView.unlockCanvasAndPost(canvas);
    }

    /**
     * 绘制人脸框。
     *
     * @param model 追踪到的人脸
     */
    private void showFrame(FaceFilter.TrackedModel model) {
        Canvas canvas = textureView.lockCanvas();
        if (canvas == null) {
            return;
        }
        // 清空canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (model != null) {
            model.getImageFrame().retain();
            rectF.set(model.getFaceRect());

            // 检测图片的坐标和显示的坐标不一样，需要转换。
            previewView.mapFromOriginalRect(rectF);
            if (model.meetCriteria()) {
                // 符合检测要求，绘制绿框
                paint.setColor(Color.GREEN);
            } else {
                // 不符合要求，绘制黄框
                paint.setColor(Color.YELLOW);

                String text = "请正视屏幕";
                float width = paint.measureText(text) + 50;
                float x = rectF.centerX() - width / 2;
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawText(text, x + 25, rectF.top - 20, paint);
                paint.setColor(Color.YELLOW);
            }
            paint.setStyle(Paint.Style.STROKE);
            // 绘制框
            canvas.drawRect(rectF, paint);
        }
        textureView.unlockCanvasAndPost(canvas);
    }

    /**
     * 获取人脸框区域。
     *
     * @return 人脸框区域
     */
    public Rect getFaceRect(FaceInfo faceInfo, ImageFrame frame) {
        Rect rect = new Rect();
        int[] points = new int[8];
        faceInfo.getRectPoints(points);

        int left = points[2];
        int top = points[3];
        int right = points[6];
        int bottom = points[7];
        int width = (right - left);
        int height = (bottom - top);

        left = (int) (faceInfo.mCenter_x - width / 2);
        top = (int) (faceInfo.mCenter_y - height / 2);


        rect.top = top < 0 ? 0 : top;
        rect.left = left < 0 ? 0 : left;
        rect.right = (left + width) > frame.getWidth() ? frame.getWidth() : (left + width);
        rect.bottom = (top + height) > frame.getHeight() ? frame.getHeight() : (top + height);

        Print.e("rect (" + top + ", " + left + ", " + right + ", " + bottom + ")");

        return rect;
    }
}
