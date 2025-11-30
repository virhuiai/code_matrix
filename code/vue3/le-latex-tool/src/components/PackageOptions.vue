<script setup lang="ts">
// 从 vue 导入需要用到的函数
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
// 从 element-plus 导入需要用到的组件
import { ElCard, ElCheckbox } from 'element-plus'

// 定义组件接收的属性（props）
// modelValue 是一个对象，键为字符串，值为布尔值
const props = defineProps<{
  modelValue: Record<string, boolean>
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

// 定义宏包配置项的接口（类似其他语言中的结构体或类定义）
// 描述了每个宏包应该包含哪些属性
interface PackageConfig {
  packageName: string     // 宏包名称
  title: string           // 显示标题
  items: Array<{ key: string; label: string }>  // 该宏包下的选项列表
  optionsMap: Record<string, string>            // 选项键到实际代码的映射
}

// 创建宏包配置的工厂函数（用于简化创建 PackageConfig 对象的过程）
// 参数：宏包名、标题、选项列表、选项映射关系
// 返回：一个符合 PackageConfig 接口的对象
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

// 通用的宏包代码生成函数
// 参数：宏包配置对象、当前选中的选项值
// 返回：生成的 LaTeX 代码字符串，如果没有选中任何选项则返回 null
const generatePackageCode = (
  pkg: PackageConfig,
  values: Record<string, boolean>
): string | null => {
  // 过滤出被选中的选项，并获取它们对应的代码片段
  const options = pkg.items
    .filter(item => values[item.key])               // 筛选出值为 true 的选项
    .map(item => pkg.optionsMap[item.key])          // 将选项键映射为实际代码
  
  // 如果有选中的选项，则生成 \PassOptionsToPackage 命令，否则返回 null
  return options.length > 0 
    ? `\\PassOptionsToPackage{${options.join(',')}}{${pkg.packageName}}`
    : null
}

// 宏包配置数据列表（使用 ref 包装以获得响应性）
// ref 是 Vue 3 Composition API 中用来创建响应式数据的方法
const packageConfigs = ref([
  // xeCJK 宏包配置
  createPackageConfig(
    'xeCJK',                    // 宏包名称
    'xeCJK 选项',               // 显示标题
    [                           // 选项列表
      { key: 'autoFakeBold', label: 'AutoFakeBold' },
      { key: 'autoFakeSlant', label: 'AutoFakeSlant' }
    ],
    {                           // 选项键到实际代码的映射
      autoFakeBold: 'AutoFakeBold=true',
      autoFakeSlant: 'AutoFakeSlant=true'
    }
  ),
  // fontspec 宏包配置
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
  // xcolor 宏包配置
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

// 计算属性：用于获取和设置选项值
// computed 是 Vue 3 中创建计算属性的方法
// get: 从 props 中获取当前值
// set: 当值发生变化时，通过 emit 发送 update:modelValue 事件通知父组件
const optionValues = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 计算属性：生成最终的 LaTeX 代码
// map: 遍历所有宏包配置，为每个宏包生成代码
// filter: 过滤掉返回 null 的结果（即没有选中任何选项的宏包）
// filter((code): code is string => code !== null) 是 TypeScript 中的类型守卫语法，
// 用于告诉编译器过滤后的数组元素都是字符串类型
// join: 将所有生成的代码用换行符连接起来
const computedLatexCode = computed(() => {
  const codes = packageConfigs.value
    .map(pkg => generatePackageCode(pkg, optionValues.value))
    .filter((code): code is string => code !== null)
  
  return codes.join('\n')
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
})
</script>

<template>
  <el-card shadow="hover">
    <div style="display: flex; flex-direction: column; gap: 15px;">
      <strong>PassOptionsToPackage</strong>
      
      <!-- 使用 v-for 指令遍历所有宏包配置 -->
      <!-- :key 是 Vue 中用于标识列表项的唯一性的特殊属性 -->
      <div v-for="pkg in packageConfigs" :key="pkg.packageName">
        <strong>{{ pkg.title }}</strong>
        <!-- 为每个宏包的选项创建复选框 -->
        <!-- v-model 是 Vue 中实现双向数据绑定的指令 -->
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