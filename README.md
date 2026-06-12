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

### 已完成 ✅

- **AI 对话** — 输入问题，DeepSeek API 实时回复，前端 Markdown 渲染
- **PDF 简历解析** — PDFBox 提取 PDF 文本，支持中文文档
- **PDF + AI 问答** — 上传简历 PDF → 拼接 Prompt → AI 结合 PDF 内容分析回答
- **模拟面试** — 选择 Java 基础 / Spring Boot / MySQL / Redis / 项目实战等方向，AI 出题 → 用户回答 → AI 评分反馈（评分 + 亮点 + 不足 + 参考答案 + 追问），完整闭环

### 待开发 🚧

- **学习路线** — 当前为静态 UI，DeepSeek 生成逻辑待接入
- **题库** — 当前为前端 mock 数据，后端管理待开发
- **数据库持久化** — 会话数据存储在浏览器内存，刷新后丢失
- **用户登录 / JWT 认证**
- **RAG / 向量知识库**
- **流式输出（SSE）**

## 项目结构

```
ai-job-helper/
├── src/main/java/com/example/aijobhelper/
│   ├── AiJobHelperApplication.java       # 启动类
│   ├── config/
│   │   └── RestTemplateConfig.java       # RestTemplate Bean
│   ├── controller/
│   │   ├── HealthController.java         # GET /api/health
│   │   ├── ChatController.java           # 聊天 + PDF 上传接口
│   │   └── MockInterviewController.java  # 模拟面试接口
│   ├── service/
│   │   ├── PdfService.java               # PDFBox 解析 PDF
│   │   ├── DeepSeekService.java          # DeepSeek API 调用（核心）
│   │   └── MockInterviewService.java     # 面试出题 + 评价（复用 DeepSeekService）
│   ├── model/
│   │   ├── dto/                           # 请求/响应 DTO（9 个）
│   │   └── vo/ApiResult.java             # 统一返回格式
│   └── exception/
│       ├── BusinessException.java         # 业务异常
│       └── GlobalExceptionHandler.java    # 全局异常处理
├── frontend/
│   └── src/
│       ├── App.vue                        # 主布局 + 页面路由
│       ├── api/
│       │   ├── chat.js                    # 聊天 / PDF 接口
│       │   └── interview.js               # 模拟面试接口
│       └── components/
│           ├── Sidebar.vue                # 侧边栏（导航+会话）
│           ├── ChatWindow.vue             # 聊天窗口（Markdown 渲染）
│           ├── ChatInput.vue              # 输入框 + 文件上传
│           ├── ChatPage.vue               # AI 对话页
│           ├── MockInterviewPage.vue      # 模拟面试页
│           ├── StudyRoadmapPage.vue       # 学习路线页
│           ├── QuestionBankPage.vue       # 题库页
│           └── KnowledgePanel.vue         # 右侧知识库面板
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

1. 复制 `src/main/resources/application-example.yml`，并重命名为 `application-local.yml`：

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
| POST | `/api/interview/start` | 模拟面试 — 生成面试题 |
| POST | `/api/interview/answer` | 模拟面试 — 提交回答并评分 |

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

**模拟面试 — 生成题目：**

```http
POST http://localhost:8080/api/interview/start
Content-Type: application/json

{
    "category": "Java 基础",
    "level": "intermediate"
}
```

**模拟面试 — 提交回答：**

```http
POST http://localhost:8080/api/interview/answer
Content-Type: application/json

{
    "question": "请解释 HashMap 的底层实现原理",
    "userAnswer": "HashMap 底层采用数组+链表+红黑树...",
    "category": "Java 基础"
}
```

## 前端启动

```bash
cd F:\ai-job-helper\frontend
npm install
npm run dev
```

浏览器访问 `http://localhost:5173`。

Vite 开发服务器自动将 `/api` 请求代理到 `http://localhost:8080`。

## 项目亮点（面试视角）

- **前后端分离** — Spring Boot 3 + Vue3，RESTful API 设计
- **AI 集成** — 封装 RestTemplate 调用 DeepSeek Chat API，统一异常处理
- **文档解析** — PDFBox 提取 PDF 文本，格式校验 + 空文件检测
- **Prompt 工程** — 模拟面试的出题 Prompt 和评分 Prompt 写在后端，返回结构稳定
- **统一返回格式** — `ApiResult<T>` 泛型封装，`GlobalExceptionHandler` 全局异常拦截
- **组件化前端** — 5 个页面、12 个组件，玻璃拟态紫色 SaaS 风格

## Git 提交历史

```
f2f3826 feat: add DeepSeek-powered mock interview flow
4767fe9 docs: add project documentation
b21598c feat: implement vue interview assistant ui
f00eb34 feat: support pdf question answering
2e98bd5 feat: 实现 DeepSeek 聊天接口
2f4dd7f 项目骨架完成
```

## 截图

> 启动项目后访问 http://localhost:5173，建议截取以下页面放入 GitHub：
>
> 1. **AI 对话页** — 发送消息 + AI 回复（Markdown 渲染效果）
> 2. **模拟面试页** — 选择方向 → 出题 → 评分反馈
> 3. **学习路线页** — 6 周手风琴 + 进度条
> 4. **题库页** — 分类筛选 + 展开 AI 答题指导
>
> （截图待补充）
