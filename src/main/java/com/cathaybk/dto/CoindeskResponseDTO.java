package com.cathaybk.dto;

import java.util.List;

/**
 * Coindesk Response DTO
 * 用於回傳處理後的資料：更新時間、幣別及匯率
 */
public class CoindeskResponseDTO {

    private String updateTime;
    private List<CurrencyData> currencies;

    public CoindeskResponseDTO() {
    }

    public CoindeskResponseDTO(String updateTime, List<CurrencyData> currencies) {
        this.updateTime = updateTime;
        this.currencies = currencies;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<CurrencyData> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CurrencyData> currencies) {
        this.currencies = currencies;
    }
}

