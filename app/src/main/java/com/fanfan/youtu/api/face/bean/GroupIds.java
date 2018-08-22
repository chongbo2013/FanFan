package com.fanfan.youtu.api.face.bean;

import com.fanfan.youtu.api.base.bean.BaseError;

import java.util.List;

/**
 * Created by android on 2018/1/4.
 */

public class GroupIds extends BaseError {


    private List<String> group_ids;

    public List<String> getGroup_ids() {
        return group_ids;
    }

    public void setGroup_ids(List<String> group_ids) {
        group_ids = group_ids;
    }
}
