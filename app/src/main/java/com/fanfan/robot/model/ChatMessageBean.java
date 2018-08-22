package com.fanfan.robot.model;

import com.fanfan.robot.app.common.ChatConst;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity//标识实体类，greenDAO会映射成sqlite的一个表，表名为实体类名的大写形式
public class ChatMessageBean {

    @Id(autoincrement = true)//该字段的类型为long或Long类型，autoincrement设置是否自动增长
    private Long id;
    @Property(nameInDb = "UserId")
    private String UserId;
    @Property(nameInDb = "UserName")
    private String UserName;
    @Property(nameInDb = "UserHeadIcon")
    private String UserHeadIcon;
    @Property(nameInDb = "time")
    private String time;//时间
    @Property(nameInDb = "type")
    private int type;
    @Property(nameInDb = "messagetype")// 标识该属性在表中对应的列名称, nameInDb设置名称
    private int messagetype;//消息类型（发送或接受）
    @Property(nameInDb = "messageContent")
    private String messageContent;//文本消息
    @Property(nameInDb = "sendState")
    @ChatConst.SendState
    private int sendState;//消息状态（发送中，发送完成，发送失败）
    //图片
    @Property(nameInDb = "imageUrl")
    private String imageUrl;
    @Property(nameInDb = "imageLocal")
    private String imageLocal;
    //语音
    @Property(nameInDb = "voiceType")
    private String voiceType;
    @Property(nameInDb = "voiceAnswer")
    private String voiceAnswer;
    @Property(nameInDb = "action")
    private String action;
    @Property(nameInDb = "expression")
    private String expression;
    @Property(nameInDb = "actionData")
    private String actionData;
    @Property(nameInDb = "expressionData")
    private String expressionData;
    //视频
    @Property(nameInDb = "videoName")
    private String videoName;
    @Property(nameInDb = "videoUrl")
    private String videoUrl;
    @Generated(hash = 1307694993)
    public ChatMessageBean(Long id, String UserId, String UserName,
            String UserHeadIcon, String time, int type, int messagetype,
            String messageContent, int sendState, String imageUrl,
            String imageLocal, String voiceType, String voiceAnswer, String action,
            String expression, String actionData, String expressionData,
            String videoName, String videoUrl) {
        this.id = id;
        this.UserId = UserId;
        this.UserName = UserName;
        this.UserHeadIcon = UserHeadIcon;
        this.time = time;
        this.type = type;
        this.messagetype = messagetype;
        this.messageContent = messageContent;
        this.sendState = sendState;
        this.imageUrl = imageUrl;
        this.imageLocal = imageLocal;
        this.voiceType = voiceType;
        this.voiceAnswer = voiceAnswer;
        this.action = action;
        this.expression = expression;
        this.actionData = actionData;
        this.expressionData = expressionData;
        this.videoName = videoName;
        this.videoUrl = videoUrl;
    }
    @Generated(hash = 1557449535)
    public ChatMessageBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserId() {
        return this.UserId;
    }
    public void setUserId(String UserId) {
        this.UserId = UserId;
    }
    public String getUserName() {
        return this.UserName;
    }
    public void setUserName(String UserName) {
        this.UserName = UserName;
    }
    public String getUserHeadIcon() {
        return this.UserHeadIcon;
    }
    public void setUserHeadIcon(String UserHeadIcon) {
        this.UserHeadIcon = UserHeadIcon;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getMessagetype() {
        return this.messagetype;
    }
    public void setMessagetype(int messagetype) {
        this.messagetype = messagetype;
    }
    public String getMessageContent() {
        return this.messageContent;
    }
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
    public int getSendState() {
        return this.sendState;
    }
    public void setSendState(int sendState) {
        this.sendState = sendState;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getImageLocal() {
        return this.imageLocal;
    }
    public void setImageLocal(String imageLocal) {
        this.imageLocal = imageLocal;
    }
    public String getVoiceType() {
        return this.voiceType;
    }
    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }
    public String getVoiceAnswer() {
        return this.voiceAnswer;
    }
    public void setVoiceAnswer(String voiceAnswer) {
        this.voiceAnswer = voiceAnswer;
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
    public String getVideoName() {
        return this.videoName;
    }
    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
    public String getVideoUrl() {
        return this.videoUrl;
    }
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

}
