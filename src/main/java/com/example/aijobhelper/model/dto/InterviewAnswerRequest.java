package com.example.aijobhelper.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 提交面试回答 — 请求
 */
public class InterviewAnswerRequest {

    @NotBlank(message = "题目不能为空")
    private String question;

    @NotBlank(message = "回答不能为空")
    private String userAnswer;

    @NotBlank(message = "方向不能为空")
    private String category;

    public InterviewAnswerRequest() {}

    public InterviewAnswerRequest(String question, String userAnswer, String category) {
        this.question = question;
        this.userAnswer = userAnswer;
        this.category = category;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
