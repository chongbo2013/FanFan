package com.fanfan.robot.ui.setting.act.face.local;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.api.FaceApi;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.Feature;
import com.baidu.aip.entity.IdentifyRet;
import com.baidu.aip.entity.User;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.TexturePreviewView;
import com.baidu.aip.face.camera.CameraView;
import com.baidu.aip.face.camera.ICameraControl;
import com.baidu.aip.manager.FaceLiveness;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.PreferencesUtil;
import com.baidu.idl.facesdk.FaceInfo;
import com.baidu.idl.facesdk.FaceTracker;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.ui.setting.SettingActivity;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RgbVideoIdentityActivity extends BarBaseActivity implements FaceDetectManager.OnFaceDetectListener {

    private static final int FEATURE_DATAS_UNREADY = 1;
    private static final int IDENTITY_IDLE = 2;
    private static final int IDENTITYING = 3;

    @BindView(R.id.camera_layout)
    FrameLayout cameraLayout;
    @BindView(R.id.preview_view)
    TexturePreviewView previewView;
    @BindView(R.id.texture_view)
    TextureView textureView;
    @BindView(R.id.user_of_max_score_tv)
    TextView userOfMaxScoreTv;
    @BindView(R.id.match_avator_iv)
    ImageView matchAvatorIv;
    @BindView(R.id.match_user_tv)
    TextView matchUserTv;
    @BindView(R.id.score_tv)
    TextView scoreTv;
    @BindView(R.id.match_rl)
    RelativeLayout matchRl;
    @BindView(R.id.facesets_count_tv)
    TextView facesetsCountTv;
    @BindView(R.id.detect_duration_tv)
    TextView detectDurationTv;
    @BindView(R.id.rgb_liveness_duration_tv)
    TextView rgbLivenessDurationTv;
    @BindView(R.id.rgb_liveness_score_tv)
    TextView rgbLivenessScoreTv;
    @BindView(R.id.feature_duration_tv)
    TextView featureDurationTv;

    public static void newInstance(Activity context, String groupId) {
        Intent intent = new Intent(context, RgbVideoIdentityActivity.class);
        intent.putExtra("group_id", groupId);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private volatile int identityStatus = FEATURE_DATAS_UNREADY;

    // 用于检测人脸。
    private FaceDetectManager faceDetectManager;
    private String groupId = "";


    private Paint paint = new Paint();

    {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(30);
    }

    RectF rectF = new RectF();

    private String userIdOfMaxScore = "";
    private float maxScore = 0;

    @Override
    protected boolean whetherNotReturn() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_identify;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            groupId = intent.getStringExtra("group_id");
        }

        faceDetectManager = new FaceDetectManager(getApplicationContext());
        // 从系统相机获取图片帧。
        final CameraImageSource cameraImageSource = new CameraImageSource(this);
        // 图片越小检测速度越快，闸机场景640 * 480 可以满足需求。实际预览值可能和该值不同。和相机所支持的预览尺寸有关。
        // 可以通过 camera.getParameters().getSupportedPreviewSizes()查看支持列表。
        // cameraImageSource.getCameraControl().setPreferredPreviewSize(1280, 720);
        cameraImageSource.getCameraControl().setPreferredPreviewSize(640, 480);

        // 设置最小人脸，该值越小，检测距离越远，该值越大，检测性能越好。范围为80-200
        FaceSDKManager.getInstance().getFaceDetector().setMinFaceSize(100);
        // FaceSDKManager.getInstance().getFaceDetector().setNumberOfThreads(4);
        // 设置预览
        cameraImageSource.setPreviewView(previewView);
        // 设置图片源
        faceDetectManager.setImageSource(cameraImageSource);
        // 设置人脸过滤角度，角度越小，人脸越正，比对时分数越高
        faceDetectManager.getFaceFilter().setAngle(20);

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
        cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_FRONT);

        // 如果不设置，人脸框会镜像，显示不准
        previewView.getTextureView().setScaleX(-1);

        faceDetectManager.setOnFaceDetectListener(this);


        DBManager.getInstance().init(this);
        if (identityStatus != FEATURE_DATAS_UNREADY) {
            return;
        }
        es.submit(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                // android.os.Process.setThreadPriority (-4);
                FaceApi.getInstance().loadFacesFromDB(groupId);
                showToast("人脸数据加载完成，即将开始1：N");
                int count = FaceApi.getInstance().getGroup2Facesets().get(groupId).size();
                displayTip("底库人脸个数：" + count, facesetsCountTv);
                identityStatus = IDENTITY_IDLE;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // 开始检测
        faceDetectManager.start();
        faceDetectManager.setUseDetect(true);
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
        if (status == FaceTracker.ErrCode.OK.ordinal() && infos != null) {
            asyncIdentity(imageFrame, infos);
        }
        showFrame(imageFrame, infos);
    }


    private ExecutorService es = Executors.newSingleThreadExecutor();

    private void asyncIdentity(final ImageFrame imageFrame, final FaceInfo[] faceInfos) {
        if (identityStatus != IDENTITY_IDLE) {
            return;
        }

        es.submit(new Runnable() {
            @Override
            public void run() {
                if (faceInfos == null || faceInfos.length == 0) {
                    return;
                }
                int liveType = PreferencesUtil.getInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity.TYPE_NO_LIVENSS);
                if (liveType == LivenessSettingActivity.TYPE_NO_LIVENSS) {
                    identity(imageFrame, faceInfos[0]);
                } else if (liveType == LivenessSettingActivity.TYPE_RGB_LIVENSS) {

                    if (rgbLiveness(imageFrame, faceInfos[0]) > 0.9) {
                        identity(imageFrame, faceInfos[0]);
                    } else {
                        showToast("rgb活体分数过低");
                    }
                }
            }
        });
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


    private void identity(ImageFrame imageFrame, FaceInfo faceInfo) {

        float raw = Math.abs(faceInfo.headPose[0]);
        float patch = Math.abs(faceInfo.headPose[1]);
        float roll = Math.abs(faceInfo.headPose[2]);
        // 人脸的三个角度大于20不进行识别
        if (raw > 20 || patch > 20 || roll > 20) {
            return;
        }

        identityStatus = IDENTITYING;

        long starttime = System.currentTimeMillis();
        int[] argb = imageFrame.getArgb();
        int rows = imageFrame.getHeight();
        int cols = imageFrame.getWidth();
        int[] landmarks = faceInfo.landmarks;
        IdentifyRet identifyRet = FaceApi.getInstance().identity(argb, rows, cols, landmarks, groupId);


        final String userId = identifyRet.getUserId();
        final float score = identifyRet.getScore();

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (userIdOfMaxScore.equals(userId)) {
                    if (score < maxScore) {
                        return;
                    } else {
                        maxScore = score;
                        userOfMaxScoreTv.setText("userId：" + userId + "\nscore：" + score);
                        scoreTv.setText(String.valueOf(maxScore));
                        return;
                    }
                } else {
                    userIdOfMaxScore = userId;
                    maxScore = score;
                }


                scoreTv.setText(String.valueOf(maxScore));
                User user = FaceApi.getInstance().getUserInfo(groupId, userId);
                if (user == null) {
                    return;
                }
                matchUserTv.setText(user.getUserInfo() + "\n" + user.getUserName());
                List<Feature> featureList = user.getFeatureList();
                if (featureList != null && featureList.size() > 0) {
                    // featureTv.setText(new String(featureList.get(0).getFeature()));
                    File faceDir = FileUitls.getFaceDirectory();
                    if (faceDir != null && faceDir.exists()) {
                        File file = new File(faceDir, featureList.get(0).getImageName());
                        if (file != null && file.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            matchAvatorIv.setImageBitmap(bitmap);
                        }
                    }
                }
            }
        });

        identityStatus = IDENTITY_IDLE;
        displayTip("特征抽取对比耗时:" + (System.currentTimeMillis() - starttime), featureDurationTv);
    }

    private void displayTip(final String text, final TextView textView) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    /**
     * 绘制人脸框。
     */
    private void showFrame(ImageFrame imageFrame, FaceInfo[] faceInfos) {
        Canvas canvas = textureView.lockCanvas();
        if (canvas == null) {
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

        return rect;
    }
}
