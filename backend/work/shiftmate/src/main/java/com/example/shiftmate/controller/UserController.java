package com.example.shiftmate.controller;

import com.example.shiftmate.dto.LoginDTO;
import com.example.shiftmate.dto.UserDTO;
import com.example.shiftmate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3030")
public class UserController {

    private final UserService userService;


    // ユーザーID重複確認
    @GetMapping("/check-duplicate")
    public ResponseEntity<Map<String, Object>> checkDuplicate(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        boolean isDuplicate = userService.checkUserIdDuplicate(userId);
        response.put("success", true);
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    // 会員登録
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserDTO userDTO) {
        Map<String, Object> response = new HashMap<>();
        UserDTO registeredUser = userService.registerUser(userDTO);
        response.put("success", true);
        response.put("message", "会員登録完了！");
        response.put("user", registeredUser);
        return ResponseEntity.ok(response);
    }

    // ログイン
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        Map<String, Object> loginResult = userService.login(loginDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "ログイン成功！");
        response.put("token", loginResult.get("token"));
        response.put("user", loginResult.get("user"));
        return ResponseEntity.ok(response);
    }

}
