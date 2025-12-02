<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider } from 'element-plus'

// 定义选项信息的接口
interface OptionInfo {
  command: string
  options: string
  package: string
}

const props = defineProps<{
  modelValue: Record<string, boolean>
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: Record<string, boolean>): void
  (e: 'codeChange', value: string, optionInfos?: OptionInfo[]): void
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

// 根据OptionInfo数组生成LaTeX代码
const generateCodeFromOptionInfos = (optionInfos: OptionInfo[]): string => {
  // 确保optionInfos是一个数组
  if (!Array.isArray(optionInfos)) {
    return ''
  }
  
  return optionInfos
    .map(info => `\\PassOptionsToPackage{${info.options}}{${info.package}}`)
    .join('\n')
}

// 生成选项信息列表
const generateOptionInfos = (): OptionInfo[] => {
  return packageConfigs.value
    .map(pkg => {
      const options = pkg.items
        .filter(item => optionValues.value[item.key])
        .map(item => pkg.optionsMap[item.key])
      
      if (options.length > 0) {
        return {
          command: 'PassOptionsToPackage',
          options: options.join(','),
          package: pkg.packageName
        }
      }
      return null
    })
    .filter((info): info is OptionInfo => info !== null)
}

// 计算属性：生成最终的 LaTeX 代码
const computedLatexCode = computed(() => {
  const optionInfos = generateOptionInfos()
  return generateCodeFromOptionInfos(optionInfos);
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
  const optionInfos = generateOptionInfos()
  emit('codeChange', computedLatexCode.value, optionInfos)
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
      width="100%"
      :before-close="closeDialog"
      class="package-options-dialog"
    >
      <el-card shadow="hover" class="package-options-content">
        <div>
          <strong>PassOptionsToPackage</strong>
          <p>为 LaTeX 宏包传递选项参数</p>
          
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <el-divider />
              
              <!-- 使用 v-for 指令遍历所有宏包配置 -->
              <div v-for="pkg in packageConfigs" :key="pkg.packageName" class="package-section">
                <strong>{{ pkg.title }}</strong>
                <!-- 为每个宏包的选项创建复选框 -->
                <div class="package-options-list">
                  <el-checkbox 
                    v-for="item in pkg.items" 
                    :key="item.key"
                    :model-value="optionValues[item.key]"
                    @update:model-value="(val) => updateOptionValue(item.key, Boolean(val))"
                    :label="item.label"
                    class="package-option-item"
                  />
                </div>
              </div>
            </div>
            
            <!-- 右栏：代码预览 -->
            <div class="package-options-right">
              <div class="code-preview">
                <pre class="code-preview-content">{{ computedLatexCode }}</pre>
              </div>
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

.package-options-dialog {
  display: flex;
  justify-content: center;
  align-items: center;
}

.package-options-dialog :deep(.el-dialog) {
  height: 80vh;
  display: flex;
  flex-direction: column;
  margin: 0 auto !important;
  max-width: 1200px;
}

.package-options-dialog :deep(.el-dialog__body) {
  flex: 1;
  overflow: hidden;
}

.package-options-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.package-options-container {
  display: flex;
  height: calc(100% - 60px);
  margin-top: 20px;
  gap: 20px;
}

.package-options-left {
  flex: 1;
  overflow-y: auto;
  padding-right: 10px;
}

.package-options-right {
  flex: 1;
  overflow-y: auto;
  padding-left: 10px;
}

.package-section {
  margin-bottom: 20px;
}

.package-options-list {
  margin-top: 10px;
  margin-left: 20px;
}

.package-option-item {
  display: block;
  margin-bottom: 8px;
}

.code-preview {
  background-color: #f5f5f5;
  border-radius: 4px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.code-preview-content {
  flex: 1;
  padding: 15px;
  overflow-x: auto;
  font-family: monospace;
  white-space: pre-wrap;
  margin: 0;
  background-color: transparent;
}
</style>