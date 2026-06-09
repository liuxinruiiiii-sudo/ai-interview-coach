package com.example.aijobhelper.model.dto;

/**
 * PDF + 问答 响应 DTO
 */
public class AskWithFileResponse {

    private String answer;
    private int pdfLength;

    public AskWithFileResponse() {
    }

    public AskWithFileResponse(String answer, int pdfLength) {
        this.answer = answer;
        this.pdfLength = pdfLength;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getPdfLength() {
        return pdfLength;
    }

    public void setPdfLength(int pdfLength) {
        this.pdfLength = pdfLength;
    }
}
