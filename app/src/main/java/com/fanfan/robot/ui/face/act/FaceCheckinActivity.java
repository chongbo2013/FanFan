package com.fanfan.robot.ui.face.act;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.db.manager.FaceAuthDBManager;
import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.FaceAuth;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.presenter.CameraPresenter;
import com.fanfan.robot.presenter.LocalSoundPresenter;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ICameraPresenter;
import com.fanfan.robot.presenter.ipersenter.ILocalSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.robot.other.udp.SocketManager;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.db.manager.CheckInDBManager;
import com.fanfan.robot.model.CheckIn;
import com.fanfan.robot.presenter.FaceCheckinPresenter;
import com.fanfan.robot.presenter.ipersenter.IFaceCheckinPresenter;
import com.fanfan.robot.ui.face.act.sign.SignAllActivity;
import com.fanfan.robot.view.CircleImageView;
import com.fanfan.robot.view.camera.DetectOpenFaceView;
import com.fanfan.robot.view.camera.DetectionFaceView;
import com.fanfan.youtu.api.face.bean.FaceIdentify;
import com.fanfan.youtu.api.face.bean.GetInfo;
import com.fanfan.youtu.api.face.bean.detectFace.Face;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.facedetect.DetectionBasedTracker;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.fanfan.robot.app.common.Constants.unusual;

/**
 * 人脸签到页面
 */
@Deprecated
public class FaceCheckinActivity extends BarBaseActivity implements
        SurfaceHolder.Callback,
        ICameraPresenter.ICameraView,
        IFaceCheckinPresenter.ICheckinView,
        ILocalSoundPresenter.ILocalSoundView,
        ISerialPresenter.ISerialView {

    @BindView(R.id.camera_surfaceview)
    SurfaceView cameraSurfaceView;
    @BindView(R.id.detection_face_view)
    DetectionFaceView detectionFaceView;
    @BindView(R.id.opencv_face_view)
    DetectOpenFaceView opencvFaceView;
    @BindView(R.id.tv_sign_info)
    TextView tvSignInfo;
    @BindView(R.id.tv_sign_all)
    TextView tvSignAll;
    @BindView(R.id.ic_head)
    CircleImageView icHead;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_again)
    TextView tvAgain;
    @BindView(R.id.confirm_layout)
    LinearLayout confirmLayout;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_job)
    TextView tvJob;
    @BindView(R.id.tv_synopsis)
    TextView tvSynopsis;
    @BindView(R.id.beauty_layout)
    RelativeLayout beautyLayout;
    @BindView(R.id.ic_beauty_head)
    ImageView icBeautyHead;
    @BindView(R.id.tv_beauty)
    TextView tvBeauty;
    @BindView(R.id.tv_face)
    TextView tvFace;
    @BindView(R.id.tv_checkin)
    TextView tvCheckIn;

    public static final int CHECK_REQUESTCODE = 232;
    public static final int CHECK_RESULTCODE = 234;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, FaceCheckinActivity.class);
        context.startActivityForResult(intent, CHECK_REQUESTCODE);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    //opencv
    private Mat mRgba;
    private Mat mGray;

    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;

    private LocalSoundPresenter mSoundPresenter;
    private SerialPresenter mSerialPresenter;

    private int mAbsoluteFaceSize = 0;
    private float mRelativeFaceSize = 0.2f;
    private int mDetectorType = JAVA_DETECTOR;
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    System.loadLibrary("detection_based_tracker");

                    try {
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            mJavaDetector = null;
                        } else

                            mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public enum State {
        CONFIRM, BEAUTY, CAMERA
    }

    private CameraPresenter mCameraPresenter;
    private FaceCheckinPresenter mCheckinPresenter;

    private FaceAuthDBManager mFaceAuthDBManager;
    private CheckInDBManager mCheckInDBManager;

    private State state = State.CAMERA;

    private boolean isBacking;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_check_in;
    }

    @Override
    protected void initView() {
        super.initView();
        SurfaceHolder holder = cameraSurfaceView.getHolder(); // 获得SurfaceHolder对象
        holder.addCallback(this); // 为SurfaceView添加状态监听
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mRgba = new Mat();
        mGray = new Mat();

        mCameraPresenter = new CameraPresenter(this, holder);

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        mCheckinPresenter = new FaceCheckinPresenter(this);

        confirmLayout.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        mFaceAuthDBManager = new FaceAuthDBManager();
        mCheckInDBManager = new CheckInDBManager();

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();
        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCheckinPresenter.start();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSoundPresenter.onResume();
        addSpeakAnswer("请对准摄像头");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraPresenter.closeCamera();
        mSoundPresenter.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCheckinPresenter.finish();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPresenter.finish();
    }

    @Override
    protected boolean setResult() {
        if (state != State.CAMERA) {
            changeCamera();
            mCheckinPresenter.setFaceIdentify();
            return true;
        }
       return super.setResult();
    }

    @Override
    public void onBackPressed() {
        if (state != State.CAMERA) {
            changeCamera();
            mCheckinPresenter.setFaceIdentify();
            return;
        }
        super.onBackPressed();
    }

    @SuppressLint("NewApi")
    @OnClick({R.id.tv_sign_info, R.id.tv_sign_all, R.id.tv_confirm, R.id.tv_again})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_sign_info:
                break;
            case R.id.tv_sign_all:
                SignAllActivity.newInstance(this);
                break;
            case R.id.tv_confirm:
                mCheckinPresenter.confirmChinkIn();
                break;
            case R.id.tv_again:
                confirmLayout.setVisibility(View.GONE);
                mCheckinPresenter.setFaceIdentify();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_white, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                SignAllActivity.newInstance(this);
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void addSpeakAnswer(String messageContent) {
        mSoundPresenter.stopEvery();
        mSoundPresenter.doAnswer(messageContent);
    }

    private void addSpeakAnswer(int res) {
        mSoundPresenter.stopEvery();
        mSoundPresenter.doAnswer(getResources().getString(res));
    }

    private void changeConfirm() {
        state = State.CONFIRM;
        beautyLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.VISIBLE);
    }

    private void changeBeauty() {
        state = State.BEAUTY;
        beautyLayout.setVisibility(View.VISIBLE);
        confirmLayout.setVisibility(View.GONE);
    }

    private void changeCamera() {
        state = State.CAMERA;
        beautyLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.GONE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCameraPresenter.openCamera();
        mCameraPresenter.doStartPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCameraPresenter.setMatrix(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCameraPresenter.closeCamera();
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
    public void previewFinish() {

    }

    @Override
    public void pictureTakenSuccess(String savePath) {

    }

    @Override
    public void pictureTakenFail() {

    }

    @Override
    public void autoFocusSuccess() {

    }

    @Override
    public void noFace() {
        detectionFaceView.clear();
        opencvFaceView.clear();
    }

    @Override
    public void tranBitmap(Bitmap bitmap, int num) {

        if (!isBacking) {

            mCheckinPresenter.faceIdentifyFace(bitmap);
        }

        if (!unusual) {
            opencvDraw(bitmap);
        }
    }

    private void opencvDraw(Bitmap bitmap) {
        Utils.bitmapToMat(bitmap, mRgba);
        Mat mat1 = new Mat();
        Utils.bitmapToMat(bitmap, mat1);
        Imgproc.cvtColor(mat1, mGray, Imgproc.COLOR_BGR2GRAY);
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }
        MatOfRect faces = new MatOfRect();
        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)

                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        } else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        }
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0) {
            opencvFaceView.setFaces(facesArray, mCameraPresenter.getOrientionOfCamera());
        }
    }

    @Override
    public void setCameraFaces(Camera.Face[] faces) {
        if (faces.length > 0) {
            detectionFaceView.setFaces(faces, mCameraPresenter.getOrientionOfCamera());
        }
    }

    @Override
    public void compareFaceAuth(String person) {
        FaceAuth faceAuth = mFaceAuthDBManager.queryByPersonId(person);
        if (faceAuth != null) {
            List<CheckIn> checkIns = mCheckInDBManager.queryByName(faceAuth.getAuthId());
            if (checkIns == null || checkIns.size() == 0) {
                mCheckinPresenter.setFaceAuth(faceAuth);
                mCheckinPresenter.detectFace();
                return;
            }
            Print.e(checkIns);
            Collections.sort(checkIns);

            CheckIn checkIn = checkIns.get(0);
            if (TimeUtils.isToday(checkIn.getTime())) {
                isToday();
            } else {
                mCheckinPresenter.setFaceAuth(faceAuth);
                mCheckinPresenter.detectFace();
            }
        } else {
            mCheckinPresenter.getPersonInfo(person);
        }
    }

    @Override
    public void identifyNoFace() {
        tvSignInfo.setText("请正对屏幕或您未注册个人信息");
//        addSpeakAnswer("请正对屏幕或您未注册个人信息");
        addSpeakAnswer("贵客您好，我是公司智能服务机器人，系统中未检测到您的身份信息，现在为您切换到服务系统并为您提供引导服务");
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8002AA");
        isBacking = true;
    }

    @Override
    public void confidenceLow(FaceIdentify.IdentifyItem identifyItem) {

        tvSignInfo.setText(String.format("识别度为 %s， 较低。请正对屏幕或您未注册个人信息", identifyItem.getConfidence()));
        addSpeakAnswer("贵客您好，我是公司智能服务机器人，系统中未检测到您的身份信息，现在为您切换到服务系统并为您提供引导服务");
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8002AA");
        isBacking = true;
    }

    @Override
    public void showConfirm(Bitmap circleBitmap, FaceAuth faceAuth) {
        CheckIn checkIn = new CheckIn();

        checkIn.setName(faceAuth.getAuthId());
        checkIn.setTime(System.currentTimeMillis());
        addSpeakAnswer("欢迎您，" + checkIn.getName() + "。" + TimeUtils.getAPm());
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8002AA");
        changeConfirm();
        icHead.setImageBitmap(circleBitmap);
        tvName.setText(faceAuth.getAuthId());
        if (faceAuth.getJob() != null) {
            tvJob.setText(faceAuth.getJob());
            tvJob.setVisibility(View.VISIBLE);
        } else {
            tvJob.setVisibility(View.GONE);
        }
        if (faceAuth.getSynopsis() != null) {
            tvSynopsis.setText(faceAuth.getSynopsis());
            tvSynopsis.setVisibility(View.VISIBLE);
        } else {
            tvSynopsis.setVisibility(View.GONE);
        }
    }

    @Override
    public void confirmChinkIn(Bitmap bitmap, String authId, Face face) {

        CheckIn checkIn = new CheckIn();
        checkIn.setName(authId);
        checkIn.setTime(System.currentTimeMillis());
        boolean insert = mCheckInDBManager.insert(checkIn);
        if (insert) {
            tvSignInfo.setText(String.format("%s 签到成功", authId));
   //         addSpeakAnswer("欢迎您，" + checkIn.getName() + "。" + TimeUtils.getAPm());
            List<CheckIn> checkIns = mCheckInDBManager.queryByName(authId);
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
            changeBeauty();
            icBeautyHead.setImageBitmap(bitmap);
            tvBeauty.setText(String.format("%d分", face.getBeauty()));
            StringBuilder builder = new StringBuilder();
            builder.append("FAN FAN 识别报告 ： \n");
            builder.append("您的年龄大约 ").append(face.getAge()).append(" , ");
            if (face.getGender() > 50) {
                builder.append("性别 男 , ");
            } else {
                builder.append("性别 女 , ");
            }
            if (face.getGlasses() == 0) {
                builder.append("不戴眼镜\n");
            } else if (face.getGlasses() == 1) {
                builder.append("佩戴眼镜\n");
            } else if (face.getGlasses() == 2) {
                builder.append("佩戴墨镜\n");
            }
            builder.append("微笑指数：").append(face.getExpression()).append(" , 请保持微笑");
            tvFace.setText(builder.toString());

            List<CheckIn> todayList = mCheckInDBManager.queryByToday();
            if (todayList == null || todayList.size() == 0) {
                tvCheckIn.setText(String.format("今日第 %d 位签到", 1));
            } else {
                tvCheckIn.setText(String.format("今日第 %d%d 位签到", todayList.size(), 1));
            }
        }
    }

    @Override
    public void isToday() {
        tvSignInfo.setText("今日您已签到");
        mCheckinPresenter.setFaceIdentify();
        addSpeakAnswer("今日您已签到");
    }

    @Override
    public void fromCloud(GetInfo getInfo) {
        FaceAuth faceAuth = new FaceAuth();
        faceAuth.setAuthId(getInfo.getPerson_name());
        faceAuth.setPersonId(getInfo.getPerson_id());
        faceAuth.setFaceCount(1);
        faceAuth.setSaveTime(System.currentTimeMillis());

        mCheckinPresenter.setFaceAuth(faceAuth);
        mCheckinPresenter.detectFace();
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
        if (state != State.CAMERA) {
            changeCamera();
            mCheckinPresenter.setFaceIdentify();
            mSoundPresenter.onCompleted();
            return;
        }
        finish();
    }

    @Override
    public void artificial() {
        addSpeakAnswer(R.string.open_artificial);
    }

    @Override
    public void face(SpecialType type, String result) {
        addSpeakAnswer(R.string.open_face);
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

        if (isBacking) {
            setResult(CHECK_RESULTCODE);
            finish();
        }
    }

    @Override
    public void refLocalPage(String key1, String key2, String key3, String key4) {
        addSpeakAnswer(R.string.open_local);
    }

    @Override
    public void stopAll() {
        super.stopAll();
        mSoundPresenter.stopEvery();
        mSoundPresenter.doAnswer(AppUtil.resFoFinal(R.array.wake_up));
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
