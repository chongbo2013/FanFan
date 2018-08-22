package com.fanfan.robot.ui.setting.act.face;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.db.manager.FaceAuthDBManager;
import com.fanfan.robot.model.FaceAuth;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.recycler.face.UserInfoAdapter;
import com.fanfan.robot.view.manager.FullyLinearLayoutManager;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.face.bean.Delperson;
import com.fanfan.youtu.api.face.bean.FacePersonid;
import com.fanfan.youtu.api.face.bean.GetInfo;
import com.fanfan.youtu.api.face.event.DelPersonEvent;
import com.fanfan.youtu.api.face.event.FacePersonidEvent;
import com.fanfan.youtu.api.face.event.GetInfoEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 人脸数据页面
 */
public class FaceDataActivity extends BarBaseActivity {

    @BindView(R.id.recycler_face)
    RecyclerView recyclerFace;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, FaceDataActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private UserInfoAdapter userInfoAdapter;
    private List<FaceAuth> faceAuths = new ArrayList<>();

    private Youtucode youtucode;

    private FaceAuthDBManager mFaceAuthDBManager;

    private MaterialDialog materialDialog;

    private int delPos;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_data;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        setAdapter();
        youtucode = Youtucode.getSingleInstance();
        youtucode.getPersonids();

        mFaceAuthDBManager = new FaceAuthDBManager();

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void setAdapter() {
        userInfoAdapter = new UserInfoAdapter(faceAuths);
        recyclerFace.setAdapter(userInfoAdapter);

        recyclerFace.setLayoutManager(new FullyLinearLayoutManager(this));
        recyclerFace.setLayoutManager(new LinearLayoutManager(this));
        recyclerFace.setItemAnimator(new DefaultItemAnimator());
        recyclerFace.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        userInfoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FaceAuth faceAuth = faceAuths.get(position);
                if (faceAuth.getAuthId() != null) {
                    PersonInfoDetailActivity.navToPersonInfoDetail(FaceDataActivity.this, faceAuth.getPersonId(),
                            PersonInfoDetailActivity.PERSONINFO_DETAIL);
                } else {
                    showProgressDialog();
                    youtucode.getInfo(faceAuth.getPersonId());
                }
            }
        });
        userInfoAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                delPos = position;
                showDialog();
                return false;
            }
        });
        userInfoAdapter.openLoadAnimation();
        userInfoAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(FacePersonidEvent event) {
        if (event.isOk()) {
            FacePersonid facePersonid = event.getBean();
            Print.e(facePersonid);
            if (facePersonid.getErrorcode() == 0) {
                List<String> personIds = facePersonid.getPerson_ids();
                if (personIds.size() > 0) {
                    tvEmpty.setVisibility(View.GONE);
                    //检测数据库完整
                    List<FaceAuth> faceAuths = mFaceAuthDBManager.loadAll();
                    if (faceAuths != null && faceAuths.size() > 0) {
                        for (int i = 0; i < faceAuths.size(); i++) {
                            FaceAuth auth = faceAuths.get(i);
                            for (String personId : personIds) {
                                if(auth.getPersonId().equals(personId)){
                                    faceAuths.remove(i);
                                    i--;
                                }
                            }
                        }
                    }
                    if (faceAuths != null && faceAuths.size() > 0) {
                        mFaceAuthDBManager.deleteList(faceAuths);
                    }
                    for (String personId : personIds) {
                        FaceAuth faceAuth = mFaceAuthDBManager.queryByPersonId(personId);
                        if (faceAuth == null) {
                            faceAuth = new FaceAuth();
                            faceAuth.setPersonId(personId);
                        }
                        userInfoAdapter.addData(faceAuth);
                    }
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            } else {
                onError(facePersonid.getErrorcode(), facePersonid.getErrormsg());
            }
        } else {
            onError(event);
        }
    }

    private void showDialog() {
        DialogUtils.showBasicNoTitleDialog(this, "确定要删除此人脸信息吗？", "取消", "确定",
                new DialogUtils.OnNiftyDialogListener() {
                    @Override
                    public void onClickLeft() {
                    }

                    @Override
                    public void onClickRight() {
                        youtucode.delPerson(faceAuths.get(delPos).getPersonId());
                    }
                });
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(DelPersonEvent event) {
        if (event.isOk()) {
            Delperson delperson = event.getBean();
            Print.e(delperson);
            if (delperson.getErrorcode() == 0) {
                String personId = delperson.getPerson_id();
                showToast("删除 ：" + delperson.getDeleted() + " 张人脸");
                FaceAuth auth = mFaceAuthDBManager.queryByPersonId(personId);
                if(auth != null) {
                    if (mFaceAuthDBManager.delete(auth)) {
                        faceAuths.remove(delPos);
                        userInfoAdapter.notifyDataSetChanged();
                        Print.e(faceAuths);
                    } else {
                        Print.e("faceAuth error");
                    }
                }
            } else {
                onError(delperson.getErrorcode(), delperson.getErrormsg());
            }
        } else {
            onError(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(GetInfoEvent event) {
        dismissProgressDialog();
        if (event.isOk()) {
            GetInfo getInfo = event.getBean();
            Print.e(getInfo);
            if (getInfo.getErrorcode() == 0) {
                String person_id = getInfo.getPerson_id();

                FaceAuth faceAuth = new FaceAuth();
                faceAuth.setPersonId(person_id);
                int position = faceAuths.indexOf(faceAuth);
                faceAuth = faceAuths.get(position);

                faceAuth.setSaveTime(System.currentTimeMillis());
                faceAuth.setAuthId(getInfo.getPerson_name());
                faceAuth.setFaceCount(getInfo.getFace_ids().size());

                if (mFaceAuthDBManager.queryByPersonId(person_id) == null) {
                    mFaceAuthDBManager.insert(faceAuth);
                }
                userInfoAdapter.notifyDataSetChanged();
            } else {
                onError(getInfo.getErrorcode(), getInfo.getErrormsg());
            }
        } else {
            onError(event);
        }
    }

    private void showProgressDialog() {
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

    private void dismissProgressDialog() {
        if (materialDialog != null && materialDialog.isShowing()) {
            materialDialog.dismiss();
            materialDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PersonInfoDetailActivity.PERSONINFO_DETAIL) {
            if (resultCode == RESULT_OK) {
                youtucode.getPersonids();
            }
        }
    }
}
