package com.fanfan.youtu.api.face.bean;

import com.fanfan.youtu.api.base.bean.BaseError;

import java.util.List;

/**
 * Created by android on 2018/1/4.
 */

public class AddFace extends BaseError {

    private String added;
    private List<String> face_ids;
    private List<Integer> ret_codes;
    private String session_id;

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public List<String> getFace_ids() {
        return face_ids;
    }

    public void setFace_ids(List<String> face_ids) {
        this.face_ids = face_ids;
    }

    public List<Integer> getRet_codes() {
        return ret_codes;
    }

    public void setRet_codes(List<Integer> ret_codes) {
        this.ret_codes = ret_codes;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
}
