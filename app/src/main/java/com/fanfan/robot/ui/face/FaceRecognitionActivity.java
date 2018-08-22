package com.fanfan.robot.ui.face;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.aip.entity.User;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.db.manager.FaceAuthDBManager;
import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.FaceAuth;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.model.UserInfo;
import com.fanfan.robot.presenter.LocalSoundPresenter;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ILocalSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.robot.other.udp.SocketManager;
import com.fanfan.robot.R;
import com.fanfan.robot.ui.face.act.AuthenticationActivity;
import com.fanfan.robot.ui.face.act.FaceCheckin2Activity;
import com.fanfan.robot.ui.face.act.FaceCheckinActivity;
import com.fanfan.robot.ui.face.act.FaceRegister2Activity;
import com.fanfan.robot.ui.face.act.FaceRegisterActivity;
import com.fanfan.robot.ui.face.act.InstagramPhotoActivity;
import com.fanfan.robot.ui.setting.act.face.local.RegActivity;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.net.DatagramPacket;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 人脸模块开始
 */
public class FaceRecognitionActivity extends BarBaseActivity implements
        ILocalSoundPresenter.ILocalSoundView,
        ISerialPresenter.ISerialView {

    @BindView(R.id.iv_face_check_in)
    ImageView ivFaceCheckIn;
    @BindView(R.id.iv_face_instagram)
    ImageView ivFaceInstagram;
    @BindView(R.id.iv_face_witness_contrast)
    ImageView ivFaceWitnessContrast;
    @BindView(R.id.iv_face_extraction)
    ImageView ivFaceExtraction;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, FaceRecognitionActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private String mInput;
    private FaceAuthDBManager mFaceAuthDBManager;

    private LocalSoundPresenter mSoundPresenter;
    private SerialPresenter mSerialPresenter;

    static {
        if (!OpenCVLoader.initDebug()) {
            System.out.println("opencv 初始化失败！");
        } else {
            System.loadLibrary("detection_based_tracker");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_recognition;
    }

    @Override
    protected void initData() {

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();
        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();

        mFaceAuthDBManager = new FaceAuthDBManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSoundPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundPresenter.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPresenter.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FaceCheckinActivity.CHECK_REQUESTCODE) {
            if (resultCode == FaceCheckinActivity.CHECK_RESULTCODE) {
                finish();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ServiceToActivityEvent event) {
        if (event.isOk()) {
            SerialBean serialBean = event.getBean();
            mSerialPresenter.onDataReceiverd(serialBean);
        } else {
            Print.e("ReceiveEvent error");
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onResultEvent(ReceiveEvent event) {
        if (event.isOk()) {
            DatagramPacket packet = event.getBean();
            if (!SocketManager.getInstance().isGetTcpIp) {
                SocketManager.getInstance().setUdpIp(packet.getAddress().getHostAddress(), packet.getPort());
            }
            String recvStr = new String(packet.getData(), 0, packet.getLength());
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, recvStr);
            Print.e(recvStr);
        } else {
            Print.e("ReceiveEvent error");
        }
    }

    @OnClick({R.id.iv_face_check_in, R.id.iv_face_instagram, R.id.iv_face_witness_contrast, R.id.iv_face_extraction})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_face_check_in:
                faceCheckIn();
                break;
            case R.id.iv_face_instagram:
                faceInstagram();
                break;
            case R.id.iv_face_witness_contrast:
                faceWitness();
                break;
            case R.id.iv_face_extraction:
                faceLiftingArea();
                break;
        }
    }


    private void faceInstagram() {
        viewAnimator(ivFaceInstagram, 30, -30, new AnimationListener.Stop() {
            @Override
            public void onStop() {
                InstagramPhotoActivity.newInstance(FaceRecognitionActivity.this);
            }
        });
    }

    private void faceLiftingArea() {
        viewAnimator(ivFaceExtraction, 30, 30, new AnimationListener.Stop() {
            @Override
            public void onStop() {
//                startExtraction();
                FaceRegister2Activity.newInstance(FaceRecognitionActivity.this);
            }
        });
    }

    private void faceWitness() {
        viewAnimator(ivFaceWitnessContrast, -30, 30, new AnimationListener.Stop() {
            @Override
            public void onStop() {
                AuthenticationActivity.newInstance(FaceRecognitionActivity.this);
            }
        });
    }

    private void faceCheckIn() {
        viewAnimator(ivFaceCheckIn, -30, -30, new AnimationListener.Stop() {
            @Override
            public void onStop() {
                FaceCheckin2Activity.newInstance(FaceRecognitionActivity.this);
            }
        });
    }


    private void viewAnimator(View view, int x, int y, AnimationListener.Stop stop) {
        ViewAnimator
                .animate(view)
                .scale(1f, 1.1f, 1f)
                .alpha(1, 0.7f, 1)
                .translationX(0, x, 0)
                .translationY(0, y, 0)
                .interpolator(new LinearInterpolator())
                .duration(500)
                .start()
                .onStop(stop);
    }

    private void addSpeakAnswer(String messageContent) {
        mSoundPresenter.doAnswer(messageContent);
    }

    private void addSpeakAnswer(int res) {
        mSoundPresenter.doAnswer(getResources().getString(res));
    }

    private void startExtraction() {
//        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
//                .title(R.string.title_face_extraction)
//                .content(R.string.input_content)
//                .inputType(
//                        InputType.TYPE_CLASS_TEXT
//                                | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
//                                | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
//                .negativeText(R.string.cancel)
//                .positiveText(R.string.confirm)
//                .inputRange(2, 6)
//                .alwaysCallInputCallback()
//                .input(getString(R.string.input_hint), "", false, new MaterialDialog.InputCallback() {
//                    @Override
//                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
//                        Print.e(input);
//                        mInput = String.valueOf(input);
//                    }
//                })
//                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        mSoundPresenter.onCompleted();
//                    }
//                })
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        judgeInput();
//                    }
//                })
//                .build();
//        materialDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                return true;
//            }
//        });
//        materialDialog.setCancelable(false);
//        materialDialog.show();
    }

    private void judgeInput() {
        FaceAuth faceAuth = mFaceAuthDBManager.queryByAuth(String.valueOf(mInput));
        if (faceAuth != null) {
            long faceAithId = faceAuth.getId();
            FaceRegisterActivity.newInstance(FaceRecognitionActivity.this, faceAithId);
        } else {
            FaceRegisterActivity.newInstance(FaceRecognitionActivity.this, String.valueOf(mInput));
        }

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    @Override
    public void showMsg(int msg) {
        showToast(msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void spakeMove(SpecialType type, String result) {
        mSoundPresenter.onCompleted();
        switch (type) {
            case Forward:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038002AA");
                break;
            case Backoff:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038008AA");
                break;
            case Turnleft:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038004AA");
                break;
            case Turnright:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038006AA");
                break;
        }
    }

    @Override
    public void openMap() {
        addSpeakAnswer(R.string.open_map);
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void artificial() {
        addSpeakAnswer(R.string.open_artificial);
    }

    @Override
    public void face(SpecialType type, String result) {
        switch (type) {
            case Face_lifting_area:
                faceLiftingArea();
                break;
            case Face_check_in:
                faceCheckIn();
                break;
            case Instagram:
                faceInstagram();
                break;
            case Witness_contrast:
                faceWitness();
                break;
        }
    }

    @Override
    public void control(SpecialType type, String result) {
        addSpeakAnswer(R.string.open_control);
    }

    @Override
    public void refLocalPage(String result) {
        addSpeakAnswer(R.string.open_local);
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void refLocalPage(String key1, String key2, String key3, String key4) {
        addSpeakAnswer(R.string.open_local);
    }

    @Override
    public void stopAll() {
        super.stopAll();
        mSoundPresenter.stopEvery();
        addSpeakAnswer("你好，这里是人脸识别页面");
    }

    @Override
    public void onMoveStop() {

    }

    @Override
    public void onMoveSpeak() {

    }

    @Override
    public void onAlarm(Alarm alarm) {

    }
}
