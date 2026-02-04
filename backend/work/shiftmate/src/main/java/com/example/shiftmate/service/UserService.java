package com.example.shiftmate.service;

import com.example.shiftmate.dto.LoginDTO;
import com.example.shiftmate.dto.UserDTO;
import com.example.shiftmate.entity.UserEntity;
import com.example.shiftmate.exception.ShiftMateException;
import com.example.shiftmate.repository.UserRepository;
import com.example.shiftmate.util.JwtUtil;
import com.example.shiftmate.util.PasswordUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    public boolean checkUserIdDuplicate(String userId) {
        try {
            return userRepository.existsByUserId(userId);
        } catch (Exception e){
            e.printStackTrace();
            throw new ShiftMateException("ユーザーID重複チェック中エラー発生",e);
        }
    }

    public UserDTO registerUser(UserDTO userDTO) {
        try {
            if (checkUserIdDuplicate(userDTO.getUserId())){
                throw new ShiftMateException("既に使用中のIDです。");
            }
            if (!passwordUtil.validatePassword(userDTO.getPassword())){
                throw new ShiftMateException("パスワードは８文字以上で、英字数字を含める必要があります。");
            }

            String hashedPassword = passwordUtil.hashPassword(userDTO.getPassword());

            UserEntity userEntity = UserEntity.builder()
                    .userId(userDTO.getUserId())
                    .password(hashedPassword)
                    .name(userDTO.getName())
                    .userType(userDTO.getUserType())
                    .build();

            UserEntity savedUser = userRepository.save(userEntity);

            return convertToDTO(savedUser);
        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("会員登録中エラー発生", e);
        }
    }

    // 로그인 (JWT 토큰 변환)
    public Map<String, Object> login(LoginDTO loginDTO) {
        try {
            Optional<UserEntity> userOptional = userRepository.findByUserId(loginDTO.getUserId());

            if (!userOptional.isPresent()) {
                throw new ShiftMateException("ユーザーIDまたはパスワードが正しくありません。");
            }

            UserEntity user = userOptional.get();

            String hashedPassword = passwordUtil.hashPassword(loginDTO.getPassword());
            if(!user.getPassword().equals(hashedPassword)){
                throw new ShiftMateException("ユーザーIDまたはパスワードが正しくありません。");
            }

            // JWT Token 生成
            String token = jwtUtil.generateToken(
                    user.getUserNumber(),
                    user.getUserId(),
                    user.getUserType()
            );

            //　応答データ
            Map<String, Object> result = new HashMap<>();
            result.put("token",token);
            result.put("user",convertToDTO(user));

            return result;
        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("ログイン中エラー発生", e);
        }
    }

    private UserDTO convertToDTO(UserEntity entity) {
        return UserDTO.builder()
                .userNumber(entity.getUserNumber())
                .userId(entity.getUserId())
                .name(entity.getName())
                .userType(entity.getUserType())
                .build();
    }
}
