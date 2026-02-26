package com.cathaybk.dto;

/**
 * Currency Data DTO
 * 用於封裝幣別資料：代碼、中文名稱、匯率
 */
public class CurrencyData {
    private String code;
    private String chineseName;
    private Double rate;

    public CurrencyData() {
    }

    public CurrencyData(String code, String chineseName, Double rate) {
       this.code = code;
       this.chineseName = chineseName;
       this.rate = rate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}

