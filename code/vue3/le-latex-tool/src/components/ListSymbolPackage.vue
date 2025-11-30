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
  // 默认启用列表和符号设置
  defaultEnabled: true,
  
  // LaTeX 代码模板
  latexTemplate: `% 列表和符号设置
\\usepackage{pifont}
\\usepackage{mflogo,metalogo,mflogo}
\\usepackage{texnames}
\\usepackage{bbding}
\\usepackage{amssymb,latexsym,textcomp,mathrsfs,euscript,yhmath}

\\usepackage{enumerate}
\\usepackage{enumitem}
\\usepackage[olditem,oldenum]{paralist}
\\usepackage{mdwlist}
\\usepackage{manfnt}
\\usepackage{eurosym}

% 自定义描述环境
\\newenvironment{descNL}[1][2em]{%
\\begin{basedescript}{\\desclabelwidth{#1}\\desclabelstyle{\\nextlinelabel}}}%
{\\end{basedescript}}

\\newenvironment{descML}[1][2em]{%
\\begin{basedescript}{\\desclabelwidth{#1}\\desclabelstyle{\\multilinelabel}}}%
{\\end{basedescript}}

\\newenvironment{descPL}[1][2em]{%
\\begin{basedescript}{\\desclabelwidth{#1}\\desclabelstyle{\\pushlabel}}}{\\end{basedescript}}

\\newlength{\\basedescriptDesclabelwidth}
\\newenvironment{dl}[1][标签]{%
\\settowidth{\\basedescriptDesclabelwidth}{\\tt #1}
\\begin{basedescript}{%
\\renewcommand{\\makelabel}[1]{\\tt ##1}%
\\desclabelwidth{\\basedescriptDesclabelwidth}%
\\desclabelstyle{\\pushlabel}}}{\\end{basedescript}}

\\newcounter{带圈文字}
\\newcommand\\带圈文字[1]{
\\protect\\setcounter{带圈文字}{171+#1}
\\protect\\ding{\\value{带圈文字}}}`
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
    console.log(`ListSymbolPackage component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">列表_符号设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="列表和符号设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>列表和符号设置</strong>
          <p>设置文档中的列表环境和符号包，包括各种列表格式和特殊符号</p>
          
          <el-checkbox v-model="isEnabled" label="启用列表和符号设置" />
          
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