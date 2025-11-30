<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElFormItem, ElInputNumber, ElDivider } from 'element-plus'

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

// LaTeX 代码模板
const latexTemplates = {
  parskip: () => {
    return `\\usepackage{parskip}`
  },
  linespread: (value: number) => {
    return `\\linespread{${value}}`
  },
  xspace: () => {
    return `\\usepackage{xspace}`
  }
}

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  const codes = []
  
  if (packages.value.parskip.enabled) {
    codes.push(latexTemplates.parskip())
  }
  
  if (packages.value.parskip.linespread.enabled) {
    codes.push(latexTemplates.linespread(packages.value.parskip.linespread.value))
  }
  
  if (packages.value.xspace.enabled) {
    codes.push(latexTemplates.xspace())
  }
  
  return codes.join('\n\n')
})

// 更新包选项
const updatePackage = (pkg: string, value: any) => {
  (packages.value as any)[pkg] = value
  emit('update:modelValue', { ...packages.value })
}

// 更新 linespread 值
const updateLinespreadValue = (value: number | undefined, oldValue: number | undefined) => {
  if (value !== undefined) {
    packages.value.parskip.linespread.value = value
    emit('update:modelValue', { ...packages.value })
  }
}

// 监听代码变化
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时触发代码变更事件
onMounted(() => {
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`DocumentLayoutPackage component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">行距和空格设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="行距和空格设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>行距和空格设置</strong>
          <p>配置文档段落间距、行距和智能空格命令</p>
          
          <el-divider />
          
          <!-- PARSKIP -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.parskip.enabled" 
              @change="(val) => updatePackage('parskip', { ...packages.parskip, enabled: Boolean(val) })"
              label="parskip - 段落间距宏包"
            />
            
            <div v-if="packages.parskip.enabled" style="margin-left: 20px; margin-top: 10px;">
              <el-checkbox 
                v-model="packages.parskip.linespread.enabled" 
                @change="(val) => updatePackage('linespread', { ...packages.parskip.linespread, enabled: Boolean(val) })"
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
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.xspace.enabled" 
              @change="(val) => updatePackage('xspace', { enabled: Boolean(val) })"
              label="xspace - 智能空格宏包"
            />
          </div>
          
          <el-divider />
          
          <!-- 代码预览 -->
          <div v-if="computedLatexCode" style="margin-top: 20px;">
            <div><strong>生成的 LaTeX 代码:</strong></div>
            <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace;">{{ computedLatexCode }}</pre>
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