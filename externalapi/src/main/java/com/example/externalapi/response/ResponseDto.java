package com.example.externalapi.response;

import java.util.List;

public class ResponseDto {
    private String status;
    private String message;
    private List<String> unifiedNationalNumbers;

    public ResponseDto() {}

    public ResponseDto(String status, String message, List<String> unifiedNationalNumbers) {
        this.status = status;
        this.message = message;
        this.unifiedNationalNumbers = unifiedNationalNumbers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getUnifiedNationalNumbers() {
        return unifiedNationalNumbers;
    }

    public void setUnifiedNationalNumbers(List<String> unifiedNationalNumbers) {
        this.unifiedNationalNumbers = unifiedNationalNumbers;
    }
}
