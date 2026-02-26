package com.cathaybk.controller;

import com.cathaybk.entity.Crud;
import com.cathaybk.service.CrudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 測試呼叫CRUD API，並顯示其內容
 */
@DisplayName("測試呼叫CRUD API，並顯示其內容")
public class CrudApiTest {

    @Mock
    private CrudService crudService;

    @InjectMocks
    private CrudController crudController;

    private List<Crud> mockCrudList;
    private Crud mockUsd;
    private Crud mockGbp;
    private Crud mockEur;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 準備測試資料
        LocalDateTime now = LocalDateTime.now();
        mockUsd = new Crud(1L, "USD", "美元", now);
        mockGbp = new Crud(2L, "GBP", "英鎊", now);
        mockEur = new Crud(3L, "EUR", "歐元", now);

        mockCrudList = new ArrayList<>();
        mockCrudList.add(mockUsd);
        mockCrudList.add(mockGbp);
        mockCrudList.add(mockEur);
    }

    // ========== GET /api/crud/query/getAll 測試 ==========

    @Test
    @DisplayName("測試 getAll - 成功查詢所有幣別")
    void testGetAll_Success() {
        // Arrange
        when(crudService.findAll()).thenReturn(mockCrudList);

        // Act
        ResponseEntity<List<Crud>> response = crudController.getAll();

        // Assert
        assertNotNull(response, "回應不應為 null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP 狀態碼應為 200 OK");
        assertNotNull(response.getBody(), "回應內容不應為 null");

        // ========== 印出查詢結果 ==========
        System.out.println("\n========== 查詢所有幣別結果 ==========");
        System.out.println("HTTP 狀態碼: " + response.getStatusCode());
        System.out.println("總筆數: " + response.getBody().size());
        System.out.println("\n幣別清單:");
        System.out.println("─────────────────────────────────────────────────────");

        for (int i = 0; i < response.getBody().size(); i++) {
            Crud crud = response.getBody().get(i);
            System.out.println("第 " + (i + 1) + " 筆:");
            System.out.println("  ID: " + crud.getId());
            System.out.println("  幣別代碼: " + crud.getCode());
            System.out.println("  中文名稱: " + crud.getChineseName());
            System.out.println("  更新時間: " + crud.getUpdateTime());
            System.out.println("─────────────────────────────────────────────────────");
        }
        System.out.println("========== 查詢結束 ==========\n");

        // 驗證第一筆資料 (USD)
        Crud firstCrud = response.getBody().get(0);
        assertEquals("USD", firstCrud.getCode(), "第一筆幣別代碼應為 USD");
        assertEquals("美元", firstCrud.getChineseName(), "第一筆中文名稱應為美元");
        assertNotNull(firstCrud.getId(), "ID 不應為 null");
        assertNotNull(firstCrud.getUpdateTime(), "更新時間不應為 null");

        // 驗證第二筆資料 (GBP)
        Crud secondCrud = response.getBody().get(1);
        assertEquals("GBP", secondCrud.getCode(), "第二筆幣別代碼應為 GBP");
        assertEquals("英鎊", secondCrud.getChineseName(), "第二筆中文名稱應為英鎊");

        // 驗證第三筆資料 (EUR)
        Crud thirdCrud = response.getBody().get(2);
        assertEquals("EUR", thirdCrud.getCode(), "第三筆幣別代碼應為 EUR");
        assertEquals("歐元", thirdCrud.getChineseName(), "第三筆中文名稱應為歐元");

        // 驗證 Service 被呼叫一次
        verify(crudService, times(1)).findAll();
    }
}

