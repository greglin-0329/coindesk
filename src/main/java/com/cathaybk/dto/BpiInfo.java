package com.cathaybk.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Coindesk API Response DTO
 * 用於封裝 coindesk.json 的原始資料
 */
public class BpiInfo {
   private LinkedHashMap<String, CurrencyInfo> currencies = new LinkedHashMap<>();

   /**
    * 使用 @JsonAnySetter 動態接收所有幣別資料
    * 例如：USD, GBP, EUR 或任何其他幣別
    */
   @JsonAnySetter
   public void setCurrency(String key, CurrencyInfo value) {
      currencies.put(key, value);
   }

   public Map<String, CurrencyInfo> getCurrencies() {
      return currencies;
   }

   public void setCurrencies(LinkedHashMap<String, CurrencyInfo> currencies) {
      this.currencies = currencies;
   }
}

