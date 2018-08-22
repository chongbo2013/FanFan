package com.fanfan.robot.listener.base;

import android.os.Bundle;

import com.fanfan.robot.model.local.Trans;
import com.fanfan.robot.model.local.Asr;
import com.fanfan.robot.model.local.Cw;
import com.fanfan.robot.model.local.Ws;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.youtu.utils.GsonUtil;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.seabreeze.log.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by dell on 2017/9/20.
 */

public class IatListener implements RecognizerListener {

    private StringBuffer mosaicSb;

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    public IatListener(RecognListener recognListener) {
        mosaicSb = new StringBuffer();
        this.recognListener = recognListener;
    }

    @Override
    public void onVolumeChanged(int i, byte[] bytes) {

    }

    @Override
    public void onBeginOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

        Print.e("onEndOfSpeech : " + mosaicSb.toString());

        if (recognListener == null) {
            recognListener.onRecognDown();
            return;
        }
        String trim = mosaicSb.toString().trim();

        String engineType = RobotInfo.getInstance().getEngineType();
        if (engineType.equals(SpeechConstant.TYPE_LOCAL)) {
            if (trim.length() > 0) {
                recognListener.onRecognResult(trim);
                mosaicSb.delete(0, mosaicSb.length());
            } else {
                recognListener.onLevelSmall();
            }
        } else {
            if (RobotInfo.getInstance().isTranslateEnable()) {
                if (trim.length() > 0) {
                    recognListener.onTranslate(trim);
                    mosaicSb.delete(0, mosaicSb.length());
                } else {
                    recognListener.onRecognDown();
                }
            } else {
                if (trim.length() > 0) {
                    recognListener.onRecognResult(trim);
                    mosaicSb.delete(0, mosaicSb.length());
                } else {
                    recognListener.onRecognDown();
                }
            }
        }
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean isHas) {

        Print.e("RecognizerResult : " + recognizerResult.getResultString());

        JSONTokener tokener = new JSONTokener(recognizerResult.getResultString());
        int sn;
        try {
            JSONObject joResult = new JSONObject(tokener);
            sn = joResult.getInt("sn");

            if (sn == 2) {
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String engineType = RobotInfo.getInstance().getEngineType();
        if (engineType.equals(SpeechConstant.TYPE_LOCAL)) {

            Asr local = GsonUtil.GsonToBean(recognizerResult.getResultString(), Asr.class);

            if (local.getSc() > 30) {

                List<Ws> wsList = local.getWs();
                for (int i = 0; i < wsList.size(); i++) {
                    Ws ws = wsList.get(i);
                    List<Cw> cwList = ws.getCw();
                    for (int j = 0; j < cwList.size(); j++) {
                        Cw cw = cwList.get(j);
                        if (!cw.getW().equals(mosaicSb.toString())) {
                            mosaicSb.append(cw.getW());
                        }
                    }
                }
            }

        } else if (engineType.equals(SpeechConstant.TYPE_CLOUD)) {

            if (RobotInfo.getInstance().isTranslateEnable()) {
                Trans trans = GsonUtil.GsonToBean(recognizerResult.getResultString(), Trans.class);
                mosaicSb.append(trans.getTrans_result().getDst());
            } else {
                Asr line = GsonUtil.GsonToBean(recognizerResult.getResultString(), Asr.class);
                List<Ws> wsList = line.getWs();
                for (int i = 0; i < wsList.size(); i++) {
                    Ws ws = wsList.get(i);
                    List<Cw> cwList = ws.getCw();
                    for (int j = 0; j < cwList.size(); j++) {
                        Cw cw = cwList.get(j);
                        mosaicSb.append(cw.getW());
                    }
                }

            }
        }

        Print.e("onResult : " + mosaicSb.toString());
    }

    public String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    @Override
    public void onError(SpeechError speechError) {
        if (recognListener != null) {
            recognListener.onErrInfo(speechError.getErrorCode());
        }
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {
    }

    private RecognListener recognListener;

    public interface RecognListener {

        void onTranslate(String result);

        void onRecognResult(String result);

        void onErrInfo(int errorCode);

        void onRecognDown();

        void onLevelSmall();
    }
}
