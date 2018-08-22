package com.fanfan.robot.ui.auxiliary;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.robot.listener.base.TtsListener;
import com.fanfan.robot.other.udp.SocketManager;
import com.fanfan.novel.utils.FucUtil;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.robot.R;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.service.CameraSerivice;
import com.fanfan.robot.other.event.FaceEvent;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.DatagramPacket;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by android on 2018/2/26.
 */

public class LockActivity extends Activity implements ISerialPresenter.ISerialView,
        TtsListener.SynListener {

    @BindView(R.id.iv_splash_back)
    ImageView ivSplashBack;

    public static boolean isShow = false;

    public static void newInstance(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, LockActivity.class);
                context.startActivity(intent);
            }
        } else {
            Intent intent = new Intent(context, LockActivity.class);
            context.startActivity(intent);
        }
    }

    private CameraSerivice cameraSerivice;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CameraSerivice.CameraBinder cameraBinder = (CameraSerivice.CameraBinder) service;
            cameraSerivice = cameraBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private SerialPresenter mSerialPresenter;

    private String mEngineType;

    private SpeechSynthesizer mTts;
    private TtsListener mTtsListener;

    private boolean isSpeak;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockactivity);
        ButterKnife.bind(this);

        mEngineType = RobotInfo.getInstance().getEngineType();
        RobotInfo.getInstance().setEngineType(SpeechConstant.TYPE_LOCAL);

        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();

        mTtsListener = new TtsListener(this);
        initTts();
        buildTts();

        Intent bindIntent = new Intent(this, CameraSerivice.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        ImageLoader.loadImage(this, ivSplashBack, R.mipmap.splash_bg, false, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {

        isShow = false;

        unbindService(connection); // 解绑服务
        mTtsListener = null;

        if (mTts != null) {
            mTts.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        brief();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(FaceEvent event) {
        if (event.isOk()) {
            Print.e(event.getBean());
            brief();
        } else {
            Print.e("ReceiveEvent error");
        }
    }

    private void brief() {
        cameraSerivice.closeCamera();
        if (!isSpeak) {
            isSpeak = true;
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8003AA");
            doAnswer("你好，欢迎您！");
        }
    }

    public void initTts() {
        if (mTts == null) {
            mTts = SpeechSynthesizer.createSynthesizer(this, new InitListener() {
                @Override
                public void onInit(int code) {
                    if (code != ErrorCode.SUCCESS) {
                        Print.e("初始化失败，错误码：" + code);
                    }
                    Print.e("local initTts success");
                }
            });
        }
    }

    public void buildTts() {
        if (mTts == null) {
            throw new NullPointerException(" mTts is null");
        }
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, FucUtil.getResTtsPath(this, RobotInfo.getInstance().getTtsLocalTalker()));
        mTts.setParameter(SpeechConstant.VOICE_NAME, RobotInfo.getInstance().getTtsLocalTalker());
        mTts.setParameter(SpeechConstant.SPEED, "60");
        mTts.setParameter(SpeechConstant.PITCH, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "100");
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    public void doAnswer(String answer) {
        mTts.startSpeaking(answer, mTtsListener);
    }

    @Override
    public void onCompleted() {
        RobotInfo.getInstance().setEngineType(mEngineType);
        isSpeak = false;
        finish();
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {

    }

    @Override
    public void showMsg(int msg) {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void stopAll() {
        brief();
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
