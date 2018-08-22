package com.fanfan.youtu.api.uploadfile.api;

import com.fanfan.youtu.api.base.Constant;
import com.fanfan.youtu.api.uploadfile.bean.Filezoo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by android on 2018/1/4.
 */

public interface FilezooService {

    @Multipart
    @POST(Constant.OR_CODE)
    Call<Filezoo> upfilesZoo(@Part("description") RequestBody description, @Part MultipartBody.Part file);

}
