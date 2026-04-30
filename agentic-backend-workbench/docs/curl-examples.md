# 调用示例

## 运行工作流并返回 JSON

```bash
curl -X POST http://localhost:8080/api/workflows/backend-dev \
  -H 'Content-Type: application/json' \
  -d @docs/sample-request.json
```

## 运行工作流并导出 ZIP

```bash
curl -X POST http://localhost:8080/api/workflows/backend-dev/zip \
  -H 'Content-Type: application/json' \
  -d @docs/sample-request.json \
  -o generated-code.zip
```
