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

    public List<ShiftRequestDTO> getShiftRequests(Long shiftNumber){
        try{
            List<ShiftRequestEntity> requests= shiftRequestRepository.findByShift_ShiftNumber(shiftNumber);
            return requests.stream()
                    .map(this::converToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("申し込みリストの照会中エラー発生",e);
        }
    }
}

