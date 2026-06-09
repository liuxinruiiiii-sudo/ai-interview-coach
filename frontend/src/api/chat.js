import axios from 'axios'

const api = axios.create({
  baseURL: '/api/chat',
  timeout: 120000
})

/**
 * 纯文本问答
 */
export function sendMessage(message) {
  return api.post('/message', { message })
}

/**
 * 上传 PDF 并解析
 */
export function uploadPdf(file) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 上传 PDF + 提问
 */
export function askWithFile(file, message) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('message', message)
  return api.post('/ask-with-file', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
