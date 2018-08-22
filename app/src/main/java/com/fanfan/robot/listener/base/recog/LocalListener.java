package com.fanfan.robot.listener.base.recog;

import com.fanfan.robot.model.local.Asr;
import com.fanfan.robot.model.local.Trans;
import com.seabreeze.log.Print;

public class LocalListener implements IRecogListener, NulState {


    protected static final String TAG = "LocalListener ";

    private long speechEndTime;

    @Override
    public void onAsrBegin() {
        speechEndTime = System.currentTimeMillis();
        Print.e(TAG + "监听已启动，检测用户说话");
    }

    @Override
    public void onAsrPartialResult(Asr recogResult, String[] results) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < results.length; i++) {
            buffer.append(results[i]);
        }
        Print.e(TAG + "临时识别结果，结果是“" + buffer.toString() + "” , sc : " + recogResult.getSc() + " , ls" + recogResult.getLs());
        Print.e(recogResult);
    }

    @Override
    public void onAsrOnlineNluResult(int type, String nluResult) {
        if (nluResult != null) {
            Print.e(TAG + "原始语义识别 type " + type + " , 结果json：" + nluResult);
        }
    }

    @Override
    public void onAsrFinalResult(String result) {
        Print.e(TAG + "识别结束，结果是“" + result + "”");
        long diffTime = System.currentTimeMillis() - speechEndTime;
        Print.e(TAG + "说话结束到识别结束耗时【" + diffTime + "ms】");
    }

    @Override
    public void onAsrEnd() {
        Print.e(TAG + "检测到用户说话结束");
    }

    @Override
    public void onAsrVolume(int volumePercent, int volume) {
        Print.i("用户说话音量 ： volumePercent ： " + volumePercent + " , volume : " + volume);
    }

    @Override
    public void onAsrFinishError(int errorCode, String errorMessage) {
        Print.i("errorCode : " + errorCode + " , errorMessage : " + errorMessage);
    }

    @Override
    public void onTrans(Trans trans) {

    }


    @Override
    public void onAsrLocalFinalResult(String key1, String key2, String key3, String key4) {
        Print.e("本地语音识别结果 ： (" + key1 + ")  (" + key2 + ")  (" + key3 + ")  (" + key4 + ")");
    }

//    @Override
//    public void onAsrLocalFinalResult(String result) {
//        Print.e("本地语音识别结果 ： " + result);
//    }

    @Override
    public void onAsrLocalDegreeLow(Asr local, int degree) {

        Print.e("本地识别置信度小 degree ： " + degree + " , local" + local);
    }

    @Override
    public void onAsrTranslateError(int errorCode) {
        Print.e("翻译出错 errorCode : " + errorCode);
        onAsrFinishError(errorCode, "translate error");
    }
}
