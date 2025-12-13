<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton } from 'element-plus'
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

// 包配置数据
const packageConfig = {
  // 默认启用 morewrites 包
  defaultEnabled: true,
  
  // LaTeX 代码模板
  latexTemplate: `% ***************************************************************
% This LATEX package is a solution for the error
%                     “no room for a new \\write”
\\usepackage{morewrites}`
}

const optionDocs = {
  enabled: {
    desc: '启用 morewrites 以解决 “no room for a new \\write” 错误',
    example: '\\usepackage{morewrites}'
  }
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

setupCodeEmission(computedLatexCode, emit, props.componentId, 'MoreWritesPackage')

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
    <el-button v-if="!props.externalTrigger" type="primary" size="small" round @click="openDialog">MoreWrites 包</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="MoreWrites 包设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <strong>MoreWrites 包设置</strong>
              <p>解决 "no room for a new \write" 错误</p>
              <el-checkbox v-model="isEnabled" label="启用 MoreWrites 包" />
              <div v-if="isEnabled" style="margin-top: 8px;">
                <div>{{ optionDocs.enabled.desc }}</div>
                <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.enabled.example }}</pre>
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
