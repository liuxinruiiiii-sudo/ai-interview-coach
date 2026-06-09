package com.example.aijobhelper.model.dto;

/**
 * PDF 上传解析结果
 */
public class UploadResult {

    private String filename;
    private int charCount;
    private String text;

    public UploadResult() {
    }

    public UploadResult(String filename, int charCount, String text) {
        this.filename = filename;
        this.charCount = charCount;
        this.text = text;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getCharCount() {
        return charCount;
    }

    public void setCharCount(int charCount) {
        this.charCount = charCount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
