<template>
  <aside class="knowledge-panel">
    <!-- 已上传文件 -->
    <div class="panel-block">
      <div class="block-header">
        <span class="block-icon">📂</span>
        <span>已上传文件</span>
        <span v-if="files.length" class="badge">{{ files.length }}</span>
      </div>

      <div v-if="files.length === 0" class="empty-block">
        <span class="empty-icon">📁</span>
        <span>点击底部 📎 上传简历或 JD</span>
      </div>

      <div v-else class="file-list">
        <div
          v-for="(f, i) in files"
          :key="i"
          class="file-item"
          :class="{ active: f.id === activeFileId }"
          @click="$emit('select-file', f.id)"
        >
          <span class="file-type-icon">📄</span>
          <div class="file-info">
            <span class="file-name">{{ f.name }}</span>
            <span class="file-meta">{{ f.charCount || 0 }} 字符</span>
          </div>
          <button class="file-remove" @click.stop="$emit('remove-file', f.id)">✕</button>
        </div>
      </div>
    </div>

    <!-- 知识库 -->
    <div class="panel-block">
      <div class="block-header">
        <span class="block-icon">📚</span>
        <span>知识库</span>
      </div>

      <div class="knowledge-list">
        <div
          v-for="kb in knowledgeItems"
          :key="kb.id"
          class="kb-item"
          @click="$emit('kb-click', kb)"
        >
          <span class="kb-icon">{{ kb.icon }}</span>
          <div class="kb-info">
            <span class="kb-title">{{ kb.title }}</span>
            <span class="kb-count">{{ kb.count }} 条</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 面试小贴士 -->
    <div class="panel-block tips-block">
      <div class="block-header">
        <span class="block-icon">💡</span>
        <span>面试小贴士</span>
      </div>
      <p class="tip-text">{{ currentTip }}</p>
    </div>
  </aside>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  files: { type: Array, default: () => [] },
  activeFileId: { type: String, default: '' }
})

defineEmits(['select-file', 'remove-file', 'kb-click'])

const knowledgeItems = [
  { id: 'java', icon: '☕', title: 'Java 核心面试题', count: 156 },
  { id: 'spring', icon: '🍃', title: 'Spring 全家桶', count: 89 },
  { id: 'mysql', icon: '🗄️', title: 'MySQL 数据库', count: 72 },
  { id: 'redis', icon: '⚡', title: 'Redis 缓存', count: 45 },
  { id: 'system', icon: '🏗️', title: '系统设计', count: 38 },
  { id: 'behavior', icon: '🤝', title: '行为面试', count: 28 }
]

const tips = [
  '面试时结合项目经历回答问题，使用 STAR 法则组织语言。',
  '技术面试前务必复习基础知识，80% 的失败源于基础不牢。',
  '准备 2-3 个深度项目，能说清楚架构、难点和解决方案。',
  '面试结尾主动提问，展示你对岗位和公司的了解和兴趣。',
  '简历中的每一条技术栈，都要准备好被深入追问。'
]

const currentTip = tips[Math.floor(Math.random() * tips.length)]
</script>

<style scoped>
.knowledge-panel {
  width: var(--panel-width);
  height: 100vh;
  background: var(--bg-panel);
  border-left: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px 12px;
  overflow-y: auto;
  flex-shrink: 0;
}

.panel-block {
  background: var(--bg-chat);
  border-radius: var(--radius);
  padding: 14px;
  border: 1px solid rgba(0,0,0,0.04);
}

.block-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 10px;
}

.block-icon {
  font-size: 16px;
}

.badge {
  margin-left: auto;
  background: var(--primary);
  color: #fff;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 600;
}

/* ── 文件区域 ── */

.empty-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px 8px;
  color: var(--text-secondary);
  font-size: 12px;
  text-align: center;
}

.empty-icon {
  font-size: 28px;
  opacity: 0.5;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s;
}

.file-item:hover,
.file-item.active {
  background: var(--primary-bg);
}

.file-type-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.file-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.file-name {
  font-size: 13px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-meta {
  font-size: 11px;
  color: var(--text-secondary);
}

.file-remove {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-secondary);
  font-size: 12px;
  padding: 2px;
  opacity: 0;
  transition: opacity 0.2s;
}

.file-item:hover .file-remove {
  opacity: 1;
}

.file-remove:hover {
  color: #ef4444;
}

/* ── 知识库 ── */

.knowledge-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.kb-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s;
}

.kb-item:hover {
  background: var(--primary-bg);
}

.kb-icon {
  font-size: 18px;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--bg-panel);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.kb-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.kb-title {
  font-size: 13px;
  font-weight: 500;
}

.kb-count {
  font-size: 11px;
  color: var(--text-secondary);
}

/* ── 小贴士 ── */

.tips-block {
  margin-top: auto;
}

.tip-text {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.6;
}
</style>
