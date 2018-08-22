package com.fanfan.robot.ui.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.db.manager.VideoDBManager;
import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.model.VideoBean;
import com.fanfan.robot.presenter.LocalSoundPresenter;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ILocalSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.robot.other.udp.SocketManager;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.recycler.video.VideoAdapter;
import com.fanfan.robot.adapter.recycler.video.VideoVerticalAdapter;
import com.fanfan.robot.view.manager.carouse.CarouselLayoutManager;
import com.fanfan.robot.view.manager.carouse.CarouselZoomPostLayoutListener;
import com.fanfan.robot.view.manager.carouse.CenterScrollListener;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/6.
 */

public class VideoIntroductionActivity extends BarBaseActivity implements
        ILocalSoundPresenter.ILocalSoundView,
        ISerialPresenter.ISerialView {

    @BindView(R.id.iv_list_hd)
    ImageView ivListHd;
    @BindView(R.id.recycler_video)
    RecyclerView recyclerVideo;
    @BindView(R.id.list_vertical)
    RecyclerView listVertical;
    @BindView(R.id.iv_upward)
    ImageView ivUpward;
    @BindView(R.id.iv_down)
    ImageView ivDown;
    @BindView(R.id.rl_vertial)
    RelativeLayout rlVertial;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, VideoIntroductionActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private VideoDBManager mVideoDBManager;

    private List<VideoBean> videoBeanList = new ArrayList<>();

    private VideoAdapter videoAdapter;
    private VideoVerticalAdapter videoVerticalAdapter;

    private boolean isOpen;

    private CarouselLayoutManager layoutManager;
    private int mCurPosition;

    private LocalSoundPresenter mSoundPresenter;
    private SerialPresenter mSerialPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_introduction;
    }

    @Override
    protected void initView() {
        super.initView();

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();
        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();

        isOpen = true;

        initSimpleAdapter();
        initRecyclerView();
    }


    @Override
    protected void initData() {
        mVideoDBManager = new VideoDBManager();

        videoBeanList = mVideoDBManager.loadAll();
        if (videoBeanList != null && videoBeanList.size() > 0) {
            isNuEmpty();
            videoAdapter.replaceData(videoBeanList);
        } else {
            isEmpty();
        }
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

        addSpeakAnswer("你好，这里是视频介绍页面，点击上下方列表或说出视屏名称可播放视频");
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

    @OnClick({R.id.iv_list_hd, R.id.iv_upward, R.id.iv_down})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_list_hd:
                if (isOpen) {
                    isOpen = false;
                    ViewAnimator
                            .animate(recyclerVideo)
                            .alpha(1, 0)
                            .scale(1f, 0.3f)
                            .duration(500)
                            .onStop(new AnimationListener.Stop() {
                                @Override
                                public void onStop() {
                                    recyclerVideo.setVisibility(View.GONE);
                                }
                            })
                            .start();
                } else {
                    isOpen = true;
                    ViewAnimator
                            .animate(recyclerVideo)
                            .alpha(0, 1)
                            .scale(0.3f, 1f)
                            .duration(500)
                            .onStart(new AnimationListener.Start() {
                                @Override
                                public void onStart() {
                                    recyclerVideo.setVisibility(View.VISIBLE);
                                }
                            })
                            .start();
                }
                break;
            case R.id.iv_upward:
                upward();
                break;
            case R.id.iv_down:
                down();
                break;
        }
    }

    private void upward() {
        if (mCurPosition == 0) {
            addSpeakAnswer("已是第一个");
        } else {
            mCurPosition--;
            layoutManager.scrollToPosition(mCurPosition);
        }
    }

    private void down() {
        if (mCurPosition == videoBeanList.size() - 1) {
            addSpeakAnswer("已是最后一个");
        } else {
            mCurPosition++;
            layoutManager.scrollToPosition(mCurPosition);
        }
    }

    private void addSpeakAnswer(String messageContent) {
        mSoundPresenter.doAnswer(messageContent);
    }

    private void addSpeakAnswer(int res) {
        mSoundPresenter.doAnswer(getResources().getString(res));
    }

    private void initSimpleAdapter() {
        videoAdapter = new VideoAdapter(videoBeanList);
        videoAdapter.openLoadAnimation();
        videoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                refVideo(videoBeanList.get(position));
            }
        });
        recyclerVideo.setAdapter(videoAdapter);
        recyclerVideo.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerVideo.setItemAnimator(new DefaultItemAnimator());
        recyclerVideo.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

    private void initRecyclerView() {
        videoVerticalAdapter = new VideoVerticalAdapter(mContext, videoBeanList);

        layoutManager = new CarouselLayoutManager(CarouselLayoutManager.VERTICAL, false);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        layoutManager.setMaxVisibleItems(3);

        listVertical.setAdapter(videoVerticalAdapter);
        listVertical.setLayoutManager(layoutManager);
        listVertical.setHasFixedSize(true);
        listVertical.addOnScrollListener(new CenterScrollListener());

        videoVerticalAdapter.setOnItemClickListener(new VideoVerticalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Print.e("onItemClick : " + position);
                refVideo(videoBeanList.get(position));
            }
        });

        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {

            @Override
            public void onCenterItemChanged(final int adapterPosition) {
                Print.e("onCenterItemChanged : " + adapterPosition);
                mCurPosition = adapterPosition;
            }
        });
    }

    private void refVideo(VideoBean itemData) {
        VideoBean videoBean = itemData;
        if (videoBean.getVideoUrl() != null) {
            VideoDetailActivity.newInstance(this, videoBean.getVideoUrl());
            return;
        } else {
            addSpeakAnswer(R.string.speakText);
        }
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
        switch (type) {
            case Next:
                upward();
                break;
            case Lase:
                down();
                break;
        }
    }

    @Override
    public void refLocalPage(String result) {
        List<VideoBean> videoBeans = mVideoDBManager.queryLikeVideoByQuestion(result);
        if (videoBeans != null && videoBeans.size() > 0) {
            if (videoBeans.size() == 1) {
                refVideo(videoBeans.get(videoBeans.size() - 1));
            } else {
                VideoBean itemData = videoBeans.get(new Random().nextInt(videoBeans.size()));
                refVideo(itemData);
            }
        } else {
            if (new Random().nextBoolean()) {
                addSpeakAnswer(AppUtil.resFoFinal(R.array.no_result));
            } else {
                addSpeakAnswer(AppUtil.resFoFinal(R.array.no_voice));
            }
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
        addSpeakAnswer("你好，这里是视频介绍页面，点击上下方列表或说出视屏名称可播放视频");
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