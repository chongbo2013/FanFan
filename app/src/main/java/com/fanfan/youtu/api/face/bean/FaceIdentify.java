package com.fanfan.youtu.api.face.bean;

import android.support.annotation.NonNull;

import com.fanfan.youtu.api.base.bean.BaseError;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by android on 2017/12/21.
 */

public class FaceIdentify extends BaseError implements Serializable {

    public String session_id;
    public ArrayList<IdentifyItem> candidates;

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public ArrayList<IdentifyItem> getCandidates() {
        return candidates;
    }

    public void setCandidates(ArrayList<IdentifyItem> candidates) {
        this.candidates = candidates;
    }


    public class IdentifyItem implements Serializable, Comparable<IdentifyItem> {

        private String person_id;
        private String face_id;
        private float confidence;
        private String tag;

        public String getPerson_id() {
            return person_id;
        }

        public void setPerson_id(String person_id) {
            this.person_id = person_id;
        }

        public String getFace_id() {
            return face_id;
        }

        public void setFace_id(String face_id) {
            this.face_id = face_id;
        }

        public float getConfidence() {
            return confidence;
        }

        public void setConfidence(float confidence) {
            this.confidence = confidence;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else {
                if (obj instanceof IdentifyItem) {
                    IdentifyItem identifyItem = (IdentifyItem) obj;
                    if (identifyItem.person_id.equals(this.person_id)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public int compareTo(@NonNull IdentifyItem identifyItem) {
            if (this.getConfidence() - identifyItem.getConfidence() >= 0) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}