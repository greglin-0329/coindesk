package com.cathaybk.controller;

import com.cathaybk.dto.CoindeskResponseDTO;
import com.cathaybk.dto.CurrencyData;
import com.cathaybk.entity.Crud;
import com.cathaybk.service.CoindeskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * CoinDeskController 單元測試
 * 測試 getCoindesk API 的資料轉換功能
 */
@DisplayName("測試資料轉換API，並顯示其內容")
public class CoinDeskControllerTest {

    @Mock
    private CoindeskService coindeskService;

    @InjectMocks
    private CoinDeskController coinDeskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("測試 getCoindesk - ResponseEntity 結構完整性")
    void testGetCoindesk_ResponseStructure() {
        // Arrange
        String expectedUpdateTime = "2024/09/02 07:07:20";
        List<CurrencyData> expectedCurrencies = new ArrayList<>();
        expectedCurrencies.add(new CurrencyData("USD", "美元", 57756.2984));
        expectedCurrencies.add(new CurrencyData("GBP", "英鎊", 43984.0203));
        expectedCurrencies.add(new CurrencyData("EUR", "歐元", 52243.2865));

        CoindeskResponseDTO mockResponse = new CoindeskResponseDTO(expectedUpdateTime, expectedCurrencies);

        when(coindeskService.getCoindeskData()).thenReturn(mockResponse);

        // Act
        ResponseEntity<CoindeskResponseDTO> response = coinDeskController.getCoindesk();

        // ========== 印出所有 ResponseEntity 資料 ==========
        System.out.println("\n========================================");
        System.out.println("=== ResponseEntity 完整資料 ===");
        System.out.println("========================================");

        // 1. HTTP 狀態碼
        System.out.println("\n【HTTP 狀態資訊】");
        System.out.println("  Status Code: " + response.getStatusCode());
        System.out.println("  Status Code Value: " + response.getStatusCodeValue());
        System.out.println("  Status Code (數字): " + response.getStatusCode().value());

        // 3. ResponseEntity Body
        System.out.println("\n【Response Body】");
        if (response.getBody() != null) {
            CoindeskResponseDTO body = response.getBody();

            // 3.1 更新時間
            System.out.println("  更新時間: " + body.getUpdateTime());

            // 3.2 幣別資料
            System.out.println("\n  幣別清單:");
            System.out.println("  總筆數: " + body.getCurrencies().size());
            System.out.println("  ─────────────────────────────────────");

            for (int i = 0; i < body.getCurrencies().size(); i++) {
                CurrencyData currency = body.getCurrencies().get(i);
                System.out.println("  第 " + (i + 1) + " 筆:");
                System.out.println("    幣別代碼: " + currency.getCode());
                System.out.println("    中文名稱: " + currency.getChineseName());
                System.out.println("    匯率: " + currency.getRate());
                System.out.println("  ─────────────────────────────────────");
            }
        } else {
            System.out.println("  (Body 為 null)");
        }
        System.out.println("\n========================================");
        System.out.println("=== 資料印出結束 ===");
        System.out.println("========================================\n");
    }
}

