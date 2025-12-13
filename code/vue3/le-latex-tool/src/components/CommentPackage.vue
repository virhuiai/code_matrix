<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider, ElAlert } from 'element-plus'
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
const isDialogOpen = ref(false)

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
    example: '\\usepackage{comment}\n\\begin{comment}\n... 注释内容 ...\n\\end{comment}'
  }
}

// 计算属性：控制启用状态
const isEnabled = computed({
  get: () => props.modelValue.enabled,
  set: (value) => emit('update:modelValue', { enabled: value })
})

// 计算属性：生成 LaTeX 代码
const latexCode = computed(() => {
  return isEnabled.value ? packageConfig.latexTemplate : ''
})

setupCodeEmission(latexCode, emit, props.componentId, 'CommentPackage')

// 打开弹窗
const showDialog = () => {
  isDialogOpen.value = true
}

// 关闭弹窗
const hideDialog = () => {
  isDialogOpen.value = false
}

defineExpose({
  showDialog,
  hideDialog
})
</script>

<template>
  <div>
    <el-dialog v-model="isDialogOpen" title="注释宏包设置" :before-close="hideDialog">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <strong>注释宏包设置</strong>
            <el-alert title="说明" description="为文档提供可屏蔽的注释环境" type="info" show-icon />
            <el-divider />
            <el-checkbox v-model="isEnabled" label="启用注释宏包" />
            <template v-if="isEnabled">
              <el-alert :title="optionDocs.enabled.desc" type="success" show-icon />
              <el-card><pre>{{ optionDocs.enabled.example }}</pre></el-card>
            </template>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <strong>代码预览</strong>
            <el-divider />
            <el-scrollbar max-height="60vh">
              <pre>{{ latexCode }}</pre>
            </el-scrollbar>
          </el-card>
        </el-col>
      </el-row>
      <template #footer></template>
    </el-dialog>
  </div>
</template>
