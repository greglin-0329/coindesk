package com.cathaybk.service;

import com.cathaybk.entity.Crud;
import com.cathaybk.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Currency Service - 幣別服務
 */
@Service
@Transactional
public class CrudService {

    @Autowired
    private CrudRepository crudRepository;

    /**
     * 查詢所有幣別
     */
    public List<Crud> findAll() {
        return crudRepository.findAll();
    }

    /**
     * 根據幣別代碼查詢
     */
    public Optional<Crud> findByCode(String code) {
        return crudRepository.findByCode(code);
    }

    /**
     * 新增幣別
     */
    public Crud save(Crud currency) {
        currency.setUpdateTime(LocalDateTime.now());
        return crudRepository.save(currency);
    }

    /**
     * 更新幣別
     */
    public Crud update(String code, String chineseName) {
        Optional<Crud> optionalCurrency = crudRepository.findByCode(code);
        if (optionalCurrency.isPresent()) {
            Crud currency = optionalCurrency.get();
            currency.setChineseName(chineseName);
            currency.setUpdateTime(LocalDateTime.now());
            return crudRepository.save(currency);
        }
        throw new RuntimeException("Currency not found: " + code);
    }

    /**
     * 刪除幣別
     */
    public void deleteByCode(String code) {
        Optional<Crud> optionalCurrency = crudRepository.findByCode(code);
        if (optionalCurrency.isPresent()) {
            crudRepository.delete(optionalCurrency.get());
        } else {
            throw new RuntimeException("Currency not found: " + code);
        }
    }

    /**
     * 檢查幣別是否存在
     */
    public boolean existsByCode(String code) {
        return crudRepository.existsByCode(code);
    }
}

