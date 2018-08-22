package com.fanfan.novel.utils.permiss;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/28/028.
 */

public interface PermissionCallback extends Serializable {

    void onClose();

    void onFinish();

    void onDeny(String permission, int position);

    void onGuarantee(String permission, int position);

}
