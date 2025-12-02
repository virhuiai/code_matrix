<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElFormItem, ElInputNumber, ElDivider } from 'element-plus'
import { generateCodeFromBoxPackageInfos, BoxPackageInfo } from '../utils/box-packages-utils'

const props = defineProps<{
  modelValue: {
    enabled: boolean
    lines?: number
    lhang?: number
    loversize?: number
  }
  componentId?: number
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

// 计算属性：生成 LaTeX 代码（使用工具函数）
const computedLatexCode = computed(() => {
  if (!lettrineConfig.value.enabled) return ''

  const infos: BoxPackageInfo[] = [{ package: 'lettrine' }]
  const usePkg = generateCodeFromBoxPackageInfos(infos)
  const cmd = `%\\lettrine[lines=${lettrineConfig.value.lines},lhang=${lettrineConfig.value.lhang},loversize=${lettrineConfig.value.loversize}] {我}{}`
  return `${usePkg}\n${cmd}`
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
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`LettrinePackage component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">Lettrine 首字下沉设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="Lettrine 首字下沉设置"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <div class="package-section">
                <strong>Lettrine 首字下沉设置</strong>
                <div class="package-options-list">
                  <el-checkbox 
                    :model-value="lettrineConfig.enabled" 
                    @update:model-value="(val) => updateConfig('enabled', Boolean(val))"
                    label="启用 Lettrine 首字下沉"
                    class="package-option-item"
                  />
                  <div v-if="lettrineConfig.enabled">
                    <el-form-item label="下沉行数 (lines)">
                      <el-input-number 
                        :model-value="lettrineConfig.lines" 
                        @update:model-value="(val) => updateConfig('lines', Number(val))"
                        :min="1"
                        :max="10"
                        size="small"
                      />
                    </el-form-item>
                    
                    <el-form-item label="左悬挂 (lhang)">
                      <el-input-number 
                        :model-value="lettrineConfig.lhang" 
                        @update:model-value="(val) => updateConfig('lhang', Number(val))"
                        :step="0.05"
                        :min="0"
                        :max="1"
                        size="small"
                      />
                    </el-form-item>
                    
                    <el-form-item label="垂直尺寸 (loversize)">
                      <el-input-number 
                        :model-value="lettrineConfig.loversize" 
                        @update:model-value="(val) => updateConfig('loversize', Number(val))"
                        :step="0.05"
                        :min="0"
                        :max="2"
                        size="small"
                      />
                    </el-form-item>
                  </div>
                </div>
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
        
      </template>
    </el-dialog>
  </div>
</template>