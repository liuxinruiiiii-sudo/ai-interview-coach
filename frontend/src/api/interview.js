import axios from 'axios'

const api = axios.create({
  baseURL: '/api/interview',
  timeout: 120000
})

/**
 * 开始模拟面试 — 生成一道面试题
 */
export function startInterview(category, level = 'intermediate') {
  return api.post('/start', { category, level })
}

/**
 * 提交回答 — AI 评价并返回反馈
 */
export function submitAnswer(question, userAnswer, category) {
  return api.post('/answer', { question, userAnswer, category })
}
