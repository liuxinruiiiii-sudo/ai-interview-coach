package com.example.aijobhelper.controller;

import com.example.aijobhelper.model.dto.InterviewAnswerRequest;
import com.example.aijobhelper.model.dto.InterviewFeedbackResponse;
import com.example.aijobhelper.model.dto.InterviewQuestionResponse;
import com.example.aijobhelper.model.dto.InterviewStartRequest;
import com.example.aijobhelper.model.vo.ApiResult;
import com.example.aijobhelper.service.MockInterviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟面试接口
 */
@RestController
@RequestMapping("/api/interview")
public class MockInterviewController {

    private final MockInterviewService mockInterviewService;

    public MockInterviewController(MockInterviewService mockInterviewService) {
        this.mockInterviewService = mockInterviewService;
    }

    /**
     * 开始模拟面试 — 生成一道面试题
     */
    @PostMapping("/start")
    public ApiResult<InterviewQuestionResponse> start(@Valid @RequestBody InterviewStartRequest request) {
        String raw = mockInterviewService.generateQuestion(request.getCategory(), request.getLevel());
        String questionId = mockInterviewService.generateQuestionId();

        // 解析 AI 返回：题目：xxx  提示：xxx
        String question = extractField(raw, "题目");
        String tips = extractField(raw, "提示");

        InterviewQuestionResponse resp = new InterviewQuestionResponse(
                questionId, request.getCategory(), question, tips);
        return ApiResult.ok(resp);
    }

    /**
     * 提交回答 — AI 评价并返回反馈
     */
    @PostMapping("/answer")
    public ApiResult<InterviewFeedbackResponse> answer(@Valid @RequestBody InterviewAnswerRequest request) {
        String raw = mockInterviewService.evaluateAnswer(
                request.getQuestion(), request.getUserAnswer(), request.getCategory());

        // 解析 AI 返回的各字段
        int score = parseScore(extractField(raw, "评分"));
        String strengths = extractField(raw, "亮点");
        String weaknesses = extractField(raw, "不足");
        String referenceAnswer = extractField(raw, "参考答案");
        String followUpQuestion = extractField(raw, "追问");
        String suggestion = extractField(raw, "建议");

        InterviewFeedbackResponse resp = new InterviewFeedbackResponse(
                score, strengths, weaknesses, referenceAnswer, followUpQuestion, suggestion);
        return ApiResult.ok(resp);
    }

    // ── 简单的字段解析工具 ──

    /**
     * 从文本中提取指定标签后的内容（标签格式：标签名：内容）
     */
    private String extractField(String text, String label) {
        if (text == null) return "";
        // 匹配 "标签名：内容" 直到下一个标签或文本结尾
        String[] lines = text.split("\\n");
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith(label + "：") || line.startsWith(label + ":")) {
                found = true;
                String val = line.substring((label + "：").length()).trim();
                if (val.isEmpty()) val = line.substring((label + ":").length()).trim();
                sb.append(val);
            } else if (found && !line.isEmpty() && !line.contains("：") && !line.contains(":")) {
                // 续行
                sb.append("\n").append(line);
            } else if (found) {
                break; // 遇到下一个标签
            }
        }
        return sb.toString().trim();
    }

    /**
     * 从字符串中提取数字分数
     */
    private int parseScore(String text) {
        if (text == null || text.isBlank()) return 70;
        // 提取数字
        String num = text.replaceAll("[^0-9]", "");
        if (num.isEmpty()) return 70;
        try {
            int s = Integer.parseInt(num);
            return Math.max(0, Math.min(100, s));
        } catch (NumberFormatException e) {
            return 70;
        }
    }
}
