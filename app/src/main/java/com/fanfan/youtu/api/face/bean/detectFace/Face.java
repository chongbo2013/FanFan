package com.fanfan.youtu.api.face.bean.detectFace;

/**
 * Created by android on 2018/1/4.
 */

public class Face {

    @Override
    public String toString() {
        return "年龄 ： " + age +
                ", 性别 ： " + gender +
                ", 微笑 ： " + expression +
                ", 眼镜 ： " + glasses +
                ", 魅力 ： " + beauty;
    }

    private int x;              //人脸框左上角x
    private int y;              //人脸框左上角y
    private int height;         //人脸框高度
    private int width;          //人脸框宽度
    private int pitch;          //上下偏移[-30,30]
    private int roll;           //平面旋转[-180,180]
    private int yaw;            //左右偏移[-30,30]
    private int age;            //年龄[0~100]
    private int gender;         //性别[0/(female)~100(male)]
    private boolean glass;      //眼镜    原字段
    private int expression;     //微笑[0(normal)~50(smile)~100(laugh)]
    private int glasses;        //眼镜[0不戴眼镜 1戴眼镜 2戴墨镜] 注：替代原glass（Bool）字段
    private int mask;
    private int hat;
    private int beauty;         //魅力[0~100]
    private Face_shape face_shape;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public int getYaw() {
        return yaw;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isGlass() {
        return glass;
    }

    public void setGlass(boolean glass) {
        this.glass = glass;
    }

    public int getExpression() {
        return expression;
    }

    public void setExpression(int expression) {
        this.expression = expression;
    }

    public int getGlasses() {
        return glasses;
    }

    public void setGlasses(int glasses) {
        this.glasses = glasses;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public int getHat() {
        return hat;
    }

    public void setHat(int hat) {
        this.hat = hat;
    }

    public int getBeauty() {
        return beauty;
    }

    public void setBeauty(int beauty) {
        this.beauty = beauty;
    }

    public Face_shape getFace_shape() {
        return face_shape;
    }

    public void setFace_shape(Face_shape face_shape) {
        this.face_shape = face_shape;
    }
}
