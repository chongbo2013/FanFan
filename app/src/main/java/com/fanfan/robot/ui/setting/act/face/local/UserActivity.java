package com.fanfan.robot.ui.setting.act.face.local;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.aip.api.FaceApi;
import com.baidu.aip.entity.Feature;
import com.baidu.aip.entity.User;
import com.baidu.aip.utils.FileUitls;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends BarBaseActivity {

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_id_tv)
    TextView userIdTv;
    @BindView(R.id.user_info_tv)
    TextView userInfoTv;
    @BindView(R.id.group_id_tv)
    TextView groupIdTv;
    @BindView(R.id.feature_tv)
    TextView featureTv;
    @BindView(R.id.face_iv)
    ImageView faceIv;


    public static void newInstance(Activity context, User user) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra("user_id", user.getUserId());
        intent.putExtra("user_info", user.getUserInfo());
        intent.putExtra("group_id", user.getGroupId());
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_user;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        String userId = intent.getStringExtra("user_id");
        String userInfo = intent.getStringExtra("user_info");
        String groupId = intent.getStringExtra("group_id");


        userIdTv.setText(userId);
        userInfoTv.setText(userInfo);
        groupIdTv.setText(groupId);

        User user = FaceApi.getInstance().getUserInfo(groupId, userId);
        List<Feature> featureList = user.getFeatureList();
        if (featureList != null && featureList.size() > 0) {
            // featureTv.setText(new String(featureList.get(0).getFeature()));
            File faceDir = FileUitls.getFaceDirectory();
            if (faceDir != null && faceDir.exists()) {
                File file = new File(faceDir, featureList.get(0).getImageName());
                if (file != null && file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    faceIv.setImageBitmap(bitmap);
                }
            }
        }
    }

}
