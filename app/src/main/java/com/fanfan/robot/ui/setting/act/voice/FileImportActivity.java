package com.fanfan.robot.ui.setting.act.voice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.novel.utils.grammer.GrammerUtils;
import com.fanfan.novel.utils.grammer.LoadDataUtils;
import com.fanfan.novel.utils.system.FileUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.db.manager.VoiceDBManager;
import com.fanfan.robot.model.VoiceBean;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.seabreeze.log.Print;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FileImportActivity extends BarBaseActivity {

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, FileImportActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_sys_excel)
    TextView tvSysExcel;
    @BindView(R.id.tv_choose)
    TextView tvChoose;
    @BindView(R.id.tv_import)
    TextView tvImport;

    private VoiceDBManager mVoiceDBManager;
    private String workPath;

    private SpeechRecognizer recognizer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_file_import;
    }

    @Override
    protected void initData() {

        mVoiceDBManager = new VoiceDBManager();

        recognizer = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code == ErrorCode.SUCCESS) {
                    recognizer = GrammerUtils.getRecognizer(FileImportActivity.this, recognizer);
                }
                Print.e("初始化失败，错误码：" + code);
            }
        });
    }


    @OnClick({R.id.tv_import, R.id.tv_choose})
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tv_choose:
                String[] items = getXlsFileName();
                if (items != null && items.length > 0) {
                    new MaterialDialog.Builder(this)
                            .title("选择需要导入的文件")
                            .items(items)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                    workPath = FileUtil.getPublicDownloadDir() + File.separator + text;
                                    tvSysExcel.setVisibility(View.VISIBLE);
                                    tvSysExcel.setText(workPath);
                                }
                            })
                            .show();
                } else {
                    showToast("没有excel批量导入表");
                }
                break;
            case R.id.tv_import:
                if (workPath == null || workPath.equals("")) {
                    showToast("请先选择excel批量导入表");
                } else {
                    addExcel();
                }
                break;
        }
    }

    private String[] getXlsFileName() {

        List<String> fileList = new ArrayList<>();

        File fileDownload = new File(FileUtil.getPublicDownloadDir());

        File[] listFiles = fileDownload.listFiles();

        for (int i = 0; i < listFiles.length; i++) {
            File file = listFiles[i];
            if (file.isFile() && file.getName().endsWith(".xls")) {
                fileList.add(file.getName());
            }
        }

        return fileList.toArray(new String[fileList.size()]);
    }

    @SuppressLint("CheckResult")
    private void addExcel() {

        tvChoose.setEnabled(false);
        tvImport.setEnabled(false);

        showProgressDialog();

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {

                List<VoiceBean> voiceBeanList = LoadDataUtils.loadExcelVoiceBean(workPath);
                if (voiceBeanList != null) {

                    mVoiceDBManager.deleteAll();
                    //插入数据库中
                    if (mVoiceDBManager.insertList(voiceBeanList)) {
                        String lexiconContents = LoadDataUtils.getLexiconContents();
                        e.onNext(lexiconContents);
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        tvChoose.setEnabled(true);
                        tvImport.setEnabled(true);

                        dismissProgressDialog();

                        Print.e(s);
                        recognizer.buildGrammar(GrammerUtils.GRAMMAR_BNF, s, new GrammarListener() {
                            @Override
                            public void onBuildFinish(String s, SpeechError speechError) {
                                if (speechError == null) {
                                    showToast("更新成功");
                                    finish();
                                } else {
                                    showToast(speechError.getErrorDescription());
                                }
                            }
                        });
                    }
                });
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
