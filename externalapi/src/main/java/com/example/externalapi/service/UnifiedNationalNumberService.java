package com.example.externalapi.service;

import com.example.externalapi.dto.UnifiedNationalNumberResponseDto;
import com.example.externalapi.repository.UnifiedNationalNumberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnifiedNationalNumberService {

    @Autowired
    private UnifiedNationalNumberRepository repository;

    public UnifiedNationalNumberResponseDto fetchNumbers(String userId) {
        List<String> numbers = repository.getUnifiedNationalNumbers(userId);
        return new UnifiedNationalNumberResponseDto(numbers);
    }
}
