package com.cathaybk.controller;

import com.cathaybk.dto.*;
import com.cathaybk.entity.Crud;
import com.cathaybk.repository.CrudRepository;
import com.cathaybk.service.CoindeskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 資料轉換相關邏輯單元測試
 */
@DisplayName("資料轉換相關邏輯單元測試")
public class DataTransformLogicTest {

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
    @DisplayName("測試資料轉換 - 完整流程（3 個幣別）")
    void testGetCoindeskData_FullConversion() {
        // Arrange - 準備 Mock API Response
        CoindeskApiResponse apiResponse = createMockApiResponse();

        // Mock RestTemplate 回傳
        when(restTemplate.getForObject(TEST_URL, CoindeskApiResponse.class))
                .thenReturn(apiResponse);

        // Mock 資料庫查詢中文名稱
        when(crudRepository.findByCode("USD")).thenReturn(Optional.of(new Crud("USD", "美元")));
        when(crudRepository.findByCode("GBP")).thenReturn(Optional.of(new Crud("GBP", "英鎊")));
        when(crudRepository.findByCode("EUR")).thenReturn(Optional.of(new Crud("EUR", "歐元")));

        // Act - 執行轉換
        CoindeskResponseDTO result = coindeskService.getCoindeskData();

        // Assert - 驗證轉換結果
        assertNotNull(result, "轉換結果不應為 null");
        assertNotNull(result.getUpdateTime(), "更新時間不應為 null");
        assertEquals(3, result.getCurrencies().size(), "應轉換 3 種幣別");

        // 驗證時間格式轉換
        assertTrue(result.getUpdateTime().matches("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}"),
                "時間格式應為 yyyy/MM/dd HH:mm:ss");

        // 驗證 USD 資料
        CurrencyData usd = result.getCurrencies().stream()
                .filter(c -> c.getCode().equals("USD"))
                .findFirst()
                .orElse(null);
        assertNotNull(usd, "應包含 USD");
        assertEquals("USD", usd.getCode());
        assertEquals("美元", usd.getChineseName());
        assertEquals(57756.2984, usd.getRate(), 0.0001);

        // 驗證 API 和資料庫被呼叫
        verify(restTemplate, times(1)).getForObject(TEST_URL, CoindeskApiResponse.class);
        verify(crudRepository, times(1)).findByCode("USD");
        verify(crudRepository, times(1)).findByCode("GBP");
        verify(crudRepository, times(1)).findByCode("EUR");
    }

    @Test
    @DisplayName("測試資料轉換 - 動態讀取所有幣別")
    void testGetCoindeskData_DynamicBpiParsing() {
        // Arrange - 包含 4 種幣別的測試資料
        CoindeskApiResponse apiResponse = createMockApiResponseWith4Currencies();

        when(restTemplate.getForObject(TEST_URL, CoindeskApiResponse.class))
                .thenReturn(apiResponse);

        // Mock 4 種幣別的中文名稱
        when(crudRepository.findByCode("USD")).thenReturn(Optional.of(new Crud("USD", "美元")));
        when(crudRepository.findByCode("GBP")).thenReturn(Optional.of(new Crud("GBP", "英鎊")));
        when(crudRepository.findByCode("EUR")).thenReturn(Optional.of(new Crud("EUR", "歐元")));
        when(crudRepository.findByCode("JPY")).thenReturn(Optional.of(new Crud("JPY", "日圓")));

        // Act
        CoindeskResponseDTO result = coindeskService.getCoindeskData();

        // Assert
        assertEquals(4, result.getCurrencies().size(), "應動態讀取 4 種幣別");

        // 驗證 JPY 也被正確轉換
        CurrencyData jpy = result.getCurrencies().stream()
                .filter(c -> c.getCode().equals("JPY"))
                .findFirst()
                .orElse(null);
        assertNotNull(jpy, "應包含 JPY");
        assertEquals("日圓", jpy.getChineseName());
    }

    @Test
    @DisplayName("測試資料轉換 - ISO 時間格式轉換")
    void testGetCoindeskData_TimeFormatConversion() {
        // Arrange - 測試不同的 ISO 時間格式
        CoindeskApiResponse apiResponse = createMockApiResponse();
        TimeInfo timeInfo = apiResponse.getTime();

        // 使用標準 ISO 8601 格式
        ReflectionTestUtils.setField(timeInfo, "updatedISO", "2024-09-02T07:07:20+00:00");

        when(restTemplate.getForObject(TEST_URL, CoindeskApiResponse.class))
                .thenReturn(apiResponse);

        when(crudRepository.findByCode(anyString())).thenReturn(Optional.of(new Crud("USD", "美元")));

        // Act
        CoindeskResponseDTO result = coindeskService.getCoindeskData();

        // Assert
        assertNotNull(result.getUpdateTime());
        assertTrue(result.getUpdateTime().matches("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        assertTrue(result.getUpdateTime().startsWith("2024/09/02"), "日期應正確轉換");
    }

    @Test
    @DisplayName("測試資料轉換 - 資料庫無中文名稱時使用預設值")
    void testGetCoindeskData_DatabaseNotFound() {
        // Arrange
        CoindeskApiResponse apiResponse = createMockApiResponse();

        when(restTemplate.getForObject(TEST_URL, CoindeskApiResponse.class))
                .thenReturn(apiResponse);

        // Mock 資料庫查不到
        when(crudRepository.findByCode(anyString())).thenReturn(Optional.empty());

        // Act
        CoindeskResponseDTO result = coindeskService.getCoindeskData();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getCurrencies().size());

        // 驗證當資料庫查不到時，中文名稱為空字串
        result.getCurrencies().forEach(currency -> {
            assertEquals("", currency.getChineseName(), "資料庫查不到時應為空字串");
        });
    }

    @Test
    @DisplayName("測試資料轉換 - 匯率精確度保留")
    void testGetCoindeskData_RatePrecision() {
        // Arrange - 測試高精度匯率
        CoindeskApiResponse apiResponse = createMockApiResponse();

        // 設定高精度匯率
        BpiInfo bpiInfo = apiResponse.getBpi();
        Map<String, CurrencyInfo> currencies = bpiInfo.getCurrencies();
        currencies.get("USD").setRateFloat(57756.298456789123);

        when(restTemplate.getForObject(TEST_URL, CoindeskApiResponse.class))
                .thenReturn(apiResponse);

        when(crudRepository.findByCode("USD")).thenReturn(Optional.of(new Crud("USD", "美元")));
        when(crudRepository.findByCode(anyString())).thenReturn(Optional.empty());

        // Act
        CoindeskResponseDTO result = coindeskService.getCoindeskData();

        // Assert
        CurrencyData usd = result.getCurrencies().stream()
                .filter(c -> c.getCode().equals("USD"))
                .findFirst()
                .orElse(null);

        assertNotNull(usd);
        assertEquals(57756.298456789123, usd.getRate(), 0.0000000001, "匯率精確度應保留");
    }

    @Test
    @DisplayName("測試資料轉換 - API 呼叫失敗拋出異常")
    void testGetCoindeskData_ApiCallFails() {
        // Arrange
        when(restTemplate.getForObject(TEST_URL, CoindeskApiResponse.class))
                .thenThrow(new RuntimeException("讀取 Coindesk API 失敗，已重試 3 次"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            coindeskService.getCoindeskData();
        });

        assertTrue(exception.getMessage().contains("讀取 Coindesk API 失敗"),
                   "應拋出正確的異常訊息");
    }

    @Test
    @DisplayName("測試資料轉換 - BPI Map 為空時")
    void testGetCoindeskData_EmptyBpiMap() {
        // Arrange
        CoindeskApiResponse apiResponse = createMockApiResponse();
        apiResponse.getBpi().getCurrencies().clear(); // 清空所有幣別

        when(restTemplate.getForObject(TEST_URL, CoindeskApiResponse.class))
                .thenReturn(apiResponse);

        // Act
        CoindeskResponseDTO result = coindeskService.getCoindeskData();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getCurrencies().size(), "沒有幣別時應回傳空清單");
    }

    @Test
    @DisplayName("測試資料轉換 - 驗證所有幣別都被處理")
    void testGetCoindeskData_AllCurrenciesProcessed() {
        // Arrange
        CoindeskApiResponse apiResponse = createMockApiResponse();

        when(restTemplate.getForObject(TEST_URL, CoindeskApiResponse.class))
                .thenReturn(apiResponse);

        when(crudRepository.findByCode("USD")).thenReturn(Optional.of(new Crud("USD", "美元")));
        when(crudRepository.findByCode("GBP")).thenReturn(Optional.of(new Crud("GBP", "英鎊")));
        when(crudRepository.findByCode("EUR")).thenReturn(Optional.of(new Crud("EUR", "歐元")));

        // Act
        CoindeskResponseDTO result = coindeskService.getCoindeskData();

        // Assert
        assertEquals(3, result.getCurrencies().size());

        // 驗證所有幣別都存在
        assertTrue(result.getCurrencies().stream().anyMatch(c -> c.getCode().equals("USD")));
        assertTrue(result.getCurrencies().stream().anyMatch(c -> c.getCode().equals("GBP")));
        assertTrue(result.getCurrencies().stream().anyMatch(c -> c.getCode().equals("EUR")));

        // 驗證所有幣別都有匯率
        result.getCurrencies().forEach(currency -> {
            assertNotNull(currency.getRate(), "每個幣別都應有匯率");
            assertTrue(currency.getRate() > 0, "匯率應大於 0");
        });
    }

    // ========== 輔助方法 ==========

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

