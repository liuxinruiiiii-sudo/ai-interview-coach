<template>
  <div class="app-layout">
    <Sidebar
      :sessions="sessions"
      :activeId="activeSession"
      :currentPage="currentPage"
      @navigate="handleNavigate"
      @new-chat="handleNewChat"
      @select-session="handleSelectSession"
      @delete-session="handleDeleteSession"
      @rename-session="handleRenameSession"
    />

    <div class="main-area">
      <!-- AI 对话 / 简历分析 -->
      <ChatPage
        v-if="currentPage === 'chat' || currentPage === 'resume'"
        :messages="currentMessages"
        :loading="loading"
        :mode="currentPage === 'resume' ? 'resume' : currentMode"
        @quick-ask="handleQuickAsk"
        @send="handleSend"
      />

      <MockInterviewPage v-else-if="currentPage === 'mock'" />
      <StudyRoadmapPage v-else-if="currentPage === 'roadmap'" />
      <QuestionBankPage v-else-if="currentPage === 'qbank'" />
    </div>

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
import ChatPage from './components/ChatPage.vue'
import MockInterviewPage from './components/MockInterviewPage.vue'
import StudyRoadmapPage from './components/StudyRoadmapPage.vue'
import QuestionBankPage from './components/QuestionBankPage.vue'
import KnowledgePanel from './components/KnowledgePanel.vue'
import { sendMessage, askWithFile } from './api/chat.js'

// ── 页面路由 ──
const currentPage = ref('chat')
const currentMode = ref('resume')

function handleNavigate(page) {
  currentPage.value = page
}

// ── 对话状态 ──
const sessions = ref([])
const activeSession = ref('')
const loading = ref(false)
const uploadedFiles = ref([])
const activeFileId = ref('')

const currentMessages = computed(() => {
  const session = sessions.value.find(s => s.id === activeSession.value)
  return session ? session.messages : []
})

// ── 新建对话 ──
function handleNewChat() {
  const s = { id: Date.now().toString(), title: '新对话', messages: [], manuallyRenamed: false }
  sessions.value.unshift(s)
  activeSession.value = s.id
  currentPage.value = 'chat'
}

// ── 选择会话 ──
function handleSelectSession(id) {
  activeSession.value = id
  currentPage.value = 'chat'
}

// ── 删除 ──
function handleDeleteSession(id) {
  const idx = sessions.value.findIndex(s => s.id === id)
  if (idx === -1) return
  sessions.value.splice(idx, 1)
  if (activeSession.value === id) {
    if (sessions.value.length > 0) activeSession.value = sessions.value[0].id
    else handleNewChat()
  }
}

// ── 重命名 ──
function handleRenameSession(id, newTitle) {
  const s = sessions.value.find(s => s.id === id)
  if (!s) return
  s.title = newTitle
  s.manuallyRenamed = true
}

// ── 消息发送 ──
function handleQuickAsk(hint) {
  if (!activeSession.value) handleNewChat()
  addMessage('user', hint)
  callTextApi(hint)
}

function handleSend({ message, file }) {
  if (!activeSession.value) handleNewChat()
  if (file) {
    const label = message || '请根据 PDF 内容回答'
    addMessage('user', label, file.name)
    const fe = { id: Date.now().toString(), name: file.name, size: file.size, charCount: null }
    uploadedFiles.value.unshift(fe)
    activeFileId.value = fe.id
    callFileApi(file, message, fe)
  } else if (message) {
    addMessage('user', message)
    callTextApi(message)
  }
}

function addMessage(role, content, pdfName = null) {
  const session = sessions.value.find(s => s.id === activeSession.value)
  if (!session) return
  session.messages.push({ id: Date.now().toString(), role, content, pdfName, time: new Date() })
  if (!session.manuallyRenamed && session.messages.length === 1 && role === 'user') {
    session.title = content.length > 15 ? content.substring(0, 15) + '…' : content
  }
}

// ── API ──
async function callTextApi(message) {
  loading.value = true
  try {
    const res = await sendMessage(message)
    addMessage('ai', res.data?.data?.answer || '未获取到回复')
  } catch (e) {
    addMessage('ai', '请求失败: ' + (e.response?.data?.message || e.message))
  } finally { loading.value = false }
}

async function callFileApi(file, message, fileEntry) {
  loading.value = true
  try {
    const res = await askWithFile(file, message)
    addMessage('ai', res.data?.data?.answer || '未获取到回复')
    if (fileEntry && res.data?.data?.pdfLength) fileEntry.charCount = res.data.data.pdfLength
  } catch (e) {
    addMessage('ai', '请求失败: ' + (e.response?.data?.message || e.message))
  } finally { loading.value = false }
}

function handleSelectFile(id) { activeFileId.value = id }
function handleRemoveFile(id) {
  uploadedFiles.value = uploadedFiles.value.filter(f => f.id !== id)
  if (activeFileId.value === id) activeFileId.value = ''
}
function handleKbClick(kb) {
  if (!activeSession.value) handleNewChat()
  const prompt = `请出几道 ${kb.title} 相关的高频面试题`
  addMessage('user', prompt)
  callTextApi(prompt)
}
</script>

<style scoped>
.app-layout { display: flex; height: 100%; }
.main-area { flex: 1; display: flex; flex-direction: column; min-width: 0; position: relative; }
</style>
