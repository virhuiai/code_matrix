<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton } from 'element-plus'
import { OptionInfo, PackageConfig } from '../types/package-options-types';
import { generateCodeFromOptionInfos } from '../utils/package-options-utils';
import { setupCodeEmission } from '../utils/code-emitter'

const props = defineProps<{
  modelValue: Record<string, boolean>
  componentId?: number
  externalTrigger?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: Record<string, boolean>): void
  (e: 'codeChange', value: string, optionInfos?: OptionInfo[]): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 宏包配置数据
const packageConfigs = ref<PackageConfig[]>([
  {
    packageName: 'xeCJK',
    title: 'xeCJK 选项',
    items: [
      { key: 'autoFakeBold', label: 'AutoFakeBold', desc: '自动伪粗体以增强中文粗体显示', example: '\\PassOptionsToPackage{AutoFakeBold=true}{xeCJK}' },
      { key: 'autoFakeSlant', label: 'AutoFakeSlant', desc: '自动伪斜体以增强中文斜体显示', example: '\\PassOptionsToPackage{AutoFakeSlant=true}{xeCJK}' }
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
      { key: 'noMath', label: 'no-math', desc: '不覆盖数学字体，避免与数学环境冲突', example: '\\PassOptionsToPackage{no-math}{fontspec}' }
    ],
    optionsMap: {
      noMath: 'no-math'
    }
  },
  {
    packageName: 'xcolor',
    title: 'xcolor 选项',
    items: [
      { key: 'prologue', label: 'prologue', desc: '提前加载颜色选项，提高兼容性', example: '\\PassOptionsToPackage{prologue}{xcolor}' },
      { key: 'dvipsnames', label: 'dvipsnames', desc: '启用 dvipsnames 预定义颜色集', example: '\\PassOptionsToPackage{dvipsnames}{xcolor}' }
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

setupCodeEmission(computedLatexCode, emit, props.componentId, 'PackageOptions')

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
  <div class="package-options-dialog">

    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="为 LaTeX 宏包传递选项参数"
      width="100%"
      :before-close="closeDialog"
    
    >
      <el-card shadow="hover" >
        <div>
          
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              
              <!-- 使用 v-for 指令遍历所有宏包配置 -->
              <div v-for="pkg in packageConfigs" :key="pkg.packageName" class="package-section">
                <strong>{{ pkg.title }}</strong>
                <div class="package-options-list">
                  <div v-for="item in pkg.items" :key="item.key" style="margin-bottom: 8px;">
                    <el-checkbox 
                      :model-value="optionValues[item.key]"
                      @update:model-value="(val: boolean | string | number) => updateOptionValue(item.key, Boolean(val))"
                      :label="item.label"
                      class="package-option-item"
                    />
                    <div v-if="optionValues[item.key]" style="margin-left: 20px; margin-top: 8px;">
                      <div>{{ item.desc }}</div>
                      <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ item.example }}</pre>
                    </div>
                  </div>
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
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 所有样式已移至 src/style.css 文件中 */
</style>
