package com.fanfan.youtu.api.uploadfile.bean;

import java.util.List;

/**
 * Created by android on 2018/1/4.
 */

public class Filezoo {

    private List<Message> message;
    private String resCode;
    private String resMsg;

    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }
}
