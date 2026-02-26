package com.cathaybk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Coindesk API Response DTO
 * 用於封裝 coindesk.json 的原始資料
 */
public class CurrencyInfo {
        private String code;
        private String symbol;
        private String rate;
        private String description;
        @JsonProperty("rate_float")
        private Double rateFloat;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getRateFloat() {
            return rateFloat;
        }

        public void setRateFloat(Double rateFloat) {
            this.rateFloat = rateFloat;
        }

}

