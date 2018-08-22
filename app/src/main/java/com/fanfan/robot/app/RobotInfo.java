package com.fanfan.robot.app;

import android.content.Context;

import com.baidu.aip.utils.PreferencesUtil;
import com.fanfan.novel.utils.youdao.TranslateLanguage;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.novel.utils.system.PreferencesUtils;
import com.fanfan.robot.ui.setting.act.face.local.LivenessSettingActivity;
import com.iflytek.cloud.SpeechConstant;
import com.seabreeze.log.Print;

/**
 * Created by android on 2018/1/5.
 */

public class RobotInfo {

    private volatile static RobotInfo instance;

    private RobotInfo() {
    }

    public static RobotInfo getInstance() {
        if (instance == null) {
            synchronized (RobotInfo.class) {
                if (instance == null) {
                    instance = new RobotInfo();
                }
            }
        }
        return instance;
    }

    public RobotInfo init(Context context) {
        setEngineType(SpeechConstant.TYPE_CLOUD);
        setControlId(Constants.controlId);
        setServerId(Constants.serverId);
        setRoomId(Constants.roomAVId);
        setTtsLineTalker(PreferencesUtils.getString(context, Constants.IAT_LINE_LANGUAGE_TALKER, "xiaoyan"));
        setTtsLocalTalker(PreferencesUtils.getString(context, Constants.IAT_LOCAL_LANGUAGE_TALKER, "xiaoyan"));
        setIatLineHear(PreferencesUtils.getString(context, Constants.IAT_LINE_LANGUAGE_HEAR, "xiaoyan"));
        setIatLocalHear(PreferencesUtils.getString(context, Constants.IAT_LOCAL_LANGUAGE_HEAR, "xiaoyan"));
        setIatLineLanguage(PreferencesUtils.getString(context, Constants.IAT_LINE_LANGUAGE, "mandarin"));
        setQueryLanage(PreferencesUtils.getBoolean(context, Constants.QUERYLANAGE, false));
        setLineSpeed(PreferencesUtils.getInt(NovelApp.getInstance().getApplicationContext(), Constants.LINE_SPEED, 60));
        setLineVolume(PreferencesUtils.getInt(NovelApp.getInstance().getApplicationContext(), Constants.LINE_VOLUME, 100));
        setCityName(PreferencesUtils.getString(NovelApp.getInstance().getApplicationContext(), Constants.CITY_NAME, "北京"));
        isInitialization = PreferencesUtils.getBoolean(context, Constants.IS_INITIALIZATION, false);
        setLanguageType(PreferencesUtils.getInt(context, Constants.LANGUAGE_TYPE, 0));
        PreferencesUtil.putInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity.TYPE_RGB_LIVENSS);
        return getInstance();
    }

    private long loginTime;

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    //IAT_CLOUD_BUILD
    private boolean cloudBuild;

    public boolean isCloudBuild() {
        if (cloudBuild)
            return true;
        return PreferencesUtils.getBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_CLOUD_BUILD, false);
    }

    public void setCloudBuild() {
        Print.e("在线语法构建成功");
        this.cloudBuild = true;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_CLOUD_BUILD, true);
    }

    //IAT_LOCAL_BUILD
    private boolean localBuild;

    public boolean isLocalBuild() {
        if (localBuild)
            return true;
        return PreferencesUtils.getBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_BUILD, false);
    }

    public void setLocalBuild() {
        Print.e("本地语法构建成功");
        this.localBuild = true;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_BUILD, true);
    }

    //IAT_CLOUD_UPDATELEXICON
    private boolean cloudUpdatelexicon;

    public boolean isCloudUpdatelexicon() {
        if (cloudUpdatelexicon)
            return true;
        return PreferencesUtils.getBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_CLOUD_UPDATELEXICON, false);
    }

    public void setCloudUpdatelexicon() {
        Print.e("在线热词上传成功");
        this.cloudUpdatelexicon = true;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_CLOUD_UPDATELEXICON, true);
    }

    //IAT_LOCAL_UPDATELEXICON
    @Deprecated
    private boolean localUpdatelexicon;

    @Deprecated
    public boolean isLocalUpdatelexicon() {
        if (localUpdatelexicon)
            return true;
        return PreferencesUtils.getBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_UPDATELEXICON, false);
    }

    @Deprecated
    public void setLocalUpdatelexicon() {
        Print.e("本地词典更新成功");
        this.localUpdatelexicon = true;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_UPDATELEXICON, true);
    }

    //本地在线语音控制
    private String mEngineType;

    public String getEngineType() {
        return mEngineType;
    }

    public void setEngineType(String engineType) {
        this.mEngineType = engineType;
    }

    //需要设置可以连接的控制端id
    private String controlId;

    public String getControlId() {
        return controlId;
    }

    public void setControlId(String controlId) {
        this.controlId = controlId;
    }

    //需要设置可以连接的服务端id
    private String serverId;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    //需要设置进入的群，以便控制端一对多
    private String roomId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    //在线的发言人
    private String ttsLineTalker;

    public String getTtsLineTalker() {
        return ttsLineTalker;
    }

    public void setTtsLineTalker(String ttsLineTalker) {
        this.ttsLineTalker = ttsLineTalker;
        PreferencesUtils.putString(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LINE_LANGUAGE_TALKER, ttsLineTalker);
    }

    //离线的发言人
    private String ttsLocalTalker;

    public String getTtsLocalTalker() {
        return ttsLocalTalker;
    }

    public void setTtsLocalTalker(String ttsLocalTalker) {
        this.ttsLocalTalker = ttsLocalTalker;
        PreferencesUtils.putString(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_LANGUAGE_TALKER, ttsLocalTalker);
    }

    //在线的监听人
    private String iatLineHear;

    public String getIatLineHear() {
        return iatLineHear;
    }

    public void setIatLineHear(String iatLineHear) {
        this.iatLineHear = iatLineHear;
    }

    //离线的监听人
    private String iatLocalHear;

    public String getIatLocalHear() {
        return iatLocalHear;
    }

    public void setIatLocalHear(String iatLocalHear) {
        this.iatLocalHear = iatLocalHear;
    }

    //在线语言区域（mandarin， en_us）
    private String iatLineLanguage;

    public String getIatLineLanguage() {
        return iatLineLanguage;
    }

    public void setIatLineLanguage(String iatLineLanguage) {
        this.iatLineLanguage = iatLineLanguage;
        setLineLanguage(iatLineLanguage);
        PreferencesUtils.putString(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LINE_LANGUAGE, iatLineLanguage);
    }

    //在线语言（zh_cn， en_us）
    private String lineLanguage;

    public String getLineLanguage() {
        return lineLanguage;
    }

    public void setLineLanguage(String iatLineLanguage) {
        if (iatLineLanguage.equals("en_us")) {
            lineLanguage = "en_us";
            setTranslateEnable(true);
        } else {
            lineLanguage = "zh_cn";
            setTranslateEnable(false);
        }
    }

    //需要翻译
    private boolean translateEnable;

    public boolean isTranslateEnable() {
        return translateEnable;
    }

    public void setTranslateEnable(boolean translateEnable) {
        this.translateEnable = translateEnable;
    }

    //是否需要中转英
    private boolean queryLanage;

    public boolean isQueryLanage() {
        return queryLanage;
    }

    public void setQueryLanage(boolean queryLanage) {
        this.queryLanage = queryLanage;
        PreferencesUtils.getBoolean(NovelApp.getInstance(), Constants.QUERYLANAGE, queryLanage);
    }


    //是否已经构建语法，上传热词等
    private boolean isInitialization;

    public boolean isInitialization() {
        return isInitialization;
    }

    public void setInitialization(boolean initialization) {
        isInitialization = initialization;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IS_INITIALIZATION, isInitialization);
    }

    private int lineSpeed;

    public int getLineSpeed() {
        return lineSpeed;
    }

    public void setLineSpeed(int speed) {
        lineSpeed = speed;
        PreferencesUtils.putInt(NovelApp.getInstance().getApplicationContext(), Constants.LINE_SPEED, speed);
    }

    private int lineVolume;

    public int getLineVolume() {
        return lineVolume;
    }

    public void setLineVolume(int volume) {
        lineVolume = volume;
        PreferencesUtils.putInt(NovelApp.getInstance().getApplicationContext(), Constants.LINE_VOLUME, volume);
    }

    private String cityName;

    public void setCityName(String cityName) {
        this.cityName = cityName;
        PreferencesUtils.putString(NovelApp.getInstance().getApplicationContext(), Constants.CITY_NAME, cityName);
    }

    public String getCityName() {
        return cityName;
    }

    // int 0 中  1 英文
    private int languageType;

    public void setLanguageType(int languageType) {
        this.languageType = languageType;
        PreferencesUtils.putInt(NovelApp.getInstance().getApplicationContext(), Constants.LANGUAGE_TYPE, languageType);
    }

    public int getLanguageType() {
        return languageType;
    }
}
