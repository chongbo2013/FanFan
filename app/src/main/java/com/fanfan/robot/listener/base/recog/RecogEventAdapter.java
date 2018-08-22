package com.fanfan.robot.listener.base.recog;

import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;

import com.fanfan.novel.utils.grammer.GrammerUtils;
import com.fanfan.novel.utils.youdao.TranslateLanguage;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.model.local.Asr;
import com.fanfan.robot.model.local.Cw;
import com.fanfan.robot.model.local.Trans;
import com.fanfan.robot.model.local.Ws;
import com.fanfan.youtu.utils.GsonUtil;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.seabreeze.log.Print;
import com.youdao.sdk.ydonlinetranslate.TranslateErrorCode;
import com.youdao.sdk.ydonlinetranslate.TranslateListener;
import com.youdao.sdk.ydtranslate.Translate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecogEventAdapter implements RecognizerListener, NulState {

    private IRecogListener listener;

    private ArrayMap<Integer, String> mIatResults;

    private TranslateLanguage translateLanguage;

    private StringBuilder sbL1;
    private StringBuilder sbL2;
    private StringBuilder sbL3;
    private StringBuilder sbL4;

    public RecogEventAdapter(IRecogListener listener) {
        this.listener = listener;
        mIatResults = new ArrayMap<>();
        translateLanguage = new TranslateLanguage();

        sbL1 = new StringBuilder();
        sbL2 = new StringBuilder();
        sbL3 = new StringBuilder();
        sbL4 = new StringBuilder();
    }


    @Override
    public void onVolumeChanged(int volume, byte[] bytes) {
//        Log.d("RecogEventAdapter", "onVolumeChanged");
        listener.onAsrVolume(bytes.length, volume);
    }

    @Override
    public void onBeginOfSpeech() {
        Log.d("RecogEventAdapter", "onBeginOfSpeech");
        listener.onAsrBegin();
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("RecogEventAdapter", "onEndOfSpeech");
        String engineType = RobotInfo.getInstance().getEngineType();
        if (engineType.equals(SpeechConstant.TYPE_CLOUD)) {

            Log.d("RecogEventAdapter", "onEndOfSpeech TYPE_CLOUD");
            if (mIatResults.size() > 0) {
                final StringBuffer sb = new StringBuffer();
                for (String result : mIatResults.values()) {
                    sb.append(result);
                }

                //手动翻译  是英文
                boolean isTranslate = RobotInfo.getInstance().getLanguageType() == 1;

                if (isTranslate) {
                    translateLanguage.queryEntoZh(sb.toString(), new TranslateListener() {
                        @Override
                        public void onError(TranslateErrorCode translateErrorCode) {
                            listener.onAsrTranslateError(translateErrorCode.getCode());
                        }

                        @Override
                        public void onResult(Translate translate, String s) {
                            int errorCode = translate.getErrorCode();
                            if (errorCode == 0) {
                                List<String> explains = translate.getExplains();
                                if (explains != null && explains.size() > 0) {
                                    String explain = explains.get(0);
                                    listener.onAsrFinalResult(explain);
                                } else {
                                    listener.onAsrFinalResult(sb.toString());
                                }
                            } else {
                                listener.onAsrTranslateError(errorCode);
                            }
                        }
                    });
                } else {

                    listener.onAsrFinalResult(sb.toString());
                }
            } else {
                listener.onAsrOnlineNluResult(STATUS_END, null);
            }
            mIatResults.clear();
            listener.onAsrEnd();
        }
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean isLast) {
        Log.d("RecogEventAdapter", "onResult");

        Print.i("onResult ++ : " + recognizerResult.getResultString());

        String engineType = RobotInfo.getInstance().getEngineType();
        if (engineType.equals(SpeechConstant.TYPE_LOCAL)) {

//            Asr local = GsonUtil.GsonToBean(recognizerResult.getResultString(), Asr.class);
//
//            if (local.getSc() > 30) {
//
//                List<Ws> wsList = local.getWs();
//
//                StringBuilder sbLocal = new StringBuilder();
//
//                for (int i = 0; i < wsList.size(); i++) {
//                    Ws ws = wsList.get(i);
//                    List<Cw> cwList = ws.getCw();
//                    for (int j = 0; j < cwList.size(); j++) {
//                        Cw cw = cwList.get(j);
//                        if (!sbLocal.equals(cw.getW())) {
//                            sbLocal.append(cw.getW());
//                        }
//                    }
//                }
//
//                listener.onAsrLocalFinalResult(sbLocal.toString());
//            } else {
//                listener.onAsrLocalDegreeLow(local, local.getSc());
//            }
            Asr local = GsonUtil.GsonToBean(recognizerResult.getResultString(), Asr.class);

            if (local.getSc() > GrammerUtils.THRESHOLD) {

                sbL1.delete(0, sbL1.length());
                sbL2.delete(0, sbL2.length());
                sbL3.delete(0, sbL3.length());
                sbL4.delete(0, sbL4.length());

                List<Ws> wsList = local.getWs();
                for (int i = 0; i < wsList.size(); i++) {

                    if (i == 0) {
                        getKeyword(wsList, i, sbL1);
                    } else if (i == 1) {
                        getKeyword(wsList, i, sbL2);
                    } else if (i == 2) {
                        getKeyword(wsList, i, sbL3);
                    } else if (i == 3) {
                        getKeyword(wsList, i, sbL4);
                    }

                }

                listener.onAsrLocalFinalResult(sbL1.toString(), sbL2.toString(), sbL3.toString(), sbL4.toString());
            } else {
                listener.onAsrLocalDegreeLow(local, local.getSc());
            }

        } else if (engineType.equals(SpeechConstant.TYPE_CLOUD)) {

            if (RobotInfo.getInstance().isTranslateEnable()) {
                Trans trans = GsonUtil.GsonToBean(recognizerResult.getResultString(), Trans.class);
                listener.onTrans(trans);
            } else {

                Asr line = GsonUtil.GsonToBean(recognizerResult.getResultString(), Asr.class);
                if (!line.getLs()) {

                    List<Ws> wsList = line.getWs();

                    if (wsList.size() > 1) {
                        String[] results = new String[wsList.size()];
                        StringBuilder builder = new StringBuilder();

                        for (int i = 0; i < wsList.size(); i++) {
                            Ws ws = wsList.get(i);
                            List<Cw> cwList = ws.getCw();
                            results[i] = cwList.get(0).getW();
                            builder.append(cwList.get(0).getW());
                        }

                        mIatResults.put(line.getSn(), builder.toString());
                        listener.onAsrPartialResult(line, results);
                    } else if (wsList.size() == 1) {
                        String w = wsList.get(0).getCw().get(0).getW();
                        if (!w.equals("")) {
                            mIatResults.put(line.getSn(), w);
                            listener.onAsrPartialResult(line, new String[]{w});
                        } else {
                            listener.onAsrOnlineNluResult(STATE_W_EMPTY, recognizerResult.getResultString());
                        }
                    } else {
                        listener.onAsrOnlineNluResult(STATUS_LIST_NUL, recognizerResult.getResultString());
                    }
                } else {

                    listener.onAsrOnlineNluResult(STATUS_LS_FALSE, recognizerResult.getResultString());
                }
            }
        }

    }

    private void getKeyword(List<Ws> wsList, int i, StringBuilder sb) {

        Ws ws = wsList.get(i);
        List<Cw> cwList = ws.getCw();

        Set<Cw> cwSet = new HashSet<>(cwList);

        for (Cw cw : cwSet) {
            if (cw.getSc() > GrammerUtils.THRESHOLD) {
                sb.append(cw.getW());
            }
        }
    }

    @Override
    public void onError(SpeechError speechError) {
        Log.d("RecogEventAdapter", "onError");
        listener.onAsrFinishError(speechError.getErrorCode(), speechError.getErrorDescription());
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {
        Log.d("RecogEventAdapter", "onEvent");
        Print.i("onEvent i : " + i + " , i1 : " + i1 + " , i2 : " + i2);
    }
}
