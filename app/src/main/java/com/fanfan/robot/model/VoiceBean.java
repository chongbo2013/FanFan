package com.fanfan.robot.model;

import com.fanfan.robot.app.common.ChatConst;
import com.fanfan.robot.view.recyclerview.tree.base.BaseItemData;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by android on 2017/12/20.
 */
@Entity
public class VoiceBean extends BaseItemData {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "localType")
    @ChatConst.LocalType
    private int localType;
    @Property(nameInDb = "saveTime")
    private long saveTime;
    @Property(nameInDb = "showTitle")
    private String showTitle;
    @Property(nameInDb = "voiceanswer")
    private String voiceAnswer;
    @Property(nameInDb = "imgUrl")
    private String imgUrl;
    @Property(nameInDb = "action")
    private String action;
    @Property(nameInDb = "expression")
    private String expression;
    @Property(nameInDb = "actionData")
    private String actionData;
    @Property(nameInDb = "expressionData")
    private String expressionData;
    @Property(nameInDb = "key1")
    private String key1;
    @Property(nameInDb = "key2")
    private String key2;
    @Property(nameInDb = "key3")
    private String key3;
    @Property(nameInDb = "key4")
    private String key4;

    @Generated(hash = 804894326)
    public VoiceBean(Long id, int localType, long saveTime, String showTitle,
                     String voiceAnswer, String imgUrl, String action, String expression,
                     String actionData, String expressionData, String key1, String key2,
                     String key3, String key4) {
        this.id = id;
        this.localType = localType;
        this.saveTime = saveTime;
        this.showTitle = showTitle;
        this.voiceAnswer = voiceAnswer;
        this.imgUrl = imgUrl;
        this.action = action;
        this.expression = expression;
        this.actionData = actionData;
        this.expressionData = expressionData;
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;
        this.key4 = key4;
    }

    @Generated(hash = 1719036352)
    public VoiceBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLocalType() {
        return this.localType;
    }

    public void setLocalType(int localType) {
        this.localType = localType;
    }

    public long getSaveTime() {
        return this.saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public String getShowTitle() {
        return this.showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public String getVoiceAnswer() {
        return this.voiceAnswer;
    }

    public void setVoiceAnswer(String voiceAnswer) {
        this.voiceAnswer = voiceAnswer;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getActionData() {
        return this.actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }

    public String getExpressionData() {
        return this.expressionData;
    }

    public void setExpressionData(String expressionData) {
        this.expressionData = expressionData;
    }

    public String getKey1() {
        return this.key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return this.key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public String getKey3() {
        return this.key3;
    }

    public void setKey3(String key3) {
        this.key3 = key3;
    }

    public String getKey4() {
        return this.key4;
    }

    public void setKey4(String key4) {
        this.key4 = key4;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VoiceBean) {
            VoiceBean voiceBean = (VoiceBean) obj;
            return voiceBean.showTitle.equals(getShowTitle());
        }
        return false;
    }
}
