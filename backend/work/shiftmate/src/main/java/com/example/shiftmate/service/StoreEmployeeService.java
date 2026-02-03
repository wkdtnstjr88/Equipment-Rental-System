package com.example.shiftmate.service;

import com.example.shiftmate.dto.StoreEmployeeDTO;
import com.example.shiftmate.entity.StoreEmployeeEntity;
import com.example.shiftmate.entity.StoreEntity;
import com.example.shiftmate.entity.UserEntity;
import com.example.shiftmate.exception.ShiftMateException;
import com.example.shiftmate.repository.StoreEmployeeRepository;
import com.example.shiftmate.repository.StoreRepository;
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
public class StoreEmployeeService {

    private final StoreEmployeeRepository storeEmployeeRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public StoreEmployeeDTO requestEmployeeApproval(Long storeNumber, Long userNumber) {
        try {
            // 店舗確認
            Optional<StoreEntity> storeOptional = storeRepository.findById(storeNumber);
            if(!storeOptional.isPresent()) {
                throw new ShiftMateException("店舗を探せません。");
            }

            StoreEntity store = storeOptional.get();

            // ユーザー確認
            Optional<UserEntity> userOptional = userRepository.findById(userNumber);
            if(!userOptional.isPresent()) {
                throw new ShiftMateException("ユーザーを探せません。");
            }

            UserEntity user = userOptional.get();

            // スタッフだけ可能
            if(!"従業員".equals(user.getUserType())) {
                throw new ShiftMateException("承認要求は従業員ユーザーのみ可能です。");
            }

            // 自分の店舗には要請不可能
            if(store.getOwner().getUserNumber().equals(userNumber)) {
                throw new ShiftMateException("自身が店長に登録している店舗にはスタッフ要請ができません。");
            }

            // 重複確認
            if(storeEmployeeRepository.existsByStore_StoreNumberAndUser_UserNumber(storeNumber, userNumber)) {
                throw new ShiftMateException("すでに承認要請をした店舗です。");
            }

            // 自動承認？手動承認？
            String initialStatus = store.getAutoApprove() ? "承認" : "待機中";

            // 承認要請生成
            StoreEmployeeEntity employeeEntity = StoreEmployeeEntity.builder()
                    .store(store)
                    .user(user)
                    .status(initialStatus)
                    .build();

            // 自動承認の場合、processedAt 設定
            if (store.getAutoApprove()) {
                employeeEntity.setProcessedAt(LocalDateTime.now());
            }

            StoreEmployeeEntity savedEmployee = storeEmployeeRepository.save(employeeEntity);

            return convertToDTO(savedEmployee);
        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("承認要請中エラー発生", e);
        }
    }

    // 店員承認要請処理　（承認・断り）
    public StoreEmployeeDTO processEmployeeRequest(Long relationNumber, String status, Long processedByUserNumber) {
        try {
            // 承認要請確認
            Optional<StoreEmployeeEntity> employeeOptional = storeEmployeeRepository.findById(relationNumber);
            if(!employeeOptional.isPresent()) {
                throw new ShiftMateException("承認要請を探せません。");
            }

            StoreEmployeeEntity employee = employeeOptional.get();

            System.out.println("=== 디버깅 ===");
            System.out.println("relationNumber: " + relationNumber);
            System.out.println("processedByUserNumber: " + processedByUserNumber);
            System.out.println("매장 소유자 번호: " + employee.getStore().getOwner().getUserNumber());
            System.out.println("============");
            // 処理者が店舗の店長なのか確認
            if(!employee.getStore().getOwner().getUserNumber().equals(processedByUserNumber)) {
                throw new ShiftMateException("該当店舗の店長だけが承認要請を処理できます。");
            }

            // 既に処理された要請なのか確認
            if (!"待機中".equals(employee.getStatus())) {
                throw new ShiftMateException("既に処理された要請です。");
            }

            // 状態有効性確認
            if (!"承認".equals(status) && !"断り".equals(status)) {
                throw new ShiftMateException("有効ではない処理状態です。承認または断りだけが可能です。");
            }

            // 状態アップデート
            employee.setStatus(status);
            employee.setProcessedAt(LocalDateTime.now());

            StoreEmployeeEntity updatedEmployee = storeEmployeeRepository.save(employee);

            return convertToDTO(updatedEmployee);
        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("承認要請の処理中エラー発生", e);
        }
    }

    // 店舗の全従業員照会（承認されている従業員だけ）
    public List<StoreEmployeeDTO> getStoreEmployees(Long storeNumber) {
        try {
            List<StoreEmployeeEntity> employees = storeEmployeeRepository
                    .findByStore_StoreNumberAndStatus(storeNumber, "承認");
            return employees.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("従業員リスト照会中エラー発生", e);
        }
    }

    // 店舗の承認待機中である要請照会
    public List<StoreEmployeeDTO> getPendingRequests(Long storeNumber) {
        try {
            List<StoreEmployeeEntity> requests = storeEmployeeRepository
                    .findByStore_StoreNumberAndStatus(storeNumber,"待機中");
            return requests.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("待機中の要請の照会中エラー発生", e);
        }
    }

    // ユーザーの全ての店舗関係照会
    public List<StoreEmployeeDTO> getUserStoreRelations(Long userNumber) {
        try {
            List<StoreEmployeeEntity> relations = storeEmployeeRepository.findByUser_UserNumber(userNumber);
            return relations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("店舗関係照会中エラー発生", e);
        }
    }

    // Entity -> DTO
    private StoreEmployeeDTO convertToDTO(StoreEmployeeEntity entity) {
        return StoreEmployeeDTO.builder()
                .relationNumber(entity.getRelationNumber())
                .storeNumber(entity.getStore().getStoreNumber())
                .userNumber(entity.getUser().getUserNumber())
                .status(entity.getStatus())
                .requestedAt(entity.getRequestedAt())
                .processedAt(entity.getProcessedAt())
                .build();
    }

}