<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox } from 'element-plus'

// 定义组件属性
const props = defineProps<{
  modelValue: Record<string, boolean>
}>()

// 定义事件发射
const emit = defineEmits<{
  (e: 'update:modelValue', value: Record<string, boolean>): void
  (e: 'codeChange', value: string): void
}>()

// 定义宏包配置项类型
interface PackageConfig {
  packageName: string
  title: string
  items: Array<{ key: string; label: string }>
  optionsMap: Record<string, string>
}

// 创建宏包工厂函数
const createPackageConfig = (
  packageName: string,
  title: string,
  items: Array<{ key: string; label: string }>,
  optionsMap: Record<string, string>
): PackageConfig => ({
  packageName,
  title,
  items,
  optionsMap
})

// 宏包通用代码生成函数
const generatePackageCode = (
  pkg: PackageConfig,
  values: Record<string, boolean>
): string | null => {
  const options = pkg.items
    .filter(item => values[item.key])
    .map(item => pkg.optionsMap[item.key])
  
  return options.length > 0 
    ? `\\PassOptionsToPackage{${options.join(',')}}{${pkg.packageName}}`
    : null
}

// 宏包配置数据
const packageConfigs = ref([
  createPackageConfig(
    'xeCJK',
    'xeCJK 选项',
    [
      { key: 'autoFakeBold', label: 'AutoFakeBold' },
      { key: 'autoFakeSlant', label: 'AutoFakeSlant' }
    ],
    {
      autoFakeBold: 'AutoFakeBold=true',
      autoFakeSlant: 'AutoFakeSlant=true'
    }
  ),
  createPackageConfig(
    'fontspec',
    'fontspec 选项',
    [
      { key: 'noMath', label: 'no-math' }
    ],
    {
      noMath: 'no-math'
    }
  ),
  createPackageConfig(
    'xcolor',
    'xcolor 选项',
    [
      { key: 'prologue', label: 'prologue' },
      { key: 'dvipsnames', label: 'dvipsnames' }
    ],
    {
      prologue: 'prologue',
      dvipsnames: 'dvipsnames'
    }
  )
])

// 获取和设置选项值
const optionValues = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 计算生成的LaTeX代码
const computedLatexCode = computed(() => {
  const codes = packageConfigs.value
    .map(pkg => generatePackageCode(pkg, optionValues.value))
    .filter((code): code is string => code !== null)
  
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
      
      <div v-for="pkg in packageConfigs" :key="pkg.packageName">
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