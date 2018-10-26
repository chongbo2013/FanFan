package com.fanfan.robot.ui.main;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.aip.ImageFrame;
import com.baidu.aip.api.FaceApi;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.Group;
import com.baidu.aip.entity.IdentifyRet;
import com.baidu.aip.entity.User;
import com.baidu.aip.face.ArgbPool;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.FaceFilter;
import com.baidu.aip.face.FaceProcessor;
import com.baidu.aip.face.OnFrameAvailableListener;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.TexturePreviewView;
import com.baidu.aip.face.camera.CameraView;
import com.baidu.aip.face.camera.ICameraControl;
import com.baidu.aip.manager.FaceLiveness;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.PreferencesUtil;
import com.baidu.idl.facesdk.FaceInfo;
import com.baidu.idl.facesdk.FaceTracker;
import com.fanfan.novel.face.FaceInitManager;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.novel.utils.camera.CameraUtils;
import com.fanfan.novel.utils.youdao.TranslateLanguage;
import com.fanfan.robot.app.NovelApp;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.app.enums.RobotType;
import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.db.manager.CheckInDBManager;
import com.fanfan.robot.db.manager.FaceAuthDBManager;
import com.fanfan.robot.db.manager.NavigationDBManager;
import com.fanfan.robot.db.manager.VoiceDBManager;
import com.fanfan.novel.im.init.LoginBusiness;
import com.fanfan.robot.listener.base.recog.AlarmListener;
import com.fanfan.robot.listener.base.recog.IRecogListener;
import com.fanfan.robot.listener.base.recog.cloud.MyRecognizer;
import com.fanfan.robot.listener.base.synthesizer.EarListener;
import com.fanfan.robot.listener.base.synthesizer.ISynthListener;
import com.fanfan.robot.listener.base.synthesizer.cloud.MySynthesizer;
import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.CheckIn;
import com.fanfan.robot.model.FaceAuth;
import com.fanfan.robot.model.NavigationBean;
import com.fanfan.robot.model.RobotBean;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.model.SpeakBean;
import com.fanfan.robot.model.UserInfo;
import com.fanfan.robot.model.VoiceBean;
import com.fanfan.robot.model.xf.Cookbook;
import com.fanfan.robot.model.xf.News;
import com.fanfan.robot.model.xf.Poetry;
import com.fanfan.robot.model.xf.englishEveryday.EnglishEveryday;
import com.fanfan.robot.model.xf.radio.Radio;
import com.fanfan.robot.model.xf.train.Train;
import com.fanfan.novel.pointdown.db.DownloadDBDao;
import com.fanfan.novel.pointdown.model.Progress;
import com.fanfan.robot.presenter.CameraPresenter;
import com.fanfan.robot.presenter.ChatPresenter;
import com.fanfan.robot.presenter.LineSoundPresenter;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.IChatPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ISynthesizerPresenter;
import com.fanfan.robot.service.LoadFileService;
import com.fanfan.robot.service.PlayService;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.service.SpeakService;
import com.fanfan.robot.service.UdpService;
import com.fanfan.robot.other.cache.MusicCache;
import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.robot.listener.music.EventCallback;
import com.fanfan.robot.other.udp.SocketManager;
import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.novel.utils.system.FileUtil;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.system.PreferencesUtils;
import com.fanfan.novel.utils.customtabs.IntentUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.dagger.componet.DaggerMainComponet;
import com.fanfan.robot.dagger.module.MainModule;
import com.fanfan.robot.db.manager.DanceDBManager;
import com.fanfan.robot.model.Dance;
import com.fanfan.robot.presenter.ipersenter.ILineSoundPresenter;
import com.fanfan.robot.ui.auxiliary.PPTActivity;
import com.fanfan.robot.ui.auxiliary.PanoramicMapActivity;
import com.fanfan.robot.ui.face.FaceRecognitionActivity;
import com.fanfan.robot.ui.face.act.DetectfaceActivity;
import com.fanfan.robot.ui.map.AMapActivity;
import com.fanfan.robot.ui.media.MultimediaActivity;
import com.fanfan.robot.ui.media.act.DanceActivity;
import com.fanfan.robot.ui.naviga.NavigationActivity;
import com.fanfan.robot.ui.setting.SettingActivity;
import com.fanfan.robot.ui.setting.act.face.local.LivenessSettingActivity;
import com.fanfan.robot.ui.setting.act.other.GreetingActivity;
import com.fanfan.robot.ui.site.PublicNumberActivity;
import com.fanfan.robot.ui.video.VideoIntroductionActivity;
import com.fanfan.robot.ui.voice.ProblemConsultingActivity;
import com.fanfan.robot.view.ChatTextView;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.base.Constant;
import com.fanfan.youtu.api.face.bean.FaceIdentify;
import com.fanfan.youtu.api.face.bean.GetInfo;
import com.fanfan.youtu.api.face.bean.detectFace.DetectFace;
import com.fanfan.youtu.api.face.bean.detectFace.Face;
import com.fanfan.youtu.api.face.event.DetectFaceEvent;
import com.fanfan.youtu.api.face.event.FaceIdentifyEvent;
import com.fanfan.youtu.api.face.event.GetInfoEvent;
import com.fanfan.youtu.api.hfrobot.bean.Check;
import com.fanfan.youtu.api.hfrobot.bean.RequestProblem;
import com.fanfan.youtu.api.hfrobot.bean.SetBean;
import com.fanfan.youtu.api.hfrobot.event.CheckEvent;
import com.fanfan.youtu.api.hfrobot.event.RequestProblemEvent;
import com.fanfan.youtu.api.hfrobot.event.SetEvent;
import com.fanfan.youtu.utils.GsonUtil;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.iflytek.cloud.SpeechConstant;
import com.seabreeze.log.Print;
import com.tencent.TIMCallBack;
import com.tencent.TIMMessage;
import com.youdao.sdk.ydonlinetranslate.TranslateErrorCode;
import com.youdao.sdk.ydonlinetranslate.TranslateListener;
import com.youdao.sdk.ydtranslate.Translate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.facedetect.DetectionBasedTracker;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.fanfan.robot.app.common.Constants.unusual;

/**
 * 新版仓库
 */
public class MainActivity extends BarBaseActivity implements
        IChatPresenter.IChatView,
        ISerialPresenter.ISerialView,
        ISynthesizerPresenter.ITtsView,
        ILineSoundPresenter.ILineSoundView,
        SurfaceHolder.Callback,
        Camera.PreviewCallback,
        FaceInitManager.SdkInitListener {

//    public static final int DELAY_MILLIS = 10 * 1000;

    public static final int PREVIEW_DELAY_MILLIS = 30 * 1000 * 1;
    public static final int GREETING_DELAY_MILLIS = 60 * 1000 * 1;

    @BindView(R.id.iv_fanfan)
    ImageView ivFanfan;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.iv_problem)
    ImageView ivProblem;
    @BindView(R.id.iv_multi_media)
    ImageView ivMultiMedia;
    @BindView(R.id.iv_face)
    ImageView ivFace;
    @BindView(R.id.iv_seting_up)
    ImageView ivSetingUp;
    @BindView(R.id.iv_public)
    ImageView ivPublic;
    @BindView(R.id.iv_navigation)
    ImageView ivNavigation;
    @BindView(R.id.chat_content)
    ChatTextView chatContent;
    @BindView(R.id.surface_view)
    SurfaceView surfaceView;
    @BindView(R.id.preview_view)
    TexturePreviewView previewView;
    @BindView(R.id.texture_view)
    TextureView textureView;

    private boolean quit;

//    static {
//        if (!OpenCVLoader.initDebug()) {
//            System.out.println("opencv 初始化失败！");
//        } else {
//            System.loadLibrary("detection_based_tracker");
//        }
//    }

//    @Inject
//    MainManager mMainManager;

    private VoiceDBManager mVoiceDBManager;

    private ServiceConnection mPlayServiceConnection;

    private MaterialDialog materialDialog;

    private boolean isPlay;

    private Youtucode youtucode;

    private int id;

    private SpeakService.SpeakBinder mSpeakBinder;

    private ServiceConnection mSpeakConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSpeakBinder = (SpeakService.SpeakBinder) service;
            mSpeakBinder.initSpeakManager(MainActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Runnable speakRunnable = new Runnable() {
        @Override
        public void run() {
            SpeakBean speakBean = null;
            if (!mySynthesizer.isSpeaking()) {
                if (mSpeakBinder != null) {
                    speakBean = mSpeakBinder.getSpeakMorestr();
                }
            }
            if (speakBean == null) {
                mHandler.postDelayed(speakRunnable, 1000);
            } else {
                if (System.currentTimeMillis() - speakBean.getTime() < 3000) {
                    if (mySynthesizer.isSpeaking()) {
                        mSpeakBinder.dispatchSpeak(speakBean);
                    } else {
                        if (speakBean.isUrl()) {
                            doUrl(speakBean.getAnwer());
                        } else {
                            doAnswer(speakBean.getAnwer());
                            if (speakBean.isAction()) {
                                speakingAddAction(speakBean.getAnwer().length());
                            }
                        }
                    }
                } else {
                    Print.e("时间过长");
                    mHandler.postDelayed(speakRunnable, 1000);
                }
            }
        }
    };

    private MyRecognizer myRecognizer;
    private boolean isOpening;

    private MySynthesizer mySynthesizer;

    private TranslateLanguage translateLanguage;

    //camera
    private Camera mCamera;

    private SurfaceHolder mHolder;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private int previewWidth = 640;
    private int previewHeight = 480;

    private boolean isPreviewing = false;

    private int orientionOfCamera;

    //解决人脸识别中完全无人脸但时不时检测到人脸
    private int count;
    private boolean isPreviewFrame;

    Runnable countRunnable = new Runnable() {
        @Override
        public void run() {
            count = 0;
            mHandler.postDelayed(countRunnable, 2000);
        }
    };

    Runnable previewRunnable = new Runnable() {
        @Override
        public void run() {
            eliminate();
        }
    };

    Runnable greetingRunnable = new Runnable() {
        @Override
        public void run() {
            if (PreferencesUtils.getBoolean(MainActivity.this, GreetingActivity.GREETING_STATE)) {
                int anInt = new Random().nextInt(3);
                String greetingStr = null;
                switch (anInt) {
                    case 0:
                        greetingStr = PreferencesUtils.getString(MainActivity.this, GreetingActivity.GREETING1);
                        break;
                    case 1:
                        greetingStr = PreferencesUtils.getString(MainActivity.this, GreetingActivity.GREETING2);
                        break;
                    case 2:
                        greetingStr = PreferencesUtils.getString(MainActivity.this, GreetingActivity.GREETING3);
                        break;
                }
                if (greetingStr != null && !greetingStr.equals("")) {
                    addSpeakAnswer("greetingStr", greetingStr, true, false);
                } else {
                    resetAll();
                    onCompleted();
                }
            } else {
                resetAll();
                onCompleted();
            }
        }
    };

    //人脸识别
    private boolean isFaceIdentify;
    private boolean isDetect;

    private FaceAuthDBManager mFaceAuthDBManager;

    private boolean isSave;

    //直接导航
    private NavigationDBManager mNavigationDBManager;

    //opencv
//    private Mat mRgba = new Mat();
//    private Mat mGray = new Mat();
//    private int mAbsoluteFaceSize = 0;
//    private float mRelativeFaceSize = 0.2f;
//
//    private File mCascadeFile;
//    private CascadeClassifier mJavaDetector;
//    private DetectionBasedTracker mNativeDetector;
//
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS: {
//                    System.loadLibrary("detection_based_tracker");
//
//                    try {
//                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
//                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
//                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
//                        FileOutputStream os = new FileOutputStream(mCascadeFile);
//
//                        byte[] buffer = new byte[4096];
//                        int bytesRead;
//                        while ((bytesRead = is.read(buffer)) != -1) {
//                            os.write(buffer, 0, bytesRead);
//                        }
//                        is.close();
//                        os.close();
//
//                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
//                        if (mJavaDetector.empty()) {
//                            mJavaDetector = null;
//                        } else
//
//                            mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
//
//                        cascadeDir.delete();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//                default: {
//                    super.onManagerConnected(status);
//                }
//                break;
//            }
//        }
//    };

    private boolean isDetector;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main1;
    }

    @Inject
    ChatPresenter mChatPresenter;
    @Inject
    SerialPresenter mSerialPresenter;
    @Inject
    LineSoundPresenter mSoundPresenter;

    @Override
    protected void initView() {
        super.initView();

        DaggerMainComponet.builder().mainModule(new MainModule(this)).build().inject(this);

        mSerialPresenter.start();
        mSoundPresenter.start();

        youtucode = Youtucode.getSingleInstance();

//        youtucode.updateProgram(1);

        Intent intent = new Intent(this, SpeakService.class);
        bindService(intent, mSpeakConn, BIND_AUTO_CREATE);

        IRecogListener iRecogListener = new AlarmListener() {
            @Override
            public void onAsrFinalResult(String result) {
                super.onAsrFinalResult(result);
                aiuiForLocal(result);
                startRecognizerListener(false);
            }

            @Override
            public void onAsrOnlineNluResult(int type, String nluResult) {
                super.onAsrOnlineNluResult(type, nluResult);
                if (type == STATUS_END) {
                    startRecognizerListener(false);
                }
            }

            @Override
            public void onAsrFinishError(int errorCode, String errorMessage) {
                super.onAsrFinishError(errorCode, errorMessage);
                startRecognizerListener(false);
            }
        };
        myRecognizer = new MyRecognizer(this, iRecogListener);

        ISynthListener iSynthListener = new EarListener() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                onRunable();
            }
        };
        mySynthesizer = new MySynthesizer(this, iSynthListener);

        //camera
        surfaceView.setVisibility(View.GONE);
        mHolder = surfaceView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);

//        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        FaceInitManager.getInstance().init(getApplicationContext(), this);
    }

    @Override
    protected void initData() {
        sendOrder(SerialService.DEV_BAUDRATE, "A50C80F3AA");
        mVoiceDBManager = new VoiceDBManager();
        mFaceAuthDBManager = new FaceAuthDBManager();
        mNavigationDBManager = new NavigationDBManager();

        loadImage(R.mipmap.fanfan_hand, R.mipmap.fanfan_lift_hand);

        translateLanguage = new TranslateLanguage();
    }

    @Override
    protected void callStop() {
        mySynthesizer.stop();
        mSoundPresenter.stopVoice();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isOpening = true;
        Constants.isDance = false;

        RobotInfo.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);

        mChatPresenter.start();

        mySynthesizer.onResume();

        myRecognizer.onResume();

        mHandler.post(countRunnable);
        mHandler.postDelayed(previewRunnable, PREVIEW_DELAY_MILLIS);
        mHandler.postDelayed(greetingRunnable, GREETING_DELAY_MILLIS);

        isSave = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        isOpening = false;

        if (mSpeakBinder != null) {
            mSpeakBinder.clear();
        }

        myRecognizer.onPause();

        mySynthesizer.onPause();

        surfaceView.setVisibility(View.GONE);
        mHandler.removeCallbacks(countRunnable);
        mHandler.removeCallbacks(previewRunnable);
        mHandler.removeCallbacks(greetingRunnable);

        setChatView(false);
        loadImage(R.mipmap.fanfan_hand, R.mipmap.fanfan_lift_hand);

        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);

        mSoundPresenter.stopVoice();

        mChatPresenter.finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {

        closeCamera();

        if (mPlayServiceConnection != null) {
            unbindService(mPlayServiceConnection);
        }
        stopService(new Intent(this, UdpService.class));
        stopService(new Intent(this, SerialService.class));
        if (mSpeakConn != null) {
            unbindService(mSpeakConn);
        }
        super.onDestroy();
        mSoundPresenter.finish();

        myRecognizer.release();
        mySynthesizer.release();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetAll();
    }

    @Override
    public void onBackPressed() {
        if (!quit) {
            showToast("再按一次退出程序");
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    quit = false;
                }
            }, 2000);
            quit = true;
        } else {
            super.onBackPressed();
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @OnClick({R.id.iv_fanfan, R.id.iv_video, R.id.iv_problem, R.id.iv_multi_media, R.id.iv_face, R.id.iv_seting_up,
            R.id.iv_public, R.id.iv_navigation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_fanfan:
                animateSequentially(ivFanfan);
                break;
            case R.id.iv_video:
                VideoIntroductionActivity.newInstance(this);
                break;
            case R.id.iv_problem:
                ProblemConsultingActivity.newInstance(this);
                break;
            case R.id.iv_multi_media:
                bindService(false);
                break;
            case R.id.iv_face:
                faceRecognition();
                break;
            case R.id.iv_seting_up:
                clickSetting();
                break;
            case R.id.iv_public:
                PublicNumberActivity.newInstance(this);
                break;
            case R.id.iv_navigation:
                startNavigation();
                break;
        }
    }

    private void startNavigation() {

        boolean navigation = PreferencesUtils.getBoolean(this, SettingActivity.NAVIGATION_CHECK, false);
        if (navigation) {
            NavigationActivity.newInstance(this);
        } else {
            addSpeakAnswer("other", "防误触模式以开启，您可以在设置中选择关闭，您也可以直接说出设置好的导航点启动导航", false, false);

        }
    }

    private void faceRecognition() {
        if (FaceSDKManager.getInstance().initStatus() == FaceSDKManager.SDK_UNACTIVATION) {
            showMsg("SDK还未激活，请先激活");
            return;
        } else if (FaceSDKManager.getInstance().initStatus() == FaceSDKManager.SDK_UNINIT) {
            showMsg("SDK还未初始化完成，请先初始化");
            return;
        } else if (FaceSDKManager.getInstance().initStatus() == FaceSDKManager.SDK_INITING) {
            showMsg("SDK正在初始化，请稍后再试");
            return;
        }
        FaceRecognitionActivity.newInstance(this);
    }


    public void startRecognizerListener(boolean focus) {
        myRecognizer.start(focus);
    }

    public void stopRecognizerListener() {
        myRecognizer.stop();
    }

    public void doUrl(String url) {

        mSoundPresenter.playVoice(url);
    }

    public void doAnswer(String messageContent) {
        boolean isTranslate = RobotInfo.getInstance().getLanguageType() == 1;
        if (isTranslate) {
            translateLanguage.queryZhtoEn(messageContent, new TranslateListener() {
                @Override
                public void onError(TranslateErrorCode translateErrorCode) {
                    onCompleted();
                }

                @Override
                public void onResult(Translate translate, String s) {
                    int errorCode = translate.getErrorCode();
                    if (errorCode == 0) {
                        List<String> translations = translate.getTranslations();
                        if (translations != null && translations.size() > 0) {
                            String translation = translations.get(0);
                            stopSound();
                            mySynthesizer.speak(translation);
                            onSpeakBegin(translation);
                        } else {
                            onCompleted();
                        }
                    } else {
                        onCompleted();
                    }
                }
            });
        } else {
            stopSound();
            mySynthesizer.speak(messageContent);
            onSpeakBegin(messageContent);
        }

    }

    private String mInput;

    private void clickSetting() {
        ivSetingUp.setEnabled(false);
        youtucode.selectSet(UserInfo.getInstance().getIdentifier());
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(SetEvent event) {
        ivSetingUp.setEnabled(true);
        if (event.isOk()) {
            SetBean bean = event.getBean();
            Print.e(bean);
            if (bean.getCode() == 0) {
                showSetDialog(bean.getData());
            } else if (bean.getCode() == 1) {
                SettingActivity.newInstance(MainActivity.this, SettingActivity.LOGOUT_TO_MAIN_REQUESTCODE);
            } else {
                onError(bean.getCode(), bean.getMsg());
            }
        } else {
            onError(event);
        }
    }

    private void showSetDialog(final SetBean.Data data) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.title_setting_pwd)
                .inputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .inputRange(6, 10)
                .alwaysCallInputCallback()
                .input(getString(R.string.input_hint_pwd), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        mInput = String.valueOf(input);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        onCompleted();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (data.getSet_pwd().equals(mInput)) {
                            SettingActivity.newInstance(MainActivity.this, SettingActivity.LOGOUT_TO_MAIN_REQUESTCODE);
                        } else {
                            showMsg("密码错误");
                        }
                    }
                })
                .build();
        materialDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return false;
            }
        });
        materialDialog.setCancelable(false);
        materialDialog.show();
    }

    private void bindService(boolean isPlay) {
        boolean meida = PreferencesUtils.getBoolean(this, SettingActivity.MEDIA_CHECK, false);
        if (meida) {
            this.isPlay = isPlay;
            if (!PreferencesUtils.getBoolean(MainActivity.this, Constants.MUSIC_UPDATE, false))
                showLoading();
            Intent intent = new Intent();
            intent.setClass(this, PlayService.class);
            mPlayServiceConnection = new PlayServiceConnection();
            bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            addSpeakAnswer("other", "管理员还没有帮我添加多媒体内容，您可以尝试问我些其它问题呢", false, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingActivity.LOGOUT_TO_MAIN_REQUESTCODE) {
            if (resultCode == SettingActivity.LOGOUT_TO_MAIN_RESULTCODE) {
                spakeLogout();
            }
        } else if (requestCode == MultimediaActivity.MULTIMEDIA_REQUESTCODE) {
            if (resultCode == MultimediaActivity.MULTIMEDIA_RESULTCODE) {
                Print.e("断开与音乐服务的连接");
                unbindService(mPlayServiceConnection);
                mChatPresenter.start();
                myRecognizer.onResume();
                mySynthesizer.onResume();
            }
        }
    }

    //**********************************************************************************************

    private boolean isSuspendAction;
    private boolean isAutoAction;

    private void sendOrder(int type, String motion) {
        receiveMotion(type, motion);
    }

    private void sendCustom(RobotBean localVoice) {
        sendCustomMessage(localVoice);
    }

    public void sendCustomMessage(RobotBean robotBean) {
        mChatPresenter.sendCustomMessage(robotBean);
    }

    public void receiveMotion(int type, String motion) {
        mSerialPresenter.receiveMotion(type, motion);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP://19
                if (!isSuspendAction) {
                    sendMsg(keyCode);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN://20
                if (!isSuspendAction) {
                    sendMsg(keyCode);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT://21
                if (!isSuspendAction) {
                    sendMsg(keyCode);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT://20
                if (!isSuspendAction) {
                    sendMsg(keyCode);
                }
                break;
            case KeyEvent.KEYCODE_BUTTON_L1:
//                onEventLR();
                break;
            case KeyEvent.KEYCODE_BUTTON_R1:
//                onEventLR();
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                sendOrder(SerialService.DEV_BAUDRATE, "A50C80F3AA");
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                sendOrder(SerialService.DEV_BAUDRATE, "A50C80F2AA");
                break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                sendAutoAction();
                break;
            case KeyEvent.KEYCODE_BUTTON_A:
                stopAutoAction();
                break;
        }
        return false;
    }

    private void sendMsg(final int keyCode) {
        isSuspendAction = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP://19
                        sendOrder(SerialService.DEV_BAUDRATE, "A5038002AA");
                        Print.e("up");
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN://20
                        sendOrder(SerialService.DEV_BAUDRATE, "A5038008AA");
                        Print.e("down");
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT://21
                        sendOrder(SerialService.DEV_BAUDRATE, "A5038004AA");
                        Print.e("left");
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT://20
                        sendOrder(SerialService.DEV_BAUDRATE, "A5038006AA");
                        Print.e("right");
                        break;
                    default:
                        isSuspendAction = false;
                }

            }
        }, 500);
    }

    public void sendAutoAction() {
        if (isAutoAction) {
            stopAutoAction();
        } else {
            isAutoAction = true;
            sendOrder(SerialService.DEV_BAUDRATE, "A503800AAA");
            Print.e("自由运动(开)");
            mHandler.postDelayed(runnable, 200);
        }
    }

    public void stopAutoAction() {
        if (isAutoAction) {
            Print.e("自由运动(关)");
            mHandler.removeCallbacks(runnable);
            sendOrder(SerialService.DEV_BAUDRATE, "A5038005AA");
            isAutoAction = false;
        }
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isAutoAction) {
                sendOrder(SerialService.DEV_BAUDRATE, "A503800AAA");
                mHandler.postDelayed(runnable, 200);
                Print.e("自由运动(开)");
            }
        }
    };

    //**********************************************************************************************

    private void addSpeakAnswer(String problem, String messageContent, boolean isAction, boolean isUrl) {

        resetAll();

        mSoundPresenter.stopVoice();
        stopRecognizerListener();
//        doAnswer(messageContent);
        if (mySynthesizer.isSpeaking()) {
            SpeakBean speakBean = new SpeakBean(problem, messageContent, System.currentTimeMillis(), isAction, isUrl);
            mSpeakBinder.dispatchSpeak(speakBean);
        } else {
            if (isUrl) {
                doUrl(messageContent);
            } else {
                doAnswer(messageContent);
                if (isAction) {
                    speakingAddAction(messageContent.length());
                }
            }
        }
    }

    private void setChatContent(String messageContent) {
        chatContent.setSpanText(mHandler, messageContent, true);
    }

    private void speakingAddAction(int length) {
        sendOrder(SerialService.DEV_BAUDRATE, Constants.SPEAK_ACTION);
    }

    //************************anim****************************
    protected void animateSequentially(View view) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                FanFanIntroduceActivity.newInstance(MainActivity.this);
//                PPTActivity.newInstance(MainActivity.this);
                String speak = "您好，智能小秘书芳芳，一些常见问题咨询您都可以问我，虽然我还没有哥哥姐姐们那么优秀，但我一定会尽心为您服务!";
                addSpeakAnswer("ClickMsg", speak, true, false);
            }
        }, 400);
        ViewAnimator
                .animate(view)
                .scale(1f, 1.3f, 1f)
                .alpha(1, 0.3f, 1)
                .translationX(0, 200, 0)
                .translationY(0, 300, 0)
                .interpolator(new LinearInterpolator())
                .duration(1200)
                .start();
    }

    //**********************************************************************************************
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onResultEvent(ReceiveEvent event) {
        if (event.isOk()) {
            DatagramPacket packet = event.getBean();
            if (!SocketManager.getInstance().isGetTcpIp) {
                SocketManager.getInstance().setUdpIp(packet.getAddress().getHostAddress(), packet.getPort());
            }
            String recvStr = new String(packet.getData(), 0, packet.getLength());
            Print.e("udp发送过来消息 ： " + recvStr);
            sendOrder(SerialService.DEV_BAUDRATE, recvStr);
        } else {
            Print.e("ReceiveEvent error");
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

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(CheckEvent event) {
        if (event.isOk()) {
            Check check = event.getBean();
            Print.e(check);
            if (check.getCode() == 0) {
                Check.CheckBean appVerBean = check.getCheck();
                int curVersion = AppUtil.getVersionCode(this);
                int newversioncode = appVerBean.getVersionCode();

                if (curVersion < newversioncode) {

                    String apkUrl = Constant.APK_URL + appVerBean.getAppName();

                    Progress progress = DownloadDBDao.getInstance().get(apkUrl);
                    if (progress == null) {
                        return;
                    }
                    if (progress.status != Progress.FINISH) {
                        return;
                    }
                    final File file = new File(progress.folder, progress.fileName);
                    if (!file.exists()) {
                        return;
                    }
                    long fileSize = FileUtil.getFileSize(file);
                    if (progress.totalSize != fileSize) {
                        return;
                    }
                    DialogUtils.showBasicNoTitleDialog(this, getString(R.string.download_check_finish), "取消", "安装",
                            new DialogUtils.OnNiftyDialogListener() {
                                @Override
                                public void onClickLeft() {

                                }

                                @Override
                                public void onClickRight() {
                                    stopService(new Intent(MainActivity.this, LoadFileService.class));
                                    AppUtil.installNormal(MainActivity.this, file);
                                }
                            });
                } else {
                    Print.e("暂时没有检测到新版本");
                }
            } else {
                onError(check.getCode(), check.getMsg());
            }
        } else {
            onError(event);
        }
    }

    //**********************************************************************************************
    @Override
    public void showLoading() {
        if (materialDialog == null) {
            materialDialog = new MaterialDialog.Builder(this)
                    .title("请稍等...")
                    .content("正在获取中...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(false)
                    .build();
        }
        materialDialog.show();
    }

    @Override
    public void dismissLoading() {
        if (materialDialog != null && materialDialog.isShowing()) {
            materialDialog.dismiss();
            materialDialog = null;
        }
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

    //**********************************************************************************************
    @Override
    public void onSpeakBegin(String answer) {

        setChatContent(answer);
        setChatView(true);
        loadImage(R.mipmap.fanfan_lift_hand, R.mipmap.fanfan_hand);
    }

    @Override
    public void onRunable() {
        setChatView(false);
        loadImage(R.mipmap.fanfan_hand, R.mipmap.fanfan_lift_hand);
        sendOrder(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
        startRecognizerListener(true);
        mHandler.post(speakRunnable);
    }

    @Override
    public void stopSound() {
        mSoundPresenter.stopVoice();
        stopRecognizerListener();
    }

    private void loadImage(int load, int place) {
        ImageLoader.loadImage(NovelApp.getInstance().getApplicationContext(), ivFanfan, load, false, place, 1000);
    }


    private void setChatView(boolean isShow) {
//        if (isShow) {
//            ViewAnimator
//                    .animate(chatContent)
//                    .alpha(0, 1)
//                    .interpolator(new LinearInterpolator())
//                    .duration(300)
//                    .onStart(new AnimationListener.Start() {
//                        @Override
//                        public void onStart() {
//
//                        }
//                    })
//                    .onStop(new AnimationListener.Stop() {
//                        @Override
//                        public void onStop() {
//                            chatContent.setVisibility(View.VISIBLE);
//                        }
//                    })
//                    .start();
//        } else {
//            ViewAnimator
//                    .animate(chatContent)
//                    .alpha(1, 0)
//                    .interpolator(new LinearInterpolator())
//                    .duration(1000)
//                    .onStart(new AnimationListener.Start() {
//                        @Override
//                        public void onStart() {
//
//                        }
//                    })
//                    .onStop(new AnimationListener.Stop() {
//                        @Override
//                        public void onStop() {
//                            chatContent.setVisibility(View.GONE);
//                        }
//                    })
//                    .start();
//        }
        if (isShow) {
            chatContent.setVisibility(View.VISIBLE);
        } else {
            chatContent.setVisibility(View.GONE);
        }
    }

    //**********************************************************************************************
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        Print.i("onSendMessageSuccess : 发送消息成功");
    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        Print.e("onSendMessageFail : 发送消息失败");
    }

    @Override
    public void parseMsgcomplete(String str) {
        addSpeakAnswer("TextMsg", str, true, false);
    }

    @Override
    public void parseCustomMsgcomplete(String customMsg) {
        RobotBean bean = GsonUtil.GsonToBean(customMsg, RobotBean.class);
        if (bean == null) {
            return;
        }
        if (bean.getType() == null || bean.getType().equals("")) {
            return;
        }
        if (bean.getOrder() == null || bean.getOrder().equals("")) {
            return;
        }
        RobotType robotType = bean.getType();
        switch (robotType) {
            case AutoAction:
                break;
            case VoiceSwitch:
                boolean isSpeech = bean.getOrder().equals("语音开");
                setSpeech(isSpeech);
                break;
            case Text:
                addSpeakAnswer("CustomMsg", bean.getOrder(), true, false);
                break;
            case SmartChat:

                break;
            case Motion:
                sendOrder(SerialService.DEV_BAUDRATE, bean.getOrder());
                break;
            case GETIP:
                Constants.CONNECT_IP = bean.getOrder();
                if (Constants.IP != null && Constants.PORT > 0) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("robotIp", Constants.IP);
                        object.put("robotPort", Constants.PORT);
                        RobotBean robotBean = new RobotBean();
                        robotBean.setOrder(object.toString());
                        robotBean.setType(RobotType.GETIP);
                        Print.e("发送: " + object.toString());
                        showToast("发送: " + object.toString());
                        sendCustom(robotBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("robotIp", "");
                        object.put("robotPort", Constants.PORT);
                        RobotBean robotBean = new RobotBean();
                        robotBean.setOrder(object.toString());
                        robotBean.setType(RobotType.GETIP);
                        Print.e("发送: " + object.toString());
                        sendCustom(robotBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case LocalVoice:
                List<VoiceBean> voiceBeanList = mVoiceDBManager.loadAll();
                List<String> anwers = new ArrayList<>();
                if (voiceBeanList != null && voiceBeanList.size() > 0) {
                    for (VoiceBean voiceBean : voiceBeanList) {
                        anwers.add(voiceBean.getShowTitle());
                    }
                    String voiceJson = GsonUtil.GsonString(anwers);
                    RobotBean localVoice = new RobotBean();
                    localVoice.setType(RobotType.LocalVoice);
                    localVoice.setOrder(voiceJson);
                    sendCustom(localVoice);
                }
                break;
            case Anwser:
                mSoundPresenter.stopVoice();
                stopRecognizerListener();
                aiuiForLocal(bean.getOrder());
                break;
        }
    }

    @Override
    public void parseServerMsgcomplete(String txt) {
        Print.e("接收到客服发来的消息： " + txt);
        //bcimnopvxyz
        txt = txt.trim();
        if (txt.equals("b")) {
            addSpeakAnswer("ServerMsg", "您可以咨询我旁边的工作人员，我是第一次到这儿来，对这个地方还不是很熟悉 ，请您谅解", true, false);
        } else if (txt.equals("c")) {
            addSpeakAnswer("ServerMsg", "尊敬的领导，你好，我叫芳芳，是北京海风智能科技自主研发的服务机器人 ，很高兴为您服务", true, false);
        } else if (txt.equals("i")) {
            addSpeakAnswer("ServerMsg", "今天多云，昼夜温差大，请适量增减衣物", true, false);
        } else if (txt.equals("m")) {
            addSpeakAnswer("ServerMsg", "您可以在我的系统中录入您的信息，下一次见到您，我就认识了", true, false);
        } else if (txt.equals("n")) {
            addSpeakAnswer("ServerMsg", "尊敬的用户，您好，如果您有什么问题想要问我，您可以站在80公分以外的安全距离对我进行提问，以防我动的时候会碰到您", true, false);
        } else if (txt.equals("o")) {
            addSpeakAnswer("ServerMsg", "您好，请让一让，谢谢您的理解配合", true, false);
        } else if (txt.equals("a")) {
            mSoundPresenter.stopVoice();
            stopRecognizerListener();
        } else if (txt.equals("s")) {
            startRecognizerListener(false);
        } else if (txt.equals("g")) {
            stopAll();

        } else if (txt.equals("h")) {//前进
            sendOrder(SerialService.DEV_BAUDRATE, "A5038002AA");

        } else if (txt.equals("j")) {//后退
            sendOrder(SerialService.DEV_BAUDRATE, "A5038008AA");

        } else if (txt.equals("k")) {//左转
            sendOrder(SerialService.DEV_BAUDRATE, "A5038004AA");

        } else if (txt.equals("l")) {//右转
            sendOrder(SerialService.DEV_BAUDRATE, "A5038006AA");

        } else if (txt.equals("q")) {
            animateSequentially(ivFanfan);

        } else if (txt.equals("w")) {
            ProblemConsultingActivity.newInstance(this);

        } else if (txt.equals("e")) {
            VideoIntroductionActivity.newInstance(this);
        } else if (txt.equals("r")) {
            NavigationActivity.newInstance(this);
        } else if (txt.equals("t")) {
            if (Constants.isDance) {
                Print.e("正在跳舞，return");
                return;
            }
            beginDance("ServerMsg");
        } else if (txt.equals("u")) {
            sendOrder(SerialService.DEV_BAUDRATE, "A50C800CAA");
            addSpeakAnswer("ServerMsg", "你好", false, false);
        } else if (txt.equals("d")) {
            if (isAutoAction) {
                return;
            }
            sendAutoAction();
        } else if (txt.equals("f")) {
            stopAutoAction();
        } else if (Arrays.asList(getResources().getStringArray(R.array.navigation_data)).contains(txt.trim())) {
            List<NavigationBean> beans = mNavigationDBManager.queryOrder(txt);
            if (beans != null && beans.size() > 0) {
                NavigationBean itemData = null;
                if (beans.size() == 1) {
                    itemData = beans.get(beans.size() - 1);
                } else {
                    itemData = beans.get(new Random().nextInt(beans.size()));
                }

                NavigationActivity.newInstance(this, itemData.getTitle());
            }
        } else {
            if (txt.equals("1") || txt.equals("2") || txt.equals("3") || txt.equals("4") || txt.equals("5")
                    || txt.equals("6") || txt.equals("7") || txt.equals("8") || txt.equals("9")) {
                int i = Integer.valueOf(txt);
                switch (i) {
                    case 1:
                        txt = "正在为您检索答案，请您稍等";
                        break;
                    case 2:
                        txt = "数据库中未检索到符合的答案，如需咨询业务相关，您可以对我说“业务办理”";
                        break;
                    case 3:
                        txt = "您好，请问您想办理什么业务";
                        break;
                    case 4:
                        txt = "您好，很高兴见到您";
                        break;
                    case 5:
                        txt = "这个问题好难啊，我还没学过这个，您能教我么";
                        break;
                    case 6:
                        txt = "领导您好，我是境境，希望您能喜欢我";
                        break;
                    case 7:
                        txt = "我是服务机器人境境";
                        break;
                    case 8:
                        txt = "您在我前面站了这么久  要和我说点什么么";
                        break;
                    case 9:
                        txt = "您好，您可以和我对话聊天，提问解答，我很乐意为您服务";
                        break;

                }
            }
            addSpeakAnswer("ServerMsg", txt, true, false);
        }
    }

    private void beginDance(final String problem) {
        Constants.isDance = true;
        addSpeakAnswer(problem, "请您欣赏舞蹈", false, false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DanceDBManager danceDBManager = new DanceDBManager();
                List<Dance> dances = danceDBManager.loadAll();
                if (dances != null && dances.size() > 0) {
                    Dance dance = dances.get(0);
                    DanceActivity.newInstance(MainActivity.this, dance.getId());
                } else {
                    addSpeakAnswer(problem, "本地暂未添加舞蹈，请到设置或多媒体中添加舞蹈", true, false);
                }
            }
        }, 2000);
    }

    //**********************************************************************************************
    @Override
    public void stopAll() {
        sendOrder(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
        mSoundPresenter.stopVoice();
        stopRecognizerListener();
        String wakeUp = AppUtil.resFoFinal(R.array.wake_up);
        mySynthesizer.stop();
        mySynthesizer.speak(wakeUp);
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

    //**********************************************************************************************
    @Override
    public void aiuiForLocal(String result) {
        String unicode = result.replaceAll("\\p{P}", "");
        if (unicode.equals("百度")) {
            IntentUtil.openUrl(mContext, "http://www.baidu.com/");
        } else if (unicode.equals("新闻")) {
            IntentUtil.openUrl(mContext, "http://www.cepb.gov.cn/");
        } else {
            List<NavigationBean> navigationBeans = mNavigationDBManager.queryNavigationByQuestion(result);
            if (navigationBeans != null && navigationBeans.size() > 0) {
                NavigationActivity.newInstance(this, result);
            } else {

                List<VoiceBean> voiceBeanList = mVoiceDBManager.loadAll();
                if (voiceBeanList != null && voiceBeanList.size() > 0) {
                    for (VoiceBean voiceBean : voiceBeanList) {
                        if (voiceBean.getShowTitle().equals(unicode)) {
                            refHomePage(voiceBean);
                            return;
                        }
                    }
                }
                mSoundPresenter.onlineResult(unicode);
            }
        }
    }

    @Override
    public void doAiuiAnwer(String question, String anwer) {
        addSpeakAnswer(question, anwer, true, false);
    }

    @Override
    public void doAiuiUrl(String question, String url) {
        addSpeakAnswer(question, url, false, true);
    }

    @Override
    public void refHomePage(VoiceBean voiceBean) {
        if (voiceBean.getActionData() != null)
            sendOrder(SerialService.DEV_BAUDRATE, voiceBean.getActionData());
        if (voiceBean.getExpressionData() != null)
            sendOrder(SerialService.DEV_BAUDRATE, voiceBean.getExpressionData());

        addSpeakAnswer(voiceBean.getShowTitle(), voiceBean.getVoiceAnswer(), true, false);
    }


    @Override
    public void refHomePage(String question, String finalText) {
    }

    @Override
    public void refHomePage(String question, String finalText, String url) {
    }

    @Override
    public void refHomePage(String question, News news) {
    }

    @Override
    public void refHomePage(String question, Radio radio) {
    }

    @Override
    public void refHomePage(String question, Poetry poetry) {
    }

    @Override
    public void refHomePage(String question, Cookbook cookbook) {
    }

    @Override
    public void refHomePage(String question, EnglishEveryday englishEveryday) {
    }

    @Override
    public void special(String result, SpecialType type) {
        switch (type) {
            case Story:
                onCompleted();
                break;
            case Music:
                bindService(true);
                break;
            case Joke:
                onCompleted();
                break;
            case Dance:
                beginDance(result);
                break;
            case Hand:
                sendOrder(SerialService.DEV_BAUDRATE, "A50C800CAA");
                addSpeakAnswer(result, "你好", false, false);
                break;
        }
    }

    @Override
    public void doCallPhone(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void startPage(SpecialType specialType) {
        switch (specialType) {
            case Fanfan:
                animateSequentially(ivFanfan);
                break;
            case Video:
                VideoIntroductionActivity.newInstance(this);
                break;
            case Problem:
                ProblemConsultingActivity.newInstance(this);
                break;
            case MultiMedia:
                bindService(false);
                break;
            case Face:
                faceRecognition();
                break;
            case Seting_up:
                clickSetting();
                break;
            case Public_num:
                PublicNumberActivity.newInstance(this);
                break;
            case Navigation:
                startNavigation();
                break;
            default:
                onCompleted();
                break;
        }
    }

    @Override
    public void spakeMove(SpecialType type, String result) {
        onCompleted();
        switch (type) {
            case Forward:
                sendOrder(SerialService.DEV_BAUDRATE, "A5038002AA");
                break;
            case Backoff:
                sendOrder(SerialService.DEV_BAUDRATE, "A5038008AA");
                break;
            case Turnleft:
                sendOrder(SerialService.DEV_BAUDRATE, "A5038004AA");
                break;
            case Turnright:
                sendOrder(SerialService.DEV_BAUDRATE, "A5038006AA");
                break;
            case StartMove:
                sendAutoAction();
                break;
            case StopMove:
                stopAutoAction();
                break;
        }
    }

    @Override
    public void openMap() {
//        AMapActivity.newInstance(this);
        Intent intent = new Intent("android.intent.action.VIEW",
                Uri.parse("androidamap://showTraffic?sourceApplication=softname&amp;poiid=BGVIS1&amp;lat=36.2&amp;lon=116.1&amp;level=10&amp;dev=0"));
        intent.setPackage("com.autonavi.minimap");
        startActivity(intent);
    }

    @Override
    public void openVr() {
        PanoramicMapActivity.newInstance(this);
    }

    @Override
    public void spakeLogout() {
        LoginBusiness.logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                showMsg("退出登录失败，请稍后重试");
            }

            @Override
            public void onSuccess() {
//                liveLogout();
                logout();
            }
        });
    }

    @Override
    public void onCompleted() {
        onRunable();
    }

    @Override
    public void noAnswer(String question) {
        String identifier = UserInfo.getInstance().getIdentifier();
        youtucode.requestProblem(identifier, question, id, 1);
    }

    @Override
    public void setSpeech(boolean speech) {
        if (speech) {
            startRecognizerListener(false);
        } else {
            stopRecognizerListener();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(RequestProblemEvent event) {
        if (!event.isOk()) {
            onError(event);
            onCompleted();
            return;
        }
        String identifier = UserInfo.getInstance().getIdentifier();
        RequestProblem requestProblem = event.getBean();
        if (requestProblem.getCode() == 2) {//添加成功
            Print.e(requestProblem.getMsg());

            mChatPresenter.sendServerMessage(identifier, requestProblem.getQuestion());
            onCompleted();
        } else if (requestProblem.getCode() == 0) {//已经添加过，有答案
            RequestProblem.AnswerBean answerBean = requestProblem.getAnswerBean();
            if (answerBean == null) {
                mChatPresenter.sendServerMessage(identifier, requestProblem.getQuestion());
                onCompleted();
                return;
            }
            Print.e(requestProblem);
            String anwer = answerBean.getAnswer();
            id = answerBean.getId();
            if (anwer == null || anwer.length() < 1) {
                mChatPresenter.sendServerMessage(identifier, requestProblem.getQuestion());
                onCompleted();
            } else {
                addSpeakAnswer(requestProblem.getQuestion(), anwer, true, false);
            }
        } else {
            mChatPresenter.sendServerMessage(identifier, requestProblem.getQuestion());
            onError(requestProblem.getCode(), requestProblem.getMsg());
            onCompleted();
        }
    }

    private ArgbPool argbPool = new ArgbPool();
    private FaceFilter faceFilter = new FaceFilter();

    @SuppressLint("CheckResult")
    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {

        if (identityStatus != IDENTITY_IDLE) {
            return;
        }
        identityStatus = IDENTITYING;

        int rotation = 360 - orientionOfCamera;
        if (rotation == 360) {
            rotation = 0;
        }
        int width = previewWidth;
        int height = previewHeight;

        int[] argb = argbPool.acquire(width, height);

        if (argb == null || argb.length != width * height) {
            argb = new int[width * height];
        }

        rotation = rotation < 0 ? 360 + rotation : rotation;
        com.baidu.aip.manager.FaceDetector.yuvToARGB(data, width, height, argb, rotation, 0);

        // 旋转了90或270度。高宽需要替换
        if (rotation % 180 == 90) {
            int temp = width;
            width = height;
            height = temp;
        }

        final ImageFrame frame = new ImageFrame();
        frame.setArgb(argb);
        frame.setWidth(width);
        frame.setHeight(height);
        frame.setPool(argbPool);

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
//                final Bitmap bitmap = Bitmap.createBitmap(frame.getArgb(), frame.getWidth(), frame.getHeight(), Bitmap.Config.ARGB_8888);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ivFanfan.setImageBitmap(bitmap);
//                    }
//                });

                int value = FaceSDKManager.getInstance().getFaceDetector().detect(frame);
                FaceInfo[] faces = FaceSDKManager.getInstance().getFaceDetector().getTrackedFaces();

                if (value == 0) {
                    faceFilter.filter(faces, frame);
                }

                if (value == FaceTracker.ErrCode.OK.ordinal() && faces != null && faces.length > 0) {

                    FaceInfo faceInfo = faces[0];

                    int liveType = PreferencesUtil.getInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity.TYPE_NO_LIVENSS);
                    if (liveType == LivenessSettingActivity.TYPE_RGB_LIVENSS) {

                        float raw = Math.abs(faceInfo.headPose[0]);
                        float patch = Math.abs(faceInfo.headPose[1]);
                        float roll = Math.abs(faceInfo.headPose[2]);
                        // 人脸的三个角度大于20不进行识别
                        if (raw > 20 || patch > 20 || roll > 20) {
                            return;
                        }

                        int[] argb = frame.getArgb();
                        int rows = frame.getHeight();
                        int cols = frame.getWidth();
                        int[] landmarks = faceInfo.landmarks;
                        IdentifyRet identifyRet = FaceApi.getInstance().identity(argb, rows, cols, landmarks, UserInfo.getInstance().getIdentifier());

                        float score = identifyRet.getScore();
                        if (score > 70) {
                            String userId = identifyRet.getUserId();
                            e.onNext(userId);
                            return;
                        } else {
                            e.onNext("");
                        }
                    }
                }

                identityStatus = IDENTITY_IDLE;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (!s.equals("")) {
                            User user = FaceApi.getInstance().getUserInfo(UserInfo.getInstance().getIdentifier(), s);
                            if (user == null) {
                                identityStatus = IDENTITY_IDLE;
                                return;
                            }
                            String userName = user.getUserName();
                            if (userName == null) {
                                identityStatus = IDENTITY_IDLE;
                                return;
                            }

                            sayHello("您好" + userName + ", 欢迎光临");
                        } else {
                            sayHello("您好" + ", 欢迎光临");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        identityStatus = IDENTITY_IDLE;
                    }
                });

        frame.release();
        argbPool.release(argb);


//        Camera.Size size = camera.getParameters().getPreviewSize();
//        YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, size.width, size.height, null);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        yuvImage.compressToJpeg(new android.graphics.Rect(0, 0, size.width, size.height), 80, baos);
//        byte[] byteArray = baos.toByteArray();
//
//        Bitmap previewBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//        int width = previewBitmap.getWidth();
//        int height = previewBitmap.getHeight();
//
//        Matrix matrix = new Matrix();
//
//        FaceDetector detector = null;
//        Bitmap faceBitmap = null;
//
//        detector = new FaceDetector(previewBitmap.getWidth(), previewBitmap.getHeight(), 10);
//        int oriention = 360 - orientionOfCamera;
//        if (oriention == 360) {
//            oriention = 0;
//        }
//        switch (oriention) {
//            case 0:
//                detector = new FaceDetector(width, height, 10);
//                matrix.postRotate(0.0f, width / 2, height / 2);
//                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
//                break;
//            case 90:
//                detector = new FaceDetector(height, width, 1);
//                matrix.postRotate(-270.0f, height / 2, width / 2);
//                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
//                break;
//            case 180:
//                detector = new FaceDetector(width, height, 1);
//                matrix.postRotate(-180.0f, width / 2, height / 2);
//                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
//                break;
//            case 270:
//                detector = new FaceDetector(height, width, 1);
//                matrix.postRotate(-90.0f, height / 2, width / 2);
//                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
//                break;
//        }
//
//        Bitmap copyBitmap = faceBitmap.copy(Bitmap.Config.RGB_565, true);
//
////        if (faceDetector(detector, copyBitmap)) return;
//
//        FaceDetector.Face[] faces = new FaceDetector.Face[10];
//        int faceNumber = detector.findFaces(copyBitmap, faces);
//        if (faceNumber > 0) {
//
//            int opencv = opencvDraw(copyBitmap);
//
//            if (opencv > 0) {
//
//                distinguish(copyBitmap);
//            } else {
//                Print.i("opencv检测人脸失败");
//                isDetector = true;
//            }
//        } else {
//            Print.i("系统检测人脸失败");
//            isDetector = true;
//        }
//
//        copyBitmap.recycle();
//        faceBitmap.recycle();
//        previewBitmap.recycle();
    }


//    private boolean faceDetector(FaceDetector detector, Bitmap copyBitmap) {
//
//        if (isSave) {
//            isSave = false;
////            BitmapUtils.saveBitmapToFile(copyBitmap, "test", System.currentTimeMillis() + ".jpg");
//        }
//
//        FaceDetector.Face[] faces = new FaceDetector.Face[10];
//        int faceNumber = detector.findFaces(copyBitmap, faces);
//        if (faceNumber > 0) {
//
//            int opencv = opencvDraw(copyBitmap);
//            Print.e("opencv : " + opencv);
//            if (opencv > 0) {
//
//                Print.e("isPreviewFrame " + isPreviewFrame + "   count :" + count);
//                if (!isPreviewFrame) {
//                    return true;
//                }
//
//                count++;
//                if (count == 2) {
//                    isPreviewFrame = false;
//                    distinguish(copyBitmap);
//                }
//            } else {
//                Print.i("opencv no face");
//            }
//        } else {
//            Print.i("camera no face");
//        }
//        return false;
//    }


//    private int opencvDraw(Bitmap bitmap) {
//
//        Utils.bitmapToMat(bitmap, mRgba);
//        Mat mat1 = new Mat();
//        Utils.bitmapToMat(bitmap, mat1);
//        Imgproc.cvtColor(mat1, mGray, Imgproc.COLOR_BGR2GRAY);
//        if (mAbsoluteFaceSize == 0) {
//            int height = mGray.rows();
//            if (Math.round(height * mRelativeFaceSize) > 0) {
//                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
//            }
//            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
//        }
//        MatOfRect faces = new MatOfRect();
//
//        if (mJavaDetector != null)
//            mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
//
//        Rect[] facesArray = faces.toArray();
//        return facesArray.length;
//    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
        doStartPreview();
        mHandler.postDelayed(previewRunnable, PREVIEW_DELAY_MILLIS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        closeCamera();
    }

    private void openCamera() {
        if (Camera.getNumberOfCameras() == 1) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        try {
            mCamera = Camera.open(mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
            closeCamera();
        }
    }

    private void closeCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
        Print.i("相机关闭...");
    }

    private void doStartPreview() {
        if (isPreviewing) {
            mCamera.stopPreview();
        }

        Camera.Parameters parameters = mCamera.getParameters();

        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setPreviewSize(previewWidth, previewHeight);
        mCamera.setParameters(parameters);


        if (unusual) {
            // 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
            orientionOfCamera = CameraUtils.getInstance().getCameraDisplayOrientation(this, mCameraId);
        } else {
            // TODO: 2018/7/4/004
            orientionOfCamera = 270;
        }
        mCamera.setDisplayOrientation(orientionOfCamera);

        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
        isPreviewing = true;
        Print.i("相机已经打开...");
    }

    public void eliminate() {
        Print.i("相机准备打开...");
        surfaceView.setVisibility(View.VISIBLE);
        count = 0;
        isPreviewFrame = true;
        isFaceIdentify = false;
        isDetector = true;
        identityStatus = IDENTITY_IDLE;
    }

    private void resetAll() {
        surfaceView.setVisibility(View.GONE);

        mHandler.removeCallbacks(previewRunnable);
        mHandler.postDelayed(previewRunnable, PREVIEW_DELAY_MILLIS);
        mHandler.removeCallbacks(greetingRunnable);
        mHandler.postDelayed(greetingRunnable, GREETING_DELAY_MILLIS);
    }


    private void distinguish(Bitmap bitmap) {
//        faceIdentifyFace(bitmap);
//        detectFace(bitmap);
//        greetingFace();
//        detectorFace();
    }

    private void detectorFace() {
        String cameraSpeak = resFoFinal(R.array.camera_speak);
        addSpeakAnswer("cameraMsg", cameraSpeak, true, false);
    }

    private void greetingFace() {
        String cameraSpeak = resFoFinal(R.array.camera_speak);
        addSpeakAnswer("cameraMsg", cameraSpeak, true, false);
    }

    private void detectFace(Bitmap bitmap) {
        if (isDetect) {
            return;
        }

        isDetect = true;
        youtucode.detectFace(bitmap, 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(DetectFaceEvent event) {
        if (event.isOk()) {
            DetectFace detectFace = event.getBean();
            Print.e(detectFace);
            if (detectFace.getErrorcode() == 0) {
                List<Face> faces = detectFace.getFace();
                if (faces != null && faces.size() > 0) {
                    if (faces.size() == 1) {

                        Face face = faces.get(0);
                        if (face.getGender() > 50) {
                            sayHello("你好, 先生");
                        } else {
                            sayHello("你好, 女士");
                        }
                    } else {
                        sayHello("大家好, 欢迎光临");
                    }
                } else {
                    againDetect();
                }
            } else {
                onError(detectFace.getErrorcode(), detectFace.getErrormsg());
                againDetect();
            }
        } else {
            onError(event);
            againDetect();
        }
    }


    public void faceIdentifyFace(Bitmap bitmap) {
        if (isFaceIdentify)
            return;

        Print.e("从云端获取人脸信息详情 ... ");
        isFaceIdentify = true;
        youtucode.faceIdentify(bitmap);
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(FaceIdentifyEvent event) {
        if (event.isOk()) {
            FaceIdentify faceIdentify = event.getBean();
            Print.e(faceIdentify);
            if (faceIdentify.getErrorcode() == 0) {

                compareFace(faceIdentify);
            } else {
                againIdentify();
                onError(faceIdentify.getErrorcode(), faceIdentify.getErrormsg());
            }
        } else {
            againIdentify();
            onError(event);
        }
    }

    private void compareFace(FaceIdentify faceIdentify) {
        Print.e("云端获取成功后取得相似度最佳的一个");
        ArrayMap<FaceIdentify.IdentifyItem, Integer> countMap = new ArrayMap<>();

        ArrayList<FaceIdentify.IdentifyItem> identifyItems = faceIdentify.getCandidates();
        if (identifyItems != null && identifyItems.size() > 0) {
            for (int i = 0; i < identifyItems.size(); i++) {
                FaceIdentify.IdentifyItem identifyItem = identifyItems.get(i);

                if (countMap.containsKey(identifyItem)) {
                    countMap.put(identifyItem, countMap.get(identifyItem) + 1);
                } else {
                    countMap.put(identifyItem, 1);
                }
            }

            ArrayMap<Integer, List<FaceIdentify.IdentifyItem>> resultMap = new ArrayMap<>();
            List<Integer> tempList = new ArrayList<Integer>();

            Iterator iterator = countMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<FaceIdentify.IdentifyItem, Integer> entry = (Map.Entry<FaceIdentify.IdentifyItem, Integer>) iterator.next();

                FaceIdentify.IdentifyItem key = entry.getKey();
                int value = entry.getValue();

                if (resultMap.containsKey(value)) {
                    List list = resultMap.get(value);
                    list.add(key);
                } else {
                    List<FaceIdentify.IdentifyItem> list = new ArrayList<>();
                    list.add(key);
                    resultMap.put(value, list);
                    tempList.add(value);
                }
            }
            //对多个人脸进行排序
            Collections.sort(tempList);

            int size = tempList.size();
            List<FaceIdentify.IdentifyItem> list = resultMap.get(tempList.get(size - 1));
            //防止人脸都是 1 时，取辨识度最大
            Collections.sort(list);
            FaceIdentify.IdentifyItem identifyItem = list.get(0);

            if (identifyItem.getConfidence() >= 70) {
                String person = identifyItem.getPerson_id();
                compareFaceAuth(person);
            } else {
                confidenceLow(identifyItem);
                againIdentify();
            }
        } else {
            identifyNoFace();
            againIdentify();
        }
    }

    private void compareFaceAuth(String person) {
        FaceAuth faceAuth = mFaceAuthDBManager.queryByPersonId(person);
        if (faceAuth != null) {
            sayHello("您好" + faceAuth.getAuthId() + ", 欢迎光临");
        } else {
            getPersonInfo(person);
        }
    }

    public void getPersonInfo(String person) {
        youtucode.getInfo(person);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(GetInfoEvent event) {
        if (event.isOk()) {
            GetInfo getInfo = event.getBean();
            Print.e(getInfo);
            if (getInfo.getErrorcode() == 0) {
                fromCloud(getInfo);
            } else {
                againIdentify();
                onError(getInfo.getErrorcode(), getInfo.getErrormsg());
            }
        } else {
            againIdentify();
            onError(event);
        }
    }

    private void fromCloud(GetInfo getInfo) {
        sayHello("您好" + getInfo.getPerson_name() + ", 欢迎光临");
    }

    private void confidenceLow(FaceIdentify.IdentifyItem identifyItem) {
        Print.e(String.format("识别度为 %s， 较低。请正对屏幕或您未注册个人信息", identifyItem.getConfidence()));
        sayHello("您好, 欢迎光临");
    }

    private void identifyNoFace() {
        Print.e("没有返回人脸信息或者服务器未注册任何人信息");
        sayHello("您好, 欢迎光临");
    }

    private void againDetect() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isDetect = false;
            }
        }, 500);
    }

    private void againIdentify() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                count = 0;
                isPreviewFrame = true;
                isFaceIdentify = false;
            }
        }, 500);
    }


    private void sayHello(String msg) {
        addSpeakAnswer("CameraMsg", msg, true, false);
    }

    //百度人脸识别初始化回调
    @SuppressLint("CheckResult")
    @Override
    public void initSuccess() {

        boolean isCreateGroup = false;
        List<Group> groupList = FaceApi.getInstance().getGroupList(0, 1000);
        String identifier = UserInfo.getInstance().getIdentifier();
        for (Group group : groupList) {
            if (group.getGroupId().equals(identifier)) {
                isCreateGroup = true;
            }
        }
        if (!isCreateGroup) {
            Group group = new Group();
            group.setGroupId(UserInfo.getInstance().getIdentifier());
            boolean ret = FaceApi.getInstance().groupAdd(group);
            if (ret) {
                showMsg("人脸识别群组创建成功");
            }
        }

        DBManager.getInstance().init(this);
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                String groupId = UserInfo.getInstance().getIdentifier();
                FaceApi.getInstance().loadFacesFromDB(groupId);
                showToast("人脸数据加载完成，即将开始1：N");
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

//        faceDetectManager = new FaceDetectManager(getApplicationContext());
//
//        previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
//        previewView.getTextureView().setScaleX(-1);
//
//        CameraImageSource cameraImageSource = new CameraImageSource(this);
//        cameraImageSource.getCameraControl().setPreferredPreviewSize(640, 480);
//        cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_PORTRAIT);
//        cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_FRONT);
//        cameraImageSource.setPreviewView(previewView);
//
////        textureView.setOpaque(false);
////        textureView.setKeepScreenOn(true);
//
//        faceDetectManager.getFaceFilter().setAngle(20);
//        faceDetectManager.setImageSource(cameraImageSource);
//        faceDetectManager.setOnFaceDetectListener(this);
//        faceDetectManager.setUseDetect(true);
//        faceDetectManager.start();

    }

    @Override
    public void initFail(String msg) {
        showMsg("sdk init fail:" + msg);
    }

    private volatile int identityStatus = FEATURE_DATAS_UNREADY;

    private static final int FEATURE_DATAS_UNREADY = 1;
    private static final int IDENTITY_IDLE = 2;
    private static final int IDENTITYING = 3;

    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final PlayService playService = ((PlayService.PlayBinder) service).getService();
            MusicCache.get().setPlayService(playService);
            playService.updateMusicList(new EventCallback<Void>() {
                @Override
                public void onEvent(Void aVoid) {
                    dismissLoading();
                    MultimediaActivity.newInstance(MainActivity.this, isPlay, MultimediaActivity.MULTIMEDIA_REQUESTCODE);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

}
