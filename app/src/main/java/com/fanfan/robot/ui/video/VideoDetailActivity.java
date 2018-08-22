package com.fanfan.robot.ui.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.robot.other.udp.SocketManager;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.novel.jcvideoplayer.JCVideoPlayerStandard;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.DatagramPacket;

import butterknife.BindView;

/**
 * Created by android on 2017/12/21.
 */

public class VideoDetailActivity extends BarBaseActivity implements ISerialPresenter.ISerialView{

    public static final String VIDEO_URL = "VideoUrl";

    @BindView(R.id.jc_video)
    JCVideoPlayerStandard mJcVideo;

    public static void newInstance(Activity context, String url) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(VIDEO_URL, url);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private SerialPresenter mSerialPresenter;

    private String upfile;

    @Override
    protected boolean whetherNotReturn() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();
    }


    @Override
    protected void initData() {
        upfile = getIntent().getStringExtra(VIDEO_URL);
        mJcVideo.setUp(upfile, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        mJcVideo.startVideo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mJcVideo.releaseAllVideos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
    public void stopAll() {
        finish();
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
