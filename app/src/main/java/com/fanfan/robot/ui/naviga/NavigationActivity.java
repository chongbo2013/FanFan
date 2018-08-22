package com.fanfan.robot.ui.naviga;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.db.manager.NavigationDBManager;
import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.NavigationBean;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.presenter.ChatPresenter;
import com.fanfan.robot.presenter.LocalSoundPresenter;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.IChatPresenter;
import com.fanfan.robot.presenter.ipersenter.ILocalSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.robot.other.udp.SocketManager;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.recycler.naviga.NavigationAdapter;
import com.fanfan.robot.model.ImageBean;
import com.fanfan.robot.ui.voice.ImageFragment;
import com.fanfan.robot.view.RangeClickImageView;
import com.seabreeze.log.Print;
import com.tencent.TIMMessage;

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

public class NavigationActivity extends BarBaseActivity implements
        ILocalSoundPresenter.ILocalSoundView,
        ISerialPresenter.ISerialView,
        IChatPresenter.IChatView,
        RangeClickImageView.OnResourceReadyListener,
        RangeClickImageView.OnRangeClickListener {

    public static final String NAVIGATION_ADDRESS = "navigation_address";

    public static final String START = "start";
    public static final String END = "end";

    //    @BindView(R.id.iv_navigation)
    RangeClickImageView ivNavigation;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.iv_navigation_image)
    ImageView ivNavigationImage;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, NavigationActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void newInstance(Activity context, String address) {
        Intent intent = new Intent(context, NavigationActivity.class);
        intent.putExtra(NAVIGATION_ADDRESS, address);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private LocalSoundPresenter mSoundPresenter;
    private SerialPresenter mSerialPresenter;
    private ChatPresenter mChatPresenter;

    private NavigationDBManager mNavigationDBManager;

    private String fileName = "image_navigation.png";

    private NavigationBean mNavigationBean;

    private List<NavigationBean> navigationBeanList = new ArrayList<>();

    private NavigationAdapter navigationAdapter;

    private int mCurrentPos;

    private ImageFragment imageFragment;
    private boolean isShow;

    private String mAddress;

    private boolean isSpeakMove;

    @Override
    protected boolean whetherNotReturn() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_navigation;
    }

    @Override
    protected void initView() {
        super.initView();

        mAddress = getIntent().getStringExtra(NAVIGATION_ADDRESS);

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();
        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();
        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.start();


//        initImage();
        initSimpleAdapter();
    }

    @Override
    protected void initData() {
        mNavigationDBManager = new NavigationDBManager();

        navigationBeanList = mNavigationDBManager.loadAll();
        if (navigationBeanList != null && navigationBeanList.size() > 0) {
            isNuEmpty();
            navigationAdapter.replaceData(navigationBeanList);
            mCurrentPos = 0;
            navigationAdapter.notifyClick(mCurrentPos);
            ImageLoader.loadLargeImage(mContext, ivNavigationImage, navigationBeanList.get(0).getImgUrl(), R.mipmap.video_image);
        } else {
            mCurrentPos = -1;
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

        if (mAddress != null) {
            refLocalPage(mAddress);
            mAddress = null;
        } else {
            addSpeakAnswer("你好，这里是导航页面，点击地图上地点可到达指定区域");
        }
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
        mHandler.removeCallbacks(moveSpeakrunnable);

        super.onDestroy();
        mSoundPresenter.finish();
    }

    @OnClick({R.id.iv_navigation_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_navigation_image:
                if (mCurrentPos > -1) {
                    NavigationBean bean = navigationBeanList.get(mCurrentPos);
                    showImage(bean);
                }
                break;
        }
    }

    private void showImage(NavigationBean bean) {
        isShow(true);
        ImageBean imageBean = new ImageBean();
        imageBean.setTop(bean.getTitle());
        imageBean.setBottom(bean.getDatail());
        imageBean.setImgUrl(bean.getImgUrl());
        imageFragment = ImageFragment.newInstance(imageBean);
        imageFragment.show(getSupportFragmentManager(), "IMAGE");
    }

    private void dismissImage() {
        if (imageFragment != null) {
            imageFragment.dismiss();
            imageFragment = null;
        }
    }

    public void isShow(boolean isShow) {
        this.isShow = isShow;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ServiceToActivityEvent event) {
        if (event.isOk()) {
            showToast("导航有消息");
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.home_black, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.home:
//                new MaterialDialog.Builder(this)
//                        .title("选择导航图")
//                        .content("目前只支持此张地图")
//                        .items(Constants.NAVIGATIONS)
//                        .itemsCallback(new MaterialDialog.ListCallback() {
//                            @Override
//                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
//                                fileName = text + ".png";
//                                initImage();
//                            }
//                        })
//                        .show();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void addSpeakAnswer(String messageContent) {

        postInteraction();

        mSoundPresenter.doAnswer(messageContent);
    }

    private void addSpeakAnswer(int res) {

        postInteraction();

        mSoundPresenter.doAnswer(getResources().getString(res));
    }

    private void initImage() {

        ivNavigation.setFileName(fileName, (int) (Constants.displayWidth), (int) (Constants.displayHeight * 0.7));
        ivNavigation.setOnResourceReadyListener(this);
        ivNavigation.setOnRangeClickListener(this);
    }

    private void initSimpleAdapter() {
        navigationAdapter = new NavigationAdapter(navigationBeanList);
        navigationAdapter.openLoadAnimation();
        navigationAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                refNavigation(navigationBeanList.get(position), position);
            }
        });

        recyclerView.setAdapter(navigationAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void refNavigation(NavigationBean itemData, int position) {

        isSpeakMove = false;
        mCurrentPos = position;
        navigationAdapter.notifyClick(mCurrentPos);
        mNavigationBean = itemData;
        addSpeakAnswer(itemData.getGuide());
        if (itemData.getNavigationData() != null) {
            mSerialPresenter.receiveMotion(SerialService.CRUISE_BAUDRATE, itemData.getNavigationData());
        }
        ImageLoader.loadLargeImage(mContext, ivNavigationImage, itemData.getImgUrl(), R.mipmap.video_image);
        if (mCurrentPos > -1) {
            NavigationBean bean = navigationBeanList.get(mCurrentPos);
            showImage(bean);
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
        if (isShow) {
            dismissImage();
            mSoundPresenter.onCompleted();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (isShow) {
            dismissImage();
            mSoundPresenter.stopEvery();
        } else {
            finish();
        }
        super.onBackPressed();
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
        List<NavigationBean> navigationBeans = mNavigationDBManager.queryNavigationByQuestion(result);
        if (navigationBeans != null && navigationBeans.size() > 0) {
            NavigationBean itemData = null;
            if (navigationBeans.size() == 1) {
                itemData = navigationBeans.get(navigationBeans.size() - 1);
            } else {
                itemData = navigationBeans.get(new Random().nextInt(navigationBeans.size()));
            }
            int index = navigationBeanList.indexOf(itemData);

            if (index != -1) {
                refNavigation(itemData, index);
            } else {
                showMsg("数据有误");
            }
        } else {
            addSpeakAnswer("点击地图上地点，我也可以带你去");
        }
    }

    @Override
    public void onCompleted() {
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
    }

    @Override
    public void refLocalPage(String key1, String key2, String key3, String key4) {
        refLocalPage(key1);
    }

    @Override
    public void stopAll() {
        super.stopAll();
        mSoundPresenter.stopEvery();
//        addSpeakAnswer("你好，这里是导航页面，点击地图上地点可到达指定区域");
        addSpeakAnswer("您好");
    }

    @Override
    public void onMoveStop() {
        mHandler.removeCallbacks(moveSpeakrunnable);

        mSerialPresenter.receiveMotion(SerialService.CRUISE_BAUDRATE, END);

        if (mNavigationBean != null) {
            dismissImage();
            Print.e(mNavigationBean.getDatail());
            addSpeakAnswer(mNavigationBean.getDatail());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.SPEAK_ACTION);
                }
            }, 500);

        }
    }

    @Override
    public void onMoveSpeak() {
        if (!mSoundPresenter.isSpeaking()) {
            if (!isSpeakMove) {
                Print.e("navi" + "发送让一让指令");
                isSpeakMove = true;
                mHandler.removeCallbacks(moveSpeakrunnable);
                mHandler.postDelayed(moveSpeakrunnable, 3000);
            } else {
                Print.e("navi" + "已经发送了让一让指令");
            }
        } else {
            Print.e("navi" + "有人在说话");
        }
    }

    Runnable moveSpeakrunnable = new Runnable() {
        @Override
        public void run() {
            Print.e("navi" + "navi" + "说出让一让");
            addSpeakAnswer(resFoFinal(R.array.navigation_move));
            isSpeakMove = false;
        }
    };


    @Override
    public void onAlarm(Alarm alarm) {

    }

    @Override
    public void onClickImage(View view, Object tag, int x, int y) {
        refLocalPage((String) tag);
    }

    @Override
    public boolean onResourceReady(int realWidth, int realHeight, int ivWidth, int ivHeight) {
        List<NavigationBean> beanAll = mNavigationDBManager.loadAll();
        if (beanAll != null) {
            for (int i = 0; i < beanAll.size(); i++) {
                NavigationBean bean = beanAll.get(i);
                if (!bean.getImgUrl().endsWith(fileName)) {
                    beanAll.remove(i);
                    i--;
                }
            }
            for (NavigationBean bean : beanAll) {
                Print.e("bean" + bean.getPosX());
                Print.e("bean" + bean.getPosY());
                ivNavigation.setClickRange(bean.getTitle(), bean.getPosX(), bean.getPosY());
            }
        }
        return false;
    }

    @Override
    public void onSendMessageSuccess(TIMMessage message) {

    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {

    }

    @Override
    public void parseMsgcomplete(String str) {

    }

    @Override
    public void parseCustomMsgcomplete(String customMsg) {

    }

    @Override
    public void parseServerMsgcomplete(String txt) {
        txt = txt.trim();
        if (txt.equals("b")) {
            if (isShow) {
                dismissImage();
                mSoundPresenter.onCompleted();
            } else {
                finish();
            }

        }
    }

}
