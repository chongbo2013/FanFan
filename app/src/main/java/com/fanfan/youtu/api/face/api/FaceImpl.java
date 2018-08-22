package com.fanfan.youtu.api.face.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.fanfan.robot.model.UserInfo;
import com.fanfan.youtu.api.base.callback.BaseCallback;
import com.fanfan.youtu.api.base.impl.BaseImpl;
import com.fanfan.youtu.api.face.event.AddPersonEvent;
import com.fanfan.youtu.api.face.event.DelFaceEvent;
import com.fanfan.youtu.api.face.event.DelPersonEvent;
import com.fanfan.youtu.api.face.event.DetectFaceEvent;
import com.fanfan.youtu.api.face.event.FaceCompareEvent;
import com.fanfan.youtu.api.face.event.FaceIdentifyEvent;
import com.fanfan.youtu.api.face.event.FaceIdsEvent;
import com.fanfan.youtu.api.face.event.FacePersonidEvent;
import com.fanfan.youtu.api.face.event.FaceinfoEvent;
import com.fanfan.youtu.api.face.event.FaceverifyEvent;
import com.fanfan.youtu.api.face.event.GetInfoEvent;
import com.fanfan.youtu.api.face.event.GroupIdsEvent;
import com.fanfan.youtu.api.face.event.NewPersonEvent;
import com.fanfan.youtu.api.face.event.PersonModifyEvent;
import com.fanfan.youtu.token.YoutuSign;
import com.fanfan.youtu.utils.BitmapUtils;
import com.fanfan.youtu.utils.GsonUtil;
import com.fanfan.youtu.utils.UUIDGenerator;
import com.seabreeze.log.Print;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by android on 2017/12/21.
 */

public class FaceImpl extends BaseImpl<FaceService> implements FaceAPI {


    public FaceImpl(@NonNull Context context) {
        super(context);
    }

    @Override
    public String getGroupids() {
        String uuid = UUIDGenerator.getUUID();
        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();
        array.put("app_id", YoutuSign.APP_ID);
        String arrStr = GsonUtil.GsonString(array);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), arrStr);
        mService.getGroupids(body)
                .enqueue(new BaseCallback<>(new GroupIdsEvent(uuid)));
        return uuid;
    }

    @Override
    public String getPersonids() {
        String uuid = UUIDGenerator.getUUID();
        mService.getPersonids(getRequestBody("group_id", getGroupId()))
                .enqueue(new BaseCallback<>(new FacePersonidEvent(uuid)));
        return uuid;
    }

    @Override
    public String getFaceids(String personId) {
        String uuid = UUIDGenerator.getUUID();

        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();

        array.put("person_id", personId);
        mService.getFaceids(getRequestBody(array))
                .enqueue(new BaseCallback<>(new FaceIdsEvent(uuid)));
        return uuid;
    }

    @Override
    public String getFaceinfo(String faceId) {
        String uuid = UUIDGenerator.getUUID();

        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();

        array.put("face_id", faceId);
        mService.getFaceinfo(getRequestBody(array))
                .enqueue(new BaseCallback<>(new FaceinfoEvent(uuid)));
        return uuid;
    }

    @Override
    public String faceverify(String personId, Bitmap bitmap) {
        String uuid = UUIDGenerator.getUUID();

        String imageData = null;
        try {
            imageData = BitmapUtils.bitmapToBase64(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();
        array.put("image", imageData);
        array.put("person_id", personId);
        mService.faceVerify(getRequestBody(array))
                .enqueue(new BaseCallback<>(new FaceverifyEvent(uuid)));
        return uuid;
    }

    @Override
    public String faceIdentify(Bitmap bitmap) {
        String uuid = UUIDGenerator.getUUID();
        String imageData = null;
        try {
            imageData = BitmapUtils.bitmapToBase64(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();
        array.put("image", imageData);
        array.put("group_id", getGroupId());
        mService.faceIdentify(getRequestBody(array))
                .enqueue(new BaseCallback<>(new FaceIdentifyEvent(uuid)));
        return uuid;
    }

    @Override
    public String newPerson(Bitmap bitmap, String personId) {
        String uuid = UUIDGenerator.getUUID();
        String imageData = null;
        try {
            imageData = BitmapUtils.bitmapToBase64(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();
        array.put("image", imageData);
        array.put("person_id", personId);
        array.put("group_ids", getGroupIds());
        mService.newPerson(getRequestBody(array))
                .enqueue(new BaseCallback<>(new NewPersonEvent(uuid)));
        return uuid;
    }

    @Override
    public String newPerson(Bitmap bitmap, String personId, String personName) {
        String uuid = UUIDGenerator.getUUID();
        String imageData = null;
        try {
            imageData = BitmapUtils.bitmapToBase64(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();
        array.put("image", imageData);
        array.put("person_id", personId);
        array.put("person_name", personName);
        array.put("group_ids", getGroupIds());
        mService.newPerson(getRequestBody(array))
                .enqueue(new BaseCallback<>(new NewPersonEvent(uuid)));
        return uuid;
    }

    @Override
    public String faceCompare(Bitmap bitmapA, Bitmap bitmapB) throws IOException {
        String uuid = UUIDGenerator.getUUID();
        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();

        String imageData = BitmapUtils.bitmapToBase64(bitmapA);
        array.put("imageA", imageData);

        imageData = BitmapUtils.bitmapToBase64(bitmapB);
        array.put("imageB", imageData);
        mService.faceCompare(getRequestBody(array))
                .enqueue(new BaseCallback<>(new FaceCompareEvent(uuid)));
        return uuid;
    }

    @Override
    public String modifyPersonName(String personId, String personName) {
        String uuid = UUIDGenerator.getUUID();

        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();

        array.put("person_id", personId);
        array.put("person_name", personName);
        mService.modifyPersonName(getRequestBody(array))
                .enqueue(new BaseCallback<>(new PersonModifyEvent(uuid)));
        return uuid;
    }

    @Override
    public String modifyPersonTag(String personId, String tag) {
        String uuid = UUIDGenerator.getUUID();

        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();

        array.put("person_id", personId);
        array.put("tag", tag);
        mService.modifyPersonName(getRequestBody(array))
                .enqueue(new BaseCallback<>(new PersonModifyEvent(uuid)));
        return uuid;
    }

    @Override
    public String delPerson(String personId) {
        String uuid = UUIDGenerator.getUUID();
        mService.delPerson(getRequestBody("person_id", personId))
                .enqueue(new BaseCallback<>(new DelPersonEvent(uuid)));
        return uuid;
    }

    @Override
    public String addFace(Bitmap bitmap, String personId) {
        String uuid = UUIDGenerator.getUUID();
        String imageData = null;
        try {
            imageData = BitmapUtils.bitmapToBase64(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] images = {imageData};
        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();
        array.put("person_id", personId);
        array.put("images", images);
        mService.addFaces(getRequestBody(array))
                .enqueue(new BaseCallback<>(new AddPersonEvent(uuid)));
        return uuid;
    }

    @Override
    public String addFaces(List<Bitmap> bitmapArr, String personId) throws IOException {
        String uuid = UUIDGenerator.getUUID();

        String[] images = new String[bitmapArr.size()];

        for (int i = 0; i < bitmapArr.size(); i++) {
            String imageData = BitmapUtils.bitmapToBase64(bitmapArr.get(i));
            images[i] = imageData;
        }

        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();
        array.put("person_id", personId);
        array.put("images", images);
        mService.addFaces(getRequestBody(array))
                .enqueue(new BaseCallback<>(new AddPersonEvent(uuid)));
        return uuid;
    }

    @Override
    public String detectFace(Bitmap bitmap, int mode) {
        String uuid = UUIDGenerator.getUUID();

        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();

        String imageData = null;
        try {
            imageData = BitmapUtils.bitmapToBase64(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        array.put("image", imageData);
        array.put("mode", mode);
        mService.detectFace(getRequestBody(array))
                .enqueue(new BaseCallback<>(new DetectFaceEvent(uuid)));
        return uuid;
    }

    @Override
    public String delFace(String personId, String faceId) {
        String uuid = UUIDGenerator.getUUID();

        String[] face_ids = {faceId};

        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();
        array.put("person_id", personId);
        array.put("face_ids", face_ids);
        mService.delFace(getRequestBody(array))
                .enqueue(new BaseCallback<>(new DelFaceEvent(uuid)));
        return uuid;
    }

    @Override
    public String getInfo(String personId) {
        String uuid = UUIDGenerator.getUUID();
        mService.getInfo(getRequestBody("person_id", personId))
                .enqueue(new BaseCallback<>(new GetInfoEvent(uuid)));
        return uuid;
    }


    private String getGroupId() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR); // 获取当前年份
        Print.e(UserInfo.getInstance().getIdentifier());
        return UserInfo.getInstance().getIdentifier();
    }

    private String[] getGroupIds() {
        String[] group_ids = {getGroupId()};
        return group_ids;
    }

    @NonNull
    private RequestBody getRequestBody(@NonNull String key, String value) {
        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, String> array = new ArrayMap<>();
        array.put(key, value);
        array.put("app_id", YoutuSign.APP_ID);
        String arrStr = GsonUtil.GsonString(array);
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), arrStr);
    }

    @NonNull
    private RequestBody getRequestBody(@NonNull ArrayMap<String, Object> array) {
        array.put("app_id", YoutuSign.APP_ID);
        String arrStr = GsonUtil.GsonString(array);
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), arrStr);
    }
}
