<template>
  <aside class="sidebar">
    <!-- Logo -->
    <div class="sidebar-header">
      <div class="logo">
        <div class="logo-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
        </div>
        <div class="logo-text">
          <span class="logo-title">面试 AI</span>
          <span class="logo-subtitle">Java 面试教练</span>
        </div>
      </div>
    </div>

    <!-- 新建对话 -->
    <div class="new-chat-wrap">
      <button class="btn-new" @click="$emit('new-chat')">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新建对话
      </button>
    </div>

    <!-- 主导航 -->
    <nav class="main-nav">
      <button
        v-for="item in navItems"
        :key="item.key"
        class="nav-item"
        :class="{ active: currentPage === item.key }"
        @click="$emit('navigate', item.key)"
      >
        <span class="nav-icon" v-html="item.icon"></span>
        <span class="nav-label">{{ item.label }}</span>
      </button>
    </nav>

    <!-- 最近对话 -->
    <div class="session-section">
      <div class="section-header">
        <span class="section-title">最近对话</span>
        <button class="search-toggle" @click="showSearch = !showSearch">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        </button>
      </div>

      <input
        v-if="showSearch"
        v-model="searchQuery"
        class="search-input"
        placeholder="搜索对话..."
        @click.stop
      />

      <div class="session-list">
        <div
          v-for="session in filteredSessions"
          :key="session.id"
          class="session-item"
          :class="{ active: session.id === activeId && currentPage === 'chat' }"
          @click="$emit('select-session', session.id)"
        >
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
          <span class="session-title" v-if="editingId !== session.id">{{ session.title }}</span>
          <input
            v-else
            ref="renameInput"
            v-model="editValue"
            class="rename-input"
            maxlength="40"
            @keydown.enter.prevent="confirmRename(session.id)"
            @keydown.esc.prevent="cancelRename"
            @blur="confirmRename(session.id)"
            @click.stop
          />

          <!-- 更多按钮 -->
          <button
            class="more-btn"
            @click.stop="toggleMenu(session.id)"
          >
            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><circle cx="12" cy="5" r="2"/><circle cx="12" cy="12" r="2"/><circle cx="12" cy="19" r="2"/></svg>
          </button>

          <!-- 菜单 -->
          <transition name="menu">
            <div v-if="menuId === session.id" class="menu-popup" @click.stop>
              <button class="menu-item" @click="startRename(session)">重命名</button>
              <div class="menu-divider"></div>
              <button class="menu-item danger" @click="confirmDelete(session.id)">删除</button>
            </div>
          </transition>
        </div>
        <div v-if="filteredSessions.length === 0" class="empty-state">暂无对话</div>
      </div>
    </div>

    <!-- 用户信息 -->
    <div class="user-footer">
      <div class="user-info">
        <span class="user-avatar">陈</span>
        <div class="user-text">
          <span class="user-name">陈明</span>
          <span class="user-role">Java 开发工程师</span>
        </div>
      </div>
    </div>

    <!-- 删除确认 -->
    <transition name="modal">
      <div v-if="deleteTarget" class="modal-overlay" @click.self="deleteTarget = null">
        <div class="modal-box">
          <p class="modal-text">确认删除该对话吗？</p>
          <p class="modal-hint">删除后无法恢复</p>
          <div class="modal-actions">
            <button class="modal-btn cancel" @click="deleteTarget = null">取消</button>
            <button class="modal-btn confirm" @click="doDelete">删除</button>
          </div>
        </div>
      </div>
    </transition>
  </aside>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'

const props = defineProps({
  sessions: { type: Array, default: () => [] },
  activeId: { type: String, default: '' },
  currentPage: { type: String, default: 'chat' }
})

const emit = defineEmits(['navigate', 'new-chat', 'select-session', 'delete-session', 'rename-session'])

const navItems = [
  { key: 'chat', label: 'AI 对话', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>' },
  { key: 'mock', label: '模拟面试', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 12m-10 0a10 10 0 1 0 20 0a10 10 0 1 0-20 0"/><path d="M8 15s1-3 4-3 4 3 4 3"/><line x1="9" y1="9" x2="9.01" y2="9"/><line x1="15" y1="9" x2="15.01" y2="9"/></svg>' },
  { key: 'roadmap', label: '学习路线', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>' },
  { key: 'qbank', label: '题库', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>' },
  { key: 'resume', label: '简历分析', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>' }
]

// ── 搜索 ──
const showSearch = ref(false)
const searchQuery = ref('')

const filteredSessions = computed(() => {
  const q = searchQuery.value.toLowerCase().trim()
  if (!q) return props.sessions
  return props.sessions.filter(s => s.title.toLowerCase().includes(q))
})

// ── 菜单 / 重命名 / 删除 ──
const menuId = ref(null)
const editingId = ref(null)
const editValue = ref('')
const deleteTarget = ref(null)

function toggleMenu(id) { menuId.value = menuId.value === id ? null : id }

function startRename(session) {
  editValue.value = session.title
  editingId.value = session.id
  menuId.value = null
  nextTick(() => { const el = document.querySelector('.rename-input'); if (el) { el.focus(); el.select() } })
}

function confirmRename(id) {
  const title = editValue.value.trim()
  if (title && editingId.value === id) emit('rename-session', id, title)
  cancelRename()
}

function cancelRename() { editingId.value = null; editValue.value = '' }

function confirmDelete(id) { menuId.value = null; deleteTarget.value = id }

function doDelete() {
  if (deleteTarget.value) { emit('delete-session', deleteTarget.value); deleteTarget.value = null }
}
</script>

<style scoped>
.sidebar {
  width: var(--sidebar-w); height: 100%;
  background: rgba(255,255,255,0.5); backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-right: 1px solid rgba(147,51,234,0.06);
  display: flex; flex-direction: column; flex-shrink: 0; position: relative;
}

/* ── Header ── */
.sidebar-header { padding: 24px 20px 4px; }
.logo { display: flex; align-items: center; gap: 12px; }
.logo-icon { width: 40px; height: 40px; background: linear-gradient(135deg, #A855F7, #7E22CE); border-radius: var(--radius-sm); display: flex; align-items: center; justify-content: center; color: #fff; box-shadow: 0 4px 16px rgba(147,51,234,0.3); }
.logo-text { display: flex; flex-direction: column; }
.logo-title { font-size: 15px; font-weight: 700; color: var(--text-900); letter-spacing: -0.02em; }
.logo-subtitle { font-size: 11px; color: var(--text-400); font-weight: 500; margin-top: 1px; }

/* ── New chat ── */
.new-chat-wrap { padding: 16px 16px 12px; }
.btn-new {
  width: 100%; padding: 10px 16px; border-radius: var(--radius);
  background: rgba(147,51,234,0.06); border: 1px dashed rgba(147,51,234,0.2);
  color: var(--purple-600); font-size: 13px; font-weight: 500;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  gap: 6px; font-family: inherit; transition: all var(--transition);
}
.btn-new:hover { background: rgba(147,51,234,0.1); border-color: rgba(147,51,234,0.4); }

/* ── Nav ── */
.main-nav { padding: 0 12px; display: flex; flex-direction: column; gap: 2px; }

.nav-item {
  display: flex; align-items: center; gap: 10px;
  padding: 9px 12px; border-radius: var(--radius-sm);
  background: transparent; border: none; cursor: pointer;
  font-size: 13px; font-weight: 500; font-family: inherit;
  color: var(--text-500); transition: all var(--transition); width: 100%; text-align: left;
}
.nav-item:hover { background: rgba(255,255,255,0.6); color: var(--text-700); }
.nav-item.active { background: rgba(147,51,234,0.06); color: var(--purple-600); font-weight: 600; }

.nav-icon { display: flex; align-items: center; flex-shrink: 0; }
.nav-icon :deep(svg) { display: block; }

/* ── Sessions ── */
.session-section { flex: 1; overflow: hidden; display: flex; flex-direction: column; padding: 16px 12px 0; }

.section-header { display: flex; align-items: center; justify-content: space-between; padding: 0 8px; margin-bottom: 6px; }

.section-title { font-size: 10px; font-weight: 700; color: var(--text-400); text-transform: uppercase; letter-spacing: 0.06em; }

.search-toggle { background: none; border: none; cursor: pointer; color: var(--text-300); padding: 2px; display: flex; border-radius: 4px; transition: all var(--transition); }
.search-toggle:hover { color: var(--text-500); background: rgba(0,0,0,0.03); }

.search-input {
  width: 100%; padding: 6px 10px; margin-bottom: 8px;
  border: 1px solid rgba(147,51,234,0.12); border-radius: 8px;
  background: rgba(255,255,255,0.7); font-size: 12px; font-family: inherit;
  color: var(--text-900); outline: none;
}
.search-input:focus { border-color: rgba(147,51,234,0.3); }

.session-list { flex: 1; overflow-y: auto; }

.session-item {
  display: flex; align-items: center; gap: 8px;
  padding: 8px 6px 8px 10px; border-radius: var(--radius-xs);
  cursor: pointer; font-size: 13px; color: var(--text-500);
  transition: all var(--transition); position: relative;
}
.session-item:hover { background: rgba(255,255,255,0.7); color: var(--text-700); }
.session-item.active { background: rgba(147,51,234,0.05); color: var(--purple-600); font-weight: 500; }

.session-title { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.rename-input { flex: 1; min-width: 0; border: 1px solid rgba(147,51,234,0.3); border-radius: 6px; background: rgba(255,255,255,0.9); padding: 3px 8px; font-size: 13px; font-family: inherit; color: var(--text-900); outline: none; }

.more-btn {
  width: 24px; height: 24px; border-radius: 6px; border: none;
  background: transparent; color: var(--text-300); cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; opacity: 0; transition: all var(--transition);
}
.session-item:hover .more-btn { opacity: 1; }
.more-btn:hover { background: rgba(147,51,234,0.08); color: var(--purple-500); }

/* ── Menu ── */
.menu-popup {
  position: absolute; right: 4px; top: 100%; z-index: 20;
  background: rgba(255,255,255,0.95); backdrop-filter: blur(16px);
  border: 1px solid rgba(147,51,234,0.1); border-radius: var(--radius-sm);
  box-shadow: var(--shadow-hover); padding: 6px; min-width: 110px;
}
.menu-item { display: flex; align-items: center; gap: 8px; width: 100%; padding: 8px 10px; border: none; border-radius: 8px; background: transparent; cursor: pointer; font-size: 13px; color: var(--text-700); font-family: inherit; transition: all var(--transition); }
.menu-item:hover { background: rgba(147,51,234,0.06); color: var(--purple-600); }
.menu-item.danger { color: #DC2626; }
.menu-item.danger:hover { background: #FEF2F2; }
.menu-divider { height: 1px; margin: 4px 8px; background: rgba(147,51,234,0.08); }

.menu-enter-active, .menu-leave-active { transition: all 0.15s ease; }
.menu-enter-from, .menu-leave-to { opacity: 0; transform: translateY(-4px); }

.empty-state { padding: 16px 8px; text-align: center; font-size: 12px; color: var(--text-300); }

/* ── User ── */
.user-footer {
  padding: 12px 16px 16px; border-top: 1px solid rgba(147,51,234,0.06);
  cursor: pointer;
}
.user-info { display: flex; align-items: center; gap: 10px; }
.user-avatar {
  width: 34px; height: 34px; border-radius: var(--radius-sm);
  background: linear-gradient(135deg, #A855F7, #7E22CE);
  color: #fff; display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 600; flex-shrink: 0;
}
.user-text { display: flex; flex-direction: column; }
.user-name { font-size: 13px; font-weight: 600; color: var(--text-900); }
.user-role { font-size: 11px; color: var(--text-400); margin-top: 1px; }

/* ── Modal ── */
.modal-overlay { position: fixed; inset: 0; z-index: 100; background: rgba(15,23,42,0.2); backdrop-filter: blur(2px); display: flex; align-items: center; justify-content: center; }
.modal-box { background: var(--white); border-radius: var(--radius-lg); padding: 24px; width: 300px; box-shadow: var(--shadow-hover); text-align: center; }
.modal-text { font-size: 15px; font-weight: 600; color: var(--text-900); margin-bottom: 4px; }
.modal-hint { font-size: 13px; color: var(--text-400); margin-bottom: 20px; }
.modal-actions { display: flex; gap: 10px; justify-content: center; }
.modal-btn { padding: 8px 24px; border-radius: var(--radius-sm); border: none; font-size: 13px; font-weight: 500; cursor: pointer; font-family: inherit; }
.modal-btn.cancel { background: var(--purple-50); color: var(--text-700); }
.modal-btn.confirm { background: #FEE2E2; color: #DC2626; }
.modal-enter-active, .modal-leave-active { transition: all 0.2s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; }
</style>
