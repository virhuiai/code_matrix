<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton } from 'element-plus'
import { setupCodeEmission } from '../utils/code-emitter'
import { generateCodeFromBoxPackageInfos, BoxPackageInfo } from '../utils/box-packages-utils'

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
  // 默认启用 fancyhdr 包
  defaultEnabled: true,
  
  // LaTeX 代码模板
  latexTemplate: `% 版式设置
\\pagestyle{fancy}
\\fancyhf{}
\\fancyhead[EL,OR]{\\thepage}
\\fancyhead[OC]{\\nouppercase{\\sf\\rightmark}}
\\fancyhead[EC]{\\nouppercase{\\sf\\leftmark}}
%%%%%% 版式修改命令用于修改系统的plain版式，使所有章标题页的页眉和页脚都为空白。
\\fancypagestyle{plain}{\\renewcommand{\\headrulewidth}{0pt}\\fancyhf{}}
%%%%%% 注意，当调用ctexcap中文标题宏包后，不能再调用fancyhdr宏包！
%%%%%% 如果需要修改版式，应启用ctex宏包的fancyhdr选项，它将自动加载fancyhdr宏包，%
%%%%%% 并对其中某些命令重新定义，以正确显示中文页眉。`
}

const optionDocs = {
  enabled: {
    desc: '启用 fancyhdr 并设置页眉页脚格式',
    example: '\\pagestyle{fancy}\n\\fancyhf{}\n\\fancyhead[EL,OR]{\\thepage}'
  }
}

// 计算属性：控制启用状态
const isEnabled = computed({
  get: () => props.modelValue.enabled,
  set: (value) => emit('update:modelValue', { enabled: value })
})

// 计算属性：生成 LaTeX 代码（使用工具函数）
const computedLatexCode = computed(() => {
  if (!isEnabled.value) return ''
  const infos: BoxPackageInfo[] = [{ package: 'fancyhdr' }]
  const usePkg = generateCodeFromBoxPackageInfos(infos)
  return `${usePkg}\n\n${packageConfig.latexTemplate}`
})

setupCodeEmission(computedLatexCode, emit, props.componentId, 'FancyhdrPackage')

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
  <div class="package-options-dialog">
    <!-- 触发弹窗的按钮 -->
    <el-button type="primary" size="small" round @click="openDialog">Fancyhdr 版式包</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="Fancyhdr 版式包设置"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <div class="package-section">
                <strong>Fancyhdr 版式包设置</strong>
                <div class="package-options-list">
                  <el-checkbox 
                    v-model="isEnabled" 
                    label="启用 Fancyhdr 版式包"
                    class="package-option-item"
                  />
                  <div v-if="isEnabled" style="margin-left: 20px; margin-top: 8px;">
                    <div>{{ optionDocs.enabled.desc }}</div>
                    <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.enabled.example }}</pre>
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
