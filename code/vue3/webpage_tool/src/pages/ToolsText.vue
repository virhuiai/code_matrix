<script setup>
import { ref } from 'vue'

const text = ref('')
function convertSpacesToUnderscore() {
  // 将连续的空白字符（包括空格、制表符、换行符等）替换为一个下划线
  text.value = text.value.replace(/\s+/g, '_')
}
</script>

<template>
  <el-card shadow="hover">
    <el-text tag="b">文本处理：空格转下划线</el-text>
    <el-divider />
    <el-input
      v-model="text"
      type="textarea"
      :autosize="{ minRows: 6, maxRows: 18 }"
      placeholder="输入文本，点击下方按钮转换空格为下划线"
    />
    <el-space size="large">
      <el-button type="primary" @click="convertSpacesToUnderscore">转换</el-button>
      <el-button @click="text = ''">清空</el-button>
    </el-space>
  </el-card>
</template>

<style scoped>
</style>
