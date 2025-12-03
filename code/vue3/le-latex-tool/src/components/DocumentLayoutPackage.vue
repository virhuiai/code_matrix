<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { generateCodeFromPackageInfos, PackageInfo } from '../utils/generic-packages-utils'
import { setupCodeEmission } from '../utils/code-emitter'

const props = defineProps<{
  modelValue: {
    parskip: {
      enabled: boolean
      linespread: {
        enabled: boolean
        value: number
      }
    }
    xspace: {
      enabled: boolean
    }
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 包配置数据
const packages = ref({
  parskip: {
    enabled: props.modelValue.parskip?.enabled !== undefined ? props.modelValue.parskip.enabled : true,
    linespread: {
      enabled: props.modelValue.parskip?.linespread?.enabled !== undefined ? props.modelValue.parskip.linespread.enabled : true,
      value: props.modelValue.parskip?.linespread?.value !== undefined ? props.modelValue.parskip.linespread.value : 1.245
    }
  },
  xspace: {
    enabled: props.modelValue.xspace?.enabled !== undefined ? props.modelValue.xspace.enabled : true
  }
})

// 计算属性：生成 LaTeX 代码（使用通用工具）
const computedLatexCode = computed(() => {
  const infos: PackageInfo[] = []
  if (packages.value.parskip.enabled) {
    infos.push({ package: 'parskip' })
  }
  if (packages.value.xspace.enabled) {
    infos.push({ package: 'xspace' })
  }
  const pkgLines = generateCodeFromPackageInfos(infos)
  const extraLines: string[] = []
  if (packages.value.parskip.linespread.enabled) {
    extraLines.push(`\\linespread{${packages.value.parskip.linespread.value}}`)
  }
  return [pkgLines, ...extraLines].filter(Boolean).join('\n\n')
})

// 更新包选项
const updatePackage = (pkg: string, value: any) => {
  (packages.value as any)[pkg] = value
  emit('update:modelValue', { ...packages.value })
}

// 更新 linespread 值
const updateLinespreadValue = (value: number | undefined) => {
  if (value !== undefined) {
    packages.value.parskip.linespread.value = value
    emit('update:modelValue', { ...packages.value })
  }
}

setupCodeEmission(computedLatexCode, emit, props.componentId, 'DocumentLayoutPackage')

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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">行距和空格设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="行距和空格设置"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <strong>行距和空格设置</strong>
              <p>配置文档段落间距、行距和智能空格命令</p>
              <el-divider />

              <!-- PARSKIP -->
              <div class="package-section">
                <el-checkbox 
                  v-model="packages.parskip.enabled" 
                  @change="(val: boolean | string | number) => updatePackage('parskip', { ...packages.parskip, enabled: Boolean(val) })"
                  label="parskip - 段落间距宏包"
                />

                <div v-if="packages.parskip.enabled" style="margin-left: 20px; margin-top: 10px;">
                  <el-checkbox 
                    v-model="packages.parskip.linespread.enabled" 
                    @update:model-value="(val: boolean | string | number) => updatePackage('linespread', { ...packages.parskip.linespread, enabled: Boolean(val) })"
                    label="linespread - 行距设置"
                  />

                  <div v-if="packages.parskip.linespread.enabled" style="margin-left: 20px; margin-top: 10px;">
                    <el-form>
                      <el-form-item label="行距系数">
                        <el-input-number 
                          v-model="packages.parskip.linespread.value"
                          @change="updateLinespreadValue"
                          :min="0.1"
                          :max="3"
                          :step="0.001"
                          size="small"
                        />
                      </el-form-item>
                    </el-form>
                  </div>
                </div>
              </div>

              <!-- XSPACE -->
              <div class="package-section">
                <el-checkbox 
                  v-model="packages.xspace.enabled" 
                  @change="(val: boolean | string | number) => updatePackage('xspace', { enabled: Boolean(val) })"
                  label="xspace - 智能空格宏包"
                />
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
