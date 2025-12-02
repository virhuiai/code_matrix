<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton } from 'element-plus'

const props = defineProps<{
  modelValue: {
    enabled: boolean
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { enabled: boolean }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 包配置数据
const packageConfig = {
  // 默认启用标题格式设置
  defaultEnabled: true,
  
  // LaTeX 代码模板
  latexTemplate: `% 层次标题格式设置
\\CTEXsetup[name={第~,~章},number={\\arabic{chapter}}]{chapter}
\\CTEXsetup[nameformat+={\\LARGE},titleformat+={\\LARGE}]{chapter}
\\CTEXsetup[beforeskip={-23pt},afterskip={20pt plus 2pt minus 2pt}]{chapter}

% 图表标题格式设置
\\usepackage[labelfont=bf,labelsep=quad]{caption}
\\DeclareCaptionFont{kai}{\\kaishu}
\\DeclareCaptionFont{five}{\\zihao{5}}
\\captionsetup{textfont=kai,font=five}`
}

// 计算属性：控制启用状态
const isEnabled = computed({
  get: () => props.modelValue.enabled,
  set: (value) => emit('update:modelValue', { enabled: value })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  return isEnabled.value ? packageConfig.latexTemplate : ''
})

// 监听代码变化
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时触发代码变更事件
onMounted(() => {
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`TitleFormatPackage component loaded successfully with ID: ${props.componentId}`)
  }
})

// 打开弹窗
const openDialog = () => {
  dialogVisible.value = true
}

// 关闭弹窗
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">标题格式设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="标题格式设置"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <div class="package-options-container">
            <div class="package-options-left">
              <strong>标题格式设置</strong>
              <p>设置章节标题和图表标题的格式</p>
              <el-checkbox v-model="isEnabled" label="启用标题格式设置" />
            </div>
            <div class="package-options-right">
              <div class="code-preview">
                <pre class="code-preview-content">{{ isEnabled ? packageConfig.latexTemplate : '' }}</pre>
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
