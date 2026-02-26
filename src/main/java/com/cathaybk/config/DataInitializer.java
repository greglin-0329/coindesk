package com.cathaybk.config;

import com.cathaybk.entity.Crud;
import com.cathaybk.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Data Initializer - 初始化資料
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CrudRepository crudRepository;

    @Override
    public void run(String... args) throws Exception {
        // 檢查資料庫是否已有資料
        if (crudRepository.count() == 0) {
            // 初始化幣別資料
            crudRepository.save(new Crud("USD", "美元"));
            crudRepository.save(new Crud("EUR", "歐元"));
            crudRepository.save(new Crud("GBP", "英鎊"));
            crudRepository.save(new Crud("JPY", "日圓"));
            crudRepository.save(new Crud("CNY", "人民幣"));

            System.out.println("========================================");
            System.out.println("初始化CRUB資料表完成！");
            System.out.println("========================================");
        }
    }
}

