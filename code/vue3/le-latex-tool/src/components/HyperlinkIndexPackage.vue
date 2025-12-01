<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider } from 'element-plus'

const props = defineProps<{
  modelValue: {
    variorefEnabled: boolean
    multindEnabled: boolean
    hyperrefEnabled: boolean
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { variorefEnabled: boolean, multindEnabled: boolean, hyperrefEnabled: boolean }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// LaTeX 代码模板
const variorefTemplate = `\\usepackage{varioref}`

const multindTemplate = `% 索引设置
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
 \\index{book}{#2}}`

const hyperrefTemplate = `% 链接设置
\\usepackage[CJKbookmarks,bookmarksnumbered,bookmarksopen,
            pdftitle={\\pdffilename},pdfauthor=virhuiai,
            colorlinks=true, pdfstartview=FitH,citecolor=blue,linktocpage,
            linkcolor=blue,urlcolor=blue,hyperindex=true]{hyperref}
% url样式设置
\\urlstyle{same}`

// 计算属性：控制启用状态
const variorefEnabled = computed({
  get: () => props.modelValue.variorefEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: value, 
    multindEnabled: props.modelValue.multindEnabled ?? false,
    hyperrefEnabled: props.modelValue.hyperrefEnabled ?? true
  })
})

const multindEnabled = computed({
  get: () => props.modelValue.multindEnabled ?? false,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: props.modelValue.variorefEnabled ?? true,
    multindEnabled: value,
    hyperrefEnabled: props.modelValue.hyperrefEnabled ?? true
  })
})

const hyperrefEnabled = computed({
  get: () => props.modelValue.hyperrefEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: props.modelValue.variorefEnabled ?? true,
    multindEnabled: props.modelValue.multindEnabled ?? false,
    hyperrefEnabled: value
  })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  const codes = []
  if (variorefEnabled.value) {
    codes.push(variorefTemplate)
  }
  if (multindEnabled.value) {
    codes.push(multindTemplate)
  }
  if (hyperrefEnabled.value) {
    codes.push(hyperrefTemplate)
  }
  return codes.join('\n\n')
})

// 监听代码变化
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时触发代码变更事件
onMounted(() => {
  // 如果未设置enabled属性，则设置默认值
  if (props.modelValue.variorefEnabled === undefined || 
      props.modelValue.multindEnabled === undefined || 
      props.modelValue.hyperrefEnabled === undefined) {
    emit('update:modelValue', { 
      variorefEnabled: true,
      multindEnabled: false,
      hyperrefEnabled: true
    })
  }
  
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`HyperlinkIndexPackage component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">链接和索引设置</el-button>
    
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
          
          <div style="margin-top: 20px;">
            <el-checkbox 
              :model-value="variorefEnabled" 
              @update:model-value="(val) => variorefEnabled = Boolean(val)"
              label="启用 varioref 宏包（用于智能交叉引用）" 
            />
            
            <div v-if="variorefEnabled" style="margin-top: 10px; margin-left: 20px;">
              <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ variorefTemplate }}</pre>
            </div>
          </div>
          
          <el-divider />
          
          <div style="margin-top: 20px;">
            <el-checkbox 
              :model-value="multindEnabled" 
              @update:model-value="(val) => multindEnabled = Boolean(val)"
              label="启用 multind 宏包（用于多重索引）" 
            />
            
            <div v-if="multindEnabled" style="margin-top: 10px; margin-left: 20px;">
              <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ multindTemplate }}</pre>
            </div>
          </div>
          
          <el-divider />
          
          <div style="margin-top: 20px;">
            <el-checkbox 
              :model-value="hyperrefEnabled" 
              @update:model-value="(val) => hyperrefEnabled = Boolean(val)"
              label="启用 hyperref 宏包（用于超链接）" 
            />
            
            <div v-if="hyperrefEnabled" style="margin-top: 10px; margin-left: 20px;">
              <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ hyperrefTemplate }}</pre>
            </div>
          </div>
          
          <div style="margin-top: 20px;">
            <strong>完整代码预览</strong>
            <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace; margin-top: 10px;">{{ computedLatexCode }}</pre>
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