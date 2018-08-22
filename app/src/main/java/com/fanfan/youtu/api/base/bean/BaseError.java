package com.fanfan.youtu.api.base.bean;

/**
 * Created by android on 2017/12/21.
 */

public class BaseError {

    private int errorcode;
    private String errormsg;

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }


}
