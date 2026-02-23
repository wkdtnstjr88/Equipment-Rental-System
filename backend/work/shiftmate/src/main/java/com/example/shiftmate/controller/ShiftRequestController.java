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
@RequestMapping("api/shift-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:3030")
public class ShiftRequestController {
    private final ShiftRequestService shiftRequestService;

    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> applyShift(@RequestBody Map<String, Long> requestBody, HttpServletRequest request){
        Map<String, Object> response= new HashMap<>();
        Long shiftNumber = requestBody.get("shiftNumber");
        Long userNumber= (Long) request.getAttribute("userNumber");
        ShiftRequestDTO shiftRequest=shiftRequestService.applyShift(shiftNumber, userNumber);
        response.put("success", true);
        response.put("message", "シフトの申し込みができました。");
        response.put("request", shiftRequest);
        return ResponseEntity.ok(response);}
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateShift(@RequestBody Map<String, Long> requestBody, HttpServletRequest request){
        Map<String, Object> response= new HashMap<>();
        Long requestNumber = requestBody.get("requestNumber");
        Long newShiftNumber = requestBody.get("newShiftNumber");
        Long userNumber= (Long) request.getAttribute("userNumber");
        ShiftRequestDTO shiftRequest=shiftRequestService.updateShiftRequest(requestNumber, newShiftNumber, userNumber);
        response.put("success", true);
        response.put("message", "シフトの修正ができました。");
        response.put("request", shiftRequest);
        return ResponseEntity.ok(response);}
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteShift(@RequestBody Map<String, Long> requestBody, HttpServletRequest request){
        Map<String, Object> response= new HashMap<>();
        Long requestNumber = requestBody.get("requestNumber");
        Long userNumber= (Long) request.getAttribute("userNumber");
        shiftRequestService.deleteShiftRequest(requestNumber, userNumber);
        response.put("success", true);
        response.put("message", "シフト申請をキャンセルしました。");
        return ResponseEntity.ok(response);}
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processRequest(@RequestBody Map<String, Object> requestBody, HttpServletRequest request){
        Map<String, Object> response=new HashMap<>();
        Long requestNumber=((Number)requestBody.get("requestNumber")).longValue();
        String status=(String) requestBody.get("status");
        Long processedByUserNumber=((Long) request.getAttribute("userNumber"));
        ShiftRequestDTO shiftRequest=shiftRequestService.processRequest(requestNumber, status, processedByUserNumber);
        response.put("success", true);
        response.put("message", "シフトの申し込みが処理されきました。");
        response.put("request", shiftRequest);
        return ResponseEntity.ok(response);}
    @DeleteMapping("/manager/emergency")
    public ResponseEntity<Map<String, Object>> emergencyDelete(@RequestBody Map<String, Long> requestBody, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Long requestNumber = requestBody.get("requestNumber");
        Long userNumber = (Long) request.getAttribute("userNumber");
        shiftRequestService.emergencyDeleteShift(requestNumber, userNumber);
        response.put("success", true);
        response.put("message", "管理者権限でシフトを削除しました。");
        return ResponseEntity.ok(response);}
    @GetMapping("/shift/{shiftNumber}")
    public ResponseEntity<Map<String, Object>> getShiftRequests(@PathVariable Long shiftNumber){
        Map<String, Object> response =new HashMap<>();
        List<ShiftRequestDTO> requests=shiftRequestService.getShiftRequests(shiftNumber);
        response.put("success", true);
        response.put("request", requests);
        return ResponseEntity.ok(response);}
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserRequests(HttpServletRequest request){
        Map<String, Object> response=new HashMap<>();
        Long userNumber=(Long) request.getAttribute("userNumber");
        List<ShiftRequestDTO> requests=shiftRequestService.getUserRequests(userNumber);
        response.put("success", true);
        response.put("request", requests);
        return ResponseEntity.ok(response);}
    @GetMapping("/shift/store/{storeNumber}")
    public ResponseEntity<Map<String, Object>> getStoreRequests(@PathVariable Long storeNumber){
        Map<String, Object> response =new HashMap<>();
        List<ShiftRequestDTO> requests=shiftRequestService.getStoreRequests(storeNumber);
        response.put("success", true);
        response.put("request", requests);
        return ResponseEntity.ok(response);}

}
