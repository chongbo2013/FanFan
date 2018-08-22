package com.fanfan.youtu.api.face.bean.detectFace;

import java.util.List;

/**
 * Created by android on 2018/1/4.
 */

public class Face_shape {

    private List<Face_profile> face_profile;
    private List<Left_eye> left_eye;
    private List<Right_eye> right_eye;
    private List<Left_eyebrow> left_eyebrow;
    private List<Right_eyebrow> right_eyebrow;
    private List<Mouth> mouth;
    private List<Nose> nose;

    class Face_profile {
        private int x;
        private int y;
    }

    class Left_eye {
        private int x;
        private int y;
    }

    class Right_eye {
        private int x;
        private int y;
    }

    class Left_eyebrow {
        private int x;
        private int y;
    }

    class Right_eyebrow {
        private int x;
        private int y;
    }

    class Mouth {
        private int x;
        private int y;
    }

    class Nose {
        private int x;
        private int y;
    }


}
