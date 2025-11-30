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

// 选项数据和LaTeX代码生成合并
const packageConfig = ref([
  { 
    packageName: 'xeCJK', 
    title: 'xeCJK 选项',
    items: [
      { key: 'autoFakeBold', label: 'AutoFakeBold' },
      { key: 'autoFakeSlant', label: 'AutoFakeSlant' }
    ],
    generateCode: (values: Record<string, boolean>) => {
      const options = []
      if (values.autoFakeBold) options.push('AutoFakeBold=true')
      if (values.autoFakeSlant) options.push('AutoFakeSlant=true')
      return options.length > 0 ? `\\PassOptionsToPackage{${options.join(',')}}{xeCJK}` : null
    }
  },
  { 
    packageName: 'fontspec', 
    title: 'fontspec 选项',
    items: [
      { key: 'noMath', label: 'no-math' }
    ],
    generateCode: (values: Record<string, boolean>) => {
      const options = []
      if (values.noMath) options.push('no-math')
      return options.length > 0 ? `\\PassOptionsToPackage{${options.join(',')}}{fontspec}` : null
    }
  },
  { 
    packageName: 'xcolor', 
    title: 'xcolor 选项',
    items: [
      { key: 'prologue', label: 'prologue' },
      { key: 'dvipsnames', label: 'dvipsnames' }
    ],
    generateCode: (values: Record<string, boolean>) => {
      const options = []
      if (values.prologue) options.push('prologue')
      if (values.dvipsnames) options.push('dvipsnames')
      return options.length > 0 ? `\\PassOptionsToPackage{${options.join(',')}}{xcolor}` : null
    }
  }
])

// 获取和设置选项值
const optionValues = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 计算生成的LaTeX代码
const computedLatexCode = computed(() => {
  const codes = packageConfig.value
    .map(pkg => pkg.generateCode(optionValues.value))
    .filter(code => code !== null)
  
  return codes.join('\n')
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
      
      <div v-for="pkg in packageConfig" :key="pkg.packageName">
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