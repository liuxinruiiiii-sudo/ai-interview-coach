<template>
  <div class="app-layout">
    <!-- 左侧：模式选择 + 会话列表 -->
    <Sidebar
      :sessions="sessions"
      :activeId="activeSession"
      :currentMode="currentMode"
      @new-chat="handleNewChat"
      @select-session="handleSelectSession"
      @change-mode="handleChangeMode"
    />

    <!-- 中间：聊天区 -->
    <div class="main-area">
      <ChatWindow
        :messages="currentMessages"
        :loading="loading"
        :mode="currentMode"
        @quick-ask="handleQuickAsk"
      />

      <ChatInput
        :loading="loading"
        @send="handleSend"
      />
    </div>

    <!-- 右侧：知识库 + 文件面板 -->
    <KnowledgePanel
      :files="uploadedFiles"
      :activeFileId="activeFileId"
      @select-file="handleSelectFile"
      @remove-file="handleRemoveFile"
      @kb-click="handleKbClick"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import Sidebar from './components/Sidebar.vue'
import ChatWindow from './components/ChatWindow.vue'
import ChatInput from './components/ChatInput.vue'
import KnowledgePanel from './components/KnowledgePanel.vue'
import { sendMessage, askWithFile } from './api/chat.js'

// ── 状态 ──

const sessions = ref([])
const activeSession = ref('')
const loading = ref(false)
const currentMode = ref('resume')
const uploadedFiles = ref([])
const activeFileId = ref('')

// ── 当前会话消息 ──

const currentMessages = computed(() => {
  const session = sessions.value.find(s => s.id === activeSession.value)
  return session ? session.messages : []
})

// ── 模式切换 ──

function handleChangeMode(mode) {
  currentMode.value = mode
  handleNewChat()
}

// ── 新建对话 ──

function handleNewChat() {
  const newSession = {
    id: Date.now().toString(),
    title: '新对话',
    messages: []
  }
  sessions.value.unshift(newSession)
  activeSession.value = newSession.id
}

// ── 选择会话 ──

function handleSelectSession(id) {
  activeSession.value = id
}

// ── 快捷提问 ──

function handleQuickAsk(hint) {
  if (!activeSession.value) handleNewChat()
  addMessage('user', hint)
  callTextApi(hint)
}

// ── 发送消息 / 文件 ──

function handleSend({ message, file }) {
  if (!activeSession.value) handleNewChat()

  if (file) {
    const label = message || '请根据 PDF 内容回答'
    addMessage('user', label, file.name)

    // 加入文件列表
    const fileEntry = {
      id: Date.now().toString(),
      name: file.name,
      size: file.size,
      charCount: null
    }
    uploadedFiles.value.unshift(fileEntry)
    activeFileId.value = fileEntry.id

    callFileApi(file, message, fileEntry)
  } else if (message) {
    addMessage('user', message)
    callTextApi(message)
  }
}

// ── 添加消息 ──

function addMessage(role, content, pdfName = null) {
  const session = sessions.value.find(s => s.id === activeSession.value)
  if (!session) return

  session.messages.push({
    id: Date.now().toString(),
    role,
    content,
    pdfName,
    time: new Date()
  })

  if (session.messages.length === 1 && role === 'user') {
    session.title = content.substring(0, 20) + (content.length > 20 ? '…' : '')
  }
}

// ── API 调用 ──

async function callTextApi(message) {
  loading.value = true
  try {
    const res = await sendMessage(message)
    addMessage('ai', res.data?.data?.answer || '未获取到回复')
  } catch (e) {
    addMessage('ai', '请求失败: ' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

async function callFileApi(file, message, fileEntry) {
  loading.value = true
  try {
    const res = await askWithFile(file, message)
    const answer = res.data?.data?.answer || '未获取到回复'
    addMessage('ai', answer)

    // 更新文件字符数
    if (fileEntry && res.data?.data?.pdfLength) {
      fileEntry.charCount = res.data.data.pdfLength
    }
  } catch (e) {
    addMessage('ai', '请求失败: ' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

// ── 文件操作 ──

function handleSelectFile(id) {
  activeFileId.value = id
}

function handleRemoveFile(id) {
  uploadedFiles.value = uploadedFiles.value.filter(f => f.id !== id)
  if (activeFileId.value === id) activeFileId.value = ''
}

// ── 知识库点击 ──

function handleKbClick(kb) {
  if (!activeSession.value) handleNewChat()
  const prompt = `请出几道 ${kb.title} 相关的高频面试题`
  addMessage('user', prompt)
  callTextApi(prompt)
}
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
}

.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
</style>
