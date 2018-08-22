package com.fanfan.robot.model.local;

/**
 * Created by android on 2018/1/19.
 */

public class Trans {

    private String from;
    private int ret;
    private String sid;
    private String to;
    private Trans_result trans_result;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Trans_result getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(Trans_result trans_result) {
        this.trans_result = trans_result;
    }
}
