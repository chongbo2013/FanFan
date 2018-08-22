package com.fanfan.youtu.api.hfrobot.bean;


/**
 * Created by lyw on 2018-05-09.
 */

public class SetBean extends RobotMsg {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        private String id;
        private String user_name;
        private String set_pwd;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getSet_pwd() {
            return set_pwd;
        }

        public void setSet_pwd(String set_pwd) {
            this.set_pwd = set_pwd;
        }
    }
}
