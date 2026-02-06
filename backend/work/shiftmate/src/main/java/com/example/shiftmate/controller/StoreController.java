package com.example.shiftmate.controller;

import com.example.shiftmate.dto.StoreDTO;
import com.example.shiftmate.service.StoreService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3030")
public class StoreController {

    private final StoreService storeService;

    //　店舗登録
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registersStore(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Tokenからユーザー情報を出す
        Long ownerUserNumber = (Long) request.getAttribute("userNumber");

        // StoreDTO 生成
        StoreDTO storeDTO = StoreDTO.builder()
                .storeName((String) requestBody.get("storeName"))
                .storeAddress((String) requestBody.get("storeAddress"))
                .category((String) requestBody.get("category"))
                .ownerUserNumber(ownerUserNumber)
                .autoApprove((Boolean) requestBody.get("autoApprove"))
                .build();

        StoreDTO registeredStore = storeService.registerStore(storeDTO);
        response.put("success", "ture");
        response.put("message", "店舗が登録されました。");
        response.put("store", registeredStore);
        return ResponseEntity.ok(response);
    }

    //　店長の店舗リスト照会
    @GetMapping("/owner")
    public ResponseEntity<Map<String, Object>> getOwnerStores(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Long ownerUserNumber = (Long) request.getAttribute("userNumber");

        List<StoreDTO> stores = storeService.getOwnerStores(ownerUserNumber);
        response.put("succes", true);
        response.put("stores", stores);
        return ResponseEntity.ok(response);
    }

    //　全ての店舗リスト照会
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllStores() {
        Map<String, Object> response = new HashMap<>();
        List<StoreDTO> stores = storeService.getAllStores();
        response.put("success", true);
        response.put("stores", stores);
        return ResponseEntity.ok(response);
    }

    // storeNumberで店舗照会
    @GetMapping("/{storeNumber}")
    public ResponseEntity<Map<String, Object>> getStore(@PathVariable Long storeNumber) {
        Map<String, Object> response = new HashMap<>();
        StoreDTO store = storeService.getStoreByNumber(storeNumber);
        response.put("seccess", true);
        response.put("store", store);
        return ResponseEntity.ok(response);
    }
}