package com.example.aijobhelper.service;

import com.example.aijobhelper.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek API 调用服务
 */
@Service
public class DeepSeekService {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekService.class);

    private static final String SYSTEM_PROMPT =
            "你是一个专业的AI面试学习助手。请用中文回答用户的问题，" +
            "回答要准确、简洁、有条理。如果用户上传了简历或JD内容，" +
            "请结合这些内容提供针对性的分析和建议。";

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url}")
    private String baseUrl;

    @Value("${deepseek.model}")
    private String model;

    private final RestTemplate restTemplate;

    public DeepSeekService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 发送消息到 DeepSeek，返回 AI 回复
     *
     * @param userMessage 用户输入的问题
     * @return AI 回复文本
     */
    public String chat(String userMessage) {
        return chat(userMessage, null);
    }

    /**
     * 发送消息 + PDF 上下文到 DeepSeek，返回 AI 回复
     *
     * @param userMessage 用户输入的问题
     * @param pdfContext  PDF 解析文本（可为 null）
     * @return AI 回复文本
     */
    public String chat(String userMessage, String pdfContext) {
        String url = baseUrl + "/chat/completions";

        // 构建消息列表
        List<Map<String, String>> messages = buildMessages(userMessage, pdfContext);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 4096);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info("调用 DeepSeek API: model={}, messageLength={}", model, userMessage.length());

        try {
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
                log.error("DeepSeek API 返回非 2xx: {}", responseEntity.getStatusCode());
                throw new BusinessException("AI 服务暂时不可用，请稍后重试");
            }

            // 解析 choices[0].message.content
            Map<String, Object> body = responseEntity.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new BusinessException("AI 服务返回空结果");
            }

            Map<String, Object> choice = choices.get(0);
            Map<String, String> message = (Map<String, String>) choice.get("message");
            String content = message.get("content");

            log.info("DeepSeek 返回成功: {} 字符", content != null ? content.length() : 0);
            return content;

        } catch (RestClientException e) {
            log.error("调用 DeepSeek API 失败", e);
            throw new BusinessException("调用 AI 服务失败: " + e.getMessage());
        }
    }

    /**
     * 构建消息列表——当有 PDF 上下文时，将其拼入 system prompt
     */
    private List<Map<String, String>> buildMessages(String userMessage, String pdfContext) {
        String systemContent = SYSTEM_PROMPT;
        if (pdfContext != null && !pdfContext.isBlank()) {
            systemContent += "\n\n以下是从用户上传的 PDF 文件中提取的内容，请参考：\n\n" + pdfContext;
        }

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemContent);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);

        return List.of(systemMessage, userMsg);
    }
}
