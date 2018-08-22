package com.fanfan.youtu.api.face.bean;

import com.fanfan.youtu.api.base.bean.BaseError;

import java.util.List;

/**
 * Created by android on 2018/1/4.
 */

public class GetInfo extends BaseError {

    private String person_id;
    private String person_name;
    private String tag;
    private String session_id;
    private List<String> face_ids;
    private List<String> group_ids;

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public List<String> getFace_ids() {
        return face_ids;
    }

    public void setFace_ids(List<String> face_ids) {
        this.face_ids = face_ids;
    }

    public List<String> getGroup_ids() {
        return group_ids;
    }

    public void setGroup_ids(List<String> group_ids) {
        this.group_ids = group_ids;
    }
}
