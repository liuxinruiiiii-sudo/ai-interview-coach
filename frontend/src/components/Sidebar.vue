<template>
  <aside class="sidebar">
    <!-- Logo -->
    <div class="sidebar-header">
      <div class="logo">
        <span class="logo-icon">AI</span>
        <div class="logo-info">
          <span class="logo-text">面试学习助手</span>
          <span class="logo-sub">Interview Coach</span>
        </div>
      </div>
    </div>

    <!-- 模式选择 -->
    <div class="mode-section">
      <div class="section-label">当前模式</div>
      <div class="mode-list">
        <button
          v-for="mode in modes"
          :key="mode.key"
          class="mode-btn"
          :class="{ active: currentMode === mode.key }"
          :style="{ '--mode-color': mode.color }"
          @click="$emit('change-mode', mode.key)"
        >
          <span class="mode-icon">{{ mode.icon }}</span>
          <div class="mode-info">
            <span class="mode-name">{{ mode.name }}</span>
            <span class="mode-desc">{{ mode.desc }}</span>
          </div>
        </button>
      </div>
    </div>

    <!-- 会话列表 -->
    <div class="session-section">
      <div class="section-label">对话历史</div>
      <button class="btn-new" @click="$emit('new-chat')">
        <span>+</span> 新建对话
      </button>
      <div class="session-list">
        <div
          v-for="session in sessions"
          :key="session.id"
          class="session-item"
          :class="{ active: session.id === activeId }"
          @click="$emit('select-session', session.id)"
        >
          <span class="session-icon">💬</span>
          <span class="session-title">{{ session.title }}</span>
        </div>
        <div v-if="sessions.length === 0" class="empty-hint">
          暂无对话记录
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
defineProps({
  sessions: { type: Array, default: () => [] },
  activeId: { type: String, default: '' },
  currentMode: { type: String, default: 'resume' }
})

defineEmits(['new-chat', 'select-session', 'change-mode'])

const modes = [
  { key: 'resume', name: '简历分析', desc: '上传简历智能分析', icon: '📋', color: 'var(--mode-resume)' },
  { key: 'interview', name: '面试模拟', desc: '模拟真实面试场景', icon: '🎯', color: 'var(--mode-interview)' },
  { key: 'plan', name: '学习计划', desc: '定制备考路线图', icon: '📝', color: 'var(--mode-plan)' }
]
</script>

<style scoped>
.sidebar {
  width: var(--sidebar-width);
  height: 100vh;
  background: var(--sidebar-bg);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  overflow: hidden;
}

.sidebar-header {
  padding: 20px 18px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 800;
  color: #fff;
  flex-shrink: 0;
}

.logo-info {
  display: flex;
  flex-direction: column;
}

.logo-text {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  line-height: 1.3;
}

.logo-sub {
  font-size: 11px;
  color: var(--text-light);
  font-weight: 400;
}

/* ── 模式选择 ── */

.mode-section {
  padding: 16px 12px 12px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}

.section-label {
  font-size: 11px;
  font-weight: 600;
  color: rgba(255,255,255,0.35);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: 0 6px;
  margin-bottom: 10px;
}

.mode-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.mode-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 10px;
  cursor: pointer;
  text-align: left;
  color: var(--text-sidebar);
  transition: all 0.2s;
}

.mode-btn:hover {
  background: var(--sidebar-hover);
}

.mode-btn.active {
  background: var(--sidebar-active);
  border-color: var(--mode-color);
}

.mode-icon {
  font-size: 20px;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255,255,255,0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.mode-btn.active .mode-icon {
  background: color-mix(in srgb, var(--mode-color) 15%, transparent);
}

.mode-info {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.mode-name {
  font-size: 13px;
  font-weight: 600;
  color: #e0def0;
}

.mode-btn.active .mode-name {
  color: #fff;
}

.mode-desc {
  font-size: 11px;
  color: var(--text-sidebar);
  margin-top: 1px;
}

/* ── 会话列表 ── */

.session-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 12px;
  overflow: hidden;
}

.btn-new {
  width: 100%;
  padding: 9px 14px;
  background: rgba(255,255,255,0.06);
  border: 1px dashed rgba(255,255,255,0.15);
  border-radius: 8px;
  color: var(--text-light);
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.2s;
  margin-top: 4px;
  margin-bottom: 8px;
}

.btn-new:hover {
  background: var(--sidebar-hover);
  border-color: rgba(255,255,255,0.3);
}

.btn-new span {
  font-size: 17px;
  font-weight: 300;
  line-height: 1;
}

.session-list {
  flex: 1;
  overflow-y: auto;
}

.session-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 10px;
  margin: 1px 0;
  border-radius: 8px;
  cursor: pointer;
  color: var(--text-sidebar);
  font-size: 13px;
  transition: all 0.15s;
}

.session-item:hover {
  background: var(--sidebar-hover);
  color: #e0def0;
}

.session-item.active {
  background: var(--sidebar-active);
  color: #fff;
}

.session-icon {
  font-size: 14px;
  flex-shrink: 0;
}

.session-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-hint {
  padding: 20px 8px;
  text-align: center;
  color: rgba(255,255,255,0.18);
  font-size: 12px;
}
</style>
