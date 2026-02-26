package com.cathaybk.controller;

import com.cathaybk.dto.CoindeskResponseDTO;
import com.cathaybk.service.CoindeskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Coindesk API Controller
 * 讀取 coindesk.json 並回傳更新時間、幣別及匯率
 */
@RestController
public class CoinDeskController {

  @Autowired
  private CoindeskService coindeskService;

  /**
   * 讀取 coindesk.json 內容並回傳
   * GET /api/coindesk
   *
   * @return CoindeskResponseDTO 包含：
   *         - updateTime: 更新時間 (格式: yyyy/MM/dd HH:mm:ss)
   *         - currencies: 幣別資料列表 (code, chineseName, rate)
   */
  @GetMapping("/api/coindesk")
  public ResponseEntity<CoindeskResponseDTO> getCoindesk() {
    try {
      CoindeskResponseDTO response = coindeskService.getCoindeskData();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}

