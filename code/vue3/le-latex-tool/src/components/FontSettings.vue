<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElInput } from 'element-plus'

const props = defineProps<{
  modelValue: {
    path: string
  }
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { path: string }): void
  (e: 'codeChange', value: string): void
}>()

const fontPath = computed({
  get: () => props.modelValue.path,
  set: (value) => emit('update:modelValue', {
    path: value
  })
})

const defaultPath = '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/'

const computedLatexCode = computed(() => {
  if (!fontPath.value) return ''
  
  return [
    `\\setCJKmainfont[Path=${fontPath.value}]{FangZhengShuSong-GBK-1.ttf}`,
    `\\setCJKsansfont[Path=${fontPath.value}]{FangZhengHeiTi-GBK-1.ttf}`,
    `\\setCJKmonofont[Path=${fontPath.value}]{FangZhengFangSong-GBK-1.ttf}`
  ].join('\n')
})

watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

onMounted(() => {
  // 如果没有初始值，则使用默认路径
  if (!fontPath.value) {
    fontPath.value = defaultPath
  }
  emit('codeChange', computedLatexCode.value)
})
</script>

<template>
  <el-card shadow="hover">
    <div>
      <strong>字体设置</strong>
      <p>设置中文字体路径</p>
      
      <el-input
        v-model="fontPath"
        placeholder="请输入字体文件路径"
        clearable
      />
    </div>
  </el-card>
</template>