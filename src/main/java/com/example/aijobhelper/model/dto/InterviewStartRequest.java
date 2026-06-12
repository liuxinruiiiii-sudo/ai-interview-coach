package com.example.aijobhelper.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 开始模拟面试 — 请求
 */
public class InterviewStartRequest {

    @NotBlank(message = "面试方向不能为空")
    private String category;

    private String level = "intermediate";

    public InterviewStartRequest() {}

    public InterviewStartRequest(String category, String level) {
        this.category = category;
        this.level = level;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
