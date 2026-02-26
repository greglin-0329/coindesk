package com.cathaybk.service;

import com.cathaybk.dto.CoindeskApiResponse;
import com.cathaybk.dto.CoindeskResponseDTO;
import com.cathaybk.dto.CurrencyData;
import com.cathaybk.entity.Crud;
import com.cathaybk.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Coindesk Service - 處理 Coindesk API 相關業務邏輯
 */
@Service
public class CoindeskService {

    @Autowired
    private CrudRepository crudRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${coindesk.json.url}")
    private String coindeskJsonUrl;

    // 輸出日期格式：1990/01/01 00:00:00
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * 讀取 coindesk.json 並轉換為 ResponseDTO
     */
    public CoindeskResponseDTO getCoindeskData() {
        // 1. 讀取 coindesk.json
        CoindeskApiResponse apiResponse = readCoindeskJson();

        // 2. 轉換更新時間格式
        String updateTime = convertTimeFormat(apiResponse.getTime().getUpdatedISO());

        // 3. 動態讀取 bpi 中的所有幣別
        List<CurrencyData> currencies = new ArrayList<>();

        // 遍歷 Map 中的所有幣別
        apiResponse.getBpi().getCurrencies().forEach((code, currencyInfo) -> {
            String chineseName = getChineseName(code);
            currencies.add(new CurrencyData(
                    currencyInfo.getCode(),
                    chineseName,
                    currencyInfo.getRateFloat()
            ));
        });

        // 4. 建立並回傳 ResponseDTO
        return new CoindeskResponseDTO(updateTime, currencies);
    }


    /**
     * 透過 RESTful API 讀取 coindesk.json 資料 (帶重試機制)
     */
    private CoindeskApiResponse readCoindeskJson() {
        return restTemplate.getForObject(coindeskJsonUrl, CoindeskApiResponse.class);
    }

    /**
     * 轉換時間格式：ISO 8601 -> yyyy/MM/dd HH:mm:ss
     * 例如：2024-09-02T07:07:20+00:00 -> 2024/09/02 07:07:20
     */
    private String convertTimeFormat(String isoDateTime) {
      try {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(isoDateTime);
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
        return localDateTime.format(OUTPUT_FORMATTER);
      } catch (Exception e) {
        // 如果解析失敗，返回當前時間
        return LocalDateTime.now().format(OUTPUT_FORMATTER);
      }
    }

    /**
     * 從資料庫查詢幣別的中文名稱
     */
    private String getChineseName(String code) {
      Optional<Crud> crud = crudRepository.findByCode(code);
      if (crud.isPresent()) {
        return crud.get().getChineseName();
      }
      return "";
    }
}

