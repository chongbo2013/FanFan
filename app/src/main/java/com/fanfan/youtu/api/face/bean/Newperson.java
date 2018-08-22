package com.fanfan.youtu.api.face.bean;

import com.fanfan.youtu.api.base.bean.BaseError;

import java.util.List;

/**
 * Created by android on 2017/12/22.
 */

public class Newperson extends BaseError {

    private String person_id;
    private int suc_group;
    private int suc_face;
    private String session_id;
    private String face_id;
    private List<String> group_ids;

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public int getSuc_group() {
        return suc_group;
    }

    public void setSuc_group(int suc_group) {
        this.suc_group = suc_group;
    }

    public int getSuc_face() {
        return suc_face;
    }

    public void setSuc_face(int suc_face) {
        this.suc_face = suc_face;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getFace_id() {
        return face_id;
    }

    public void setFace_id(String face_id) {
        this.face_id = face_id;
    }

    public List<String> getGroup_ids() {
        return group_ids;
    }

    public void setGroup_ids(List<String> group_ids) {
        this.group_ids = group_ids;
    }
}
