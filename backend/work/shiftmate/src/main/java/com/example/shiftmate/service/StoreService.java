package com.example.shiftmate.service;

import com.example.shiftmate.dto.StoreDTO;
import com.example.shiftmate.entity.StoreEntity;
import com.example.shiftmate.entity.UserEntity;
import com.example.shiftmate.exception.ShiftMateException;
import com.example.shiftmate.repository.StoreRepository;
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
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    // 店舗登録
    public StoreDTO registerStore(StoreDTO storeDTO) {
        try {
            // ユーザー確認
            Optional<UserEntity> userOptional = userRepository.findById(storeDTO.getOwnerUserNumber());
            if(!userOptional.isPresent()){
                throw new ShiftMateException("ユーザーを探せません。");
            }

            UserEntity user = userOptional.get();
            if (!"店長".equals(user.getUserType())) {
                throw new ShiftMateException("職員は店舗登録が不可能です。");
            }

            // 店舗生成
            StoreEntity storeEntity = StoreEntity.builder()
                    .storeName(storeDTO.getStoreName())
                    .storeAddress(storeDTO.getStoreAddress())
                    .category(storeDTO.getCategory())
                    .owner(user)
                    .autoApprove(storeDTO.getAutoApprove() != null ? storeDTO.getAutoApprove() : false)
                    .build();

            StoreEntity savedStore = storeRepository.save(storeEntity);

            return convertToDTO(savedStore);
        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("店舗登録中エラー発生", e);
        }
    }

    // 店長の店舗リスト紹介
    public List<StoreDTO> getOwnerStores(Long ownerUserNumber) {
        try {
            List<StoreEntity> stores = storeRepository.findByOwner_UserNumber(ownerUserNumber);
            return stores.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("店舗リスト照会中エラー発生", e);
        }
    }

    // 全ての店舗リスト照会
    public List<StoreDTO> getAllStores() {
        try {
            List<StoreEntity> stores = storeRepository.findAll();
            return stores.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("店舗リスト照会中エラー発生", e);
        }
    }

    // storeNumberで店舗照会
    public StoreDTO getStoreByNumber(Long storeNumber) {
        try {
            Optional<StoreEntity> storeOptional = storeRepository.findById(storeNumber);
            if ( !storeOptional.isPresent() ) {
                throw new ShiftMateException("店舗を探せません。");
            }
            return convertToDTO(storeOptional.get());
        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("店舗照会中エラー発生", e);
        }
    }

    // Entity -> DTO 変換
    private StoreDTO convertToDTO(StoreEntity entity) {
        return StoreDTO.builder()
                .storeNumber(entity.getStoreNumber())
                .storeName(entity.getStoreName())
                .storeAddress(entity.getStoreAddress())
                .category(entity.getCategory())
                .ownerUserNumber(entity.getOwner().getUserNumber())
                .autoApprove(entity.getAutoApprove())
                .build();
    }
}
