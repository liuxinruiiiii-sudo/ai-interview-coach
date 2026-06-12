<template>
  <aside class="panel">
    <!-- Tab 切换 -->
    <div class="tab-row">
      <button class="tab-btn" :class="{ active: activeTab === 'files' }" @click="activeTab = 'files'">文件</button>
      <button class="tab-btn" :class="{ active: activeTab === 'library' }" @click="activeTab = 'library'">题库</button>
      <button class="tab-btn" :class="{ active: activeTab === 'tips' }" @click="activeTab = 'tips'">技巧</button>
    </div>

    <!-- Tab: 文件 -->
    <div v-if="activeTab === 'files'">
      <div class="card">
        <div class="card-head"><span class="card-title">上传的文档</span></div>

        <div v-if="files.length === 0" class="drop-zone" @dragover.prevent @drop.prevent>
          <div class="drop-icon">
            <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
          </div>
          <span class="drop-title">上传 PDF 文件</span>
          <span class="drop-hint">拖拽到此处或点击底部 📎</span>
        </div>

        <div v-else class="file-list">
          <div v-for="f in files" :key="f.id" class="file-row" :class="{ active: f.id === activeFileId }" @click="$emit('select-file', f.id)">
            <span class="file-icon">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
            </span>
            <div class="file-info">
              <span class="file-name">{{ f.name }}</span>
              <span class="file-meta">{{ f.size ? (f.size / 1024).toFixed(0) + ' KB' : '' }} {{ f.charCount ? '· 已分析' : '· 处理中' }}</span>
            </div>
            <button class="file-del" @click.stop="$emit('remove-file', f.id)">
              <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
        </div>
      </div>

      <!-- 简历诊断 -->
      <div class="card">
        <div class="card-head"><span class="card-title">简历诊断</span></div>
        <div class="insight-list">
          <div class="insight-item pass"><span class="in-label">Java 经验</span><span class="in-value">3 年</span></div>
          <div class="insight-item pass"><span class="in-label">Spring Boot</span><span class="in-value">✅ 已检测</span></div>
          <div class="insight-item fail"><span class="in-label">云经验</span><span class="in-value">❌ 缺失</span></div>
          <div class="insight-item partial"><span class="in-label">系统设计</span><span class="in-value">⚠️ 部分</span></div>
        </div>
      </div>

      <button class="upload-btn">上传 PDF 简历</button>
    </div>

    <!-- Tab: 题库 -->
    <div v-if="activeTab === 'library'" class="card">
      <div class="card-head"><span class="card-title">知识库</span></div>
      <div class="kb-list">
        <div v-for="kb in knowledgeItems" :key="kb.id" class="kb-row" @click="$emit('kb-click', kb)">
          <span class="kb-icon">{{ kb.icon }}</span>
          <div class="kb-info">
            <span class="kb-name">{{ kb.title }}</span>
            <span class="kb-meta">{{ kb.count }} 题 · {{ kb.level }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Tab: 技巧 -->
    <div v-if="activeTab === 'tips'">
      <div class="card">
        <div class="card-head"><span class="card-title">面试技巧</span></div>
        <div class="tip-list">
          <div class="tip-item">💬 回答行为类问题时使用 STAR 法则</div>
          <div class="tip-item">⚖️ 系统设计时务必说明各方案的权衡取舍</div>
          <div class="tip-item">📂 准备 3 个能深入讲解的项目经历</div>
        </div>
      </div>
      <div class="card progress-card">
        <div class="card-head"><span class="card-title">我的进度</span></div>
        <div class="progress-list">
          <div class="prog-item">
            <span class="prog-label">模拟面试</span>
            <span class="prog-val">3/10</span>
          </div>
          <div class="prog-item">
            <span class="prog-label">已解答题目</span>
            <span class="prog-val">42/100</span>
          </div>
          <div class="prog-item">
            <span class="prog-label">已覆盖主题</span>
            <span class="prog-val">5/12</span>
          </div>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref } from 'vue'

defineProps({ files: { type: Array, default: () => [] }, activeFileId: String })
defineEmits(['select-file', 'remove-file', 'kb-click'])

const activeTab = ref('files')

const knowledgeItems = [
  { id: 'spring', icon: '🍃', title: 'Spring Boot', count: 48, level: '核心' },
  { id: 'concurrency', icon: '⚡', title: '并发编程', count: 35, level: '进阶' },
  { id: 'patterns', icon: '📐', title: '设计模式', count: 29, level: '核心' },
  { id: 'micro', icon: '🔧', title: '微服务', count: 41, level: '进阶' },
  { id: 'jvm', icon: '🔥', title: 'JVM 调优', count: 22, level: '高级' },
  { id: 'sql', icon: '🗄', title: 'SQL & JPA', count: 31, level: '核心' }
]
</script>

<style scoped>
.panel { width: var(--panel-w); height: 100%; padding: 16px 14px; display: flex; flex-direction: column; gap: 10px; overflow-y: auto; flex-shrink: 0; }

/* ── Tabs ── */
.tab-row { display: flex; gap: 4px; padding: 2px; background: rgba(147,51,234,0.04); border-radius: var(--radius); margin-bottom: 2px; }

.tab-btn {
  flex: 1; padding: 7px 0; border: none; border-radius: var(--radius-sm);
  background: transparent; cursor: pointer; font-size: 12px; font-weight: 500;
  color: var(--text-400); font-family: inherit; transition: all var(--transition);
}
.tab-btn.active { background: var(--white); color: var(--purple-600); font-weight: 600; box-shadow: var(--shadow-xs); }
.tab-btn:hover:not(.active) { color: var(--text-500); }

/* ── Card ── */
.card { background: rgba(255,255,255,0.7); backdrop-filter: blur(12px); border: 1px solid rgba(255,255,255,0.6); border-radius: var(--radius-lg); padding: 16px 16px 12px; box-shadow: var(--shadow-card); }

.card-head { margin-bottom: 12px; }
.card-title { font-size: 13px; font-weight: 700; color: var(--text-900); }

/* ── Drop zone ── */
.drop-zone { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 20px 16px; border: 2px dashed rgba(147,51,234,0.15); border-radius: var(--radius); cursor: pointer; transition: all var(--transition); }
.drop-zone:hover { border-color: rgba(147,51,234,0.35); background: rgba(147,51,234,0.02); }
.drop-icon { color: var(--purple-300); }
.drop-title { font-size: 13px; font-weight: 500; color: var(--text-700); }
.drop-hint { font-size: 11px; color: var(--text-400); }

/* ── Files ── */
.file-list { display: flex; flex-direction: column; gap: 3px; }

.file-row { display: flex; align-items: center; gap: 10px; padding: 8px 10px; border-radius: var(--radius-xs); cursor: pointer; transition: all var(--transition); color: var(--text-500); }
.file-row:hover, .file-row.active { background: rgba(147,51,234,0.05); color: var(--purple-600); }
.file-icon { flex-shrink: 0; display: flex; }
.file-info { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.file-name { font-size: 13px; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.file-meta { font-size: 11px; color: var(--text-400); margin-top: 1px; }
.file-del { background: none; border: none; cursor: pointer; padding: 3px; display: flex; color: var(--text-300); opacity: 0; transition: all var(--transition); }
.file-row:hover .file-del { opacity: 1; }
.file-del:hover { color: #EF4444; }

/* ── Insights ── */
.insight-list { display: flex; flex-direction: column; gap: 4px; }
.insight-item { display: flex; justify-content: space-between; padding: 6px 0; font-size: 12px; border-bottom: 1px solid rgba(0,0,0,0.02); }
.in-label { color: var(--text-500); }
.insight-item.pass .in-value { color: #059669; }
.insight-item.fail .in-value { color: #DC2626; }
.insight-item.partial .in-value { color: #D97706; }

.upload-btn { width: 100%; padding: 10px; border-radius: var(--radius); background: rgba(147,51,234,0.06); border: 1px dashed rgba(147,51,234,0.2); color: var(--purple-600); font-size: 13px; font-weight: 500; cursor: pointer; font-family: inherit; margin-top: 10px; transition: all var(--transition); }
.upload-btn:hover { background: rgba(147,51,234,0.1); }

/* ── Knowledge ── */
.kb-list { display: flex; flex-direction: column; gap: 2px; }

.kb-row { display: flex; align-items: center; gap: 10px; padding: 8px 10px; border-radius: var(--radius-xs); cursor: pointer; transition: all var(--transition); color: var(--text-500); }
.kb-row:hover { background: rgba(147,51,234,0.05); color: var(--purple-600); }

.kb-icon { font-size: 18px; width: 28px; text-align: center; flex-shrink: 0; }
.kb-info { flex: 1; display: flex; justify-content: space-between; align-items: center; }
.kb-name { font-size: 13px; }
.kb-meta { font-size: 11px; color: var(--text-400); }

/* ── Tips ── */
.tip-list { display: flex; flex-direction: column; gap: 8px; }
.tip-item { font-size: 12px; color: var(--text-500); line-height: 1.5; }

.progress-card { background: linear-gradient(135deg, rgba(147,51,234,0.03), rgba(168,85,247,0.05)); border-color: rgba(147,51,234,0.08); }

.progress-list { display: flex; flex-direction: column; gap: 8px; }
.prog-item { display: flex; justify-content: space-between; align-items: center; font-size: 13px; }
.prog-label { color: var(--text-500); }
.prog-val { font-weight: 600; color: var(--purple-600); }
</style>
