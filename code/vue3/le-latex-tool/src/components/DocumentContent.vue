<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElDialog, ElButton } from 'element-plus'
import { setupCodeEmission } from '../utils/code-emitter'

const props = defineProps<{
  modelValue: {
    enabled: boolean
  }
  componentId?: number
  externalTrigger?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { enabled: boolean }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 文档内容配置数据
const documentConfig = {
  // 默认启用文档内容
  defaultEnabled: true,
  
  // LaTeX 文档内容模板
  latexTemplate: `\\begin{document}

\\section{mainfont}

Hello,world!

你好，世界！

\\section{sansfont}

{\\sf
Hello,world!
你好，世界！
}

\\section{monofont}

{\\tt
Hello,world!
你好，世界！
}
\\end{document}
xelatex -shell-escape a.tex`
}

// 计算属性：控制启用状态
const isEnabled = computed({
  get: () => props.modelValue.enabled,
  set: (value) => emit('update:modelValue', { enabled: value })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  return isEnabled.value ? documentConfig.latexTemplate : ''
})

setupCodeEmission(computedLatexCode, emit, props.componentId, 'DocumentContent')

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

    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="文档内容设置"

      fullscreen
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <strong>文档内容设置</strong>
              <p>设置文档的主要内容</p>
              <el-checkbox v-model="isEnabled" label="启用文档内容" />
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
