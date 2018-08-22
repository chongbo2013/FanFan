package com.fanfan.youtu.api.face.bean;

import com.fanfan.youtu.api.base.bean.BaseError;
import com.fanfan.youtu.api.face.bean.detectFace.Face_shape;

import java.util.List;

/**
 * Created by android on 2018/1/4.
 */

public class FaceShape extends BaseError {

    private String session_id;
    private int image_height;
    private int image_width;
    private List<Face_shape> face_shape;

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public int getImage_height() {
        return image_height;
    }

    public void setImage_height(int image_height) {
        this.image_height = image_height;
    }

    public int getImage_width() {
        return image_width;
    }

    public void setImage_width(int image_width) {
        this.image_width = image_width;
    }

    public List<Face_shape> getFace_shape() {
        return face_shape;
    }

    public void setFace_shape(List<Face_shape> face_shape) {
        this.face_shape = face_shape;
    }
}
