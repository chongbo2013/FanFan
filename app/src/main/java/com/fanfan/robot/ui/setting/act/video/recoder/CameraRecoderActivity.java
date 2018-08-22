package com.fanfan.robot.ui.setting.act.video.recoder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.novel.utils.bitmap.BitmapUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.view.camera.CameraView;
import com.fanfan.robot.view.camera.CaptureLayout;
import com.seabreeze.log.Print;

import java.io.File;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/3/14/014.
 */

public class CameraRecoderActivity extends BarBaseActivity {

    @BindView(R.id.camera_view)
    CameraView cameraView;

    public static final String FIRST_FRAME = "FIRST_FRAME";
    public static final String VIDEO_URL = "VIDEO_URL";

    public static final int REAODER_RESULTCODE = 226;

    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, CameraRecoderActivity.class);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera_reaoder;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        cameraView.setSaveVideoPath(Constants.RECORDER_PATH);

        cameraView.setFeatures(CameraView.BUTTON_STATE_ONLY_RECORDER);
        cameraView.setMediaQuality(CameraView.MEDIA_QUALITY_MIDDLE);

        cameraView.setErrorLisenter(new CameraView.ErrorListener() {
            @Override
            public void onError() {
                finish();
            }

            @Override
            public void AudioPermissionError() {

            }
        });

        cameraView.setCameraLisenter(new CameraView.CameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                long dataTake = System.currentTimeMillis();
                if (BitmapUtils.saveBitmapToFile(bitmap, Constants.PICTURETAKEN, dataTake + ".jpg")) {
                    Print.e(Constants.PROJECT_PATH + Constants.PICTURETAKEN + File.separator + dataTake + ".jpg");
                }
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                long dataTake = System.currentTimeMillis();
                if (BitmapUtils.saveBitmapToFile(firstFrame, Constants.PICTURETAKEN, dataTake + ".jpg")) {
                    Intent intent = new Intent();
                    intent.putExtra(VIDEO_URL, url);
                    intent.putExtra(FIRST_FRAME, Constants.PROJECT_PATH + Constants.PICTURETAKEN + File.separator + dataTake + ".jpg");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        cameraView.setLeftClickListener(new CaptureLayout.ClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.onPause();
    }
}
