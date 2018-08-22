package com.fanfan.robot.model;

/**
 * Created by zhangyuanyuan on 2017/9/26.
 */

public class UserInfo {

    private String identifier;
    private String userSig;

    private String nickName;
    private String userPass;

    private static UserInfo ourInstance = new UserInfo();

    public static UserInfo getInstance() {
        return ourInstance;
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public String getNikeName() {
        return nickName;
    }

    public void setNikeName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
}
