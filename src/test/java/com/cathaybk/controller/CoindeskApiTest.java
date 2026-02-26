package com.cathaybk.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cathaybk.dto.BpiInfo;
import com.cathaybk.dto.CoindeskApiResponse;
import com.cathaybk.dto.CoindeskResponseDTO;
import com.cathaybk.dto.CurrencyInfo;
import com.cathaybk.dto.TimeInfo;
import com.cathaybk.entity.Crud;
import com.cathaybk.repository.CrudRepository;
import com.cathaybk.service.CoindeskService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

/**
 * 測試呼叫coindesk API，並顯示其內容
 */
@DisplayName("測試呼叫coindesk API，並顯示其內容")
public class CoindeskApiTest {

    @Mock
    private CrudRepository crudRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CoindeskService coindeskService;

    private static final String TEST_URL = "https://kengp3.github.io/blog/coindesk.json";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(coindeskService, "coindeskJsonUrl", TEST_URL);
    }

    @Test
    @DisplayName("測試 RestTemplate.getForObject - 顯示 API 回傳內容")
    void testRestTemplateGetForObject_DisplayContent() {
        // Arrange - 準備 Mock API Response
        CoindeskApiResponse apiResponse = createMockApiResponse();

        // Mock RestTemplate 回傳
        when(restTemplate.getForObject(TEST_URL, CoindeskApiResponse.class))
                .thenReturn(apiResponse);

        // Act - 呼叫 RestTemplate
        CoindeskApiResponse result = restTemplate.getForObject(TEST_URL, CoindeskApiResponse.class);

        // ========== 顯示 RestTemplate.getForObject() 回傳內容 ==========
        System.out.println("\n========== RestTemplate.getForObject() 回傳內容 ==========");
        System.out.println("請求 URL: " + TEST_URL);
        System.out.println("回傳類型: " + CoindeskApiResponse.class.getSimpleName());
        System.out.println("\n--- Time 資訊 ---");
        System.out.println("  updatedISO: " + result.getTime().getUpdatedISO());

        System.out.println("\n--- BPI (Bitcoin Price Index) 資訊 ---");
        System.out.println("  幣別數量: " + result.getBpi().getCurrencies().size());

        result.getBpi().getCurrencies().forEach((code, currencyInfo) -> {
            System.out.println("\n  幣別: " + code);
            System.out.println("    code: " + currencyInfo.getCode());
            System.out.println("    rate_float: " + currencyInfo.getRateFloat());
        });

        System.out.println("\n========== 顯示結束 ==========\n");

        // Assert - 驗證回傳內容
        assertNotNull(result, "RestTemplate 回傳結果不應為 null");
        assertNotNull(result.getTime(), "Time 資訊不應為 null");
        assertNotNull(result.getBpi(), "BPI 資訊不應為 null");
        assertEquals(3, result.getBpi().getCurrencies().size(), "應包含 3 種幣別");

        // 驗證 RestTemplate 被呼叫
        verify(restTemplate, times(1)).getForObject(TEST_URL, CoindeskApiResponse.class);
    }

    @Test
    @DisplayName("測試 readCoindeskJson - 展示 RestTemplate 在 Service 中的使用")
    void testReadCoindeskJson_RestTemplateInService() {
        // Arrange
        CoindeskApiResponse mockApiResponse = createMockApiResponse();

        when(restTemplate.getForObject(TEST_URL, CoindeskApiResponse.class))
                .thenReturn(mockApiResponse);

        when(crudRepository.findByCode(anyString())).thenReturn(Optional.of(new Crud("USD", "美元")));

        // Act - 透過 Service 間接呼叫 RestTemplate
        System.out.println("\n========== Service 呼叫流程 ==========");
        System.out.println("1. CoindeskService.getCoindeskData() 被呼叫");
        System.out.println("2. 內部呼叫 readCoindeskJson()");
        System.out.println("3. readCoindeskJson() 使用 RestTemplate.getForObject()");
        System.out.println("   - URL: " + TEST_URL);
        System.out.println("   - 回傳類型: CoindeskApiResponse.class");

        CoindeskResponseDTO result = coindeskService.getCoindeskData();

        System.out.println("\n4. RestTemplate 回傳 CoindeskApiResponse");
        System.out.println("   - Time: " + mockApiResponse.getTime().getUpdatedISO());
        System.out.println("   - BPI 幣別數: " + mockApiResponse.getBpi().getCurrencies().size());

        System.out.println("\n5. Service 進行資料轉換");
        System.out.println("   - 時間格式轉換: ISO 8601 → yyyy/MM/dd HH:mm:ss");
        System.out.println("   - 動態讀取所有幣別");
        System.out.println("   - 查詢資料庫取得中文名稱");

        System.out.println("\n6. 回傳 CoindeskResponseDTO");
        System.out.println("   - updateTime: " + result.getUpdateTime());
        System.out.println("   - currencies 數量: " + result.getCurrencies().size());

        result.getCurrencies().forEach(currency -> {
            System.out.println("     ● " + currency.getCode() + " / " +
                             currency.getChineseName() + " / " + currency.getRate());
        });

        System.out.println("========== 流程結束 ==========\n");

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getCurrencies().size());
        verify(restTemplate, times(1)).getForObject(TEST_URL, CoindeskApiResponse.class);
    }

    /**
     * 建立 Mock API Response (3 個幣別)
     */
    private CoindeskApiResponse createMockApiResponse() {
        CoindeskApiResponse response = new CoindeskApiResponse();

        // Time Info
        TimeInfo timeInfo = new TimeInfo();
        ReflectionTestUtils.setField(timeInfo, "updatedISO", "2024-09-02T07:07:20+00:00");
        ReflectionTestUtils.setField(response, "time", timeInfo);

        // BPI Info - 使用 Map 動態加入幣別
        BpiInfo bpiInfo = new BpiInfo();

        CurrencyInfo usd = new CurrencyInfo();
        usd.setCode("USD");
        usd.setRateFloat(57756.2984);
        bpiInfo.setCurrency("USD", usd);

        CurrencyInfo gbp = new CurrencyInfo();
        gbp.setCode("GBP");
        gbp.setRateFloat(43984.0203);
        bpiInfo.setCurrency("GBP", gbp);

        CurrencyInfo eur = new CurrencyInfo();
        eur.setCode("EUR");
        eur.setRateFloat(52243.2865);
        bpiInfo.setCurrency("EUR", eur);

        ReflectionTestUtils.setField(response, "bpi", bpiInfo);

        return response;
    }

    /**
     * 建立 Mock API Response (4 個幣別，含 JPY)
     */
    private CoindeskApiResponse createMockApiResponseWith4Currencies() {
        CoindeskApiResponse response = createMockApiResponse();

        // 新增 JPY
        CurrencyInfo jpy = new CurrencyInfo();
        jpy.setCode("JPY");
        jpy.setRateFloat(8234567.89);
        response.getBpi().setCurrency("JPY", jpy);

        return response;
    }
}

