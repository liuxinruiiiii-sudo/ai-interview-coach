<template>
  <footer class="input-area">
    <!-- 文件标签 -->
    <transition name="fade">
      <div v-if="selectedFile" class="file-chip">
        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
        <span>{{ selectedFile.name }}</span>
        <button class="chip-close" @click="clearFile">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>
    </transition>

    <!-- 输入胶囊 -->
    <div class="input-pill">
      <label class="pill-upload" title="上传 PDF">
        <input type="file" accept=".pdf" hidden @change="handleFileChange" ref="fileInput" />
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
      </label>

      <textarea
        v-model="message" class="pill-input"
        placeholder="输入问题，或上传 PDF 简历…"
        rows="1" :disabled="loading"
        @keydown.enter.exact.prevent="handleSend"
        @input="autoResize" ref="textArea"
      ></textarea>

      <button class="pill-send" :class="{ ready: canSend }" :disabled="!canSend || loading" @click="handleSend">
        <svg v-if="!loading" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
        <span v-else class="spinner"></span>
      </button>
    </div>
  </footer>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
defineProps({ loading: Boolean })
const emit = defineEmits(['send'])
const message = ref(''), selectedFile = ref(null), fileInput = ref(null), textArea = ref(null)
const canSend = computed(() => message.value.trim() || selectedFile.value)

function handleFileChange(e) { const f = e.target.files[0]; if (f) selectedFile.value = f }
function clearFile() { selectedFile.value = null; if (fileInput.value) fileInput.value.value = '' }
function handleSend() { if (!canSend.value) return; emit('send', { message: message.value.trim(), file: selectedFile.value }); message.value = ''; selectedFile.value = null; if (fileInput.value) fileInput.value.value = ''; autoResize() }
function autoResize() { nextTick(() => { if (textArea.value) { textArea.value.style.height = 'auto'; textArea.value.style.height = Math.min(textArea.value.scrollHeight, 120) + 'px' } }) }
</script>

<style scoped>
.input-area {
  display: flex; flex-direction: column; align-items: center;
  padding: 0 24px 24px;
}

/* ── File chip ── */
.file-chip {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 7px 16px; margin-bottom: 10px;
  background: rgba(255,255,255,0.8); backdrop-filter: blur(10px);
  border: 1px solid rgba(147,51,234,0.12);
  border-radius: var(--radius-full); font-size: 13px;
  color: var(--purple-600); box-shadow: var(--shadow-card);
}

.chip-close { background: none; border: none; cursor: pointer; padding: 2px; display: flex; color: var(--text-400); }
.chip-close:hover { color: #EF4444; }

/* ── Pill ── */
.input-pill {
  display: flex; align-items: flex-end; gap: 6px;
  width: 100%; max-width: 800px;
  background: rgba(255,255,255,0.7); backdrop-filter: blur(16px);
  border: 1px solid rgba(147,51,234,0.08);
  border-radius: var(--radius-full); padding: 8px 8px 8px 20px;
  box-shadow: var(--shadow-input); transition: all var(--transition);
}

.input-pill:focus-within {
  border-color: rgba(147,51,234,0.3);
  box-shadow: 0 4px 24px rgba(147,51,234,0.12);
  background: rgba(255,255,255,0.95);
}

/* ── Upload ── */
.pill-upload {
  width: 36px; height: 36px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; color: var(--text-400); transition: all var(--transition); flex-shrink: 0;
}

.pill-upload:hover { background: var(--purple-50); color: var(--purple-600); }

/* ── Input ── */
.pill-input {
  flex: 1; border: none; background: transparent; outline: none;
  font-size: 14px; line-height: 1.5; resize: none;
  min-height: 22px; max-height: 120px; font-family: inherit;
  color: var(--text-900); padding: 6px 0;
}

.pill-input::placeholder { color: var(--text-300); }

/* ── Send ── */
.pill-send {
  width: 38px; height: 38px; border-radius: 50%; border: none;
  background: transparent; color: var(--text-300);
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; transition: all var(--transition);
}

.pill-send.ready {
  background: linear-gradient(135deg, #A855F7, #7E22CE);
  color: #fff; box-shadow: 0 4px 16px rgba(147,51,234,0.35);
}

.pill-send.ready:hover { transform: scale(1.08); box-shadow: 0 6px 24px rgba(147,51,234,0.45); }

.pill-send:disabled { cursor: not-allowed; }

/* ── Spinner ── */
.spinner { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* ── Transition ── */
.fade-enter-active, .fade-leave-active { transition: all 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(6px); }
</style>
