package com.fanfan.robot.ui.media.act;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.widget.ImageView;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.music.DanceUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.db.manager.DanceDBManager;
import com.fanfan.robot.model.Dance;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

public class DanceActivity extends BarBaseActivity implements ISerialPresenter.ISerialView {

    public static final String STOP_DANCE = "A50C80E2AA";

    @BindView(R.id.iv_splash_back)
    ImageView ivSplashBack;

    public static final String DANCE_ID = "DANCE_ID";

    public static void newInstance(Activity context, long danceId) {
        Intent intent = new Intent(context, DanceActivity.class);
        intent.putExtra(DANCE_ID, danceId);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private Dance dance;

    private NoneReceiver noneReceiver = new NoneReceiver();

    public static final String ACTION_NONE_CLOSE = "action_none_close";

    private SerialPresenter mSerialPresenter;

    @Override
    protected boolean whetherNotReturn() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_none;
    }

    @Override
    protected void initData() {
        mSerialPresenter = new SerialPresenter(this);

        long danceId = getIntent().getLongExtra(DANCE_ID, -1);
        if (danceId != -1) {
            DanceDBManager danceDBManager = new DanceDBManager();
            dance = danceDBManager.selectByPrimaryKey(danceId);
            DanceUtils.getInstance().startDanceName(this, dance.getPath(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopAll();
                }
            });
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, dance.getOrderData());
        }

        ImageLoader.loadImage(this, ivSplashBack, R.mipmap.splash_bg, false, 2000);
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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NONE_CLOSE);
        registerReceiver(noneReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(noneReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.iv_splash_back)
    public void onViewClicked() {
        stopAll();
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
    public void stopAll() {
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, STOP_DANCE);
        DanceUtils.getInstance().stopPlay();
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

    public class NoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_NONE_CLOSE)) {
                stopAll();
            }
        }
    }
}
