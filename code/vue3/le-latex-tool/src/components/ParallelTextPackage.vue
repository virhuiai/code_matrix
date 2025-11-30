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
  // 默认启用对译环境设置
  defaultEnabled: true,
  
  // LaTeX 代码模板
  latexTemplate: `% 对译环境设置
\\usepackage{pdfcolparcolumns}

\\newlength{\\栏间距}
\\setlength{\\栏间距}{1em}

\\newlength{\\左栏宽}
\\newlength{\\右栏宽}

\\newcommand\\栏宽调整[1]{%
\\setlength{\\左栏宽}{#1\\textwidth}%
\\setlength{\\右栏宽}{\\textwidth-\\栏间距-\\左栏宽}%
}

% \\栏宽调整{0.37}
\\setlength{\\左栏宽}{0.4\\textwidth}
\\setlength{\\右栏宽}{0.51\\textwidth}

\\newcommand\\栏体[1]{\\colchunk{#1}}
\\newcommand\\换栏{\\colplacechunks}

\\newenvironment{双栏}%
{\\begin{parcolumns}[rulebetween=false,distance=\\栏间距,colwidths={1=\\左栏宽,2=\\右栏宽}]{2}}%
{\\end{parcolumns}}
 
\\usepackage{paracol}
\\newcommand\\栏宽调整二[1]{%
\\columnratio{#1}}%

\\newenvironment{双栏二}%
{\\begin{paracol}{2}}%
{\\end{paracol}}

\\newcommand\\换栏二{\\switchcolumn}`
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
    console.log(`ParallelTextPackage component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">对译环境</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="对译环境设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>对译环境设置</strong>
          <p>设置文档中的对译环境，包括双栏排版和栏宽调整命令</p>
          
          <el-checkbox v-model="isEnabled" label="启用对译环境设置" />
          
          <div v-if="isEnabled" style="margin-top: 20px;">
            <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace;">{{ packageConfig.latexTemplate }}</pre>
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