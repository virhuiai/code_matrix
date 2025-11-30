<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElDialog, ElButton } from 'element-plus'

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
\\end{document}`
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">文档内容设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="文档内容设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>文档内容设置</strong>
          <p>设置文档的主要内容</p>
          
          <el-checkbox v-model="isEnabled" label="启用文档内容" />
          
          <div v-if="isEnabled" style="margin-top: 20px;">
            <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace;">{{ documentConfig.latexTemplate }}</pre>
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