package com.fanfan.robot.model.hotword;

import java.util.List;

/**
 * Created by android on 2017/12/29.
 */

public class Userword {

    private String name;
    private List<String> words;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public List<String> getWords() {
        return words;
    }


}
