package com.fanfan.youtu.api.uploadfile.bean;

import java.io.Serializable;

/**
 * Created by zhangyuanyuan on 2017/11/4.
 */

public class Message implements Serializable{

    private String number;
    private String url;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
