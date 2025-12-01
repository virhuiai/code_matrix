<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider } from 'element-plus'

const props = defineProps<{
  modelValue: Record<string, boolean>
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: Record<string, boolean>): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 定义宏包配置项的接口
interface PackageConfig {
  packageName: string
  title: string
  items: Array<{ key: string; label: string }>
  optionsMap: Record<string, string>
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
const optionValues = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 通用的宏包代码生成函数
const generatePackageCode = (pkg: PackageConfig, values: Record<string, boolean>): string | null => {
  const options = pkg.items
    .filter(item => values[item.key])
    .map(item => pkg.optionsMap[item.key])
  
  return options.length > 0 
    ? `\\PassOptionsToPackage{${options.join(',')}}{${pkg.packageName}}`
    : null
}

// 计算属性：生成最终的 LaTeX 代码
const computedLatexCode = computed(() => {
  return packageConfigs.value
    .map(pkg => generatePackageCode(pkg, optionValues.value))
    .filter((code): code is string => code !== null)
    .join('\n')
})

// 更新选项值
const updateOptionValue = (key: string, value: boolean) => {
  emit('update:modelValue', { ...optionValues.value, [key]: value })
}

// 监听代码变化
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时触发代码变更事件
onMounted(() => {
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`PackageOptions component loaded successfully with ID: ${props.componentId}`)
  }
})

// 弹窗控制方法
const openDialog = () => {
  dialogVisible.value = true
}

const closeDialog = () => {
  dialogVisible.value = false
}

defineExpose({
  openDialog,
  closeDialog
})
</script>

<template>
  <div>
    <!-- 触发弹窗的按钮 -->
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">Package Options 宏包选项</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="Package Options 宏包选项"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>PassOptionsToPackage</strong>
          <p>为 LaTeX 宏包传递选项参数</p>
          
          <div style="margin-top: 20px;">
            <el-divider />
            
            <!-- 使用 v-for 指令遍历所有宏包配置 -->
            <div v-for="pkg in packageConfigs" :key="pkg.packageName" style="margin-bottom: 20px;">
              <strong>{{ pkg.title }}</strong>
              <!-- 为每个宏包的选项创建复选框 -->
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  v-for="item in pkg.items" 
                  :key="item.key"
                  :model-value="optionValues[item.key]"
                  @update:model-value="(val) => updateOptionValue(item.key, val)"
                  :label="item.label"
                  style="display: block; margin-bottom: 8px;"
                />
              </div>
            </div>
            
            <div style="margin-top: 20px;">
              <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace;">{{ computedLatexCode }}</pre>
            </div>
          </div>
        </div>
      </el-card>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="closeDialog">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 所有样式已移至 src/style.css 文件中 */
</style>