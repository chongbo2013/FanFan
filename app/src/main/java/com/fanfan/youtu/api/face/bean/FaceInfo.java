package com.fanfan.youtu.api.face.bean;

import com.fanfan.youtu.api.base.bean.BaseError;

/**
 * Created by android on 2018/1/4.
 */

public class FaceInfo extends BaseError {

    private Face_info face_info;

    public Face_info getFace_info() {
        return face_info;
    }

    public void setFace_info(Face_info face_info) {
        this.face_info = face_info;
    }

    public class Face_info {

        private String face_id;
        private int x;
        private int y;
        private int height;
        private int width;
        private int pitch;
        private int roll;
        private int yaw;
        private int age;
        private int gender;
        private boolean glass;
        private int expression;

        public String getFace_id() {
            return face_id;
        }

        public void setFace_id(String face_id) {
            this.face_id = face_id;
        }

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
    }
}
