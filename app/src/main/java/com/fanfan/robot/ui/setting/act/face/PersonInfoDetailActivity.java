package com.fanfan.robot.ui.setting.act.face;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.db.manager.FaceAuthDBManager;
import com.fanfan.robot.model.FaceAuth;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.recycler.face.FaceListAdapter;
import com.fanfan.robot.view.manager.FullyLinearLayoutManager;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.face.bean.DelFace;
import com.fanfan.youtu.api.face.bean.GetInfo;
import com.fanfan.youtu.api.face.event.DelFaceEvent;
import com.fanfan.youtu.api.face.event.GetInfoEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/9.
 */

public class PersonInfoDetailActivity extends BarBaseActivity {

    @BindView(R.id.tv_person_name)
    TextView tvPersonName;
    @BindView(R.id.ll_person_name)
    LinearLayout llPersonName;
    @BindView(R.id.tv_person_tag)
    TextView tvPersonTag;
    @BindView(R.id.ll_person_tag)
    LinearLayout llPersonTag;
    @BindView(R.id.face_recycler)
    RecyclerView faceRecycler;

    public static final int PERSONINFO_DETAIL = 227;

    public static void navToPersonInfoDetail(Activity context, String userInfoId, int requestCode) {
        Intent intent = new Intent(context, PersonInfoDetailActivity.class);
        intent.putExtra("userInfoId", userInfoId);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private String userInfoId;

    private FaceListAdapter faceListAdapter;

    private Youtucode youtucode;

    private FaceAuthDBManager mFaceAuthDBManager;

    private boolean isModify;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_info_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        userInfoId = getIntent().getStringExtra("userInfoId");

        youtucode = Youtucode.getSingleInstance();

        mFaceAuthDBManager = new FaceAuthDBManager();
    }


    @Override
    protected void initData() {
        youtucode.getInfo(userInfoId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(GetInfoEvent event) {
        if (event.isOk()) {
            GetInfo getInfo = event.getBean();
            Print.e(getInfo);
            if (getInfo.getErrorcode() == 0) {
                String person_id = getInfo.getPerson_id();
                tvPersonName.setText(getInfo.getPerson_name().equals("") ? "请设置" : getInfo.getPerson_name());
                tvPersonTag.setText(getInfo.getTag().equals("") ? "请设置" : getInfo.getTag());
                List<String> list = getInfo.getFace_ids();
                if (list != null && list.size() > 0) {
                    setAdapter(list);
                }
            } else {
                onError(getInfo.getErrorcode(), getInfo.getErrormsg());
            }
        } else {
            onError(event);
        }
    }

    @Override
    protected void setListener() {

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
    protected boolean setResult() {
        if (isModify) {
            setResult(RESULT_OK);
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isModify) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    private void setAdapter(final List<String> face_ids) {

        faceListAdapter = new FaceListAdapter(face_ids);
        faceListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                FaceInfoDetailActivity.navToFaceInfoDetail(PersonInfoDetailActivity.this, face_ids.get(position));
            }
        });
        faceListAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                final String face_id = face_ids.get(position);
                DialogUtils.showBasicNoTitleDialog(PersonInfoDetailActivity.this, "您确定要删除此人脸信息吗", "取消", "确定",
                        new DialogUtils.OnNiftyDialogListener() {
                            @Override
                            public void onClickLeft() {

                            }

                            @Override
                            public void onClickRight() {
                                youtucode.delFace(userInfoId, face_id);
                            }
                        });
                return false;
            }
        });
        faceListAdapter.openLoadAnimation();
        faceListAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        faceRecycler.setAdapter(faceListAdapter);

        faceRecycler.setLayoutManager(new FullyLinearLayoutManager(this));
        faceRecycler.setLayoutManager(new LinearLayoutManager(this));
        faceRecycler.setItemAnimator(new DefaultItemAnimator());
        faceRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(DelFaceEvent event) {
        if (event.isOk()) {
            DelFace faceIdentify = event.getBean();
            Print.e(faceIdentify);
            if (faceIdentify.getErrorcode() == 0) {
                List<String> faceIds = faceIdentify.getFace_ids();
                if (faceIds.size() > 0) {
                    String faceId = faceIds.get(0);
//                    faceListAdapter.removeItem(faceId);
                    FaceAuth faceAuth = mFaceAuthDBManager.queryByPersonId(userInfoId);
                    faceAuth.setFaceCount(faceAuth.getFaceCount() - 1);
                    mFaceAuthDBManager.update(faceAuth);
                    faceListAdapter.remove(faceIds.indexOf(faceId));
                    Print.e(faceIds);
                    showToast("删除成功");
                }
            } else {
                onError(faceIdentify.getErrorcode(), faceIdentify.getErrormsg());
            }
        } else {
            onError(event);
        }
    }

    @OnClick({R.id.ll_person_name, R.id.ll_person_tag})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_person_name:
                ModifyActivity.navToModify(this, userInfoId, ModifyActivity.MODIFY_NAME_CODE, tvPersonName.getText().toString());
                break;
            case R.id.ll_person_tag:
                ModifyActivity.navToModify(this, userInfoId, ModifyActivity.MODIFY_TAG_CODE, tvPersonTag.getText().toString());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ModifyActivity.MODIFY_NAME_CODE:
                if (resultCode == RESULT_OK) {
                    String etModify = data.getStringExtra("etModify");
                    tvPersonName.setText(etModify);
                    FaceAuth faceAuth = mFaceAuthDBManager.queryByPersonId(userInfoId);
                    faceAuth.setAuthId(etModify);
                    mFaceAuthDBManager.update(faceAuth);
                    isModify = true;
                }
                break;
            case ModifyActivity.MODIFY_TAG_CODE:
                if (resultCode == RESULT_OK) {
                    String etModify1 = data.getStringExtra("etModify");
                    tvPersonTag.setText(etModify1);
                }
                break;
        }
    }

}