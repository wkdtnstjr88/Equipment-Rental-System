package com.example.shiftmate.service;

import com.example.shiftmate.dto.ShiftDTO;
import com.example.shiftmate.entity.ShiftEntity;
import com.example.shiftmate.entity.StoreEntity;
import com.example.shiftmate.exception.ShiftMateException;
import com.example.shiftmate.repository.ShiftRepository;
import com.example.shiftmate.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final StoreRepository storeRepository;

    // シフト生成
    public ShiftDTO createShift(ShiftDTO shiftDTO) {
        try {
            Optional<StoreEntity> storeOptional = storeRepository.findById(shiftDTO.getStoreNumber());
            // 店舗の確認（登録していない店舗）
            if (!storeOptional.isPresent()) {
                throw new ShiftMateException("店舗を探せません。");
            }

            // 時間の有効性検証
            if (shiftDTO.getStartTime() >= shiftDTO.getEndTime()) {
                throw new ShiftMateException("終了時間は開始時間より遅くなかればなりません。");
            }

            if (shiftDTO.getStartTime() < 0 || shiftDTO.getStartTime() > 24 ||
                    shiftDTO.getEndTime() < 0 || shiftDTO.getEndTime() > 24) {
                throw new ShiftMateException("時間は０~24の間の数で入力してください。");
            }

            if (shiftDTO.getMaxEmployees() <= 0) {
                throw new ShiftMateException("最大人数は一人以上です。");
            }

            // シフト生成の時、時間重複検証
            List<ShiftEntity> existingShifts = shiftRepository.findByStore_StoreNumberAndShiftDate(
                    shiftDTO.getStoreNumber(),
                    shiftDTO.getShiftDate()
            );
            for ( ShiftEntity existingShift : existingShifts ) {
                if (isTimeOverlap(
                        shiftDTO.getStartTime(),
                        shiftDTO.getEndTime(),
                        existingShift.getStartTime(),
                        existingShift.getEndTime())) {
                    throw new ShiftMateException(
                            String.format("当該日の%d時 ~ %d時の時間帯に既にシフトが存在します。",
                                    existingShift.getStartTime(),
                                    existingShift.getEndTime())
                    );
                }
            }

            // シフト生成
            ShiftEntity shiftEntity = ShiftEntity.builder()
                    .store(storeOptional.get())
                    .shiftDate(shiftDTO.getShiftDate())
                    .startTime(shiftDTO.getStartTime())
                    .endTime(shiftDTO.getEndTime())
                    .maxEmployees(shiftDTO.getMaxEmployees())
                    .currentEmployees(0)
                    .build();

            ShiftEntity savedShift = shiftRepository.save(shiftEntity);

            return convertToDTO(savedShift);
        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("シフト生成中エラー発生", e);
        }
    }

    // 店舗の全てのシフト照会
    public List<ShiftDTO> getStoreShifts(Long storeNumber) {
        try {
            List<ShiftEntity> shifts = shiftRepository.findByStore_StoreNumber(storeNumber);
            return shifts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("シフト照会中エラー発生", e);
        }
    }

    // 店舗の特定日付のシフト照会
    public List<ShiftDTO> getStoreShiftsByDate(Long storeNumber, LocalDate date) {
        try {
            List<ShiftEntity> shifts = shiftRepository.findByStore_StoreNumberAndShiftDate(storeNumber, date);
            return shifts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("シフト照会中エラー発生", e);
        }
    }

    // 店舗の期間別シフト照会
    public List<ShiftDTO> getStoreShiftsByDateRange(Long storeNumber, LocalDate startDate, LocalDate endDate) {
        try {
            List<ShiftEntity> shifts = shiftRepository.findByStore_StoreNumberAndShiftDateBetween(storeNumber, startDate, endDate);
            return shifts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("シフト照会中エラー発生", e);
        }
    }

    // シフト番号からシフト照会
    public ShiftDTO getShiftByNumber(Long shiftNumber) {
        try {
            Optional<ShiftEntity> shiftOptional = shiftRepository.findById(shiftNumber);
            if (!shiftOptional.isPresent()) {
                throw new ShiftMateException("シフトを探せません。");
            }
            return convertToDTO(shiftOptional.get());
        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("シフト照会中エラー発生", e);
        }
    }

    @Transactional // このアノテーションにより、エンティティの変更がDBに自動反映（ダーティチェック）されます。
    public ShiftDTO updateShift(Long shiftNumber, ShiftDTO shiftDTO) {

        // 1. リポジトリから既存のエンティティを照会 (DBからデータを取得)
        ShiftEntity shiftEntity = shiftRepository.findById(shiftNumber)
                .orElseThrow(() -> new RuntimeException("該当するシフトが見つかりません。"));

        // 2. 外部キー(店舗)のリレーション処理
        if (shiftDTO.getStoreNumber() != null) {
            StoreEntity store = storeRepository.findById(shiftDTO.getStoreNumber())
                    .orElseThrow(() -> new RuntimeException("店舗情報が見つかりません。"));
            shiftEntity.setStore(store); // リレーションの紐付け
        }

        // 3. 一般フィールドの更新 (DTO -> エンティティ)
        if (shiftDTO.getShiftDate() != null) shiftEntity.setShiftDate(shiftDTO.getShiftDate());
        if (shiftDTO.getStartTime() != null) shiftEntity.setStartTime(shiftDTO.getStartTime());
        if (shiftDTO.getEndTime() != null) shiftEntity.setEndTime(shiftDTO.getEndTime());
        if (shiftDTO.getMaxEmployees() != null) shiftEntity.setMaxEmployees(shiftDTO.getMaxEmployees());

        // 4. 結果返却のため、新しいDTOに変換 (エンティティ -> DTO)
        return convertToDTO(shiftEntity);
    }

    // isTimeOverlap Method ( 時間帯が重ねた場合 )
    private boolean isTimeOverlap(int start1, int end1, int start2, int end2) {
        if (start1 < start2 && end1 > start2) {
            return true;
        }

        if (start1 >= start2 && end1 <= end2) {
            return true;
        }

        if (start1 >= start2 && start1 < end2) {
            return true;
        }

        if (start1 <= start2 && end1 >= end2) {
            return true;
        }

        return false;
    }



    // Entity -> DTO 変換
    private ShiftDTO convertToDTO(ShiftEntity entity) {
        return ShiftDTO.builder()
                .shiftNumber(entity.getShiftNumber())
                .storeNumber(entity.getStore().getStoreNumber())
                .shiftDate(entity.getShiftDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .maxEmployees(entity.getMaxEmployees())
                .currentEmployees(entity.getCurrentEmployees())
                .build();
    }
}