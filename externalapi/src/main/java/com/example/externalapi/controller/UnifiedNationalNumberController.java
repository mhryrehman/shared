package com.example.externalapi.controller;

import com.example.externalapi.dto.UnifiedNationalNumberResponseDto;
import com.example.externalapi.response.ResponseDto;
import com.example.externalapi.service.UnifiedNationalNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/numbers")
public class UnifiedNationalNumberController {

    @Autowired
    private UnifiedNationalNumberService service;

    @GetMapping
    public ResponseEntity<ResponseDto<UnifiedNationalNumberResponseDto>> getUnifiedNationalNumbers() {
        UnifiedNationalNumberResponseDto responseData = service.fetchNumbers();

        ResponseDto<UnifiedNationalNumberResponseDto> response = new ResponseDto<>(
                "success",
                "Unified national numbers fetched successfully",
                responseData
        );

        return ResponseEntity.ok(response);
    }
}
