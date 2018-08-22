package com.fanfan.youtu.api.hfrobot.api;

import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public interface RobotAPI {

    String updateProgram(int type);

    String downloadFileWithDynamicUrlSync(@Url String fileUrl);

    String requestProblem(String identifier, String problem, int id, int type);

    String addSet(String user_name, String set_pwd);

    String updateSet(String user_name, String set_pwd);

    String selectSet(String user_name);
}
