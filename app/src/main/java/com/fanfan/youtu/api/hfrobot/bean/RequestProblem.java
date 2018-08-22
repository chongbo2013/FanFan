package com.fanfan.youtu.api.hfrobot.bean;

/**
 * Created by Administrator on 2018/3/29/029.
 */

public class RequestProblem extends RobotMsg {


    /**
     * answerBean : {"id":"283","identifier":"user001","problem":"今天天气怎么样2","keyword":"今天天气怎么样2","answer":"","validate_date":"0000-00-00 00:00:00","status":"0"}
     */

    private String question;
    private AnswerBean answerBean;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public AnswerBean getAnswerBean() {
        return answerBean;
    }

    public void setAnswerBean(AnswerBean answerBean) {
        this.answerBean = answerBean;
    }

    public static class AnswerBean {
        /**
         * id : 283
         * identifier : user001
         * problem : 今天天气怎么样2
         * keyword : 今天天气怎么样2
         * answer :
         * validate_date : 0000-00-00 00:00:00
         * status : 0
         */

        private int id;
        private String identifier;
        private String problem;
        private String keyword;
        private String answer;
        private String validate_date;
        private String status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getProblem() {
            return problem;
        }

        public void setProblem(String problem) {
            this.problem = problem;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getValidate_date() {
            return validate_date;
        }

        public void setValidate_date(String validate_date) {
            this.validate_date = validate_date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
