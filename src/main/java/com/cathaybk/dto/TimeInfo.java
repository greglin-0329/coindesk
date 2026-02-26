package com.cathaybk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Coindesk API Response DTO
 * 用於封裝 coindesk.json 的原始資料
 */
public class TimeInfo {
   private String updated;
   private String updatedISO;
   private String updateduk;

   public String getUpdated() {
            return updated;
        }
   public void setUpdated(String updated) {
            this.updated = updated;
        }

   public String getUpdatedISO() {
            return updatedISO;
        }
   public void setUpdatedISO(String updatedISO) {
     this.updatedISO = updatedISO;
   }

        public String getUpdateduk() {
            return updateduk;
        }
        public void setUpdateduk(String updateduk) {
            this.updateduk = updateduk;
        }

}

