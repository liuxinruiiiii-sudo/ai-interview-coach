package com.example.aijobhelper.controller;

import com.example.aijobhelper.model.dto.AskWithFileResponse;
import com.example.aijobhelper.model.dto.ChatRequest;
import com.example.aijobhelper.model.dto.ChatResponse;
import com.example.aijobhelper.model.dto.UploadResult;
import com.example.aijobhelper.model.vo.ApiResult;
import com.example.aijobhelper.service.DeepSeekService;
import com.example.aijobhelper.service.PdfService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 对话接口 —— 支持文本问答 & PDF 上传解析
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final PdfService pdfService;
    private final DeepSeekService deepSeekService;

    public ChatController(PdfService pdfService, DeepSeekService deepSeekService) {
        this.pdfService = pdfService;
        this.deepSeekService = deepSeekService;
    }

    /**
     * 纯文本问答 —— 直接调用 DeepSeek 获取回复
     *
     * <pre>
     * POST /api/chat/message
     * Content-Type: application/json
     * {"message": "请介绍一下Spring Boot的核心特性"}
     * </pre>
     */
    @PostMapping("/message")
    public ApiResult<ChatResponse> message(@Valid @RequestBody ChatRequest request) {
        String answer = deepSeekService.chat(request.getMessage());
        return ApiResult.ok(new ChatResponse(answer));
    }

    /**
     * 上传 PDF 文件并解析文本
     *
     * <pre>
     * curl -X POST http://localhost:8080/api/chat/upload \
     *   -F "file=@简历.pdf"
     * </pre>
     */
    @PostMapping("/upload")
    public ApiResult<UploadResult> upload(@RequestParam("file") MultipartFile file) {
        String text = pdfService.extractText(file);
        UploadResult result = new UploadResult(file.getOriginalFilename(), text.length(), text);
        return ApiResult.ok("PDF 解析成功", result);
    }

    /**
     * 上传 PDF + 提问 —— 解析 PDF 文本，拼接 Prompt，调用 DeepSeek 获取回复
     *
     * <pre>
     * curl -X POST http://localhost:8080/api/chat/ask-with-file \
     *   -F "file=@简历.pdf" \
     *   -F "message=这份简历有哪些亮点？"
     * </pre>
     */
    @PostMapping("/ask-with-file")
    public ApiResult<AskWithFileResponse> askWithFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("message") String message) {

        // 1. 提取 PDF 文本
        String pdfText = pdfService.extractText(file);

        // 2. 拼接 Prompt
        String prompt = String.format("""
                请根据以下 PDF 内容回答用户问题。

                【PDF内容】
                %s

                【用户问题】
                %s""", pdfText, message);

        // 3. 调用 DeepSeek
        String answer = deepSeekService.chat(prompt);

        // 4. 返回
        return ApiResult.ok(new AskWithFileResponse(answer, pdfText.length()));
    }
}
