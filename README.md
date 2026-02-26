# Coindesk API 專案

### 1. 測試 API

使用 Postman、curl 或瀏覽器測試 API：

# 取得比特幣價格
curl http://localhost:8080/api/coindesk

# 查詢所有幣別
curl http://localhost:8080/api/crud/query/getAll

# 查詢單一幣別
curl http://localhost:8080/api/crud/query/USD

# 新增幣別
curl -X POST http://localhost:8080/api/crud/insert \
  -H "Content-Type: application/json" \
  -d '{"code":"TWD","chineseName":"臺幣"}'

# 更新幣別
curl -X POST http://localhost:8080/api/crud/update \
  -H "Content-Type: application/json" \
  -d '{"code":"USD","chineseName":"美金"}'

# 刪除幣別
curl -X POST http://localhost:8080/api/crud/delete \
  -H "Content-Type: application/json" \
  -d '{"code":"TWD"}'

## API 端點總覽表

| API 分類 | HTTP 方法 | 端點 | 功能 |
|---------|----------|------|------|
| **Coindesk API** | | | |
| | GET | `/api/coindesk` | 取得比特幣價格資訊 |
| **幣別管理** | | | |
| | GET | `/api/crud/query/getAll` | 查詢所有幣別 |
| | GET | `/api/crud/query/{code}` | 根據代碼查詢幣別 |
| | POST | `/api/crud/insert` | 新增幣別 |
| | POST | `/api/crud/update` | 更新幣別 |
| | POST | `/api/crud/delete` | 刪除幣別 |



