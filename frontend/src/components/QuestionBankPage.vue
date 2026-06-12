<template>
  <main class="page qb-page">
    <div class="page-top">
      <h2 class="page-title">面试题库</h2>
      <p class="page-sub">265 道精选 Java 面试题</p>
    </div>

    <!-- 分类筛选 -->
    <div class="filter-tabs">
      <button
        v-for="cat in categories"
        :key="cat"
        class="filter-btn"
        :class="{ active: activeCategory === cat }"
        @click="activeCategory = cat"
      >{{ cat }}</button>
    </div>

    <!-- 题目列表 -->
    <div class="qb-list">
      <div
        v-for="q in filteredQuestions"
        :key="q.id"
        class="qb-card"
        :class="{ expanded: expandedId === q.id }"
        @click="expandedId = expandedId === q.id ? null : q.id"
      >
        <div class="qb-card-header">
          <div class="qb-tags">
            <span class="qb-diff" :class="q.difficulty">{{ q.difficulty }}</span>
            <span class="qb-cat">{{ q.category }}</span>
          </div>
          <span class="qb-title">{{ q.title }}</span>
        </div>

        <div v-if="expandedId === q.id" class="qb-expand">
          <div class="qb-guide">
            <span class="guide-label">AI 答题指导</span>
            <p class="guide-text">回答时先讲核心概念，再结合项目实践说明使用场景。注意说明方案的 trade-off，展现技术判断力。回答前可向面试官确认范围。</p>
          </div>
          <button class="practice-btn">和 AI 练习这道题 →</button>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, computed } from 'vue'

const activeCategory = ref('全部')
const expandedId = ref(null)

const categories = ['全部', 'Spring Boot', '并发编程', 'JVM', '设计模式', '系统设计', 'Java 17+']

const questions = [
  { id: 1, title: '请解释 @Component、@Service、@Repository 和 @Controller 的区别。', difficulty: '基础', category: 'Spring Boot' },
  { id: 2, title: 'synchronized 和 ReentrantLock 有何区别？各自适用于什么场景？', difficulty: '进阶', category: '并发编程' },
  { id: 3, title: '描述 G1 垃圾收集器的工作原理，以及它与 CMS 的主要区别。', difficulty: '高级', category: 'JVM' },
  { id: 4, title: '请实现建造者模式（Builder Pattern），它解决了构造函数的哪些问题？', difficulty: '基础', category: '设计模式' },
  { id: 5, title: 'Spring 的事务传播机制是如何工作的？请解释 REQUIRED 与 REQUIRES_NEW 的区别。', difficulty: '中级', category: 'Spring Boot' },
  { id: 6, title: '设计一个每日承载 1 亿请求的短链接服务，请描述你的整体思路。', difficulty: '进阶', category: '系统设计' },
  { id: 7, title: '什么是密封类（Sealed Class）和记录类（Record）？它们各自适合用在什么场景？', difficulty: '基础', category: 'Java 17+' },
  { id: 8, title: '如何诊断和解决线上 Full GC 频繁的问题？', difficulty: '高级', category: 'JVM' },
  { id: 9, title: 'Redis 缓存穿透、击穿、雪崩分别是什么？如何解决？', difficulty: '中级', category: 'Spring Boot' },
  { id: 10, title: '请比较 HashMap、ConcurrentHashMap 和 Hashtable 的线程安全实现差异。', difficulty: '进阶', category: '并发编程' }
]

const filteredQuestions = computed(() => {
  if (activeCategory.value === '全部') return questions
  return questions.filter(q => q.category === activeCategory.value)
})
</script>

<style scoped>
.page { flex: 1; overflow-y: auto; padding: 32px 40px; max-width: 800px; margin: 0 auto; width: 100%; }

.page-top { margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 700; color: var(--text-900); margin-bottom: 6px; }
.page-sub { font-size: 14px; color: var(--text-500); }

/* ── Filter tabs ── */
.filter-tabs { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 20px; }

.filter-btn {
  padding: 6px 16px; border-radius: var(--radius-full);
  border: 1px solid rgba(147,51,234,0.08); background: rgba(255,255,255,0.6);
  cursor: pointer; font-size: 12px; font-weight: 500; font-family: inherit;
  color: var(--text-500); transition: all var(--transition);
}
.filter-btn:hover { border-color: rgba(147,51,234,0.25); color: var(--purple-600); }
.filter-btn.active { background: var(--purple-600); color: #fff; border-color: var(--purple-600); }

/* ── Question cards ── */
.qb-list { display: flex; flex-direction: column; gap: 6px; }

.qb-card {
  background: rgba(255,255,255,0.6); backdrop-filter: blur(8px);
  border: 1px solid rgba(147,51,234,0.04); border-radius: var(--radius);
  padding: 14px 18px; cursor: pointer; transition: all var(--transition);
}
.qb-card:hover { border-color: rgba(147,51,234,0.12); background: rgba(255,255,255,0.85); }
.qb-card.expanded { border-color: rgba(147,51,234,0.2); background: rgba(255,255,255,0.9); }

.qb-card-header { display: flex; flex-direction: column; gap: 6px; }

.qb-tags { display: flex; gap: 6px; }

.qb-diff {
  font-size: 10px; font-weight: 600; padding: 2px 8px; border-radius: var(--radius-full);
  text-transform: uppercase;
}
.qb-diff.基础 { background: #ECFDF5; color: #059669; }
.qb-diff.中级 { background: #FFFBEB; color: #D97706; }
.qb-diff.进阶 { background: #FEF2F2; color: #DC2626; }
.qb-diff.高级 { background: #EDE9FE; color: #7C3AED; }

.qb-cat {
  font-size: 10px; font-weight: 500; padding: 2px 8px;
  border-radius: var(--radius-full); background: var(--purple-50); color: var(--purple-600);
}

.qb-title { font-size: 13px; font-weight: 500; color: var(--text-900); line-height: 1.5; }

/* ── Expand ── */
.qb-expand { margin-top: 14px; padding-top: 14px; border-top: 1px solid rgba(147,51,234,0.06); }

.qb-guide { margin-bottom: 12px; }
.guide-label { font-size: 11px; font-weight: 600; color: var(--purple-500); display: block; margin-bottom: 6px; }
.guide-text { font-size: 12px; color: var(--text-400); line-height: 1.6; }

.practice-btn {
  padding: 8px 18px; border-radius: var(--radius-sm);
  background: linear-gradient(135deg, #A855F7, #7E22CE);
  color: #fff; font-size: 12px; font-weight: 500; font-family: inherit;
  border: none; cursor: pointer; transition: all var(--transition);
}
.practice-btn:hover { opacity: 0.9; }
</style>
