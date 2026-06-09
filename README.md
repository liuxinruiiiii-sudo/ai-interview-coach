# AI 面试学习助手

基于 **Spring Boot + Vue3 + DeepSeek API** 开发的 AI 面试学习助手，支持简历分析、面试模拟、学习计划生成等功能。

## 技术栈

| 层级 | 技术 |
|---|---|
| 后端 | Java 17 / Spring Boot 3.2 / Maven |
| 前端 | Vue3 / Vite / Axios |
| AI | DeepSeek API |
| PDF | Apache PDFBox 3.0 |
| 数据库 | 无（内存版，暂不接库） |

## 功能

- **AI 问答** — 输入问题，DeepSeek 实时回复
- **PDF 简历分析** — 上传简历 PDF，AI 提取内容并给出面试建议
- **面试模拟** — AI 化身面试官，模拟真实面试场景
- **学习计划** — 根据目标岗位生成个性化备考方案

## 项目结构

```
ai-job-helper/
├── src/main/java/com/example/aijobhelper/
│   ├── AiJobHelperApplication.java       # 启动类
│   ├── config/
│   │   └── RestTemplateConfig.java       # RestTemplate Bean
│   ├── controller/
│   │   ├── HealthController.java         # GET /api/health
│   │   └── ChatController.java           # 聊天接口
│   ├── service/
│   │   ├── PdfService.java               # PDFBox 解析 PDF
│   │   └── DeepSeekService.java          # DeepSeek API 调用
│   ├── model/
│   │   ├── dto/                           # 请求/响应 DTO
│   │   └── vo/ApiResult.java             # 统一返回格式
│   └── exception/
│       ├── BusinessException.java         # 业务异常
│       └── GlobalExceptionHandler.java    # 全局异常处理
├── frontend/
│   └── src/
│       ├── App.vue                        # 主布局（三栏）
│       ├── api/chat.js                    # Axios 请求封装
│       └── components/
│           ├── Sidebar.vue                # 侧边栏（模式+会话）
│           ├── ChatWindow.vue             # 聊天窗口
│           ├── ChatInput.vue              # 输入框+文件上传
│           └── KnowledgePanel.vue         # 知识库面板
├── pom.xml
└── README.md
```

## 后端启动

```bash
cd F:\ai-job-helper
D:\apache-maven-3.9.4\bin\mvn.cmd spring-boot:run
```

启动前请确认 `application.yml` 中 DeepSeek API Key 已配置。

### API Key 配置

项目使用 Spring Profile 管理敏感配置：

1. 在 `src/main/resources/` 下创建 `application-local.yml`：

```yaml
deepseek:
  api-key: sk-你的真实APIKey
```

2. 此文件已加入 `.gitignore`，不会提交到仓库。

3. `application.yml` 中默认使用占位符 `${DEEPSEEK_API_KEY:your-api-key}`，启动时 `local` profile 会自动覆盖。

### 接口列表

| 方法 | 路径 | 功能 |
|---|---|---|
| GET | `/api/health` | 健康检查 |
| POST | `/api/chat/message` | 纯文本问答 |
| POST | `/api/chat/upload` | 上传 PDF 并解析 |
| POST | `/api/chat/ask-with-file` | 上传 PDF + 提问 |

### Postman 测试

**纯文本问答：**

```http
POST http://localhost:8080/api/chat/message
Content-Type: application/json

{
    "message": "请介绍 Spring Boot 的核心特性"
}
```

**上传 PDF + 提问：**

```http
POST http://localhost:8080/api/chat/ask-with-file
Content-Type: multipart/form-data

file: 简历.pdf
message: 这份简历有哪些亮点？
```

## 前端启动

```bash
cd F:\ai-job-helper\frontend
npm install
npm run dev
```

浏览器访问 `http://localhost:5173`。

Vite 开发服务器自动将 `/api` 请求代理到 `http://localhost:8080`。

## Git 提交历史

```
b21598c feat: implement vue interview assistant ui
f00eb34 feat: support pdf question answering
2e98bd5 feat: 实现 DeepSeek 聊天接口
2f4dd7f 项目骨架完成
```
