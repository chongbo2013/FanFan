package com.fanfan.youtu.api.face.api;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.List;

/**
 * Created by android on 2017/12/21.
 */

public interface FaceAPI {

    /**
     * 获取一个AppId下所有group列表
     *
     * @return
     */
    String getGroupids();

    /**
     * 获取一个组Group中所有person列表
     *
     * @return
     */
    String getPersonids();

    /**
     * 获取一个组person中所有face列表
     *
     * @param personId
     * @return
     */
    String getFaceids(String personId);

    /**
     * 获取一个face的相关特征信息
     *
     * @param faceId
     * @return
     */
    String getFaceinfo(String faceId);

    /**
     * 给定一个Face和一个Person，返回是否是同一个人的判断以及置信度。
     *
     * @param personId
     * @param bitmap
     * @return
     */
    String faceverify(String personId, Bitmap bitmap);

    /**
     * 对于一个待识别的人脸图片，在一个Group中识别出最相似的Top5 Person作为其身份返回，返回的Top5中按照相似度从大到小排列。
     *
     * @param bitmap
     * @return
     * @throws IOException
     */
    String faceIdentify(Bitmap bitmap) throws IOException;

    /**
     * 创建一个Person，并将Person放置到group_ids指定的组当中
     *
     * @param bitmap
     * @param personId
     * @return
     */
    String newPerson(Bitmap bitmap, String personId);

    String newPerson(Bitmap bitmap, String personId, String personName);

    /**
     * 计算两个Face的相似性以及五官相似度。
     *
     * @param bitmapA
     * @param bitmapB
     * @return
     * @throws IOException
     */
    String faceCompare(Bitmap bitmapA, Bitmap bitmapB) throws IOException;

    /**
     * 设置个体名字
     *
     * @param personId
     * @param personName
     * @return
     */
    String modifyPersonName(String personId, String personName);

    /**
     * 设置个体 tag
     *
     * @param personId
     * @param tag
     * @return
     */
    String modifyPersonTag(String personId, String tag);

    /**
     * 删除一个Person
     *
     * @param personId
     * @return
     */
    String delPerson(String personId);

    /**
     * 添加一个人脸
     *
     * @param bitmap
     * @param personId
     * @return
     * @throws IOException
     */
    String addFace(Bitmap bitmap, String personId);

    /**
     * 添加一组人脸
     *
     * @param bitmapArr
     * @param personId
     * @return
     * @throws IOException
     */
    String addFaces(List<Bitmap> bitmapArr, String personId) throws IOException;

    /**
     * @param bitmap base64编码的二进制图片数据
     * @param mode   检测模式0/1正常/大脸模式
     * @return
     */
    String detectFace(Bitmap bitmap, int mode);

    /**
     * 删除一个person下的face，包括特征，属性和face_id.
     *
     * @param personId 待删除人脸的person ID
     * @param faceId   删除人脸id
     * @return
     */
    String delFace(String personId, String faceId);

    /**
     * 获取个体信息
     *
     * @param personId
     * @return
     */
    String getInfo(String personId);
}