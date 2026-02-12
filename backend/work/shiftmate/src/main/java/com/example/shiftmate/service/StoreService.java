package com.example.shiftmate.service;

import com.example.shiftmate.dto.StoreDTO;
import com.example.shiftmate.entity.StoreEntity;
import com.example.shiftmate.entity.UserEntity;
import com.example.shiftmate.exception.ShiftMateException;
import com.example.shiftmate.repository.StoreRepository;
import com.example.shiftmate.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Store;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService  {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    //店舗登録
    public StoreDTO registerStore(StoreDTO storeDTO){
        try{
            //ユーザー確認
            Optional<UserEntity> userOptional = userRepository.findById(storeDTO.getOwnerUserNumber());
            if(!userOptional.isPresent()){
                throw new ShiftMateException("ユーザーを探せません。");
            }

            UserEntity user = userOptional.get();
            if(!"店長".equals(user.getUserType())){
                throw new ShiftMateException("職員は店舗登録が不可能です。");
            }

            //店舗生産
            StoreEntity storeEntity = StoreEntity.builder()
                    .storeName(storeDTO.getStoreName())
                    .storeAddress(storeDTO.getStoreAddress())
                    .category(storeDTO.getCategory())
                    .owner(user)
                    .autoApprove(storeDTO.getAutoApprove() != null ? storeDTO.getAutoApprove() : false)
                    .build();

            StoreEntity savedStore = storeRepository.save(storeEntity);

            return convertToDTO(savedStore);
        }catch (ShiftMateException e){
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            throw new ShiftMateException("店舗登録中エラー発生", e);
        }
    }

    //店長の店舗リスト紹介
    public List<StoreDTO> getOwnerStores(Long ownerUserNumber){
        try{
            List<StoreEntity> stores = storeRepository.findByOwner_UserNumber(ownerUserNumber);
            return stores.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("店舗登録中エラー発生", e);
        }
    }

    // 全ての店舗リスト照会
    public List<StoreDTO> getAllStores(){
        try{
            List<StoreEntity> stores = storeRepository.findAll();
            return stores.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("店舗リスト照会中エラー発生", e);
        }
    }

    // storeNumberで店舗照会
    public StoreDTO getStoreByNumber(Long storeNumber){
        try{
            Optional<StoreEntity> storeOptional = storeRepository.findById(storeNumber);
            if ( !storeOptional.isPresent() ){
                throw new ShiftMateException("店舗を探せません。");
            }
            return convertToDTO(storeOptional.get());
        }catch (ShiftMateException e){
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            throw new ShiftMateException("店舗紹介中エラー発生", e);
        }
    }



    //　店舗情報変更
    public StoreDTO updateStore(Long storeNumber, StoreDTO storeDTO, Long ownerUserNumber){
        try{
            // 1.　店舗存在確認
            StoreEntity storeEntity = storeRepository.findById(storeNumber)
                    .orElseThrow(() -> new ShiftMateException("店舗が見つかりません。"));
            // 2. 本人確認
            if (!storeEntity.getOwner().getUserNumber().equals(ownerUserNumber)){
                throw new ShiftMateException("店舗情報を修正する権限がありません。");
            }

            // 3. 情報アップデート(Entity 内部の値を変更)
            StoreEntity updatedStore = StoreEntity.builder()
                    .storeNumber(storeEntity.getStoreNumber())
                    .storeName(storeDTO.getStoreName())
                    .storeAddress(storeDTO.getStoreAddress())
                    .category(storeDTO.getCategory())
                    .owner(storeEntity.getOwner())
                    .createdAt(storeEntity.getCreatedAt())
                    .updatedAt(storeEntity.getUpdatedAt())
                    .autoApprove(storeDTO.getAutoApprove() !=null ? storeDTO.getAutoApprove() : storeEntity.getAutoApprove())
                    .build();

            // 4. 保存及びDTO返還
            return convertToDTO(storeRepository.save(updatedStore));

        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("店舗修正中エラーが発生しました。", e);
        }
    }

    // 店舗削除
    public void deleteStore(Long storeNumber, Long ownerUserNumber) {
        try {
            // 1. 店舗存在確認
            StoreEntity storeEntity = storeRepository.findById(storeNumber)
                    .orElseThrow(() -> new ShiftMateException("店舗が見つかりません。"));

            // 2. 権限確認
            if (!storeEntity.getOwner().getUserNumber().equals(ownerUserNumber)){
                throw new ShiftMateException(("店舗を削除する権限がありません。"));
            }

            // 3. 削除実行
            storeRepository.delete(storeEntity);
        } catch (ShiftMateException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShiftMateException("店舗削除中にエラー発生しました。");
        }
    }
    //Entity -> 変換
    private StoreDTO convertToDTO(StoreEntity entity){
        return StoreDTO.builder()
                .storeNumber(entity.getStoreNumber())
                .storeName(entity.getStoreName())
                .storeAddress(entity.getStoreAddress())
                .category(entity.getCategory())
                .ownerUserNumber(entity.getOwner().getUserNumber())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .autoApprove(entity.getAutoApprove())
                .build();
    }
}