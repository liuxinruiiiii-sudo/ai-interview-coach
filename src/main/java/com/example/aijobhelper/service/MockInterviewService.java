package com.example.aijobhelper.service;

import com.example.aijobhelper.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 模拟面试服务 —— 复用 DeepSeekService 生成题目 & 评价回答
 */
@Service
public class MockInterviewService {

    private static final Logger log = LoggerFactory.getLogger(MockInterviewService.class);

    private final DeepSeekService deepSeekService;

    public MockInterviewService(DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    /**
     * 根据方向生成一道面试题
     */
    public String generateQuestion(String category, String level) {
        String prompt = String.format("""
                你是一名资深 Java 面试官。请根据以下要求出一道面试题。

                方向：%s
                难度：%s

                要求：
                1. 题目要具体，不要泛泛而谈
                2. 如果能包含代码场景更好
                3. 同时给出 1-2 条答题提示（tips）
                4. 不要输出任何其他内容，只输出题目和提示
                5. 格式：
                题目：xxx
                提示：xxx""", category, level);

        String result = deepSeekService.chat(prompt);
        if (result == null || result.isBlank()) {
            throw new BusinessException("AI 生成题目失败，请重试");
        }
        log.info("生成面试题: category={}, level={}, len={}", category, level, result.length());
        return result;
    }

    /**
     * 对用户的回答进行评价
     */
    public String evaluateAnswer(String question, String userAnswer, String category) {
        String prompt = String.format("""
                你是一名资深 Java 面试官。请对以下回答进行专业评价。

                面试方向：%s
                原题：%s
                用户回答：%s

                请按以下格式输出（严格遵守，不要输出其他内容）：

                评分：[0-100的数字]
                亮点：[分析回答的优点，1-2句话]
                不足：[指出存在的问题，1-2句话]
                参考答案：[给出一个标准参考答案]
                追问：[给出一道相关的追问题目]
                建议：[给出学习建议]""", category, question, userAnswer);

        String result = deepSeekService.chat(prompt);
        if (result == null || result.isBlank()) {
            throw new BusinessException("AI 评价失败，请重试");
        }
        log.info("评价回答: category={}, answerLen={}, resultLen={}", category, userAnswer.length(), result.length());
        return result;
    }

    /**
     * 生成唯一 questionId
     */
    public String generateQuestionId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
