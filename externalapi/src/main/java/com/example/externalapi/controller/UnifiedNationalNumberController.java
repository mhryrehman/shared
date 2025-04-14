package com.example.externalapi.controller;

import com.example.externalapi.dto.UnifiedNationalNumberResponseDto;
import com.example.externalapi.response.ResponseDto;
import com.example.externalapi.service.UnifiedNationalNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/numbers")
public class UnifiedNationalNumberController {

    @Autowired
    private UnifiedNationalNumberService service;

//    @GetMapping
//    public ResponseEntity<UnifiedNationalNumberResponseDto> getUnifiedNationalNumbers(@RequestParam String userId) {
//        UnifiedNationalNumberResponseDto responseData = service.fetchNumbers(userId);
//
//        ResponseDto<UnifiedNationalNumberResponseDto> response = new ResponseDto<>(
//                "success",
//                "Unified national numbers fetched successfully",
//                responseData
//        );
//
//        return ResponseEntity.ok(responseData);
//    }

    @GetMapping
    public ResponseEntity<ResponseDto> getUnifiedNationalNumbers(@RequestParam(required = false) String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ResponseDto("error", "userId must not be null or empty", null)
                );
            }

            UnifiedNationalNumberResponseDto responseData = service.fetchNumbers(userId);

            ResponseDto response = new ResponseDto(
                    "success",
                    "Unified national numbers fetched successfully",
                    responseData.getUnifiedNationalNumbers()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ResponseDto("error", "Failed to fetch unified national numbers", null)
            );
        }
    }



}
