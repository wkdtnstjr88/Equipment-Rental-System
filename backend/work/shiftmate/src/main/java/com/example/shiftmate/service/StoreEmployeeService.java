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

    // [追加] 管理者向け: 届いているシフトの変更又はキャンセルの申請を照会
    // (statusが'変更要請'になっているもの)
    public List<StoreEmployeeDTO> getShiftChangeRequests(Long storeNumber) {
        try {
            List<StoreEmployeeEntity> requests = storeEmployeeRepository
                    .findByStore_StoreNumberAndStatus(storeNumber, "変更要請");

            return requests.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("変更要請照会中エラー発生", e);
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
                // 下の2行追加しておきました。ご確認願います。（reason, adminComment)
                .reason(entity.getReason())
                .adminComment(entity.getAdminComment())
                .requestedAt(entity.getRequestedAt())
                .processedAt(entity.getProcessedAt())
                .build();
    }

    // 店舗の全従業員照会（全ての状態）-- 2026.03.09 Seodam Cho
    public List<StoreEmployeeDTO> getEmployeesByStore(Long storeNumber) {
        try {
            List<StoreEmployeeEntity> employees = storeEmployeeRepository
                    .findByStore_StoreNumber(storeNumber);
            return employees.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("従業員リスト照会中エラー発生", e);
        }
    }

    // 従業員解雇（管理者ー店長のみ可能）
    public void fireEmployee(Long relationNumber, Long ownerUserNumber) {
        try {
            // 承認要請確認
            Optional<StoreEmployeeEntity> employeeOptional = storeEmployeeRepository.findById(relationNumber);
            if (!employeeOptional.isPresent()) {
                throw new ShiftMateException("従業員関係を探せません。");
            }

            StoreEmployeeEntity employee = employeeOptional.get();

            // 承認済みの従業員のみ解雇可能
            if (!"承認".equals(employee.getStatus())) {
                throw new ShiftMateException("承認済みの従業員のみ解雇できます。");
            }

            storeEmployeeRepository.delete(employee);

        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("解雇処理中エラー発生", e);
        }


    }

    // シフトの変更及び取り消し (従業員が理由と共に転送)
    // 従業員側からの変更申請　
    // PATCH http://localhost:8080/api/employees/{relationNo}/cancel-request?userNumber={userNo}
    public void requestCancelWithReason(Long relationNumber, Long userNumber, String reason) { // userNumberを追加
        StoreEmployeeEntity request = storeEmployeeRepository.findById(relationNumber)
                .orElseThrow(() -> new ShiftMateException("該当する記録が見つかりません。")); // メッセージ修正

        // 1. 本人確認ロジックを追加 (セキュリティのため重要)
        if (!request.getUser().getUserNumber().equals(userNumber)) {
            throw new ShiftMateException("本人の申請のみ変更可能です。");
        }

        // 2. すでに承認された従業員のみ変更申請可能
        if (!"承認".equals(request.getStatus())) {
            throw new ShiftMateException("確定されたスケジュールのみ変更可能です。");
        }

        // 3. 状態を’変更要請’に変えてから理由を保存
        request.setStatus("変更要請");
        request.setReason(reason);

        storeEmployeeRepository.save(request);
    }
    // シフトのキャンセルを管理者から断る時コメントを残すメソッド。
    // http://localhost:8080/api/store-employees/4/reject -> 4はrelation_number
    @Transactional
    public void rejectCancelRequest(Long relationNumber, String adminComment) {
        StoreEmployeeEntity request = storeEmployeeRepository.findById(relationNumber)
                .orElseThrow(() -> new ShiftMateException("該当する記録が見つかりません。"));

        // 状態を再び '承認'に変えて管理者のコメント保存
        request.setStatus("承認");
        // status=承認,以外は「店舗の全従業員照会」で確認できないため実際には’不可’でもシステム上’承認’します。
        request.setAdminComment(adminComment);

        storeEmployeeRepository.save(request);
    }
    //管理者がシフト変更を承認する時
    @Transactional
    public void approveCancelRequest(Long relationNumber) {
        StoreEmployeeEntity request = storeEmployeeRepository.findById(relationNumber)
                .orElseThrow(() -> new ShiftMateException("該当記録が見つかりません。"));

        // 承認された際にはDBからデータ自体を削除 (DBから行を削除)
        storeEmployeeRepository.delete(request);
    }


}