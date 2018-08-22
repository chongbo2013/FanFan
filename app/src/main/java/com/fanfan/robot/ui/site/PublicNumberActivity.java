package com.fanfan.robot.ui.site;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;

import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.db.manager.SiteDBManager;
import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.model.SiteBean;
import com.fanfan.robot.presenter.LocalSoundPresenter;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ILocalSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.robot.other.udp.SocketManager;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.customtabs.IntentUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.other.HotAdapter;
import com.seabreeze.log.Print;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

/**
 * Created by android on 2018/1/6.
 */

public class PublicNumberActivity extends BarBaseActivity implements
        ILocalSoundPresenter.ILocalSoundView,
        ISerialPresenter.ISerialView {

    @BindView(R.id.iv_splash_back)
    ImageView ivSplashBack;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.tag_flow_layout)
    TagFlowLayout mTagFlowLayout;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, PublicNumberActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private LocalSoundPresenter mSoundPresenter;
    private SerialPresenter mSerialPresenter;

    private SiteDBManager mSiteDBManager;

    private List<SiteBean> siteBeanList = new ArrayList<>();

    private HotAdapter mSiteAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_public_number;
    }

    @Override
    protected void initView() {
        super.initView();

//        loadImage();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                }, 500);
            }
        });

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();
        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();


        mSiteAdapter = new HotAdapter<>(this, siteBeanList);
        mTagFlowLayout.setAdapter(mSiteAdapter);

        mTagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                refSite(siteBeanList.get(position), position);
                return false;
            }
        });

        mSiteDBManager = new SiteDBManager();
    }

    @Override
    protected void initData() {

        mSwipeRefreshLayout.setRefreshing(false);
        siteBeanList = mSiteDBManager.loadAll();
        if (siteBeanList != null && siteBeanList.size() > 0) {
            isNuEmpty();
            mSiteAdapter = new HotAdapter<>(this, siteBeanList);
            mTagFlowLayout.setAdapter(mSiteAdapter);
        } else {
            isEmpty();
        }

    }

    private void loadImage() {
        ImageLoader.loadImage(this, ivSplashBack, R.mipmap.splash_bg, R.mipmap.splash_bg);

        ImageLoader.loadImage(mContext, ivCode, R.mipmap.ic_code, 300, 300, null);
    }

    private void refSite(SiteBean itemData, int position) {
        IntentUtil.openUrl(mContext, itemData.getUrl());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPresenter.finish();
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
        List<SiteBean> siteBeans = mSiteDBManager.queryLikeSiteByName(result);
        if (siteBeans != null && siteBeans.size() > 0) {
            SiteBean itemData = null;
            if (siteBeans.size() == 1) {
                itemData = siteBeans.get(siteBeans.size() - 1);
            } else {
                itemData = siteBeans.get(new Random().nextInt(siteBeans.size()));
            }
            int index = siteBeanList.indexOf(itemData);
            refSite(itemData, index);
        } else {
            addSpeakAnswer(AppUtil.resFoFinal(R.array.no_result));
        }
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void refLocalPage(String key1, String key2, String key3, String key4) {
        refLocalPage(key1);
    }

    @Override
    public void stopAll() {
        super.stopAll();
        mSoundPresenter.stopEvery();
        addSpeakAnswer("识别图中二维码");
//        mSoundPresenter.doAnswer(resFoFinal(R.array.wake_up));
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
