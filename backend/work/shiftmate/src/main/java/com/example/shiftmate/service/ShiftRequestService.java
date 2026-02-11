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

    public ShiftRequestDTO applyShift(Long shiftNumber, Long userNumber) {
        try {
            Optional<ShiftEntity> shiftOptional = shiftRepository.findById(shiftNumber);
            if (!shiftOptional.isPresent()) {
                throw new ShiftMateException("シフトを探せません。");
            }
            ShiftEntity shift = shiftOptional.get();
            Optional<UserEntity> userOptional = userRepository.findById(userNumber);
            if (!userOptional.isPresent()) {
                throw new ShiftMateException("ユーザーを探せません。");
            }
            UserEntity user = userOptional.get();
            if (!"従業員".equals(user.getUserType())) {
                throw new ShiftMateException("シフトの申し込みは従業員しか可能です。");
            }
            Optional<StoreEmployeeEntity> storeEmployeeOptional = storeEmployeeRepository.findByStore_StoreNumberAndUser_UserNumber(shift.getStore().getStoreNumber(), userNumber);
            if (!storeEmployeeOptional.isPresent()) {
                throw new ShiftMateException("該当店舗のスタッフのみシフト申請が可能です。");
            }
            StoreEmployeeEntity storeEmployee = storeEmployeeOptional.get();
            if (!"承認".equals(storeEmployee.getStatus())) {
                if ("待機中".equals(storeEmployee.getStatus())) {
                    throw new ShiftMateException("店舗の店長の承認が必要です。");
                } else if ("断り".equals(storeEmployee.getStatus())) {
                    throw new ShiftMateException("該当店舗から断りました。");
                } else {
                    throw new ShiftMateException("また店舗のスタッフで承認されませんでした。");
                }
            }

            if (shiftRequestRepository.existsByShift_ShiftNumberAndUser_UserNumber(shiftNumber, userNumber)) {
                throw new ShiftMateException("既に申し込んだシフトです。");
            }

            if (shift.getCurrentEmployees() >= shift.getMaxEmployees()) {
                throw new ShiftMateException("定員が締め切られました。　他の時間帯を選んでください。");
            }

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

    public ShiftRequestDTO updateShiftRequest(Long requestNumber, Long newShiftNumber, Long userNumber) {
        try {
            Optional<ShiftRequestEntity> shiftRequestOptional = shiftRequestRepository.findById(requestNumber);
            if (!shiftRequestOptional.isPresent()) {
                throw new ShiftMateException("申し込んだシフトが見つかりません。");
            }
            ShiftRequestEntity shiftRequest = shiftRequestOptional.get();
            Optional<ShiftEntity> shiftOptional = shiftRepository.findById(newShiftNumber);
            if (!shiftOptional.isPresent()) {
                throw new ShiftMateException("新しいシフトが見つかりません。");
            }
            UserEntity user = shiftRequest.getUser();
            if (!user.getUserNumber().equals(userNumber)) {
                throw new ShiftMateException("ご自身のシフトのみ修正可能です。");
            }
            if (!"従業員".equals(user.getUserType())) {
                throw new ShiftMateException("従業員専用の機能です。");
            }
            if ("承認".equals(shiftRequest.getStatus())) {
                throw new ShiftMateException("承認済みのシフトは変更できません。店長に相談してください。");
            }
            ShiftEntity newShift = shiftOptional.get();
            if (newShift.getCurrentEmployees() >= newShift.getMaxEmployees()) {
                throw new ShiftMateException("定員が締め切られました。他の時間帯を選んでください。");
            }
            if (shiftRequestRepository.existsByShift_ShiftNumberAndUser_UserNumber(newShiftNumber, userNumber)) {
                throw new ShiftMateException("既に申し込んだシフトです。");
            }
            Optional<ShiftRequestEntity> oldRequestOptional =
                    shiftRequestRepository.findByRequestNumberAndUser_UserNumber(requestNumber, userNumber);

            shiftRequest.setShift(newShift);
            shiftRequest.setStatus("待機中");
            ShiftRequestEntity savedRequest = shiftRequestRepository.save(shiftRequest);
            return convertToDTO(savedRequest);
            } catch (ShiftMateException e) {
                System.out.println("X ShiftMateException: " + e.getMessage());
                throw e;
            } catch (Exception e) {
                System.out.println("X Exception: " + e.getMessage());
                e.printStackTrace();
                throw new ShiftMateException("シフト修正中エラー発生", e);
            }
    }

    public void deleteShiftRequest(Long requestNumber, Long userNumber) {
        try {
            Optional<ShiftRequestEntity> shiftRequestOptional = shiftRequestRepository.findById(requestNumber);
            if (!shiftRequestOptional.isPresent()) {
                throw new ShiftMateException("削除するシフト申請が見つかりません。");
            }
            ShiftRequestEntity request = shiftRequestOptional.get();
            if (!request.getUser().getUserNumber().equals(userNumber)) {
                throw new ShiftMateException("ご自身のシフトのみ削除可能です。");
            }
            if ("承認".equals(request.getStatus())) {
                throw new ShiftMateException("承認済みのシフトは削除できません。店長に相談してください。");
            }
            shiftRequestRepository.delete(request);
            } catch (ShiftMateException e) {
                System.out.println("X ShiftMateException: " + e.getMessage());
                throw e;
            } catch (Exception e) {
                System.out.println("X Exception: " + e.getMessage());
                e.printStackTrace();
                throw new ShiftMateException("シフトキャンセル中にエラーが発生しました。", e);
            }
    }

    public void emergencyDeleteShift(Long requestNumber, Long userNumber) {
        try {
            Optional<UserEntity> managerOptional = userRepository.findById(userNumber);
            if (!managerOptional.isPresent()) {
                throw new ShiftMateException("管理者情報が見つかりません。");
            }
            UserEntity manager = managerOptional.get();
            if (!"店長".equals(manager.getUserType())) {
                throw new ShiftMateException("この機能は店長のみ使用可能です。");
            }
            Optional<ShiftRequestEntity> requestOptional = shiftRequestRepository.findById(requestNumber);
            if (!requestOptional.isPresent()) {
                throw new ShiftMateException("削除対象のシフト申請が見つかりません。");
            }
            ShiftRequestEntity request = requestOptional.get();
            if ("承認".equals(request.getStatus())) {
                ShiftEntity shift = request.getShift();
                int currentCount = shift.getCurrentEmployees();
                if (currentCount > 0) {
                    shift.setCurrentEmployees(currentCount - 1);
                    shiftRepository.save(shift);
                }
            }
            shiftRequestRepository.delete(request);
            } catch (ShiftMateException e) {
                System.out.println("X ShiftMateException: " + e.getMessage());
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                throw new ShiftMateException("緊急削除中にエラーが発生しました。", e);
            }
    }

    public List<ShiftRequestDTO> getShiftRequests(Long shiftNumber){
        try{
            List<ShiftRequestEntity> requests= shiftRequestRepository.findByShift_ShiftNumber(shiftNumber);
            return requests.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("申し込みリストの照会中エラー発生",e);
        }
    }

    public ShiftRequestDTO processRequest(Long requestNumber, String status, Long processedByUserNumber){
        try{
            Optional<ShiftRequestEntity> requestOptional = shiftRequestRepository.findById(requestNumber);
            if (!requestOptional.isPresent()){throw new ShiftMateException("申し込み履歴が見つかりません");}
            ShiftRequestEntity request=requestOptional.get();
            ShiftEntity shift=request.getShift();
            if (!shift.getStore().getOwner().getUserNumber().equals(processedByUserNumber)){throw new ShiftMateException("該当店舗の店長のみ申し込みを処理できます。");}
            if (!"待機中".equals(request.getStatus())){throw new ShiftMateException("既に処理された申し込みです。");}
            if (!"承認".equals(status)&&!"断り".equals(status)){throw new ShiftMateException("無効な処理状態です。（承認または断りのみ可能）");}
            if ("承認".equals(status)){if (shift.getCurrentEmployees()>= shift.getMaxEmployees()){throw new ShiftMateException("シフト定員が締め切られました。");}
                shift.setCurrentEmployees(shift.getCurrentEmployees()+1);
                shiftRepository.save(shift);}
            request.setStatus(status);
            request.setProcessedAt(LocalDateTime.now());
            ShiftRequestEntity updatedRequest=shiftRequestRepository.save(request);
            return convertToDTO(updatedRequest);}
        catch (ShiftMateException e) {throw e;}
        catch (Exception e) {e.printStackTrace(); throw new ShiftMateException("申し込み処理中エラー発生。");}
    }

    public List<ShiftRequestDTO> getUserRequests(Long userNumber){
        try{List<ShiftRequestEntity> requests=shiftRequestRepository.findByUser_UserNumber(userNumber);
            return requests.stream().map(this::convertToDTO).collect(Collectors.toList());}
        catch (Exception e){e.printStackTrace(); throw new ShiftMateException("申し込みリストの照会中エラー発生", e);}}

    private ShiftRequestDTO convertToDTO(ShiftRequestEntity entity){
        return ShiftRequestDTO.builder()
                .requestNumber(entity.getRequestNumber())
                .shiftNumber(entity.getShift().getShiftNumber())
                .userNumber(entity.getUser().getUserNumber())
                .status(entity.getStatus())
                .appliedAt(entity.getAppliedAt())
                .processedAt(entity.getProcessedAt())
                .build();}

}





