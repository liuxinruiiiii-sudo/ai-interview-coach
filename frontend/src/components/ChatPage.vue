<template>
  <div class="chat-page">
    <ChatWindow
      :messages="messages"
      :loading="loading"
      :mode="mode"
      @quick-ask="$emit('quick-ask', $event)"
    />
    <ChatInput
      :loading="loading"
      @send="$emit('send', $event)"
    />
  </div>
</template>

<script setup>
import ChatWindow from './ChatWindow.vue'
import ChatInput from './ChatInput.vue'

defineProps({
  messages: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  mode: { type: String, default: 'resume' }
})

defineEmits(['quick-ask', 'send'])
</script>

<style scoped>
.chat-page {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}
</style>
