package com.example.EquipmentRentalSystem.controller;

import com.example.EquipmentRentalSystem.dto.ShiftDTO;
import com.example.EquipmentRentalSystem.service.ShiftService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3030")
public class ShiftController {

    private final ShiftService shiftService;

    // シフト生成...
    @PostMapping("/create")
    public ResponseEntity<Map<String,Object>> createShift(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        ShiftDTO shiftDTO = ShiftDTO.builder()
                .storeNumber(((Number) requestBody.get("storeNumber")).longValue())
                .shiftDate(LocalDate.parse((String) requestBody.get("shiftDate")))
                .startTime(((Number) requestBody.get("startTime")).intValue())
                .endTime(((Number) requestBody.get("endTime")).intValue())
                .maxEmployees(((Number) requestBody.get("maxEmployees")).intValue())
                .build();

        ShiftDTO createdShift = shiftService.createShift(shiftDTO);
        response.put("success", true);
        response.put("message", "シフトが生成されました。");
        response.put("shift", createdShift);
        return ResponseEntity.ok(response);
    }

    // 店舗の全てのシフト照会
    @GetMapping("/store/{storeNumber}")
    public ResponseEntity<Map<String, Object>> getStoreShifts(@PathVariable Long storeNumber) {
        Map<String, Object> response = new HashMap<>();
        List<ShiftDTO> shifts = shiftService.getStoreShifts(storeNumber);
        response.put("success", true);
        response.put("shifts", shifts);
        return ResponseEntity.ok(response);
    }

    // 店舗の特定日付シフト照会
    @GetMapping("/store/{storeNumber}/date/{date}")
    public ResponseEntity<Map<String, Object>> getStoreShiftsByDate(
            @PathVariable Long storeNumber,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<String, Object> response = new HashMap<>();
        List<ShiftDTO> shifts = shiftService.getStoreShiftsByDate(storeNumber, date);
        response.put("success", true);
        response.put("shifts", shifts);
        return ResponseEntity.ok(response);
    }

    // 店舗の期間別シフト照会
    @GetMapping("/store/{storeNumber}/range")
    public ResponseEntity<Map<String, Object>> getStoreShiftsByDateRange(
            @PathVariable Long storeNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> response = new HashMap<>();
        List<ShiftDTO> shifts = shiftService.getStoreShiftsByDateRange(storeNumber, startDate, endDate);
        response.put("success", true);
        response.put("shifts", shifts);
        return ResponseEntity.ok(response);
    }

    // シフト番号から照会
    @GetMapping("/{shiftNumber}")
    public ResponseEntity<Map<String, Object>> getShift(@PathVariable Long shiftNumber) {
        Map<String, Object> response = new HashMap<>();
        ShiftDTO shift = shiftService.getShiftByNumber(shiftNumber);
        response.put("success", true);
        response.put("shift", shift);
        return ResponseEntity.ok(response);
    }

    // [2] シフト情報更新 (PUT)
    // アドレス: PUT /api/shifts/{shiftNumber}
    @PutMapping("/{shiftNumber}")
    public ResponseEntity<Map<String, Object>> updateShift(
            @PathVariable Long shiftNumber,
            @RequestBody ShiftDTO shiftDTO) {

        Map<String, Object> response = new HashMap<>();

        // サービス層で更新ロジックを実行 (DTO -> エンティティ反映 -> 再びDTOに変換して返却)
        ShiftDTO updatedShift = shiftService.updateShift(shiftNumber, shiftDTO);

        response.put("success", true);
        response.put("message", "シフトが正常に更新されました.");
        response.put("shift", updatedShift);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{shiftNumber}")
        public ResponseEntity<Map<String, Object>> deleteShift(@PathVariable Long shiftNumber) {
            Map<String, Object> response = new HashMap<>();

            shiftService.deleteShift(shiftNumber);

            response.put("success", true);
            response.put("message", "シフトが正常に削除されました。");

            return ResponseEntity.ok(response);
        }

}
