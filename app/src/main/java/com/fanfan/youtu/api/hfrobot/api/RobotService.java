package com.fanfan.youtu.api.hfrobot.api;

import com.fanfan.youtu.api.hfrobot.bean.Check;
import com.fanfan.youtu.api.hfrobot.bean.RequestProblem;
import com.fanfan.youtu.api.hfrobot.bean.RobotMsg;
import com.fanfan.youtu.api.hfrobot.bean.SetBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

import static com.fanfan.youtu.api.base.Constant.H_NAME;
import static com.fanfan.youtu.api.base.Constant.ROBOT_NAME;

/**
 * Created by android on 2017/12/21.
 */

public interface RobotService {

    @Headers({H_NAME + ":" + ROBOT_NAME})
    @GET("robot/check_update.php")
    Call<Check> updateProgram(@Query("type") int type);

    @Headers({H_NAME + ":" + ROBOT_NAME})
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    @Headers({H_NAME + ":" + ROBOT_NAME})
    @GET("robot/request_problem.php")
    Call<RequestProblem> requestProblem(@Query("identifier") String identifier, @Query("problem") String problem, @Query("id") int id, @Query("type") int type);

    @Headers({H_NAME + ":" + ROBOT_NAME})
    @POST("robot/user_data.php")
    Call<RobotMsg> addSet(@Query("user_name")String user_name, @Query("set_pwd")String set_pwd, @Query("type") int type);

    @Headers({H_NAME + ":" + ROBOT_NAME})
    @POST("robot/user_data.php")
    Call<RobotMsg> updateSet(@Query("user_name")String user_name, @Query("set_pwd")String set_pwd, @Query("type") int type);

    @Headers({H_NAME + ":" + ROBOT_NAME})
    @POST("robot/user_data.php")
    Call<SetBean> selectSet(@Query("user_name")String user_name, @Query("type") int type);
}
