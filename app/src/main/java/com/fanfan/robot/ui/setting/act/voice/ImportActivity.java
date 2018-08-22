package com.fanfan.robot.ui.setting.act.voice;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.novel.utils.grammer.GrammerUtils;
import com.fanfan.novel.utils.grammer.LoadDataUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.recycler.other.ImportAdapter;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.db.manager.NavigationDBManager;
import com.fanfan.robot.db.manager.SiteDBManager;
import com.fanfan.robot.db.manager.VideoDBManager;
import com.fanfan.robot.db.manager.VoiceDBManager;
import com.fanfan.robot.model.Channel;
import com.fanfan.robot.model.NavigationBean;
import com.fanfan.robot.model.SiteBean;
import com.fanfan.robot.model.VideoBean;
import com.fanfan.robot.model.VoiceBean;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.seabreeze.log.Print;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function4;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android on 2018/1/19.
 */

public class ImportActivity extends BarBaseActivity {


    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, ImportActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_import)
    Button btnImport;

    private List<VoiceBean> mVoiceBeans;
    private List<VideoBean> mVideoBeans;
    private List<SiteBean> mSiteBeans;
    private List<NavigationBean> mNavigationBeans;

    private VideoDBManager mVideoDBManager;
    private VoiceDBManager mVoiceDBManager;
    private NavigationDBManager mNavigationDBManager;
    private SiteDBManager mSiteDBManager;

    private ImportAdapter mAdapter;

    private SpeechRecognizer recognizer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import;
    }

    @Override
    protected void initData() {

        showProgressDialog();

        Observable<List<VoiceBean>> voice = loadVoice();

        Observable<List<VideoBean>> video = loadVideo();

        Observable<List<SiteBean>> site = loadSite();

        Observable<List<NavigationBean>> navigation = loadNavigation();

        mVideoDBManager = new VideoDBManager();
        mVoiceDBManager = new VoiceDBManager();
        mSiteDBManager = new SiteDBManager();
        mNavigationDBManager = new NavigationDBManager();

        setAdapter(voice, video, site, navigation);

        recognizer = SpeechRecognizer.createRecognizer(mContext, null);
    }

    @Override
    protected void onDestroy() {
        if (recognizer != null) {
            recognizer.cancel();
        }
        super.onDestroy();
    }

    private Observable<List<VoiceBean>> loadVoice() {

        return Observable.create(new ObservableOnSubscribe<List<VoiceBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<VoiceBean>> e) throws Exception {

                List<VoiceBean> voiceBeanList = LoadDataUtils.loadVoiceBean();

                e.onNext(voiceBeanList);

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    private Observable<List<VideoBean>> loadVideo() {

        return Observable.create(new ObservableOnSubscribe<List<VideoBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<VideoBean>> e) throws Exception {

                List<VideoBean> videoBeanList = LoadDataUtils.loadVideoBean();

                e.onNext(videoBeanList);
            }
        });
    }


    private Observable<List<SiteBean>> loadSite() {

        return Observable.create(new ObservableOnSubscribe<List<SiteBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SiteBean>> e) throws Exception {

                List<SiteBean> siteBeanList = LoadDataUtils.loadSite();

                e.onNext(siteBeanList);
            }
        });
    }


    private Observable<List<NavigationBean>> loadNavigation() {

        return Observable.create(new ObservableOnSubscribe<List<NavigationBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<NavigationBean>> e) throws Exception {

                List<NavigationBean> navigationBeans = LoadDataUtils.loadNavigation();

                e.onNext(navigationBeans);
            }
        });
    }


    private void setAdapter(Observable<List<VoiceBean>> voice, Observable<List<VideoBean>> video,
                            Observable<List<SiteBean>> site, Observable<List<NavigationBean>> navigation) {

        Observable.zip(voice, video, site, navigation,
                new Function4<List<VoiceBean>, List<VideoBean>, List<SiteBean>, List<NavigationBean>, List<Channel>>() {
                    @Override
                    public List<Channel> apply(List<VoiceBean> voiceBeans, List<VideoBean> videoBeans, List<SiteBean> siteBeans,
                                               List<NavigationBean> navigationBeans) throws Exception {

                        List<Channel> channels = new ArrayList<>();

                        //转换本地语音
                        transformationVoice(voiceBeans, channels);

                        //转换本地视频
                        transformationVideo(videoBeans, channels);

                        //转换本地导航
                        transformationNavigation(navigationBeans, channels);

                        //转换本地网址
                        transformationSite(siteBeans, channels);

                        mVoiceBeans = voiceBeans;
                        mVideoBeans = videoBeans;
                        mNavigationBeans = navigationBeans;
                        mSiteBeans = siteBeans;

                        return channels;
                    }
                })
                .subscribe(new Consumer<List<Channel>>() {
                    @Override
                    public void accept(List<Channel> channels) throws Exception {

                        dismissProgressDialog();

                        mAdapter = new ImportAdapter(channels);
                        GridLayoutManager manager = new GridLayoutManager(ImportActivity.this, 3);
                        mRecyclerView.setLayoutManager(manager);
                        mRecyclerView.setAdapter(mAdapter);
                        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                int itemViewType = mAdapter.getItemViewType(position);
                                return itemViewType == Channel.TYPE_CONTENT ? 1 : 3;
                            }
                        });
                    }
                });
    }

    private void transformationVoice(List<VoiceBean> voiceBeans, List<Channel> channels) {
        Channel voiceChannel = new Channel();
        voiceChannel.setItemtype(Channel.TYPE_TITLE);
        voiceChannel.setChannelName("本地语音");
        channels.add(voiceChannel);

        if (voiceBeans != null && voiceBeans.size() > 0) {
            for (VoiceBean bean : voiceBeans) {
                Channel channelList = new Channel();
                channelList.setItemtype(Channel.TYPE_CONTENT);
                channelList.setChannelName(bean.getShowTitle());
                channels.add(channelList);
            }
        } else {
            Channel channelList = new Channel();
            channelList.setItemtype(Channel.TYPE_CONTENT);
            channelList.setChannelName("请手动添加");
            channels.add(channelList);
        }
    }

    private void transformationVideo(List<VideoBean> videoBeans, List<Channel> channels) {
        Channel videoChannel = new Channel();
        videoChannel.setItemtype(Channel.TYPE_TITLE);
        videoChannel.setChannelName("本地视频");
        channels.add(videoChannel);

        if (videoBeans.size() > 0) {
            for (VideoBean bean : videoBeans) {
                Channel channelList = new Channel();
                channelList.setItemtype(Channel.TYPE_CONTENT);
                channelList.setChannelName(bean.getShowTitle());
                channels.add(channelList);
            }
        } else {
            Channel channelList = new Channel();
            channelList.setItemtype(Channel.TYPE_CONTENT);
            channelList.setChannelName("请手动添加");
            channels.add(channelList);
        }
    }

    private void transformationNavigation(List<NavigationBean> navigationBeans, List<Channel> channels) {
        Channel navigationChannel = new Channel();
        navigationChannel.setItemtype(Channel.TYPE_TITLE);
        navigationChannel.setChannelName("导航");
        channels.add(navigationChannel);

        if (navigationBeans.size() > 0) {
            for (NavigationBean bean : navigationBeans) {
                Channel channelList = new Channel();
                channelList.setItemtype(Channel.TYPE_CONTENT);
                channelList.setChannelName(bean.getTitle());
                channels.add(channelList);
            }
        } else {
            Channel channelList = new Channel();
            channelList.setItemtype(Channel.TYPE_CONTENT);
            channelList.setChannelName("请手动添加");
            channels.add(channelList);
        }
    }

    private void transformationSite(List<SiteBean> siteBeans, List<Channel> channels) {
        Channel siteChannel = new Channel();
        siteChannel.setItemtype(Channel.TYPE_TITLE);
        siteChannel.setChannelName("网址");
        channels.add(siteChannel);

        if (siteBeans.size() > 0) {
            for (SiteBean bean : siteBeans) {
                Channel channelList = new Channel();
                channelList.setItemtype(Channel.TYPE_CONTENT);
                channelList.setChannelName(bean.getName());
                channels.add(channelList);
            }
        } else {
            Channel channelList = new Channel();
            channelList.setItemtype(Channel.TYPE_CONTENT);
            channelList.setChannelName("请手动添加");
            channels.add(channelList);
        }
    }

    @OnClick({R.id.tv_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_import:

                showProgressDialog();

                mVideoDBManager.deleteAll();
                mVoiceDBManager.deleteAll();
                mSiteDBManager.deleteAll();
                mNavigationDBManager.deleteAll();

                mVoiceDBManager.insertList(mVoiceBeans);
                mVideoDBManager.insertList(mVideoBeans);
                mSiteDBManager.insertList(mSiteBeans);
                mNavigationDBManager.insertList(mNavigationBeans);

                Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {

                        String lexiconContents = LoadDataUtils.getLexiconContents();

                        e.onNext(lexiconContents);
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {

                                recognizer = GrammerUtils.getRecognizer(ImportActivity.this, recognizer);

                                Print.e(s);
                                recognizer.buildGrammar(GrammerUtils.GRAMMAR_BNF, s, new GrammarListener() {
                                    @Override
                                    public void onBuildFinish(String s, SpeechError speechError) {
                                        if (speechError == null) {
                                            showToast("一键导入完成");
                                            dismissProgressDialog();
                                            finish();
                                        } else {
                                            showToast(speechError.getErrorDescription());
                                        }
                                    }
                                });
                            }
                        });
                break;
        }
    }

    private MaterialDialog materialDialog;

    private void showProgressDialog() {
        materialDialog = new MaterialDialog.Builder(this)
                .title("等待")
                .content("语音导入需要一段时间，请耐心等待")
                .progress(true, 0)
                .show();
    }

    private void dismissProgressDialog() {
        if (materialDialog != null && materialDialog.isShowing()) {
            materialDialog.dismiss();
            materialDialog = null;
        }
    }

}
