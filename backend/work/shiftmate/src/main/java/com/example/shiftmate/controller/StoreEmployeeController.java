package com.example.shiftmate.controller;

import com.example.shiftmate.dto.StoreEmployeeDTO;
import com.example.shiftmate.service.StoreEmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/store-employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3030")
public class StoreEmployeeController{
// 260205
    private final StoreEmployeeService storeEmployeeService;

    // 店舗スタッフ継承要請
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> requestEmployeeApproval(
            @RequestBody Map<String, Long> requestBody,
            HttpServletRequest request){

        Map<String, Object> response = new HashMap<>();
        Long storeNumber = requestBody.get("storeNumber");
        Long userNumber = (Long) request.getAttribute("userNumber"); // <- tokenから抜き出す

        StoreEmployeeDTO employeeDTO = storeEmployeeService.requestEmployeeApproval(storeNumber, userNumber);
        response.put("success", true);

        if ("継承".equals(employeeDTO.getStatus())){
            response.put("message", "自動継承されました。シフト申請ができます。");
        } else {
            response.put("message", "継承要請が完了されました。店長の継承をお待ちください。");
        }

        response.put("relation", employeeDTO);
        return ResponseEntity.ok(response);
    }

    // スタッフ継承要請の処理
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processEmployeeRequest(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request){
        Map<String, Object> response = new HashMap<>();
        Long relationNumber = ((Number) requestBody.get("relationNumber")).longValue();
        String status = (String) requestBody.get("status");
        Long processedByUserNumber = ((Long) request.getAttribute("userNumber"));

        StoreEmployeeDTO employeeDTO = storeEmployeeService.processEmployeeRequest(
                relationNumber, status, processedByUserNumber
        );
        response.put("success", true);
        response.put("message", "申請が処理されました。");
        response.put("relation", employeeDTO);
        return ResponseEntity.ok(response);
    }

    // 店舗に承認しているスタッフ照会
    @GetMapping("/store/{storeNumber}")
    public ResponseEntity<Map<String, Object>> getStoreEmployees(@PathVariable Long storeNumber){
        Map<String, Object> response = new HashMap<>();
        List<StoreEmployeeDTO> employees = storeEmployeeService.getStoreEmployees(storeNumber);
        response.put("success", true);
        response.put("employees", employees);
        return ResponseEntity.ok(response);
    }

    // 店舗の承認待機中の要請の照会
    @GetMapping("/store/{storeNumber}/pending")
    public ResponseEntity <Map<String, Object>> getPendingRequests(@PathVariable Long storeNumber){
        Map<String, Object> response = new HashMap<>();
        List<StoreEmployeeDTO> requests = storeEmployeeService.getPendingRequests(storeNumber);
        response.put("success", true);
        response.put("requests", requests);
        return ResponseEntity.ok(response);
    }

//    // [追加] 管理者向け: 当店舗に届いているシフトの変更又は取り消し申請のリスト閲覧
//    @GetMapping("/store/{storeNumber}/shift-requests")
//    public ResponseEntity<List<StoreEmployeeDTO>> getShiftRequests(@PathVariable Long storeNumber) {
//        // サービスから getShiftChangeRequests メソッドを呼び出し
//        List<StoreEmployeeDTO> requests = storeEmployeeService.getShiftChangeRequests(storeNumber);
//        return ResponseEntity.ok(requests);
//    }

    // ユーザーのすべての店舗関係照会
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserStoreRelations(HttpServletRequest request){
        Map<String, Object> response = new HashMap<>();
        Long userNumber = (Long) request.getAttribute("userNumber");
        List<StoreEmployeeDTO> relations = storeEmployeeService.getUserStoreRelations(userNumber);
        response.put("success", true);
        response.put("relations", relations);
        return ResponseEntity.ok(response);
    }
    // 従業員の解雇（管理者のみ許可）
    @DeleteMapping("/employees/{relationNumber}")
    public ResponseEntity<Map<String, Object>> fireEmployee(
            @PathVariable Long relationNumber,
            @RequestParam Long ownerUserNumber
    ) {
        //　１．サービス呼び出し（ビズネスロジック実行）
        storeEmployeeService.fireEmployee(relationNumber, ownerUserNumber);

        //　２．応答データ生成
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "従業員が解雇に成功しました。");

        //　３．応答変換（200　OKとともにJSONメッセージ転送
        return ResponseEntity.ok(response);
    }

//    // 承認待機状態の要請取り消し又は承認済みのシフトの取り消し申請
//    @PatchMapping("/{relationNumber}/cancel-request")
//    public ResponseEntity<?> requestCancel(
//            @PathVariable Long relationNumber,
//            @RequestParam Long userNumber,
//            @RequestBody Map<String, String> body) {
//
//        // 1. JSON Bodyから取り消しの理由抽出
//        String reason = body.get("reason");
//
//        // 2. サービス呼び出し
//        storeEmployeeService.requestCancelWithReason(relationNumber, userNumber, reason);
//
//        // 3. 応答変換
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "取り消し申請の受付に成功しました。");
//
//        return ResponseEntity.ok(response);
//    }
//    // 管理者が呼び出しするエンドポイント
//    @PatchMapping("/{relationNumber}/reject")
//    public ResponseEntity<?> rejectRequest(
//            @PathVariable Long relationNumber,
//            @RequestBody Map<String, String> body) {
//
//        String adminComment = body.get("adminComment");
//        storeEmployeeService.rejectCancelRequest(relationNumber, adminComment);
//
//        return ResponseEntity.ok(Map.of("success", true, "message", "変更不可処理が完了されました。"));
//    }
//
//    // 店長からの従業員シフト変更申請を承認 (データ削除処理)
//    @DeleteMapping("/{relationNumber}/approve")
//    public ResponseEntity<?> approveRequest(@PathVariable Long relationNumber) {
//
//        storeEmployeeService.approveCancelRequest(relationNumber);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "シフトの取り消し申請が承認されました。");
//
//        return ResponseEntity.ok(response);
//    }
}
