<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton } from 'element-plus'

const props = defineProps<{
  modelValue: {
    enabled: boolean
  }
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { enabled: boolean }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 包配置数据
const packageConfig = {
  // 默认启用链接和索引设置
  defaultEnabled: true,
  
  // LaTeX 代码模板
  latexTemplate: `% 链接和索引设置
\\usepackage{varioref}
\\usepackage{multind}
\\makeindex{command}
\\makeindex{package}
\\makeindex{environment}
\\makeindex{book}

% 命令定义
\\newcommand{\\cmd}[1]{%
\\texttt{\\symbol{92}#1}%
}
\\newcommand{\\Com}[2]{%
 \\texttt{#1}%
 \\index{command}{#2}}
\\newcommand{\\Pac}[2]{%
 \\textsf{#1}%
 \\index{package}{#2}}
\\newcommand{\\Env}[2]{%
 \\texttt{#1}%
 \\index{environment}{#1}}
 \\newcommand{\\Boo}[2]{%
 \\texttt{#1}%
 \\index{book}{#2}}

% 链接设置
\\usepackage[CJKbookmarks,bookmarksnumbered,bookmarksopen,
            pdftitle={\\pdffilename},pdfauthor=virhuiai,
            colorlinks=true, pdfstartview=FitH,citecolor=blue,linktocpage,
            linkcolor=blue,urlcolor=blue,hyperindex=true]{hyperref}
% url样式设置
\\urlstyle{same}`
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">链接_索引设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="链接和索引设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>链接和索引设置</strong>
          <p>设置文档中的链接和索引功能，包括超链接、参考文献链接以及各种索引</p>
          
          <el-checkbox v-model="isEnabled" label="启用链接和索引设置" />
          
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