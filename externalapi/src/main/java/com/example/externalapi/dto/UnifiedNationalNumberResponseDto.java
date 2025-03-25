package com.example.externalapi.dto;

import java.util.List;

public class UnifiedNationalNumberResponseDto {
    private List<String> unifiedNationalNumbers;

    public UnifiedNationalNumberResponseDto() {
    }

    public UnifiedNationalNumberResponseDto(List<String> unifiedNationalNumbers) {
        this.unifiedNationalNumbers = unifiedNationalNumbers;
    }

    public List<String> getUnifiedNationalNumbers() {
        return unifiedNationalNumbers;
    }

    public void setUnifiedNationalNumbers(List<String> unifiedNationalNumbers) {
        this.unifiedNationalNumbers = unifiedNationalNumbers;
    }
}
