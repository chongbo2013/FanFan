package com.fanfan.robot.ui.face.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.robot.other.udp.SocketManager;
import com.fanfan.robot.presenter.CameraPresenter;
import com.fanfan.robot.presenter.DetectFacePresenter;
import com.fanfan.robot.presenter.LocalSoundPresenter;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ICameraPresenter;
import com.fanfan.robot.presenter.ipersenter.IDetectFacePresenter;
import com.fanfan.robot.presenter.ipersenter.ILocalSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.view.CircleImageView;
import com.fanfan.robot.view.camera.DetectOpenFaceView;
import com.fanfan.robot.view.camera.DetectionFaceView;
import com.fanfan.youtu.api.face.bean.detectFace.Face;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;

import butterknife.BindView;

import static com.fanfan.robot.app.common.Constants.unusual;

public class DetectfaceActivity extends BarBaseActivity implements
        SurfaceHolder.Callback,
        ICameraPresenter.ICameraView,
        IDetectFacePresenter.IDetectFaceView,
        ILocalSoundPresenter.ILocalSoundView,
        ISerialPresenter.ISerialView {


    @BindView(R.id.camera_surfaceview)
    SurfaceView cameraSurfaceView;
    @BindView(R.id.detection_face_view)
    DetectionFaceView detectionFaceView;
    @BindView(R.id.opencv_face_view)
    DetectOpenFaceView opencvFaceView;
    @BindView(R.id.beauty_layout)
    LinearLayout beautyLayout;
    @BindView(R.id.ic_head)
    ImageView icHead;
    @BindView(R.id.tv_beauty)
    TextView tvBeauty;
    @BindView(R.id.tv_face)
    TextView tvFace;
    @BindView(R.id.tv_welcome)
    TextView tvWelcome;


    static {
        if (!OpenCVLoader.initDebug()) {
            System.out.println("opencv 初始化失败！");
        } else {
            System.loadLibrary("detection_based_tracker");
        }
    }

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, DetectfaceActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


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


    public enum State {
        BEAUTY, CAMERA
    }

    private CameraPresenter mCameraPresenter;
    private DetectFacePresenter mDetectFacePresenter;

    private LocalSoundPresenter mSoundPresenter;
    private SerialPresenter mSerialPresenter;

    private State state = State.CAMERA;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detectface;
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

        mDetectFacePresenter = new DetectFacePresenter(this);
    }

    @Override
    protected void initData() {
        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();
        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDetectFacePresenter.start();
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
        mDetectFacePresenter.finish();
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
            mDetectFacePresenter.setFaceDetect();
            return true;
        }
        return super.setResult();
    }

    @Override
    public void onBackPressed() {
        if (state != State.CAMERA) {
            changeCamera();
            mDetectFacePresenter.setFaceDetect();
            return;
        }
        super.onBackPressed();
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

    private void changeBeauty() {
        state = State.BEAUTY;
        beautyLayout.setVisibility(View.VISIBLE);
        cameraSurfaceView.setVisibility(View.GONE);
    }

    private void changeCamera() {
        state = State.CAMERA;
        beautyLayout.setVisibility(View.GONE);
        cameraSurfaceView.setVisibility(View.VISIBLE);
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
        if (state == State.CAMERA) {
            mDetectFacePresenter.detectFace(bitmap);
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
    public void showConfirm(Face face, Bitmap bitmap) {
        changeBeauty();

        icHead.setImageBitmap(bitmap);

        tvBeauty.setText(String.format("%d分", face.getBeauty()));

        StringBuilder builder = new StringBuilder();
        StringBuilder welCome = new StringBuilder();

        builder.append("FAN FAN 识别报告 ： \n");
        builder.append("您的年龄大约 ").append(face.getAge()).append(" , ");
        if (face.getGender() > 50) {
            builder.append("性别 男 , ");
            welCome.append("欢迎您，先生");
        } else {
            builder.append("性别 女 , ");
            welCome.append("欢迎您，女士");
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
        tvWelcome.setText(welCome.toString());

        addSpeakAnswer(welCome.toString());
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8002AA");
    }

    //*************************************************//

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
        setResult();
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
