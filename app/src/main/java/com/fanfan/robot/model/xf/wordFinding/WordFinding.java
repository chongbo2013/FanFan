package com.fanfan.robot.model.xf.wordFinding;

import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/12/6.
 */

public class WordFinding {

    private List<String> antonym;
    private String source;
    private List<String> synonym;
    private String word;

    public List<String> getAntonym() {
        return antonym;
    }

    public void setAntonym(List<String> antonym) {
        this.antonym = antonym;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getSynonym() {
        return synonym;
    }

    public void setSynonym(List<String> synonym) {
        this.synonym = synonym;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
