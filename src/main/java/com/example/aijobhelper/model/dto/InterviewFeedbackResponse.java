package com.example.aijobhelper.model.dto;

/**
 * 模拟面试 — AI 评价反馈
 */
public class InterviewFeedbackResponse {

    private int score;
    private String strengths;
    private String weaknesses;
    private String referenceAnswer;
    private String followUpQuestion;
    private String suggestion;

    public InterviewFeedbackResponse() {}

    public InterviewFeedbackResponse(int score, String strengths, String weaknesses,
                                      String referenceAnswer, String followUpQuestion, String suggestion) {
        this.score = score;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.referenceAnswer = referenceAnswer;
        this.followUpQuestion = followUpQuestion;
        this.suggestion = suggestion;
    }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }
    public String getReferenceAnswer() { return referenceAnswer; }
    public void setReferenceAnswer(String referenceAnswer) { this.referenceAnswer = referenceAnswer; }
    public String getFollowUpQuestion() { return followUpQuestion; }
    public void setFollowUpQuestion(String followUpQuestion) { this.followUpQuestion = followUpQuestion; }
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
}
