package com.fanfan.robot.ui.face.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.fanfan.robot.R;
import com.fanfan.robot.presenter.FaceRegisterPresenter;
import com.fanfan.robot.presenter.ipersenter.IFaceRegisterPresenter;
import com.fanfan.robot.view.camera.DetectOpenFaceView;
import com.fanfan.robot.view.camera.DetectionFaceView;
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

import butterknife.BindView;
import butterknife.OnClick;

import static com.fanfan.robot.app.common.Constants.unusual;
import static com.fanfan.robot.ui.face.act.FaceRegisterActivity.State.ADDFACE;
import static com.fanfan.robot.ui.face.act.FaceRegisterActivity.State.NEWPERSON;

/**
 * 人脸提取页面
 */
@Deprecated
public class FaceRegisterActivity extends BarBaseActivity implements
        SurfaceHolder.Callback,
        ICameraPresenter.ICameraView,
        IFaceRegisterPresenter.IFaceRegView,
        ILocalSoundPresenter.ILocalSoundView,
        ISerialPresenter.ISerialView {

    @BindView(R.id.camera_surfaceview)
    SurfaceView cameraSurfaceView;
    @BindView(R.id.detection_face_view)
    DetectionFaceView detectionFaceView;
    @BindView(R.id.opencv_face_view)
    DetectOpenFaceView opencvFaceView;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.rl_add_face)
    RelativeLayout addFaceLayout;
    @BindView(R.id.tv_face_num)
    TextView tvFaceNum;
    @BindView(R.id.iv_add_face)
    ImageView ivAddFace;
    @BindView(R.id.tv_synopsis)
    TextView tvSynopsis;
    @BindView(R.id.tv_job)
    TextView tvJob;


    public static final String AUTHID = "authId";
    public static final String FACE_AUTH_ID = "face_auth_id";

    public static void newInstance(Activity context, String authId) {
        Intent intent = new Intent(context, FaceRegisterActivity.class);
        intent.putExtra(AUTHID, authId);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void newInstance(Activity context, long id) {
        Intent intent = new Intent(context, FaceRegisterActivity.class);
        intent.putExtra(FACE_AUTH_ID, id);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private CameraPresenter mCameraPresenter;
    private FaceRegisterPresenter mFaceRegisterPresenter;

    private LocalSoundPresenter mSoundPresenter;
    private SerialPresenter mSerialPresenter;

    //opencv
    private Mat mRgba;
    private Mat mGray;

    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;

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

    private String mAuthId;
    private FaceAuth faceAuth;
    private FaceAuthDBManager mFaceAuthDBManager;

    private State state = NEWPERSON;

    public enum State {
        NEWPERSON, ADDFACE
    }

    private String mInput;

    @Override
    protected boolean whetherNotReturn() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_register;
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

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();
        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

    }

    @Override
    protected void initData() {
        mFaceAuthDBManager = new FaceAuthDBManager();
        long faceAuthId = getIntent().getLongExtra(FACE_AUTH_ID, -1);
        if (faceAuthId != -1) {
            faceAuth = mFaceAuthDBManager.selectByPrimaryKey(faceAuthId);
            mAuthId = faceAuth.getAuthId();
            state = ADDFACE;
            tvFaceNum.setText(String.format("%d 张", faceAuth.getFaceCount()));
            if (faceAuth.getFaceCount() > 10) {
                addFaceLayout.setVisibility(View.GONE);
                tvTip.setText("已超过注册上线");
            }
            if (faceAuth.getJob() != null) {
                tvJob.setText(faceAuth.getJob());
            }
            if (faceAuth.getSynopsis() != null) {
                tvSynopsis.setText(faceAuth.getSynopsis());
            }
            addFaceLayout.setVisibility(View.VISIBLE);
        } else {
            mAuthId = getIntent().getStringExtra(AUTHID);
            state = NEWPERSON;
            tvFaceNum.setText(String.format("%d 张", faceAuth == null ? 0 : faceAuth.getFaceCount()));
            addFaceLayout.setVisibility(View.GONE);
        }
        mFaceRegisterPresenter = new FaceRegisterPresenter(this, mAuthId);

        if (state == NEWPERSON) {
            tvJob.setVisibility(View.GONE);
            tvSynopsis.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mFaceRegisterPresenter.start();
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
        mCameraPresenter.closeCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFaceRegisterPresenter.finish();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPresenter.finish();
    }

    @OnClick({R.id.iv_add_face, R.id.tv_synopsis, R.id.tv_job})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_face:
                ivAddFace.setEnabled(false);
                addFaceLayout.setVisibility(View.GONE);
                mFaceRegisterPresenter.setAddface();
                break;
            case R.id.tv_synopsis:
                shiwSynopsis(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (faceAuth == null) {
                            showToast("请先注册人脸");
                            return;
                        }
                        faceAuth.setSynopsis(mInput);
                        mFaceAuthDBManager.update(faceAuth);
                        tvSynopsis.setText(faceAuth.getSynopsis());
                    }
                });
                break;
            case R.id.tv_job:
                showJob(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (faceAuth == null) {
                            showToast("请先注册人脸");
                            return;
                        }
                        faceAuth.setJob(mInput);
                        mFaceAuthDBManager.update(faceAuth);
                        tvJob.setText(faceAuth.getJob());
                    }
                });
                break;
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

    private void addSpeakAnswer(String messageContent) {
        mSoundPresenter.doAnswer(messageContent);
    }

    private void addSpeakAnswer(int res) {
        mSoundPresenter.doAnswer(getResources().getString(res));
    }

    private void showJob(MaterialDialog.SingleButtonCallback callback) {
        showInputDialog("职位", 2, 10, callback);
    }

    private void shiwSynopsis(MaterialDialog.SingleButtonCallback callback) {
        showInputDialog("简介", 10, 20, callback);
    }

    private void showInputDialog(String title, int min, int max, MaterialDialog.SingleButtonCallback callback) {
        new MaterialDialog.Builder(this)
                .title(title)
                .inputType(
                        InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                                | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText(R.string.confirm)
                .inputRange(min, max)
                .alwaysCallInputCallback()
                .input("请输入" + title, "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Print.e(input);
                        mInput = String.valueOf(input);
                    }
                })
                .onPositive(callback)
                .build()
                .show();

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

        if (state == NEWPERSON) {
            mFaceRegisterPresenter.newPerson(bitmap);
        }

        mFaceRegisterPresenter.uploadFace(faceAuth, bitmap);

        mFaceRegisterPresenter.detectFace(bitmap);

        if (!unusual) {
            opencvDraw(bitmap);
        }
        opencvDraw(bitmap);
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
    public void newpersonSuccess(String person_id) {
        faceAuth = new FaceAuth();
        faceAuth.setAuthId(mAuthId);
        faceAuth.setPersonId(person_id);
        faceAuth.setFaceCount(1);
        faceAuth.setSaveTime(System.currentTimeMillis());
        boolean insert = mFaceAuthDBManager.insert(faceAuth);
        tvJob.setVisibility(View.VISIBLE);
        tvSynopsis.setVisibility(View.VISIBLE);
        if (insert) {
            tvTip.setText("添加个人信息成功");
            tvFaceNum.setText(String.format("%d 张", faceAuth.getFaceCount()));
            addFaceLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void uploadBitmapFinish(int number) {
        if (number != 0) {
            faceAuth.setFaceCount(faceAuth.getFaceCount() + number);
            faceAuth.setSaveTime(System.currentTimeMillis());
            boolean insert = mFaceAuthDBManager.update(faceAuth);
            if (insert) {
                tvTip.setText("增加人脸成功");
                tvFaceNum.setText(String.format("%d 张", faceAuth.getFaceCount()));
                ivAddFace.setEnabled(true);
                addFaceLayout.setVisibility(View.VISIBLE);
                if (faceAuth.getFaceCount() > 10) {
                    addFaceLayout.setVisibility(View.GONE);
                    tvTip.setText("已超过注册上线");
                }
            }
        } else {
            tvTip.setText("对个体添加了几乎相同的人脸, 请变换表情试试 .");
            mFaceRegisterPresenter.setAddface();
        }
    }

    //**********************************************************************************************
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
