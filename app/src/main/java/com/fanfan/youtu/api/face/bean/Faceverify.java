package com.fanfan.youtu.api.face.bean;

import com.fanfan.youtu.api.base.bean.BaseError;

/**
 * Created by android on 2018/1/4.
 */

public class Faceverify extends BaseError {

    private double confidence;
    private boolean ismatch;
    private String session_id;

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public boolean isIsmatch() {
        return ismatch;
    }

    public void setIsmatch(boolean ismatch) {
        this.ismatch = ismatch;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
}
