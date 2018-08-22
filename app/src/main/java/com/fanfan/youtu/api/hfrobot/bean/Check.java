package com.fanfan.youtu.api.hfrobot.bean;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public class Check extends RobotMsg {


    /**
     * check : {"id":"4","type":"1","versionCode":"2","versionName":"11","appName":"ew.apk","updateUrl":"files/robot.apk","upgradeInfo":"ds","updateTime":"2018-03-29 16:16:51"}
     */

    private CheckBean check;

    public CheckBean getCheck() {
        return check;
    }

    public void setCheck(CheckBean check) {
        this.check = check;
    }

    public static class CheckBean {
        /**
         * id : 4
         * type : 1
         * versionCode : 2
         * versionName : 11
         * appName : ew.apk
         * updateUrl : files/robot.apk
         * upgradeInfo : ds
         * updateTime : 2018-03-29 16:16:51
         */

        private int id;
        private int type;
        private int versionCode;
        private String versionName;
        private String appName;
        private String updateUrl;
        private String upgradeInfo;
        private String updateTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getUpdateUrl() {
            return updateUrl;
        }

        public void setUpdateUrl(String updateUrl) {
            this.updateUrl = updateUrl;
        }

        public String getUpgradeInfo() {
            return upgradeInfo;
        }

        public void setUpgradeInfo(String upgradeInfo) {
            this.upgradeInfo = upgradeInfo;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
