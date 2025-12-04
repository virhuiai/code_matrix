<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton } from 'element-plus'
import { setupCodeEmission } from '../utils/code-emitter'

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
  // 默认启用注释设置
  defaultEnabled: true,
  
  // LaTeX 代码模板
  latexTemplate: `% 注释宏包设置
\\usepackage{comment}
%%%%%% \\begin{comment}
%%%%%% 注释内容
%%%%%% \\end{comment}`
}

const optionDocs = {
  enabled: {
    desc: '启用 comment 宏包以包裹需要忽略的内容',
    example: '\\begin{comment}\n... 注释内容 ...\n\\end{comment}'
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

setupCodeEmission(computedLatexCode, emit, props.componentId, 'CommentPackage')

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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">注释</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="注释宏包设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>注释宏包设置</strong>
          <p>设置文档中的注释环境，可以方便地添加和移除注释内容</p>
          
          <el-checkbox v-model="isEnabled" label="启用注释宏包" />
          <div v-if="isEnabled" style="margin-top: 8px;">
            <div>{{ optionDocs.enabled.desc }}</div>
            <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.enabled.example }}</pre>
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
