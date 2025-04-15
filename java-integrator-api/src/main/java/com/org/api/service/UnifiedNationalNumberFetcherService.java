package com.org.api.service;

import com.org.api.dto.UnifiedNationalNumberDTO;
import com.org.api.repository.UnifiedNationalNumberFetcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnifiedNationalNumberFetcherService {

    @Autowired
    private UnifiedNationalNumberFetcherRepository repository;

    public UnifiedNationalNumberDTO getUnifiedNationalNumbers(String userId) {
        List<String> numbers = repository.fetchUnifiedNationalNumbers(userId);
        return new UnifiedNationalNumberDTO(numbers);
    }
}
