<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { generateCodeFromBoxPackageInfos, BoxPackageInfo } from '../utils/box-packages-utils'
import { setupCodeEmission } from '../utils/code-emitter'

const props = defineProps<{
  modelValue: {
    enabled: boolean
    lines?: number
    lhang?: number
    loversize?: number
  }
  componentId?: number
  externalTrigger?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { enabled: boolean; lines?: number; lhang?: number; loversize?: number }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const isDialogOpen = ref(false)

// lettrine 配置数据
const lettrineOptions = ref({
  enabled: props.modelValue.enabled !== undefined ? props.modelValue.enabled : true,
  lines: props.modelValue.lines !== undefined ? props.modelValue.lines : 1,
  lhang: props.modelValue.lhang !== undefined ? props.modelValue.lhang : 0.1,
  loversize: props.modelValue.loversize !== undefined ? props.modelValue.loversize : 0.1
})

// 计算属性：生成 LaTeX 代码（使用工具函数）
const latexCode = computed(() => {
  if (!lettrineOptions.value.enabled) return ''

  const infos: BoxPackageInfo[] = [{ package: 'lettrine' }]
  const usePkg = generateCodeFromBoxPackageInfos(infos)
  const cmd = `%\\lettrine[lines=${lettrineOptions.value.lines},lhang=${lettrineOptions.value.lhang},loversize=${lettrineOptions.value.loversize}] {我}{}`
  return `${usePkg}\n${cmd}`
})

// 更新配置
const updateOption = (field: string, value: any) => {
  (lettrineOptions.value as any)[field] = value
  emit('update:modelValue', { ...lettrineOptions.value })
}

setupCodeEmission(latexCode, emit, props.componentId, 'LettrinePackage')

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
  <div class="package-options-dialog">

    <!-- 弹窗 -->
    <el-dialog
      v-model="isDialogOpen"
      title="Lettrine 首字下沉设置"
      :before-close="hideDialog"
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
                    :model-value="lettrineOptions.enabled" 
                    @update:model-value="(val: boolean | string | number) => updateOption('enabled', Boolean(val))"
                    label="启用 Lettrine 首字下沉"
                    class="package-option-item"
                  />
                  <div v-if="lettrineOptions.enabled">
                    <el-form-item label="下沉行数 (lines)">
                      <el-input-number 
                        :model-value="lettrineOptions.lines" 
                        @update:model-value="(val: number | string) => updateOption('lines', Number(val))"
                        :min="1"
                        :max="10"
                        size="small"
                      />
                    </el-form-item>
                    
                    <el-form-item label="左悬挂 (lhang)">
                      <el-input-number 
                        :model-value="lettrineOptions.lhang" 
                        @update:model-value="(val: number | string) => updateOption('lhang', Number(val))"
                        :step="0.05"
                        :min="0"
                        :max="1"
                        size="small"
                      />
                    </el-form-item>
                    
                    <el-form-item label="垂直尺寸 (loversize)">
                      <el-input-number 
                        :model-value="lettrineOptions.loversize" 
                        @update:model-value="(val: number | string) => updateOption('loversize', Number(val))"
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
                <pre class="code-preview-content">{{ latexCode }}</pre>
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
