package com.fanfan.robot.ui.face.act;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.api.FaceApi;
import com.baidu.aip.entity.ARGBImg;
import com.baidu.aip.entity.Feature;
import com.baidu.aip.entity.User;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.FaceCropper;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.TexturePreviewView;
import com.baidu.aip.face.camera.CameraView;
import com.baidu.aip.face.camera.ICameraControl;
import com.baidu.aip.manager.FaceDetector;
import com.baidu.aip.manager.FaceLiveness;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.FeatureUtils;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.ImageUtils;
import com.baidu.aip.utils.PreferencesUtil;
import com.baidu.idl.facesdk.FaceInfo;
import com.baidu.idl.facesdk.FaceTracker;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.model.UserInfo;
import com.fanfan.robot.ui.setting.act.face.local.LivenessSettingActivity;
import com.seabreeze.log.Print;

import java.io.File;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FaceRegister2Activity extends BarBaseActivity implements FaceDetectManager.OnFaceDetectListener {

    @BindView(R.id.preview_view)
    TexturePreviewView previewView;
    @BindView(R.id.texture_view)
    TextureView textureView;
    @BindView(R.id.tip_tv)
    TextView tipTv;
    @BindView(R.id.username_et)
    EditText usernameEt;
    @BindView(R.id.sure_btn)
    Button sureBtn;
    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.avatar_iv)
    ImageView avatarIv;
    @BindView(R.id.camera_layout)
    RelativeLayout cameraLayout;
    @BindView(R.id.camera_bottom)
    LinearLayout cameraBottom;
    @BindView(R.id.testimg)
    ImageView testimg;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, FaceRegister2Activity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // 用于检测人脸。
    private FaceDetectManager faceDetectManager;

    private User user;

    private String absolutePath;

    @Override
    protected boolean whetherNotReturn() {
        return true;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_register2;
    }

    @Override
    protected void initData() {
        cameraBottom.setVisibility(View.GONE);

        faceDetectManager = new FaceDetectManager(getApplicationContext());

        FaceSDKManager.getInstance().getFaceDetector().setMinFaceSize(100);

        CameraImageSource cameraImageSource = new CameraImageSource(this);
        cameraImageSource.getCameraControl().setPreferredPreviewSize(640, 480);
        cameraImageSource.setPreviewView(previewView);

        faceDetectManager.setImageSource(cameraImageSource);
        faceDetectManager.setUseDetect(true);

        textureView.setOpaque(false);
        textureView.setKeepScreenOn(true);
        previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
        cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_PORTRAIT);
        cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_FRONT);
        previewView.getTextureView().setScaleX(-1);

        faceDetectManager.setOnFaceDetectListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        cameraBottom.setVisibility(View.GONE);
        cameraLayout.setVisibility(View.VISIBLE);
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


    private void displayTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tipTv.setText(tip);
            }
        });
    }

    @OnClick(R.id.sure_btn)
    public void onViewClicked() {

        String userName = usernameEt.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            showToast("username不能为空");
            return;
        }

        String uid = UUID.randomUUID().toString();

        user = new User();
        user.setUserId(uid);
        user.setUserInfo(uid);
        user.setUserName(userName);
        user.setGroupId(UserInfo.getInstance().getIdentifier());

        register();
    }


    @Override
    public void onDetectFace(int status, FaceInfo[] infos, ImageFrame imageFrame) {
//        final Bitmap bitmap1 = Bitmap.createBitmap(imageFrame.getArgb(), imageFrame.getWidth(), imageFrame.getHeight(), Bitmap.Config.ARGB_8888);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                testimg.setImageBitmap(bitmap1);
//            }
//        });
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
                showToast("选择rgb检测");
            } else if (liveType == LivenessSettingActivity.TYPE_RGB_LIVENSS) {
                if (FaceLiveness.getInstance().rgbLiveness(imageFrame.getArgb(),
                        imageFrame.getWidth(), imageFrame.getHeight(), faceInfo.landmarks) > 0.9) {

                    final Bitmap bitmap = FaceCropper.getFace(imageFrame.getArgb(), faceInfo, imageFrame.getWidth());
                    File faceDir = FileUitls.getFaceDirectory();
                    if (faceDir != null) {
                        String imageName = UUID.randomUUID().toString();
                        File file = new File(faceDir, imageName);
                        // 压缩人脸图片至300 * 300，减少网络传输时间
                        ImageUtils.resize(bitmap, file, 300, 300);
                        absolutePath = file.getAbsolutePath();

                        faceDetectManager.stop();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                cameraLayout.setVisibility(View.GONE);
                                cameraBottom.setVisibility(View.VISIBLE);

                                avatarIv.setImageBitmap(bitmap);
                            }
                        });

                    } else {
                        showToast("注册人脸目录未找到");
                    }

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

//                showFrame(imageFrame, infos);
    }

    @SuppressLint("CheckResult")
    private void register() {

        if (TextUtils.isEmpty(absolutePath)) {
            showToast("人脸文件不存在");
            return;
        }
        final File file = new File(absolutePath);
        if (!file.exists()) {
            showToast("人脸文件不存在");
            return;
        }

        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                ARGBImg argbImg = FeatureUtils.getARGBImgFromPath(absolutePath);
                byte[] bytes = new byte[2048];
                int ret = FaceSDKManager.getInstance().getFaceFeature().faceFeature(argbImg, bytes);
                if (ret == FaceDetector.NO_FACE_DETECTED) {
                    showToast("人脸太小（必须打于最小检测人脸minFaceSize），或者人脸角度太大，人脸不是朝上");
                } else if (ret != -1) {
                    Feature feature = new Feature();
                    feature.setGroupId(UserInfo.getInstance().getIdentifier());
                    feature.setUserId(user.getUserId());
                    feature.setFeature(bytes);
                    feature.setImageName(file.getName());

                    user.getFeatureList().add(feature);
                    if (FaceApi.getInstance().userAdd(user)) {
                        showToast("注册成功");
                        e.onNext(true);
                        return;
                    } else {
                        showToast("注册失败");
                    }

                } else {
                    showToast("抽取特征失败");
                }
                e.onNext(false);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        finish();
                    }
                });
    }
}
