<template>
  <main class="page roadmap-page">
    <div class="page-top">
      <h2 class="page-title">6 周学习路线</h2>
      <p class="page-sub">为中级 Java 开发岗位量身定制</p>
    </div>

    <!-- 总体进度 -->
    <div class="overall-progress">
      <div class="op-stats">
        <div class="op-item">
          <span class="op-value">28%</span>
          <span class="op-label">总体进度</span>
        </div>
        <div class="op-divider"></div>
        <div class="op-item">
          <span class="op-value">第 2 周，共 6 周</span>
          <span class="op-label">进度正常</span>
        </div>
        <div class="op-divider"></div>
        <div class="op-item">
          <span class="op-value">🔥 5 天</span>
          <span class="op-label">已连续学习</span>
        </div>
      </div>
      <div class="op-bar">
        <div class="op-fill" style="width:28%"></div>
      </div>
    </div>

    <!-- 周列表 -->
    <div class="week-list">
      <div
        v-for="week in weeks"
        :key="week.num"
        class="week-card"
        :class="[week.status === 'locked' ? 'locked' : '', expandedWeek === week.num ? 'expanded' : '']"
      >
        <div class="week-header" @click="toggleWeek(week.num)">
          <div class="week-badge" :class="week.status">
            <span v-if="week.status === 'complete'">✅</span>
            <span v-else-if="week.status === 'active'">🔵</span>
            <span v-else>🔒</span>
          </div>
          <div class="week-info">
            <span class="week-name">第 {{ week.num }} 周</span>
            <span class="week-status-text">{{ statusText(week.status) }}</span>
          </div>
          <span class="week-progress">{{ week.done }}/{{ week.topics.length }} 个主题</span>
          <svg class="week-chevron" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"/></svg>
        </div>

        <div v-if="expandedWeek === week.num || week.status === 'active'" class="week-body">
          <div class="week-title-row">
            <span class="week-theme">{{ week.theme }}</span>
          </div>
          <div v-for="t in week.topics" :key="t" class="topic-item">
            <span class="topic-check" :class="{ done: week.status === 'complete' }">
              <svg v-if="week.status === 'complete'" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg>
              <span v-else></span>
            </span>
            <span>{{ t }}</span>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref } from 'vue'

const expandedWeek = ref(2)

const weeks = [
  { num: 1, status: 'complete', theme: 'Java 基础强化', done: 4, topics: ['面向对象与集合框架', '异常处理与泛型', 'IO 与 NIO', 'Java 8-17 新特性'] },
  { num: 2, status: 'active', theme: '并发编程与多线程', done: 2, topics: ['线程生命周期与状态', 'synchronized、volatile 与原子类', 'ExecutorService 与线程池', 'CompletableFuture 使用模式'] },
  { num: 3, status: 'upcoming', theme: 'Spring Boot 与微服务', done: 0, topics: ['Spring 核心原理', 'Spring Boot 自动配置', '微服务架构设计', 'Docker & K8s 基础'] },
  { num: 4, status: 'upcoming', theme: '数据库与持久层', done: 0, topics: ['MySQL 索引与优化', '事务与锁机制', 'Redis 缓存策略', '分库分表方案'] },
  { num: 5, status: 'locked', theme: '系统设计', done: 0, topics: ['分布式系统基础', '高并发设计模式', '常见系统设计题', '容量估算与优化'] },
  { num: 6, status: 'locked', theme: '冲刺模拟与综合复盘', done: 0, topics: ['全真模拟面试', '薄弱环节强化', '项目经历包装', '薪资谈判技巧'] }
]

function toggleWeek(num) {
  if (expandedWeek.value === num) expandedWeek.value = null
  else expandedWeek.value = num
}

function statusText(status) {
  return { complete: '已完成', active: '进行中', upcoming: '即将开始', locked: '未解锁' }[status] || ''
}
</script>

<style scoped>
.page { flex: 1; overflow-y: auto; padding: 32px 40px; max-width: 800px; margin: 0 auto; width: 100%; }

.page-top { margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 700; color: var(--text-900); margin-bottom: 6px; }
.page-sub { font-size: 14px; color: var(--text-500); }

/* ── Overall progress ── */
.overall-progress {
  background: rgba(255,255,255,0.7); backdrop-filter: blur(10px);
  border: 1px solid rgba(147,51,234,0.06); border-radius: var(--radius-lg);
  padding: 20px 24px; margin-bottom: 20px;
}
.op-stats { display: flex; align-items: center; gap: 20px; margin-bottom: 14px; }
.op-item { display: flex; flex-direction: column; }
.op-value { font-size: 16px; font-weight: 700; color: var(--text-900); }
.op-label { font-size: 11px; color: var(--text-400); margin-top: 2px; }
.op-divider { width: 1px; height: 30px; background: rgba(147,51,234,0.08); }
.op-bar { height: 6px; background: var(--purple-100); border-radius: 3px; overflow: hidden; }
.op-fill { height: 100%; background: linear-gradient(90deg, #A855F7, #7E22CE); border-radius: 3px; transition: width 0.6s ease; }

/* ── Weeks ── */
.week-list { display: flex; flex-direction: column; gap: 6px; }

.week-card {
  background: rgba(255,255,255,0.6); backdrop-filter: blur(8px);
  border: 1px solid rgba(147,51,234,0.04); border-radius: var(--radius);
  overflow: hidden; transition: all var(--transition);
}
.week-card.locked { opacity: 0.5; }
.week-card.expanded { background: rgba(255,255,255,0.85); }

.week-header {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 18px; cursor: pointer; transition: all var(--transition);
}
.week-header:hover { background: rgba(147,51,234,0.02); }

.week-badge { width: 32px; height: 32px; border-radius: var(--radius-sm); display: flex; align-items: center; justify-content: center; font-size: 14px; flex-shrink: 0; }
.week-badge.complete { background: #ECFDF5; }
.week-badge.active { background: #EDE9FE; }
.week-badge.upcoming, .week-badge.locked { background: var(--purple-50); }

.week-info { display: flex; flex-direction: column; flex: 1; }
.week-name { font-size: 14px; font-weight: 600; color: var(--text-900); }
.week-status-text { font-size: 11px; color: var(--text-400); margin-top: 1px; }

.week-progress { font-size: 12px; color: var(--text-400); }

.week-chevron { transition: transform var(--transition); color: var(--text-300); }
.week-card.expanded .week-chevron { transform: rotate(180deg); }

/* ── Week body ── */
.week-body { padding: 0 18px 16px 62px; }
.week-theme { font-size: 13px; font-weight: 600; color: var(--purple-600); }
.week-title-row { margin-bottom: 10px; }

.topic-item {
  display: flex; align-items: center; gap: 10px;
  padding: 6px 0; font-size: 13px; color: var(--text-500);
}
.topic-check {
  width: 20px; height: 20px; border-radius: 6px;
  border: 1.5px solid rgba(147,51,234,0.2); display: flex;
  align-items: center; justify-content: center; flex-shrink: 0;
}
.topic-check.done { background: #10B981; border-color: #10B981; color: #fff; }
</style>
