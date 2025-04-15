package com.org.api.dto;

import java.util.List;

public class UnifiedNationalNumberDTO {
    private List<String> unifiedNationalNumbers;

    public UnifiedNationalNumberDTO() {
    }

    public UnifiedNationalNumberDTO(List<String> unifiedNationalNumbers) {
        this.unifiedNationalNumbers = unifiedNationalNumbers;
    }

    public List<String> getUnifiedNationalNumbers() {
        return unifiedNationalNumbers;
    }

    public void setUnifiedNationalNumbers(List<String> unifiedNationalNumbers) {
        this.unifiedNationalNumbers = unifiedNationalNumbers;
    }
}
