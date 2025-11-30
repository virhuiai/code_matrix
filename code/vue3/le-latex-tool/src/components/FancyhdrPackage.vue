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
  // 默认启用 fancyhdr 包
  defaultEnabled: true,
  
  // LaTeX 代码模板
  latexTemplate: `% 版式设置
\\pagestyle{fancy}
\\fancyhf{}
\\fancyhead[EL,OR]{\\thepage}
\\fancyhead[OC]{\\nouppercase{\\fangsong\\rightmark}}
\\fancyhead[EC]{\\nouppercase{\\fangsong\\leftmark}}
%%%%%% 版式修改命令用于修改系统的plain版式，使所有章标题页的页眉和页脚都为空白。
\\fancypagestyle{plain}{\\renewcommand{\\headrulewidth}{0pt}\\fancyhf{}}
%%%%%% 注意，当调用ctexcap中文标题宏包后，不能再调用fancyhdr宏包！
%%%%%% 如果需要修改版式，应启用ctex宏包的fancyhdr选项，它将自动加载fancyhdr宏包，%
%%%%%% 并对其中某些命令重新定义，以正确显示中文页眉。`
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
    console.log(`FancyhdrPackage component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">Fancyhdr 版式包</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="Fancyhdr 版式包设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>Fancyhdr 版式包设置</strong>
          <p>设置文档的页眉页脚样式</p>
          
          <el-checkbox v-model="isEnabled" label="启用 Fancyhdr 版式包" />
          
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