package com.example.shiftmate.controller;

import com.example.shiftmate.dto.ShiftRequestDTO;
import com.example.shiftmate.service.ShiftRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shift-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3030")
public class ShiftRequestController {

    private final ShiftRequestService shiftRequestService;

    // 申し込み
    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> applyShift(
            @RequestBody Map<String, Long> requestBody,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        Long shiftNumber = requestBody.get("shiftNumber");
        Long userNumber = (Long) request.getAttribute("userNumber");

        ShiftRequestDTO shiftRequest = shiftRequestService.applyShift(shiftNumber, userNumber);
        response.put("success", true);
        response.put("message", "シフトの申し込みができました。");
        response.put("request", shiftRequest);
        return ResponseEntity.ok(response);
    }

    // シフトの申し込み　処理
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processRequest(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Long requestNumber = ((Number) requestBody.get("requestNumber")).longValue();
        String status = (String) requestBody.get("status");
        Long processedByUserNumber = ((Long) request.getAttribute("userNumber"));

        ShiftRequestDTO shiftRequest = shiftRequestService.processRequest(
                requestNumber, status, processedByUserNumber);
        response.put("success", true);
        response.put("message", "申し込みが処理されました。");
        response.put("request", shiftRequest);
        return ResponseEntity.ok(response);
    }

    // 全てのシフトの申し込み照会
    @GetMapping("/shift/{shiftNumber}")
    public ResponseEntity<Map<String, Object>> getShiftRequests(@PathVariable Long shiftNumber) {
        Map<String, Object> response = new HashMap<>();
        List<ShiftRequestDTO> requests = shiftRequestService.getShiftRequests(shiftNumber);
        response.put("success", true);
        response.put("requests", requests);
        return ResponseEntity.ok(response);
    }

    // ユーザーの全ての申し込み照会
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserRequests(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Long userNumber = (Long) request.getAttribute("userNumber");
        List<ShiftRequestDTO> requests = shiftRequestService.getUserRequests(userNumber);
        response.put("success", true);
        response.put("requests", requests);
        return ResponseEntity.ok(response);
    }



}
