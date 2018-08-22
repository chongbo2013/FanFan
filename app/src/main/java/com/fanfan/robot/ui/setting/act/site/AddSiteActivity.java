package com.fanfan.robot.ui.setting.act.site;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.fanfan.novel.utils.grammer.GrammerUtils;
import com.fanfan.novel.utils.grammer.LoadDataUtils;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.db.manager.NavigationDBManager;
import com.fanfan.robot.db.manager.SiteDBManager;
import com.fanfan.robot.db.manager.VideoDBManager;
import com.fanfan.robot.db.manager.VoiceDBManager;
import com.fanfan.robot.model.NavigationBean;
import com.fanfan.robot.model.SiteBean;
import com.fanfan.robot.model.VideoBean;
import com.fanfan.robot.model.VoiceBean;
import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.app.NovelApp;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android on 2018/2/23.
 */

public class AddSiteActivity extends BarBaseActivity {

    public static final String SITE_ID = "siteId";
    public static final String RESULT_CODE = "site_title_result";

    @BindView(R.id.et_site_name)
    EditText etSiteName;
    @BindView(R.id.et_site_url)
    EditText etSiteUrl;

    public static final int ADD_SITE_REQUESTCODE = 223;

    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, AddSiteActivity.class);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void newInstance(Activity context, long id, int requestCode) {
        Intent intent = new Intent(context, AddSiteActivity.class);
        intent.putExtra(SITE_ID, id);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private long saveLocalId;

    private SiteDBManager mSiteDBManager;

    private SiteBean siteBean;

    private SpeechRecognizer recognizer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_site;
    }

    @Override
    protected void initData() {
        saveLocalId = getIntent().getLongExtra(SITE_ID, -1);

        mSiteDBManager = new SiteDBManager();

        if (saveLocalId != -1) {
            siteBean = mSiteDBManager.selectByPrimaryKey(saveLocalId);

            etSiteName.setText(siteBean.getName());
            etSiteUrl.setText(siteBean.getUrl());
        } else {
            siteBean = new SiteBean();
        }

        recognizer = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code == ErrorCode.SUCCESS) {
                    Print.e("初始化成功，错误码：" + code);
                }
                Print.e("初始化失败，错误码：" + code);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (recognizer != null) {
            recognizer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.finish_black, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish:

                if (siteBean == null) {
                    showToast("error！");
                    break;
                }
                if (AppUtil.isEmpty(etSiteName)) {
                    showToast("名称不能为空！");
                    break;
                }
                if (AppUtil.isEmpty(etSiteUrl)) {
                    showToast("链接不能为空！");
                    break;
                }
                if (etSiteName.getText().toString().trim().length() > 20) {
                    showToast("输入 20 字以内");
                    break;
                }
                if (!AppUtil.matcherUrl(AppUtil.getText(etSiteUrl))) {
                    showToast("输入的网址不合法");
                    break;
                }
                if (saveLocalId == -1) {
                    List<SiteBean> been = mSiteDBManager.querySiteByName(etSiteName.getText().toString().trim());
                    if (!been.isEmpty()) {
                        showToast("请不要添加相同的链接！");
                        break;
                    }
                }
                videoIsexit();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void videoIsexit() {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {

                insertSiteBean();

                String lexiconContents = LoadDataUtils.getLexiconContents();

                e.onNext(lexiconContents);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        recognizer = GrammerUtils.getRecognizer(AddSiteActivity.this, recognizer);

                        Print.e(s);
                        recognizer.buildGrammar(GrammerUtils.GRAMMAR_BNF, s, new GrammarListener() {
                            @Override
                            public void onBuildFinish(String s, SpeechError speechError) {
                                if (speechError == null) {
                                    Intent intent = new Intent();
                                    intent.putExtra(RESULT_CODE, saveLocalId);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    showToast(speechError.getErrorDescription());
                                }
                            }
                        });
                    }
                });
    }

    private void insertSiteBean() {
        String name = AppUtil.getText(etSiteName);
        String url = AppUtil.getText(etSiteUrl);

        siteBean.setName(name);
        siteBean.setUrl(url);
        siteBean.setSaveTime(System.currentTimeMillis());
        if (saveLocalId == -1) {
            saveLocalId = mSiteDBManager.insertForId(siteBean);
        } else {
            siteBean.setId(saveLocalId);
            mSiteDBManager.update(siteBean);
        }
    }


}
