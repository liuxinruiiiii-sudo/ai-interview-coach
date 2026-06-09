<template>
  <main class="chat-window" ref="chatContainer">
    <!-- 模式指示器 -->
    <div class="mode-bar">
      <span class="mode-dot" :style="{ background: modeColor }"></span>
      <span class="mode-label">{{ modeLabel }}</span>
      <span class="mode-tip">{{ modeTip }}</span>
    </div>

    <!-- 欢迎页 -->
    <div v-if="messages.length === 0" class="welcome">
      <div class="welcome-icon">{{ modeIcon }}</div>
      <h2>{{ modeTitle }}</h2>
      <p>{{ modeSubtitle }}</p>
      <div class="quick-hints">
        <button
          v-for="hint in modeHints"
          :key="hint"
          class="hint-btn"
          @click="$emit('quick-ask', hint)"
        >{{ hint }}</button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div v-else class="message-list">
      <div
        v-for="msg in messages"
        :key="msg.id"
        class="message"
        :class="msg.role"
      >
        <div class="msg-avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
        <div class="msg-body">
          <div class="msg-content" v-text="msg.content"></div>
          <div v-if="msg.pdfName" class="msg-pdf-badge">
            📄 {{ msg.pdfName }}
          </div>
        </div>
      </div>

      <div v-if="loading" class="message ai">
        <div class="msg-avatar">🤖</div>
        <div class="msg-body">
          <div class="typing-indicator">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, watch, nextTick, computed } from 'vue'

const props = defineProps({
  messages: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  mode: { type: String, default: 'resume' }
})

defineEmits(['quick-ask'])

const chatContainer = ref(null)

const modeConfig = computed(() => {
  const map = {
    resume: {
      label: '简历分析模式',
      tip: '上传简历后 AI 将深度分析亮点与不足',
      icon: '📋',
      color: 'var(--mode-resume)',
      title: '简历智能分析',
      subtitle: '上传简历，获取针对性的优化建议与面试策略',
      hints: [
        '请分析我的简历亮点和不足',
        '这份简历适合投递什么岗位？',
        '请帮我优化项目经历的描述',
        '根据简历给我一些面试建议'
      ]
    },
    interview: {
      label: '面试模拟模式',
      tip: 'AI 化身面试官，模拟真实面试场景',
      icon: '🎯',
      color: 'var(--mode-interview)',
      title: '模拟面试',
      subtitle: '选择岗位方向，开始仿真面试练习',
      hints: [
        '请模拟一次Java后端面试',
        '请出一道系统设计题',
        '请对我的回答打分并给出建议',
        '请模拟压力面试场景'
      ]
    },
    plan: {
      label: '学习计划模式',
      tip: '根据目标岗位生成个性化备考计划',
      icon: '📝',
      color: 'var(--mode-plan)',
      title: '备考学习计划',
      subtitle: '设定目标，生成专属备考路线图',
      hints: [
        '为我制定一个月的Java面试备考计划',
        '我需要重点复习哪些知识点？',
        '请推荐学习资源和顺序',
        '如何平衡算法和项目复习？'
      ]
    }
  }
  return map[props.mode] || map.resume
})

const modeLabel = computed(() => modeConfig.value.label)
const modeTip = computed(() => modeConfig.value.tip)
const modeIcon = computed(() => modeConfig.value.icon)
const modeColor = computed(() => modeConfig.value.color)
const modeTitle = computed(() => modeConfig.value.title)
const modeSubtitle = computed(() => modeConfig.value.subtitle)
const modeHints = computed(() => modeConfig.value.hints)

watch(() => props.messages.length, async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
})
</script>

<style scoped>
.chat-window {
  flex: 1;
  overflow-y: auto;
  background: var(--bg-main);
  display: flex;
  flex-direction: column;
}

/* ── 模式栏 ── */

.mode-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: var(--bg-chat);
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
}

.mode-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.mode-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.mode-tip {
  margin-left: auto;
  font-size: 12px;
  color: var(--text-secondary);
}

/* ── 欢迎页 ── */

.welcome {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 40px;
}

.welcome-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.welcome h2 {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 6px;
}

.welcome p {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 24px;
}

.quick-hints {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  max-width: 480px;
}

.hint-btn {
  padding: 9px 20px;
  border: 1px solid var(--border);
  border-radius: 20px;
  background: var(--bg-chat);
  color: var(--text-primary);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.hint-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
}

/* ── 消息列表 ── */

.message-list {
  flex: 1;
  max-width: 800px;
  width: 100%;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message {
  display: flex;
  gap: 12px;
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  background: var(--bg-chat);
  box-shadow: var(--shadow);
}

.msg-body {
  flex: 1;
  min-width: 0;
}

.msg-content {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.message.user .msg-content {
  background: var(--msg-user);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message.ai .msg-content {
  background: var(--bg-chat);
  border: 1px solid var(--border);
  border-bottom-left-radius: 4px;
}

.msg-pdf-badge {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-secondary);
}

/* ── 打字动画 ── */

.typing-indicator {
  padding: 12px 16px;
  background: var(--bg-chat);
  border: 1px solid var(--border);
  border-radius: 12px;
  display: inline-flex;
  gap: 4px;
  align-items: center;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  background: var(--primary-light);
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }
.typing-indicator span:nth-child(3) { animation-delay: 0s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}
</style>
