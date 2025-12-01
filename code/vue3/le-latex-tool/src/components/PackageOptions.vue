<script setup lang="ts">
// 从 vue 导入需要用到的函数
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
// 从 element-plus 导入需要用到的组件
import { ElCard, ElCheckbox } from 'element-plus'

// 定义组件接收的属性（props）
// modelValue 是一个对象，键为字符串，值为布尔值
const props = defineProps<{
  modelValue: Record<string, boolean>
  componentId?: number
}>()
/**
 Record<Keys, Type> 构造一个对象类型：
    ‌键‌ 来自 Keys 类型（通常是字符串或数字的联合类型）
    ‌值‌ 全部为 Type 类型3
 * 示例‌：定义分数记录
type Scores = Record<'数学' | '英语' | '科学', number>;
const 学生成绩: Scores = {
  数学: 90,
  英语: 85,
  科学: 92
};

 */


// 定义组件可以发出的事件（emits）
// update:modelValue 事件用于更新 modelValue 的值
// codeChange 事件用于通知父组件代码发生了变化
const emit = defineEmits<{
  (e: 'update:modelValue', value: Record<string, boolean>): void
  (e: 'codeChange', value: string): void
}>()

// 定义宏包配置项的接口
interface PackageConfig {
  packageName: string
  title: string
  items: Array<{ key: string; label: string }>
  optionsMap: Record<string, string>
}

// 通用的宏包代码生成函数
const generatePackageCode = (pkg: PackageConfig, values: Record<string, boolean>): string | null => {
  const options = pkg.items
    .filter(item => values[item.key])
    .map(item => pkg.optionsMap[item.key])
  
  return options.length > 0 
    ? `\\PassOptionsToPackage{${options.join(',')}}{${pkg.packageName}}`
    : null
}

// 宏包配置数据
const packageConfigs = ref<PackageConfig[]>([
  {
    packageName: 'xeCJK',
    title: 'xeCJK 选项',
    items: [
      { key: 'autoFakeBold', label: 'AutoFakeBold' },
      { key: 'autoFakeSlant', label: 'AutoFakeSlant' }
    ],
    optionsMap: {
      autoFakeBold: 'AutoFakeBold=true',
      autoFakeSlant: 'AutoFakeSlant=true'
    }
  },
  {
    packageName: 'fontspec',
    title: 'fontspec 选项',
    items: [
      { key: 'noMath', label: 'no-math' }
    ],
    optionsMap: {
      noMath: 'no-math'
    }
  },
  {
    packageName: 'xcolor',
    title: 'xcolor 选项',
    items: [
      { key: 'prologue', label: 'prologue' },
      { key: 'dvipsnames', label: 'dvipsnames' }
    ],
    optionsMap: {
      prologue: 'prologue',
      dvipsnames: 'dvipsnames'
    }
  }
])

// 计算属性：用于获取和设置选项值
// computed 是 Vue 3 中创建计算属性的方法
// get: 从 props 中获取当前值
// set: 当值发生变化时，通过 emit 发送 update:modelValue 事件通知父组件
const optionValues = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 计算属性：生成最终的 LaTeX 代码
const computedLatexCode = computed(() => {
  return packageConfigs.value
    .map(pkg => generatePackageCode(pkg, optionValues.value))
    .filter((code): code is string => code !== null)
    .join('\n')
})

// 监听器：监听 computedLatexCode 的变化
// 当生成的代码发生变化时，通过 emit 发送 codeChange 事件通知父组件
// watch 是 Vue 3 中监听响应式数据变化的方法
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 生命周期钩子：组件挂载完成后执行
// onMounted 是 Vue 3 中组件挂载完成后的回调函数
// 在组件初始化时也发送一次初始代码
onMounted(() => {
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`PackageOptions component loaded successfully with ID: ${props.componentId}`)
  }
})
</script>

<template>
  <el-card shadow="hover" class="package-options-card">
    <div class="package-options">
      <strong class="package-options__title">PassOptionsToPackage</strong>
      
      <!-- 使用 v-for 指令遍历所有宏包配置 -->
      <!-- :key 是 Vue 中用于标识列表项的唯一性的特殊属性 -->
      <div v-for="pkg in packageConfigs" :key="pkg.packageName" class="package-options__group">
        <strong class="package-options__group-title">{{ pkg.title }}</strong>
        <!-- 为每个宏包的选项创建复选框 -->
        <!-- v-model 是 Vue 中实现双向数据绑定的指令 -->
        <el-checkbox 
          v-for="item in pkg.items" 
          :key="item.key"
          v-model="optionValues[item.key]" 
          :label="item.label" 
          class="package-options__checkbox"
        />
      </div>
      
    
    </div>
  </el-card>
</template>

<style scoped>
/* 所有样式已移至 src/style.css 文件中 */
</style>