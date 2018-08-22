package com.fanfan.youtu.api.uploadfile.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fanfan.youtu.api.base.callback.BaseCallback;
import com.fanfan.youtu.api.base.impl.BaseImpl;
import com.fanfan.youtu.api.uploadfile.event.UpfilesZooEvent;
import com.fanfan.youtu.utils.UUIDGenerator;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by android on 2018/1/4.
 */

public class FilezooImpl extends BaseImpl<FilezooService> implements FilezooAPI {

    public FilezooImpl(@NonNull Context context) {
        super(context);
    }

    @Override
    public String upfilesZoo(String zooPath) {
        String uuid = UUIDGenerator.getUUID();
        File file = new File(zooPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploadFile_0", file.getName(), requestFile);
        String descriptionString = "This is a description";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
        mService.upfilesZoo(description, body).enqueue(new BaseCallback<>(new UpfilesZooEvent(uuid)));
        return uuid;
    }
}
