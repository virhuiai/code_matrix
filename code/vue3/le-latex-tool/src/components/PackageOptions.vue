<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElInput } from 'element-plus'

// 定义组件属性
const props = defineProps<{
  modelValue: Record<string, boolean>
}>()

// 定义事件发射
const emit = defineEmits<{
  (e: 'update:modelValue', value: Record<string, boolean>): void
  (e: 'codeChange', value: string): void
}>()

// 选项数据
const options = ref([
  { 
    packageName: 'xeCJK', 
    title: 'xeCJK 选项',
    items: [
      { key: 'autoFakeBold', label: 'AutoFakeBold' },
      { key: 'autoFakeSlant', label: 'AutoFakeSlant' }
    ]
  },
  { 
    packageName: 'fontspec', 
    title: 'fontspec 选项',
    items: [
      { key: 'noMath', label: 'no-math' }
    ]
  },
  { 
    packageName: 'xcolor', 
    title: 'xcolor 选项',
    items: [
      { key: 'prologue', label: 'prologue' },
      { key: 'dvipsnames', label: 'dvipsnames' }
    ]
  }
])

// 获取和设置选项值
const optionValues = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 计算生成的LaTeX代码
const computedLatexCode = computed(() => {
  const rs = []
  
  // xeCJK 选项处理
  const options_xeCJK = []
  if (optionValues.value.autoFakeBold) options_xeCJK.push('AutoFakeBold=true')
  if (optionValues.value.autoFakeSlant) options_xeCJK.push('AutoFakeSlant=true')
  if (options_xeCJK.length > 0) {
    const option_xeCJK = options_xeCJK.join(',')
    rs.push(`\\PassOptionsToPackage{${option_xeCJK}}{xeCJK}`)
  }

  // fontspec 选项处理
  const options_fontspec = []
  if (optionValues.value.noMath) options_fontspec.push('no-math')
  if (options_fontspec.length > 0) {
    const option_fontspec = options_fontspec.join(',')
    rs.push(`\\PassOptionsToPackage{${option_fontspec}}{fontspec}`)
  }

  // xcolor 选项处理
  const options_xcolor = []
  if (optionValues.value.prologue) options_xcolor.push('prologue')
  if (optionValues.value.dvipsnames) options_xcolor.push('dvipsnames')
  if (options_xcolor.length > 0) {
    const option_xcolor = options_xcolor.join(',')
    rs.push(`\\PassOptionsToPackage{${option_xcolor}}{xcolor}`)
  }

  return rs.join('\n')
})

// 监听代码变化并向父组件发送事件
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时也发送一次初始代码
onMounted(() => {
  emit('codeChange', computedLatexCode.value)
})
</script>

<template>
  <el-card shadow="hover">
    <div style="display: flex; flex-direction: column; gap: 15px;">
      <strong>PassOptionsToPackage</strong>
      
      <div v-for="pkg in options" :key="pkg.packageName">
        <strong>{{ pkg.title }}</strong>
        <el-checkbox 
          v-for="item in pkg.items" 
          :key="item.key"
          v-model="optionValues[item.key]" 
          :label="item.label" 
          style="display: block; margin-left: 20px;"
        />
      </div>
      
    
    </div>
  </el-card>
</template>

<style scoped>
</style>