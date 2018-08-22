package com.fanfan.youtu.api.face.bean;

import com.fanfan.youtu.api.base.bean.BaseError;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 2017/12/21.
 */

public class FacePersonid extends BaseError implements Serializable {

    private List<String> person_ids;

    public List<String> getPerson_ids() {
        return person_ids;
    }

    public void setPerson_ids(List<String> person_ids) {
        this.person_ids = person_ids;
    }
}
