<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElFormItem, ElInputNumber, ElDivider } from 'element-plus'

const props = defineProps<{
  modelValue: {
    enabled: boolean
    lines?: number
    lhang?: number
    loversize?: number
  }
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { enabled: boolean; lines?: number; lhang?: number; loversize?: number }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// lettrine 配置数据
const lettrineConfig = ref({
  enabled: props.modelValue.enabled !== undefined ? props.modelValue.enabled : true,
  lines: props.modelValue.lines !== undefined ? props.modelValue.lines : 1,
  lhang: props.modelValue.lhang !== undefined ? props.modelValue.lhang : 0.1,
  loversize: props.modelValue.loversize !== undefined ? props.modelValue.loversize : 0.1
})

// LaTeX 代码模板
const latexTemplates = {
  lettrinePackage: '\\usepackage{lettrine}',
  lettrineCommand: (options: { lines: number; lhang: number; loversize: number }) => {
    return `%\\lettrine[lines=${options.lines},lhang=${options.lhang},loversize=${options.loversize}] {我}{}`
  }
}

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  const codes = []
  
  if (lettrineConfig.value.enabled) {
    codes.push(latexTemplates.lettrinePackage)
    codes.push(latexTemplates.lettrineCommand({
      lines: lettrineConfig.value.lines,
      lhang: lettrineConfig.value.lhang,
      loversize: lettrineConfig.value.loversize
    }))
  }
  
  return codes.join('\n')
})

// 更新配置
const updateConfig = (field: string, value: any) => {
  (lettrineConfig.value as any)[field] = value
  emit('update:modelValue', { ...lettrineConfig.value })
}

// 监听代码变化
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时触发代码变更事件
onMounted(() => {
  emit('update:modelValue', { ...lettrineConfig.value })
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">Lettrine 首字下沉设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="Lettrine 首字下沉设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>Lettrine 首字下沉设置</strong>
          <p>设置首字下沉效果</p>
          
          <el-checkbox 
            v-model="lettrineConfig.enabled" 
            @change="(val) => updateConfig('enabled', val)"
            label="启用 Lettrine 首字下沉宏包"
          />
          
          <div v-if="lettrineConfig.enabled" style="margin-top: 20px;">
            <el-divider />
            <div style="margin-bottom: 15px;">
              <el-form-item label="下沉行数 (lines)">
                <el-input-number 
                  v-model="lettrineConfig.lines" 
                  @change="(val) => updateConfig('lines', val)"
                  :min="1"
                  :max="10"
                  size="small"
                />
              </el-form-item>
            </div>
            
            <div style="margin-bottom: 15px;">
              <el-form-item label="左悬挂 (lhang)">
                <el-input-number 
                  v-model="lettrineConfig.lhang" 
                  @change="(val) => updateConfig('lhang', val)"
                  :min="0"
                  :max="1"
                  :step="0.1"
                  size="small"
                />
              </el-form-item>
            </div>
            
            <div style="margin-bottom: 15px;">
              <el-form-item label="垂直尺寸 (loversize)">
                <el-input-number 
                  v-model="lettrineConfig.loversize" 
                  @change="(val) => updateConfig('loversize', val)"
                  :min="0"
                  :max="1"
                  :step="0.1"
                  size="small"
                />
              </el-form-item>
            </div>
            
            <div style="margin-top: 20px;">
              <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace;">{{ computedLatexCode }}</pre>
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