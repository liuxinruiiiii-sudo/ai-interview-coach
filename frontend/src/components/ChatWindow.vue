<template>
  <main class="chat-window" ref="chatContainer">
    <!-- 顶部模式指示 -->
    <div class="top-bar">
      <span class="bar-dot"></span>
      <span class="bar-label">{{ modeIcon }} &nbsp;{{ modeLabel }}模式</span>
    </div>

    <!-- 欢迎区域 -->
    <div v-if="messages.length === 0" class="hero">
      <div class="hero-visual">
        <div class="hero-blur"></div>
        <div class="hero-icon-wrap">
          <span class="hero-icon">{{ modeIcon }}</span>
        </div>
      </div>

      <h1 class="hero-title">{{ modeTitle }}</h1>
      <p class="hero-subtitle">{{ modeSubtitle }}</p>

      <!-- 功能卡片 -->
      <div class="feature-grid">
        <button
          v-for="card in modeHints"
          :key="card.title"
          class="feature-card"
          @click="$emit('quick-ask', card.prompt)"
        >
          <span class="fc-icon">{{ card.icon }}</span>
          <span class="fc-title">{{ card.title }}</span>
          <span class="fc-desc">{{ card.desc }}</span>
        </button>
      </div>
    </div>

    <!-- 消息区 -->
    <div v-else class="messages-area">
      <div v-for="msg in messages" :key="msg.id" class="msg-row" :class="msg.role">
        <div class="msg-avatar" v-if="msg.role === 'ai'">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
        </div>
        <div class="msg-bubble" :class="msg.role">
          <div v-if="msg.pdfName" class="bubble-file">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
            {{ msg.pdfName }}
          </div>
          <div v-if="msg.role === 'ai'" class="bubble-text markdown-body" v-html="renderMarkdown(msg.content)"></div>
          <div v-else class="bubble-text" v-text="msg.content"></div>
        </div>
      </div>

      <div v-if="loading" class="msg-row ai">
        <div class="msg-avatar">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
        </div>
        <div class="msg-bubble ai typing">
          <span></span><span></span><span></span>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, watch, nextTick, computed } from 'vue'
import { marked } from 'marked'

// 配置 marked：安全起见禁用原始 HTML
marked.setOptions({ breaks: true, gfm: true })

function renderMarkdown(text) {
  if (!text) return ''
  return marked.parse(text)
}

const props = defineProps({ messages: { type: Array, default: () => [] }, loading: Boolean, mode: { type: String, default: 'resume' } })
defineEmits(['quick-ask'])
const chatContainer = ref(null)

const config = computed(() => {
  const m = {
    resume: { label: '简历分析', icon: '📋', title: '简历智能分析', subtitle: '上传简历 PDF，获取深度分析与针对性面试策略',
      hints: [
        { icon: '✨', title: '分析简历亮点', desc: '挖掘核心竞争力', prompt: '请分析我的简历亮点和不足' },
        { icon: '🎯', title: '匹配目标岗位', desc: '评估 JD 匹配度', prompt: '这份简历适合投递什么岗位？' },
        { icon: '📝', title: '优化项目描述', desc: '让经历更有说服力', prompt: '帮我优化项目经历的描述方式' },
        { icon: '💡', title: '面试策略', desc: '制定专属面试计划', prompt: '根据这份简历给我面试策略建议' }
      ]},
    interview: { label: '面试模拟', icon: '🎯', title: '模拟面试', subtitle: 'AI 化身面试官，沉浸式模拟真实面试场景',
      hints: [
        { icon: '💻', title: '技术面试', desc: 'Java 后端真题演练', prompt: '模拟一次 Java 后端技术面试' },
        { icon: '🏗️', title: '系统设计', desc: '架构与设计方案', prompt: '出一道系统设计题并评估我的回答' },
        { icon: '⭐', title: '回答评分', desc: '详细反馈与建议', prompt: '对我的回答打分并给出改进建议' },
        { icon: '🤝', title: '行为面试', desc: 'BQ 软技能练习', prompt: '模拟一次行为面试（BQ）' }
      ]},
    plan: { label: '学习计划', icon: '📝', title: '备考学习计划', subtitle: '设定目标，生成个性化备考路线图',
      hints: [
        { icon: '📅', title: '月度备考方案', desc: '30 天系统化准备', prompt: '制定一个月的 Java 面试备考计划' },
        { icon: '🔍', title: '薄弱点诊断', desc: '找到提升方向', prompt: '我目前的薄弱环节在哪里？' },
        { icon: '📚', title: '资源推荐', desc: '精选书籍与课程', prompt: '推荐适合我的学习资源和顺序' },
        { icon: '⚖️', title: '时间分配', desc: '算法/项目/基础', prompt: '如何平衡算法刷题和项目复盘？' }
      ]}
  }
  return m[props.mode] || m.resume
})

const modeLabel = computed(() => config.value.label)
const modeIcon = computed(() => config.value.icon)
const modeTitle = computed(() => config.value.title)
const modeSubtitle = computed(() => config.value.subtitle)
const modeHints = computed(() => config.value.hints)

watch(() => props.messages.length, async () => { await nextTick(); if (chatContainer.value) chatContainer.value.scrollTop = chatContainer.value.scrollHeight })
</script>

<style scoped>
.chat-window {
  flex: 1; overflow-y: auto; display: flex; flex-direction: column;
}

/* ── Top bar ── */
.top-bar {
  display: flex; align-items: center; gap: 10px;
  padding: 16px 32px; flex-shrink: 0;
}

.bar-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--purple-400); }

.bar-label { font-size: 13px; font-weight: 500; color: var(--text-500); }

/* ── Hero ── */
.hero {
  flex: 1; display: flex; flex-direction: column;
  align-items: center; justify-content: center; padding: 40px 32px 72px;
}

.hero-visual { position: relative; margin-bottom: 28px; }

.hero-blur {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
  width: 100px; height: 100px;
  background: radial-gradient(circle, rgba(168,85,247,0.2), transparent 70%);
  border-radius: 50%;
}

.hero-icon-wrap {
  width: 72px; height: 72px; border-radius: var(--radius-lg);
  background: linear-gradient(135deg, #F5F3FF, #EDE9FE);
  border: 1px solid rgba(147,51,234,0.1);
  display: flex; align-items: center; justify-content: center;
  position: relative; z-index: 1;
}

.hero-icon { font-size: 32px; }

.hero-title {
  font-size: 30px; font-weight: 700; color: var(--text-900);
  letter-spacing: -0.03em; margin-bottom: 10px;
}

.hero-subtitle {
  font-size: 15px; color: var(--text-400); letter-spacing: 0.02em; margin-bottom: 36px;
}

/* ── Feature grid ── */
.feature-grid {
  display: grid; grid-template-columns: 1fr 1fr;
  gap: 14px; max-width: 560px; width: 100%;
}

.feature-card {
  display: flex; flex-direction: column; gap: 6px;
  padding: 20px 22px;
  background: rgba(255,255,255,0.75);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(147,51,234,0.06);
  border-radius: var(--radius-lg);
  cursor: pointer; text-align: left; font-family: inherit;
  transition: all var(--transition);
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
  border-color: rgba(147,51,234,0.15);
  background: rgba(255,255,255,0.95);
}

.fc-icon { font-size: 24px; }

.fc-title { font-size: 14px; font-weight: 600; color: var(--text-900); }

.fc-desc { font-size: 12px; color: var(--text-400); }

/* ── Messages ── */
.messages-area {
  flex: 1; max-width: 760px; width: 100%; margin: 0 auto;
  padding: 28px 24px 16px; display: flex; flex-direction: column; gap: 24px;
}

.msg-row { display: flex; gap: 12px; align-items: flex-end; }

.msg-row.user { justify-content: flex-end; }

.msg-avatar {
  width: 34px; height: 34px; border-radius: var(--radius-xs);
  background: linear-gradient(135deg, #A855F7, #7E22CE);
  color: #fff; display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; box-shadow: 0 2px 8px rgba(147,51,234,0.2);
}

.msg-bubble {
  max-width: 74%; padding: 12px 18px; border-radius: var(--radius-lg);
  font-size: 14px; line-height: 1.7;
}

.msg-bubble.user {
  background: linear-gradient(135deg, #9333EA, #7E22CE);
  color: #fff; border-bottom-right-radius: 6px;
  box-shadow: 0 4px 16px rgba(147,51,234,0.2);
}

.msg-bubble.ai {
  background: var(--white); color: var(--text-900);
  border: 1px solid rgba(147,51,234,0.05);
  border-bottom-left-radius: 6px; box-shadow: var(--shadow-card);
}

.bubble-file {
  display: inline-flex; align-items: center; gap: 5px;
  padding: 3px 10px; border-radius: 6px;
  background: rgba(255,255,255,0.15); font-size: 12px; margin-bottom: 8px;
}

.msg-bubble.ai .bubble-file { background: var(--purple-50); }

.bubble-text { white-space: pre-wrap; word-break: break-word; }

/* ── Markdown 渲染样式 ── */
.markdown-body :deep(h1), .markdown-body :deep(h2), .markdown-body :deep(h3) {
  font-weight: 600; margin: 16px 0 8px; line-height: 1.4;
}
.markdown-body :deep(h1) { font-size: 1.25em; }
.markdown-body :deep(h2) { font-size: 1.15em; }
.markdown-body :deep(h3) { font-size: 1.05em; }
.markdown-body :deep(p) { margin: 4px 0; }
.markdown-body :deep(strong) { font-weight: 600; color: var(--text-900); }
.markdown-body :deep(ul), .markdown-body :deep(ol) { padding-left: 20px; margin: 6px 0; }
.markdown-body :deep(li) { margin: 2px 0; }
.markdown-body :deep(li::marker) { color: var(--purple-400); }
.markdown-body :deep(code) {
  background: var(--purple-50); color: var(--purple-700);
  padding: 1px 6px; border-radius: 4px; font-size: 0.9em;
  font-family: 'SF Mono', 'Consolas', 'Monaco', monospace;
}
.markdown-body :deep(pre) {
  background: #1E1E2E; color: #E2E8F0;
  padding: 14px 18px; border-radius: var(--radius-sm);
  overflow-x: auto; margin: 10px 0; font-size: 13px; line-height: 1.6;
}
.markdown-body :deep(pre code) {
  background: none; color: inherit; padding: 0; border-radius: 0; font-size: inherit;
}
.markdown-body :deep(blockquote) {
  border-left: 3px solid var(--purple-300);
  padding-left: 14px; margin: 8px 0; color: var(--text-500);
}
.markdown-body :deep(a) {
  color: var(--purple-600); text-decoration: underline;
}
.markdown-body :deep(hr) {
  border: none; border-top: 1px solid var(--purple-100); margin: 12px 0;
}
.markdown-body :deep(table) {
  border-collapse: collapse; width: 100%; margin: 8px 0;
}
.markdown-body :deep(th), .markdown-body :deep(td) {
  border: 1px solid var(--purple-100); padding: 6px 10px; text-align: left; font-size: 13px;
}
.markdown-body :deep(th) { background: var(--purple-50); font-weight: 600; }

.msg-bubble.typing { display: flex; gap: 4px; align-items: center; padding: 14px 20px; }

.msg-bubble.typing span {
  width: 5px; height: 5px; background: #D1D5DB; border-radius: 50%;
  animation: pulse 1.4s infinite ease-in-out both;
}
.msg-bubble.typing span:nth-child(1) { animation-delay: -0.32s; }
.msg-bubble.typing span:nth-child(2) { animation-delay: -0.16s; }

@keyframes pulse {
  0%, 80%, 100% { opacity: 0.3; transform: scale(0.8); }
  40% { opacity: 1; transform: scale(1); }
}
</style>
