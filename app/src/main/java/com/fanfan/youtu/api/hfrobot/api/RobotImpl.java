package com.fanfan.youtu.api.hfrobot.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fanfan.youtu.api.base.callback.BaseCallback;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.base.impl.BaseImpl;
import com.fanfan.youtu.api.hfrobot.event.AddSetEvent;
import com.fanfan.youtu.api.hfrobot.event.CheckEvent;
import com.fanfan.youtu.api.hfrobot.event.RequestProblemEvent;
import com.fanfan.youtu.api.hfrobot.event.SetEvent;
import com.fanfan.youtu.api.hfrobot.event.UpdateSetEvent;
import com.fanfan.youtu.utils.UUIDGenerator;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public class RobotImpl extends BaseImpl<RobotService> implements RobotAPI {
    public RobotImpl(@NonNull Context context) {
        super(context);
    }

    @Override
    public String updateProgram(int type) {
        String uuid = UUIDGenerator.getUUID();
        mService.updateProgram(type)
                .enqueue(new BaseCallback<>(new CheckEvent(uuid)));
        return uuid;
    }

    @Override
    public String downloadFileWithDynamicUrlSync(String fileUrl) {
        String uuid = UUIDGenerator.getUUID();
        mService.downloadFileWithDynamicUrlSync(fileUrl)
                .enqueue(new BaseCallback<>(new BaseEvent<ResponseBody>(uuid)));
        return uuid;
    }

    @Override
    public String requestProblem(String identifier, String problem, int id, int type) {
        String uuid = UUIDGenerator.getUUID();
        mService.requestProblem(identifier, problem, id, type)
                .enqueue(new BaseCallback<>(new RequestProblemEvent(uuid)));
        return uuid;
    }@Override
    public String addSet(String user_name, String set_pwd ) {
        String uuid = UUIDGenerator.getUUID();
        mService.addSet(user_name, set_pwd, 0)
                .enqueue(new BaseCallback<>(new AddSetEvent(uuid)));
        return uuid;
    }

    @Override
    public String updateSet(String user_name, String set_pwd ) {
        String uuid = UUIDGenerator.getUUID();
        mService.updateSet(user_name, set_pwd, 1)
                .enqueue(new BaseCallback<>(new UpdateSetEvent(uuid)));
        return uuid;
    }

    @Override
    public String selectSet(String user_name ) {
        String uuid = UUIDGenerator.getUUID();
        mService.selectSet(user_name  ,2)
                .enqueue(new BaseCallback<>(new SetEvent(uuid)));
        return uuid;
    }
}