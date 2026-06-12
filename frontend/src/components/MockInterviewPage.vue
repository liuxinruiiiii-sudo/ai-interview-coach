<template>
  <main class="page mock-page">
    <div class="page-top">
      <h2 class="page-title">模拟面试</h2>
      <p class="page-sub">AI 模拟真实面试场景，感受真实压力</p>
    </div>

    <!-- ====== 状态 0: 选择方向 ====== -->
    <template v-if="state === 'selecting'">
      <div class="section">
        <h3 class="section-title">选择考察方向</h3>
        <div class="focus-grid">
          <button v-for="f in focusAreas" :key="f.key" class="focus-card"
            :class="{ selected: selectedFocus === f.key }"
            @click="selectedFocus = f.key">
            <span class="fc-icon">{{ f.icon }}</span>
            <div class="fc-info">
              <span class="fc-name">{{ f.name }}</span>
              <span class="fc-meta">{{ f.count }} 题 · {{ f.level }}</span>
            </div>
          </button>
        </div>
      </div>

      <div class="section">
        <h3 class="section-title">面试难度</h3>
        <div class="duration-row">
          <button v-for="l in levels" :key="l.key" class="dur-btn"
            :class="{ active: selectedLevel === l.key }"
            @click="selectedLevel = l.key">{{ l.label }}</button>
        </div>
      </div>

      <button class="start-btn" @click="handleStart">
        <span v-if="loading" class="spinner"></span>
        <span v-else>开始模拟面试</span>
      </button>
    </template>

    <!-- ====== 状态 1: 展示题目 ====== -->
    <template v-if="state === 'question' || state === 'answering'">
      <div class="question-card">
        <div class="qc-header">
          <span class="qc-badge">{{ activeCategoryName }}</span>
          <span class="qc-id">#{{ questionId }}</span>
        </div>
        <p class="qc-text">{{ currentQuestion }}</p>
        <p v-if="currentTips" class="qc-tips">💡 {{ currentTips }}</p>
      </div>

      <div class="section">
        <h3 class="section-title">你的回答</h3>
        <textarea v-model="userAnswer" class="answer-input"
          placeholder="在此输入你的回答…" rows="6"
          :disabled="state === 'evaluating'"></textarea>
      </div>

      <button class="start-btn" @click="handleSubmit" :disabled="!userAnswer.trim() || loading">
        <span v-if="loading" class="spinner"></span>
        <span v-else>提交回答</span>
      </button>

      <button class="skip-btn" @click="handleReset">换一题</button>
    </template>

    <!-- ====== 状态 2: 评价反馈 ====== -->
    <template v-if="state === 'feedback'">
      <div class="feedback-card">
        <div class="fb-score-wrap">
          <div class="fb-score-ring" :style="{ '--pct': feedback.score }">
            <span class="fb-score-num">{{ feedback.score }}</span>
            <span class="fb-score-label">分</span>
          </div>
        </div>

        <div class="fb-block pass">
          <span class="fb-label">✅ 亮点</span>
          <p>{{ feedback.strengths }}</p>
        </div>

        <div class="fb-block fail">
          <span class="fb-label">⚠️ 不足</span>
          <p>{{ feedback.weaknesses }}</p>
        </div>

        <div class="fb-block">
          <span class="fb-label">📝 参考答案</span>
          <p>{{ feedback.referenceAnswer }}</p>
        </div>

        <div class="fb-block">
          <span class="fb-label">🔗 追问</span>
          <p>{{ feedback.followUpQuestion }}</p>
        </div>

        <div class="fb-block">
          <span class="fb-label">📚 学习建议</span>
          <p>{{ feedback.suggestion }}</p>
        </div>
      </div>

      <button class="start-btn" @click="handleReset">再来一题</button>
    </template>

    <!-- ====== 错误 ====== -->
    <div v-if="error" class="error-msg">{{ error }}</div>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import { startInterview, submitAnswer } from '../api/interview.js'

const focusAreas = [
  { key: 'Java 基础', name: 'Java 核心', count: 45, level: '综合', icon: '☕' },
  { key: 'Spring Boot', name: 'Spring Boot', count: 38, level: '中级', icon: '🍃' },
  { key: 'MySQL', name: 'MySQL', count: 72, level: '中级', icon: '🗄' },
  { key: 'Redis', name: 'Redis', count: 45, level: '进阶', icon: '⚡' },
  { key: '项目实战', name: '项目实战', count: 30, level: '综合', icon: '🏗️' }
]

const levels = [
  { key: 'beginner', label: '初级' },
  { key: 'intermediate', label: '中级' },
  { key: 'advanced', label: '高级' }
]

// ── 状态 ──
const state = ref('selecting')     // selecting | question | answering | evaluating | feedback
const selectedFocus = ref('Java 基础')
const selectedLevel = ref('intermediate')
const loading = ref(false)
const error = ref('')

const questionId = ref('')
const currentQuestion = ref('')
const currentTips = ref('')
const userAnswer = ref('')
const feedback = ref({ score: 0, strengths: '', weaknesses: '', referenceAnswer: '', followUpQuestion: '', suggestion: '' })

const activeCategoryName = ref('')

// ── 开始 ──
async function handleStart() {
  loading.value = true; error.value = ''; state.value = 'selecting'
  try {
    const res = await startInterview(selectedFocus.value, selectedLevel.value)
    const d = res.data.data
    questionId.value = d.questionId
    currentQuestion.value = d.question
    currentTips.value = d.tips || ''
    activeCategoryName.value = d.category
    state.value = 'question'
  } catch (e) {
    error.value = '生成题目失败: ' + (e.response?.data?.message || e.message)
  } finally { loading.value = false }
}

// ── 提交回答 ──
async function handleSubmit() {
  if (!userAnswer.value.trim()) return
  loading.value = true; error.value = ''; state.value = 'evaluating'
  try {
    const res = await submitAnswer(currentQuestion.value, userAnswer.value, activeCategoryName.value)
    feedback.value = res.data.data
    state.value = 'feedback'
  } catch (e) {
    error.value = '评价失败: ' + (e.response?.data?.message || e.message)
    state.value = 'question'
  } finally { loading.value = false }
}

// ── 换一题 ──
function handleReset() {
  state.value = 'selecting'
  userAnswer.value = ''
  error.value = ''
}
</script>

<style scoped>
.page { flex: 1; overflow-y: auto; padding: 32px 40px; max-width: 800px; margin: 0 auto; width: 100%; }

.page-top { margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 700; color: var(--text-900); margin-bottom: 6px; }
.page-sub { font-size: 14px; color: var(--text-500); }

.section { margin-top: 28px; }
.section-title { font-size: 13px; font-weight: 600; color: var(--text-500); margin-bottom: 12px; }

/* ── Focus cards ── */
.focus-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; }

.focus-card {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 16px; border-radius: var(--radius);
  background: rgba(255,255,255,0.7); backdrop-filter: blur(10px);
  border: 1px solid rgba(147,51,234,0.06); cursor: pointer;
  transition: all var(--transition); font-family: inherit; text-align: left;
}
.focus-card:hover { border-color: rgba(147,51,234,0.15); background: rgba(255,255,255,0.9); }
.focus-card.selected { border-color: rgba(147,51,234,0.3); background: rgba(147,51,234,0.04); box-shadow: 0 2px 12px rgba(147,51,234,0.08); }

.fc-icon { font-size: 24px; }
.fc-info { display: flex; flex-direction: column; }
.fc-name { font-size: 13px; font-weight: 600; color: var(--text-900); }
.fc-meta { font-size: 11px; color: var(--text-400); margin-top: 2px; }

/* ── Level ── */
.duration-row { display: flex; gap: 8px; }
.dur-btn {
  padding: 8px 20px; border-radius: var(--radius-full);
  border: 1px solid rgba(147,51,234,0.1); background: rgba(255,255,255,0.7);
  cursor: pointer; font-size: 13px; font-family: inherit; color: var(--text-500);
  transition: all var(--transition);
}
.dur-btn:hover { border-color: rgba(147,51,234,0.3); }
.dur-btn.active { background: var(--purple-600); color: #fff; border-color: var(--purple-600); }

/* ── Start / Submit ── */
.start-btn {
  margin-top: 28px; width: 100%; padding: 14px;
  border-radius: var(--radius); border: none;
  background: linear-gradient(135deg, #A855F7, #7E22CE);
  color: #fff; font-size: 15px; font-weight: 600; font-family: inherit;
  cursor: pointer; transition: all var(--transition);
  box-shadow: 0 4px 16px rgba(147,51,234,0.3);
  display: flex; align-items: center; justify-content: center; gap: 8px;
}
.start-btn:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 6px 24px rgba(147,51,234,0.4); }
.start-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.skip-btn {
  margin-top: 12px; margin-bottom: 20px; width: 100%; padding: 10px;
  border-radius: var(--radius); border: 1px dashed rgba(147,51,234,0.2);
  background: transparent; color: var(--text-400); font-size: 13px;
  cursor: pointer; font-family: inherit; transition: all var(--transition);
}
.skip-btn:hover { border-color: rgba(147,51,234,0.4); color: var(--purple-600); }

/* ── Question card ── */
.question-card {
  background: rgba(255,255,255,0.75); backdrop-filter: blur(10px);
  border: 1px solid rgba(147,51,234,0.1); border-radius: var(--radius-lg);
  padding: 22px 24px; margin-bottom: 8px;
}
.qc-header { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.qc-badge { font-size: 11px; font-weight: 600; padding: 3px 10px; border-radius: var(--radius-full); background: #EDE9FE; color: var(--purple-600); }
.qc-id { font-size: 11px; color: var(--text-300); }
.qc-text { font-size: 15px; line-height: 1.7; color: var(--text-900); }
.qc-tips { margin-top: 12px; font-size: 12px; color: var(--text-400); }

/* ── Answer input ── */
.answer-input {
  width: 100%; padding: 14px 18px; border-radius: var(--radius);
  border: 1px solid rgba(147,51,234,0.1); background: rgba(255,255,255,0.7);
  font-size: 14px; font-family: inherit; line-height: 1.6; resize: vertical;
  color: var(--text-900); outline: none; transition: all var(--transition);
}
.answer-input:focus { border-color: rgba(147,51,234,0.3); box-shadow: 0 0 0 3px rgba(147,51,234,0.06); }
.answer-input:disabled { opacity: 0.5; }

/* ── Feedback ── */
.feedback-card {
  background: rgba(255,255,255,0.75); backdrop-filter: blur(10px);
  border: 1px solid rgba(147,51,234,0.08); border-radius: var(--radius-lg);
  padding: 24px; display: flex; flex-direction: column; gap: 18px;
}

.fb-score-wrap { display: flex; justify-content: center; margin-bottom: 8px; }
.fb-score-ring {
  width: 100px; height: 100px; border-radius: 50%;
  background: conic-gradient(#7C3AED calc(var(--pct) * 1%), #F3E8FF 0);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  position: relative;
}
.fb-score-ring::after {
  content: ''; position: absolute; width: 82px; height: 82px;
  border-radius: 50%; background: #fff;
}
.fb-score-num { font-size: 28px; font-weight: 800; color: var(--text-900); position: relative; z-index: 1; }
.fb-score-label { font-size: 11px; color: var(--text-400); position: relative; z-index: 1; }

.fb-block p { font-size: 13px; color: var(--text-600); line-height: 1.6; margin-top: 4px; }
.fb-label { font-size: 12px; font-weight: 600; }
.fb-block.pass .fb-label { color: #059669; }
.fb-block.fail .fb-label { color: #D97706; }

/* ── Error ── */
.error-msg {
  margin-top: 16px; padding: 12px 18px; border-radius: var(--radius);
  background: #FEF2F2; color: #DC2626; font-size: 13px;
}

/* ── Spinner ── */
.spinner {
  width: 18px; height: 18px; border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>
