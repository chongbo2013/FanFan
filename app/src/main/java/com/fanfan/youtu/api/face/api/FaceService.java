package com.fanfan.youtu.api.face.api;

import com.fanfan.youtu.api.face.bean.AddFace;
import com.fanfan.youtu.api.face.bean.DelFace;
import com.fanfan.youtu.api.face.bean.Delperson;
import com.fanfan.youtu.api.face.bean.FaceCompare;
import com.fanfan.youtu.api.face.bean.FaceIdentify;
import com.fanfan.youtu.api.face.bean.FaceIds;
import com.fanfan.youtu.api.face.bean.FaceInfo;
import com.fanfan.youtu.api.face.bean.FacePersonid;
import com.fanfan.youtu.api.face.bean.FaceShape;
import com.fanfan.youtu.api.face.bean.Faceverify;
import com.fanfan.youtu.api.face.bean.GetInfo;
import com.fanfan.youtu.api.face.bean.GroupIds;
import com.fanfan.youtu.api.face.bean.Newperson;
import com.fanfan.youtu.api.face.bean.PersonModify;
import com.fanfan.youtu.api.face.bean.detectFace.DetectFace;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by android on 2017/12/21.
 */

public interface FaceService {

    /**
     * 获取组列表
     * 获取一个AppId下所有group列表
     *
     * @param body
     * @return
     */
    @POST("youtu/api/getgroupids")
    Call<GroupIds> getGroupids(@Body RequestBody body);

    /**
     * 获取人列表
     * 获取一个组Group中所有person列表
     * 必须	group_id	String	组id
     *
     * @param body
     * @return
     */
    @POST("youtu/api/getpersonids")
    Call<FacePersonid> getPersonids(@Body RequestBody body);

    /**
     * 获取人脸列表
     * 获取一个组person中所有face列表
     * 必须	person_id	String	个体id
     *
     * @param body
     * @return
     */
    @POST("youtu/api/getfaceids")
    Call<FaceIds> getFaceids(@Body RequestBody body);

    /**
     * 获取人脸信息
     * 获取一个face的相关特征信息
     * 必须	face_id	String	人脸id
     *
     * @param body
     * @return
     */
    @POST("youtu/api/getfaceinfo")
    Call<FaceInfo> getFaceinfo(@Body RequestBody body);

    /**
     * 人脸验证
     * 给定一个Face和一个Person，返回是否是同一个人的判断以及置信度。
     * 必须	person_id	String	待验证的Person
     * 可选	image	String(Bytes)	base64编码的二进制图片数据
     * 可选	url	String	图片的image 和url只提供一个就可以了,如果都提供,只使用url
     *
     * @param body
     * @return
     */
    @POST("youtu/api/faceverify")
    Call<Faceverify> faceVerify(@Body RequestBody body);

    /**
     * 人脸识别
     * 对于一个待识别的人脸图片，在一个Group中识别出最相似的Top5 Person作为其身份返回，返回的Top5中按照相似度从大到小排列。
     * 必须	group_id	String	候选人组id
     * 可选	image	String(Bytes)	base64编码的二进制图片数据
     * 可选	url	String	图片的url, image 和url只提供一个就可以了,如果都提供,只使用url
     *
     * @param body
     * @return
     */
    @POST("youtu/api/faceidentify")
    Call<FaceIdentify> faceIdentify(@Body RequestBody body);

    /**
     * 个体创建
     * 创建一个Person，并将Person放置到group_ids指定的组当中
     * 必须	group_ids	Array(String)	加入到组的列表
     * 必须	person_id	String	指定的个体id
     * 可选	image	String(Bytes)	base64编码的二进制图片数据
     * 可选	url	String	图片的url, image 和url只提供一个就可以了,如果都提供,只使用url
     * 可选	person_name	String	名字
     * 可选	tag	String	备注信息
     *
     * @param body
     * @return
     */
    @POST("youtu/api/newperson")
    Call<Newperson> newPerson(@Body RequestBody body);

    /**
     * 人脸对比
     * 计算两个Face的相似性以及五官相似度。
     * 可选	imageA	String	使用base64编码的二进制图片数据A
     * 可选	imageB	String	使用base64编码的二进制图片数据B
     * 可选	urlA	String	A图片的url, image 和url只提供一个就可以了,如果都提供,只使用url
     * 可选	urlB	String	B图片的url, image 和url只提供一个就可以了,如果都提供,只使用url
     *
     * @param body
     * @return
     */
    @POST("youtu/api/facecompare")
    Call<FaceCompare> faceCompare(@Body RequestBody body);

    /**
     * 设置信息
     * 设置Person的name.
     * 必须	person_id	String	相应person的id
     * 可选	person_name	String	新的name
     * 可选	tag	String	备注信息
     *
     * @param body
     * @return
     */
    @POST("youtu/api/setinfo")
    Call<PersonModify> modifyPersonName(@Body RequestBody body);

    /**
     * 删除个体
     * 删除一个Person
     * 必须	person_id	String	待删除个体ID
     *
     * @param body
     * @return
     */
    @POST("youtu/api/delperson")
    Call<Delperson> delPerson(@Body RequestBody body);

    /**
     * 增加人脸
     * 将一组Face加入到一个Person中。注意，一个Face只能被加入到一个Person中；一个Person最多允许包含20个Face；
     * 一次调用最多加入5个Face；并且加入与库中几乎相同的人脸会返回错误。
     *
     * @param body
     * @return
     */
    @POST("youtu/api/addface")
    Call<AddFace> addFaces(@Body RequestBody body);

    /**
     * 人脸检测
     * 检测给定图片(Image)中的所有人脸(Face)的位置和相应的面部属性。位置包括(x,y,w,h)，面部属性包括性别(gender)，年龄(age)，
     * 表情(expression),魅力(beauty)，眼镜(glasses)和姿态(pitch，roll，yaw)，至多返回5个人脸的属性。
     * 可选	image	String(Bytes)	base64编码的二进制图片数据
     * 可选	url	    String	        图片的url,image和url只提供一个就可以了,如果都提供,只使用url
     * 可选	mode	Int	            检测模式0/1正常/大脸模式
     *
     * @param body
     * @return
     */
    @POST("youtu/api/detectface")
    Call<DetectFace> detectFace(@Body RequestBody body);

    /**
     * 五官定位
     * 对请求图片进行五官定位，计算构成人脸轮廓的88个点，包括眉毛（左右各8点）、眼睛（左右各8点）、鼻子（13点）、嘴巴（22点）、脸型轮廓（21点）。
     * 各个部分点的顺序如图 faceshape.jpg
     * 可选	image	String(Bytes)	base64编码的二进制图片数据
     * 可选	url	String	图片的url，image和url只提供一个就可以了，如果都提供，只使用url
     * 可选	mode	Int	检测模式 0 正常（默认）/ 1 大脸模式
     *
     * @param body
     * @return
     */
    @POST("youtu/api/faceshape")
    Call<FaceShape> faceShape(@Body RequestBody body);

    /**
     * 删除人脸
     * 删除一个person下的face，包括特征，属性和face_id.
     * 必须  app_id	String	App的 App ID, 请在应用管理中心添加应用后获取
     * 必须  person_id	String	待删除人脸的person ID
     * 必须  face_ids	Array(String)	删除人脸id的列表
     *
     * @param body
     * @return
     */
    @POST("youtu/api/delface")
    Call<DelFace> delFace(@Body RequestBody body);

    /**
     * 获取信息
     * 获取一个Person的信息, 包括name, id, tag, 相关的face, 以及groups等信息。
     * 必须	person_id	String	待查询个体的ID
     *
     * @param body
     * @return
     */
    @POST("youtu/api/getinfo")
    Call<GetInfo> getInfo(@Body RequestBody body);

    /**
     * 多人脸检索
     * 上传人脸图片，进行多人脸检索。
     * 头部信息
     * Host	是	String	图片云服务器域名，固定为vip-api.youtu.qq.com
     * Content-Length	是	Int32	整个请求包体内容的总长度，单位：字节（Byte）。
     * Content-Type	是	String	text/json表示json格式，application/x-protobuf表示pb
     * Authorization	是	String	多次有效签名,用于鉴权， 具体生成方式详见 鉴权签名方法
     * 请求包体
     * 二选一	group_id	String	检索的groupid
     * 二选一	group_ids	Array(String)	检索的groupid
     * 可选	topn	Int32	候选人脸数量，一般使用默认值5
     * 可选	min_size	String	人脸检测最小尺寸，一般使用默认值40
     * 可选	image	String(bytes)	base64编码的二进制图片数据
     * 可选	url	String	图片的url, image 和url只提供一个,如果都提供,只使用url
     * 可选	session_id	String	Session id
     *
     * @return
     */
    @POST("youtu/api/multifaceidentify")
    Call<Object> multiFaceIdentify();
}
