package com.fanfan.robot.ui.setting.act.face.local;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.api.FaceApi;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.ARGBImg;
import com.baidu.aip.entity.Feature;
import com.baidu.aip.entity.Group;
import com.baidu.aip.entity.User;
import com.baidu.aip.face.FaceCropper;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.FileImageSource;
import com.baidu.aip.manager.FaceDetector;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.FeatureUtils;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.ImageUtils;
import com.baidu.aip.utils.PreferencesUtil;
import com.baidu.idl.facesdk.FaceInfo;
import com.baidu.idl.facesdk.FaceTracker;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class RegActivity extends BarBaseActivity {

    public static final int SOURCE_REG = 1;

    private static final int REQUEST_CODE_PICK_IMAGE = 1000;
    public static final int REQUEST_CODE_AUTO_DETECT = 100;

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.userinfo_et)
    EditText etUserinfo;
    @BindView(R.id.username_et)
    EditText etUsername;
    @BindView(R.id.avatar_iv)
    ImageView avatarIv;
    @BindView(R.id.auto_detect_btn)
    Button autoDetectBtn;
    @BindView(R.id.pick_from_album_btn)
    Button pickFromAlbumBtn;
    @BindView(R.id.submit_btn)
    Button submitBtn;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, RegActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    // 从相机识别时使用。
    private FaceDetectManager detectManager;
    private List<String> groupIds = new ArrayList<>();
    private String groupId = "";

    // 注册时使用人脸图片路径。
    private String faceImagePath;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_reg;
    }

    @Override
    protected void initData() {

        detectManager = new FaceDetectManager(getApplicationContext());

        submitBtn.setVisibility(View.GONE);

        List<Group> groupList = DBManager.getInstance().queryGroups(0, 1000);
        for (Group group : groupList) {
            groupIds.add(group.getGroupId());
        }
        if (groupIds.size() > 0) {
            groupId = groupIds.get(0);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupIds);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    @Override
    protected void setListener() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < groupIds.size()) {
                    groupId = groupIds.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick({R.id.auto_detect_btn, R.id.pick_from_album_btn, R.id.submit_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.auto_detect_btn:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, 100);
                    return;
                }
                avatarIv.setImageResource(R.mipmap.avatar);

                faceImagePath = null;
                int type = PreferencesUtil.getInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity.TYPE_NO_LIVENSS);

                if (type == LivenessSettingActivity.TYPE_NO_LIVENSS || type == LivenessSettingActivity.TYPE_RGB_LIVENSS) {
                    RgbDetectActivity.newInstance(RegActivity.this);
                } else {
                    showToast("暂不支持");
                }
                break;
            case R.id.pick_from_album_btn:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            100);
                    return;
                }
                avatarIv.setImageResource(R.mipmap.avatar);
                faceImagePath = null;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.submit_btn:
                register(faceImagePath);
                break;
        }
    }

    private void register(final String filePath) {

        final String userinfo = etUserinfo.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        if (TextUtils.isEmpty(userinfo)) {
            showToast("userid不能为空");
            return;
        }
        if (TextUtils.isEmpty(username)) {
            showToast("username不能为空");
            return;
        }
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z_-]{1,}$");
        Matcher matcher = pattern.matcher(userinfo);
        if (!matcher.matches()) {
            showToast("userid由数字、字母、下划线中的一个或者多个组合");
            return;
        }

        // final String groupId = groupIdEt.getText().toString().trim();
        if (TextUtils.isEmpty(groupId)) {
            showToast("分组groupId为空");
            return;
        }
        matcher = pattern.matcher(userinfo);
        if (!matcher.matches()) {
            showToast("groupId由数字、字母、下划线中的一个或者多个组合");
            return;
        }
        /*
         * 用户id（由数字、字母、下划线组成），长度限制128B
         * uid为用户的id,百度对uid不做限制和处理，应该与您的帐号系统中的用户id对应。
         *
         */
        final String uid = UUID.randomUUID().toString();
        // String uid = 修改为自己用户系统中用户的id;

        if (TextUtils.isEmpty(faceImagePath)) {
            showToast("人脸文件不存在");
            return;
        }
        final File file = new File(filePath);
        if (!file.exists()) {
            showToast("人脸文件不存在");
            return;
        }


        final User user = new User();
        user.setUserId(uid);
        user.setUserInfo(userinfo);
        user.setUserName(username);
        user.setGroupId(groupId);

        Executors.newSingleThreadExecutor().submit(new Runnable() {

            @Override
            public void run() {
                ARGBImg argbImg = FeatureUtils.getARGBImgFromPath(filePath);
                byte[] bytes = new byte[2048];
                int ret = FaceSDKManager.getInstance().getFaceFeature().faceFeature(argbImg, bytes);
                if (ret == FaceDetector.NO_FACE_DETECTED) {
                    showToast("人脸太小（必须打于最小检测人脸minFaceSize），或者人脸角度太大，人脸不是朝上");
                } else if (ret != -1) {
                    Feature feature = new Feature();
                    feature.setGroupId(groupId);
                    feature.setUserId(uid);
                    feature.setFeature(bytes);
                    feature.setImageName(file.getName());

                    user.getFeatureList().add(feature);
                    if (FaceApi.getInstance().userAdd(user)) {
                        showToast("注册成功");
                        finish();
                    } else {
                        showToast("注册失败");
                    }

                } else {
                    showToast("抽取特征失败");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_AUTO_DETECT && data != null) {
            faceImagePath = data.getStringExtra("file_path");

            Bitmap bitmap = BitmapFactory.decodeFile(faceImagePath);
            avatarIv.setImageBitmap(bitmap);
            submitBtn.setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String filePath = getRealPathFromURI(uri);
            detect(filePath);
        }
    }


    // 从相册检测。
    private void detect(final String filePath) {

        FileImageSource fileImageSource = new FileImageSource();
        fileImageSource.setFilePath(filePath);
        detectManager.setImageSource(fileImageSource);
        detectManager.setUseDetect(true);
        detectManager.setOnFaceDetectListener(new FaceDetectManager.OnFaceDetectListener() {
            @Override
            public void onDetectFace(int status, FaceInfo[] faces, ImageFrame frame) {
                if (faces != null && status != FaceTracker.ErrCode.NO_FACE_DETECTED.ordinal()
                        && status != FaceTracker.ErrCode.UNKNOW_TYPE.ordinal()) {
                    final Bitmap cropBitmap = FaceCropper.getFace(frame.getArgb(), faces[0], frame.getWidth());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            avatarIv.setImageBitmap(cropBitmap);
                        }
                    });

                    // File file = File.createTempFile(UUID.randomUUID().toString() + "", ".jpg");
                    File faceDir = FileUitls.getFaceDirectory();
                    if (faceDir != null) {
                        String imageName = UUID.randomUUID().toString();
                        File file = new File(faceDir, imageName);
                        // 压缩人脸图片至300 * 300，减少网络传输时间
                        ImageUtils.resize(cropBitmap, file, 300, 300);
                        RegActivity.this.faceImagePath = file.getAbsolutePath();
                        submitBtn.setVisibility(View.VISIBLE);
                    } else {
                        showToast("注册人脸目录未找到");
                    }
                } else {
                    showToast("未检测到人脸，可能原因：人脸太小（必须大于最小检测人脸minFaceSize），或者人脸角度太大，人脸不是朝上");
                }
            }
        });
        detectManager.start();
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }
}
