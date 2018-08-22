package com.fanfan.youtu.api.face.bean;

import com.fanfan.youtu.api.base.bean.BaseError;

import java.util.List;

/**
 * Created by android on 2018/1/4.
 */

public class FaceIds extends BaseError {


    private List<String> face_ids;

    public List<String> getFace_ids() {
        return face_ids;
    }

    public void setFace_ids(List<String> face_ids) {
        this.face_ids = face_ids;
    }
}
