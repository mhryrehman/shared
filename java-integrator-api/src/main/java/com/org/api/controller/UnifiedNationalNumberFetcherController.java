package com.org.api.controller;

import com.org.api.dto.UnifiedNationalNumberDTO;
import com.org.api.response.ResponseDTO;
import com.org.api.service.UnifiedNationalNumberFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/numbers")
public class UnifiedNationalNumberFetcherController {

    @Autowired
    private UnifiedNationalNumberFetcherService service;

    @GetMapping
    public ResponseEntity<ResponseDTO> getUnifiedNationalNumbers(@RequestParam(required = false) String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ResponseDTO("error", "userId must not be null or empty", null)
                );
            }

            UnifiedNationalNumberDTO responseData = service.getUnifiedNationalNumbers(userId);

            ResponseDTO response = new ResponseDTO(
                    "success",
                    "Unified national numbers fetched successfully",
                    responseData.getUnifiedNationalNumbers()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ResponseDTO("error", "Failed to fetch unified national numbers", null)
            );
        }
    }

}
