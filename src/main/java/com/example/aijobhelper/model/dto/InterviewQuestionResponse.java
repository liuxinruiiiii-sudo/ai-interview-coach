package com.example.aijobhelper.model.dto;

/**
 * 模拟面试 — AI 生成的题目
 */
public class InterviewQuestionResponse {

    private String questionId;
    private String category;
    private String question;
    private String tips;

    public InterviewQuestionResponse() {}

    public InterviewQuestionResponse(String questionId, String category, String question, String tips) {
        this.questionId = questionId;
        this.category = category;
        this.question = question;
        this.tips = tips;
    }

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getTips() { return tips; }
    public void setTips(String tips) { this.tips = tips; }
}
