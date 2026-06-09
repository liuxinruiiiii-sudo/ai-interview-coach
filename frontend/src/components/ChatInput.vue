<template>
  <footer class="chat-input-area">
    <!-- PDF 文件预览条 -->
    <div v-if="selectedFile" class="file-preview">
      <span class="file-icon">📄</span>
      <span class="file-name">{{ selectedFile.name }}</span>
      <button class="file-remove" @click="clearFile">✕</button>
    </div>

    <div class="input-row">
      <!-- PDF 上传按钮 -->
      <label class="upload-btn" title="上传 PDF">
        <input
          type="file"
          accept=".pdf"
          hidden
          @change="handleFileChange"
          ref="fileInput"
        />
        <span>📎</span>
      </label>

      <!-- 文本输入框 -->
      <textarea
        v-model="message"
        class="text-input"
        placeholder="输入您的问题..."
        rows="1"
        :disabled="loading"
        @keydown.enter.exact.prevent="handleSend"
        @input="autoResize"
        ref="textArea"
      ></textarea>

      <!-- 发送按钮 -->
      <button
        class="send-btn"
        :disabled="!canSend || loading"
        @click="handleSend"
      >
        <span v-if="loading" class="spinner"></span>
        <span v-else>➤</span>
      </button>
    </div>
  </footer>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['send', 'upload-file'])

const message = ref('')
const selectedFile = ref(null)
const fileInput = ref(null)
const textArea = ref(null)

const canSend = computed(() => {
  return message.value.trim() || selectedFile.value
})

function handleFileChange(e) {
  const file = e.target.files[0]
  if (file) {
    selectedFile.value = file
  }
}

function clearFile() {
  selectedFile.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

function handleSend() {
  if (!canSend.value || props.loading) return

  const msg = message.value.trim()
  const file = selectedFile.value

  emit('send', { message: msg, file: file })

  message.value = ''
  selectedFile.value = null
  if (fileInput.value) fileInput.value.value = ''
  autoResize()
}

function autoResize() {
  nextTick(() => {
    if (textArea.value) {
      textArea.value.style.height = 'auto'
      textArea.value.style.height = Math.min(textArea.value.scrollHeight, 120) + 'px'
    }
  })
}
</script>

<style scoped>
.chat-input-area {
  border-top: 1px solid var(--border);
  padding: 16px 24px 20px;
  background: var(--bg-chat);
  max-width: 860px;
  margin: 0 auto;
  width: 100%;
}

.file-preview {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: var(--msg-ai);
  border-radius: 8px;
  margin-bottom: 10px;
  font-size: 13px;
}

.file-name {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-remove {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-secondary);
  font-size: 14px;
  padding: 0 2px;
}

.file-remove:hover {
  color: #e74c3c;
}

.input-row {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 8px 12px;
  transition: border-color 0.2s;
}

.input-row:focus-within {
  border-color: var(--primary);
}

.upload-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 18px;
  transition: all 0.15s;
  flex-shrink: 0;
}

.upload-btn:hover {
  background: rgba(108, 92, 231, 0.1);
}

.text-input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  line-height: 1.5;
  resize: none;
  min-height: 24px;
  max-height: 120px;
  font-family: inherit;
  color: var(--text-primary);
}

.text-input::placeholder {
  color: var(--text-secondary);
}

.text-input:disabled {
  opacity: 0.5;
}

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: var(--primary);
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s;
}

.send-btn:hover:not(:disabled) {
  background: #5a4bd1;
}

.send-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
