package com.example.shiftmate.service;

import com.example.shiftmate.dto.ShiftRequestDTO;
import com.example.shiftmate.entity.ShiftEntity;
import com.example.shiftmate.entity.ShiftRequestEntity;
import com.example.shiftmate.entity.StoreEmployeeEntity;
import com.example.shiftmate.entity.UserEntity;
import com.example.shiftmate.exception.ShiftMateException;
import com.example.shiftmate.repository.ShiftRepository;
import com.example.shiftmate.repository.ShiftRequestRepository;
import com.example.shiftmate.repository.StoreEmployeeRepository;
import com.example.shiftmate.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShiftRequestService {

    private final ShiftRequestRepository shiftRequestRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private final StoreEmployeeRepository storeEmployeeRepository;

    // シフト申し込み
    public ShiftRequestDTO applyShift(Long shiftNumber, Long userNumber) {
        try {
            Optional<ShiftEntity> shiftOptional = shiftRepository.findById(shiftNumber);
            if( !shiftOptional.isPresent() ) {
                throw new ShiftMateException("シフトを探せません。");
            }

            ShiftEntity shift = shiftOptional.get();

            // ユーザー確認
            Optional<UserEntity> userOptional = userRepository.findById(userNumber);
            if ( !userOptional.isPresent() ) {
                throw new ShiftMateException("ユーザーを探せません。");
            }

            UserEntity user = userOptional.get();

            if (!"従業員".equals(user.getUserType())){
                throw new ShiftMateException("シフトの申し込みは従業員しか可能です。");
            }

            // 店舗のスタッフ検証
            Optional<StoreEmployeeEntity> storeEmployeeOptional =
                   storeEmployeeRepository.findByStore_StoreNumberAndUser_UserNumber(
                           shift.getStore().getStoreNumber(),
                           userNumber
                   );

            if(!storeEmployeeOptional.isPresent()) {
                throw new ShiftMateException("該当店舗のスタッフのみシフト申請が可能です。");
            }

            StoreEmployeeEntity storeEmployee = storeEmployeeOptional.get();

            if(!"承認".equals(storeEmployee.getStatus())) {
                if ("待機中".equals(storeEmployee.getStatus())) {
                    throw new ShiftMateException("店舗の店長の承認が必要です。");
                } else if ("断り".equals(storeEmployee.getStatus())) {
                    throw new ShiftMateException("該当店舗から断りました。");
                } else {
                    throw new ShiftMateException("まだ店舗のスタッフで承認されませんでした。");
                }
            }

            // 重複して申し込みした場合
            if (shiftRequestRepository.existsByShift_ShiftNumberAndUser_UserNumber(shiftNumber, userNumber)) {
                throw new ShiftMateException("既に申し込んだシフトです");
            }

            // 定員確認
            if (shift.getCurrentEmployees() >= shift.getMaxEmployees()) {
                throw new ShiftMateException("定員が締め切られました。他の時間帯を選んでください。");
            }

            // 申し込み生成
            ShiftRequestEntity requestEntity = ShiftRequestEntity.builder()
                    .shift(shift)
                    .user(user)
                    .status("待機中")
                    .build();

            ShiftRequestEntity savedRequest = shiftRequestRepository.save(requestEntity);
            return convertToDTO(savedRequest);

        } catch (ShiftMateException e) {
            System.out.println("X ShiftMateException: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("X Exception: " + e.getMessage());
            e.printStackTrace();
            throw new ShiftMateException("シフト申し込む中エラー発生", e);
        }
    }

    // シフト申し込み処理　（承認　・　断り）
    public ShiftRequestDTO processRequest(Long requestNumber, String status, Long processedByUserNumber) {
        try {
            // 申し込み確認
            Optional<ShiftRequestEntity> requestOptional = shiftRequestRepository.findById(requestNumber);
            if (!requestOptional.isPresent()) {
                throw new ShiftMateException("申し込み履歴が見つかりません。");
            }

            ShiftRequestEntity request = requestOptional.get();

            // 処理者が店長なのか確認
            ShiftEntity shift = request.getShift();
            if (!shift.getStore().getOwner().getUserNumber().equals(processedByUserNumber)) {
                throw new ShiftMateException("該当店舗の店長のみ申し込みを処理できます。");
            }

            // 既に処理した申し込みかないかにかんして確認
            if (!"待機中".equals(request.getStatus())) {
                throw new ShiftMateException("既に処理された申し込みです。");
            }

            // 状態有効性確認
            if (!"承認".equals(status) && !"断り".equals(status)) {
                throw new ShiftMateException("無効な処理状態です。 (承認または断りのみ可能)");
            }

            // 承認した時定員確認
            if ("承認".equals(status)) {
                if (shift.getCurrentEmployees() >= shift.getMaxEmployees()) {
                    throw new ShiftMateException("シフト定員が締め切られました。");
                }

                // 現在人数増加
                shift.setCurrentEmployees(shift.getCurrentEmployees() + 1);
                shiftRepository.save(shift);
            }

            // 申し込み状態アップデート
            request.setStatus(status);
            request.setProcessedAt(LocalDateTime.now());

            ShiftRequestEntity updatedRequest = shiftRequestRepository.save(request);

            return convertToDTO(updatedRequest);
        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("申し込み処理中エラー発生", e);
        }
    }

    // シフトの全ての申し込み照会
    public List<ShiftRequestDTO> getShiftRequests(Long shiftNumber) {
        try {
            List<ShiftRequestEntity> requests = shiftRequestRepository.findByShift_ShiftNumber(shiftNumber);
            return requests.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("申し込みリストの照会中エラー発生", e);
        }
    }


    // ユーザーの全ての申し込み照会
    public List<ShiftRequestDTO> getUserRequests(Long userNumber) {
        try {
            List<ShiftRequestEntity> requests = shiftRequestRepository.findByUser_UserNumber(userNumber);
            return requests.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("申し込みリストの照会中エラー発生", e);
        }
    }

    // Entity -> DTO
    private ShiftRequestDTO convertToDTO(ShiftRequestEntity entity) {
        return ShiftRequestDTO.builder()
                .requestNumber(entity.getRequestNumber())
                .shiftNumber(entity.getShift().getShiftNumber())
                .userNumber(entity.getUser().getUserNumber())
                .status(entity.getStatus())
                .appliedAt(entity.getAppliedAt())
                .processedAt(entity.getProcessedAt())
                .build();
    }
}
