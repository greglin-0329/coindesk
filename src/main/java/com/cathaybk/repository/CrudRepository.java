package com.cathaybk.repository;

import com.cathaybk.entity.Crud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Currency Repository - 幣別資料存取介面
 */
@Repository
public interface CrudRepository extends JpaRepository<Crud, Long> {

    /**
     * 根據幣別代碼查詢
     * @param code 幣別代碼 (例如: USD, EUR, GBP)
     * @return Currency
     */
    Optional<Crud> findByCode(String code);

    /**
     * 檢查幣別代碼是否存在
     * @param code 幣別代碼
     * @return boolean
     */
    boolean existsByCode(String code);
}

