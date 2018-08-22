package com.fanfan.robot.ui.face.act;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.TextureView;
import android.view.View;
import android.widget.LinearLayout;
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
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.db.manager.CheckInDBManager;
import com.fanfan.robot.model.CheckIn;
import com.fanfan.robot.model.FaceAuth;
import com.fanfan.robot.model.UserInfo;
import com.fanfan.robot.ui.face.act.sign.SignAllActivity;
import com.fanfan.robot.ui.setting.act.face.local.LivenessSettingActivity;
import com.fanfan.robot.view.CircleImageView;
import com.seabreeze.log.Print;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FaceCheckin2Activity extends BarBaseActivity implements FaceDetectManager.OnFaceDetectListener {

    private static final int FEATURE_DATAS_UNREADY = 1;
    private static final int IDENTITY_IDLE = 2;
    private static final int IDENTITYING = 3;

    @BindView(R.id.preview_view)
    TexturePreviewView previewView;
    @BindView(R.id.texture_view)
    TextureView textureView;
    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ic_head)
    CircleImageView icHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_again)
    TextView tvAgain;
    @BindView(R.id.confirm_layout)
    LinearLayout confirmLayout;
    @BindView(R.id.tv_sign_info)
    TextView tvSignInfo;
    @BindView(R.id.tv_sign_all)
    TextView tvSignAll;
    @BindView(R.id.backdrop)
    RelativeLayout backdrop;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, FaceCheckin2Activity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private CheckInDBManager mCheckInDBManager;

    private volatile int identityStatus = FEATURE_DATAS_UNREADY;

    // 用于检测人脸。
    private FaceDetectManager faceDetectManager;

    private User user;

    @Override
    protected boolean whetherNotReturn() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_check_in2;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {

        mCheckInDBManager = new CheckInDBManager();
        confirmLayout.setVisibility(View.GONE);

        faceDetectManager = new FaceDetectManager(getApplicationContext());
        FaceSDKManager.getInstance().getFaceDetector().setMinFaceSize(100);

        CameraImageSource cameraImageSource = new CameraImageSource(this);
        cameraImageSource.getCameraControl().setPreferredPreviewSize(640, 480);
        faceDetectManager.setImageSource(cameraImageSource);
        faceDetectManager.getFaceFilter().setAngle(20);
        cameraImageSource.setPreviewView(previewView);
        textureView.setOpaque(false);
        textureView.setKeepScreenOn(true);
        previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
        cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_PORTRAIT);
        cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_FRONT);
        previewView.getTextureView().setScaleX(-1);
        faceDetectManager.setUseDetect(true);
        faceDetectManager.setOnFaceDetectListener(this);

        DBManager.getInstance().init(this);
        if (identityStatus != FEATURE_DATAS_UNREADY) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                String groupId = UserInfo.getInstance().getIdentifier();
                FaceApi.getInstance().loadFacesFromDB(groupId);
                showToast("人脸数据加载完成，即将开始1：N");
                int count = FaceApi.getInstance().getGroup2Facesets().get(groupId).size();
                displayTip("底库人脸个数：" + count, tvSignInfo);
                e.onNext(IDENTITY_IDLE);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                        identityStatus = integer;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

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


    private void displayTip(final String text, final TextView textView) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    @Override
    public void onDetectFace(int status, FaceInfo[] infos, ImageFrame imageFrame) {
        if (status == FaceTracker.ErrCode.OK.ordinal() && infos != null) {
            asyncIdentity(imageFrame, infos);
        }
        showFrame(imageFrame, infos);
    }

    @SuppressLint("CheckResult")
    private void asyncIdentity(final ImageFrame imageFrame, final FaceInfo[] faceInfos) {
        if (identityStatus != IDENTITY_IDLE) {
            return;
        }

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (faceInfos == null || faceInfos.length == 0) {
                    return;
                }

                FaceInfo faceInfo = faceInfos[0];

                int liveType = PreferencesUtil.getInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity.TYPE_NO_LIVENSS);
                if (liveType == LivenessSettingActivity.TYPE_NO_LIVENSS) {
                    displayTip("选择rgb检测", tvSignInfo);
                } else if (liveType == LivenessSettingActivity.TYPE_RGB_LIVENSS) {

                    if (FaceLiveness.getInstance().rgbLiveness(imageFrame.getArgb(),
                            imageFrame.getWidth(), imageFrame.getHeight(), faceInfo.landmarks) > 0.9) {

                        float raw = Math.abs(faceInfo.headPose[0]);
                        float patch = Math.abs(faceInfo.headPose[1]);
                        float roll = Math.abs(faceInfo.headPose[2]);
                        // 人脸的三个角度大于20不进行识别
                        if (raw > 20 || patch > 20 || roll > 20) {
                            return;
                        }

                        int[] argb = imageFrame.getArgb();
                        int rows = imageFrame.getHeight();
                        int cols = imageFrame.getWidth();
                        int[] landmarks = faceInfo.landmarks;
                        IdentifyRet identifyRet = FaceApi.getInstance().identity(argb, rows, cols, landmarks, UserInfo.getInstance().getIdentifier());

                        float score = identifyRet.getScore();
                        if (score > 70) {
                            String userId = identifyRet.getUserId();
                            e.onNext(userId);
                        } else {
                            displayTip("识别率较低，请正对摄像头或您未注册", tvSignInfo);
                        }

                    } else {
                        displayTip("rgb活体分数过低", tvSignInfo);
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String userId) throws Exception {

                        user = FaceApi.getInstance().getUserInfo(UserInfo.getInstance().getIdentifier(), userId);
                        if (user == null) {
                            identityStatus = IDENTITY_IDLE;
                            return;
                        }
                        String userName = user.getUserName();
                        if (userName == null) {
                            identityStatus = IDENTITY_IDLE;
                            return;
                        }

                        List<CheckIn> checkIns = mCheckInDBManager.queryByName(userName);
                        if (checkIns != null && checkIns.size() > 0) {
                            Print.e(checkIns);
                            Collections.sort(checkIns);

                            CheckIn checkIn = checkIns.get(0);
                            if (TimeUtils.isToday(checkIn.getTime())) {
                                displayTip("今日您已签到", tvSignInfo);

                                identityStatus = IDENTITY_IDLE;
                                return;
                            }
                        }

                        faceDetectManager.stop();

                        confirmLayout.setVisibility(View.VISIBLE);

                        tvName.setText(user.getUserName());
                        List<Feature> featureList = user.getFeatureList();
                        if (featureList != null && featureList.size() > 0) {
                            // featureTv.setText(new String(featureList.get(0).getFeature()));
                            File faceDir = FileUitls.getFaceDirectory();
                            if (faceDir != null && faceDir.exists()) {
                                File file = new File(faceDir, featureList.get(0).getImageName());
                                if (file != null && file.exists()) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    icHead.setImageBitmap(bitmap);
                                }
                            }
                        }
                    }
                });

    }


    @SuppressLint("DefaultLocale")
    @OnClick({R.id.tv_confirm, R.id.tv_again, R.id.tv_sign_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm:
                if (user == null) {
                    return;
                }
                CheckIn checkIn = new CheckIn();
                checkIn.setName(user.getUserName());
                checkIn.setTime(System.currentTimeMillis());
                boolean insert = mCheckInDBManager.insert(checkIn);
                if (!insert) {
                    return;
                }
                tvSignInfo.setText(String.format("%s 签到成功", user.getUserId()));
                List<CheckIn> checkIns = mCheckInDBManager.queryByName(user.getUserId());
                List<CheckIn> screenIns = new ArrayList<>();
                for (CheckIn in : checkIns) {
                    if (TimeUtils.isToday(in.getTime())) {
                        screenIns.add(in);
                    }
                }
                if (screenIns.size() > 0) {
                    Collections.sort(screenIns);
                    for (int i = 0; i < screenIns.size() - 1; i++) {
                        mCheckInDBManager.delete(screenIns.get(i));
                    }
                }
                List<CheckIn> todayList = mCheckInDBManager.queryByToday();
                if (todayList == null || todayList.size() == 0) {
                    displayTip(String.format("今日第 %d 位签到", 1), tvSignInfo);
                } else {
                    displayTip(String.format("今日第 %d%d 位签到", todayList.size(), 1), tvSignInfo);
                }

                confirmLayout.setVisibility(View.GONE);

                identityStatus = IDENTITYING;
                faceDetectManager.start();
                break;
            case R.id.tv_again:

                confirmLayout.setVisibility(View.GONE);

                identityStatus = IDENTITYING;
                faceDetectManager.start();
                break;
            case R.id.tv_sign_all:
                SignAllActivity.newInstance(this);
                break;
        }
    }


    private Paint paint = new Paint();

    {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(30);
    }

    RectF rectF = new RectF();

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
