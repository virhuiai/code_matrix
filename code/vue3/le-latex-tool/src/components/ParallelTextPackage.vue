<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider, ElAlert } from 'element-plus'

const props = defineProps<{
  modelValue: {
    enabled: boolean
    pdfcolparcolumnsEnabled: boolean
    paracolEnabled: boolean
    lengthsEnabled: boolean
    commandsEnabled: boolean
    dualColumnEnvEnabled: boolean
    dualColumnEnvTwoEnabled: boolean
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { 
    enabled: boolean
    pdfcolparcolumnsEnabled: boolean
    paracolEnabled: boolean
    lengthsEnabled: boolean
    commandsEnabled: boolean
    dualColumnEnvEnabled: boolean
    dualColumnEnvTwoEnabled: boolean
  }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 各宏包的LaTeX代码模板
const packageTemplates = {
  pdfcolparcolumns: '\\usepackage{pdfcolparcolumns}',
  paracol: '\\usepackage{paracol}'
}

// 长度定义代码模板
const lengthTemplates = `\\newlength{\\栏间距}
\\setlength{\\栏间距}{1em}

\\newlength{\\左栏宽}
\\newlength{\\右栏宽}`

// 命令定义代码模板
const commandTemplates = `\\newcommand\\栏宽调整[1]{%
\\setlength{\\左栏宽}{#1\\textwidth}%
\\setlength{\\右栏宽}{\\textwidth-\\栏间距-\\左栏宽}%
}

% \\栏宽调整{0.37}
\\setlength{\\左栏宽}{0.4\\textwidth}
\\setlength{\\右栏宽}{0.51\\textwidth}

\\newcommand\\栏体[1]{\\colchunk{#1}}
\\newcommand\\换栏{\\colplacechunks}

\\newcommand\\栏宽调整二[1]{%
\\columnratio{#1}}%

\\newcommand\\换栏二{\\switchcolumn}`

// 环境定义代码模板
const environmentTemplates = {
  dualColumn: `\\newenvironment{双栏}%
{\\begin{parcolumns}[rulebetween=false,distance=\\栏间距,colwidths={1=\\左栏宽,2=\\右栏宽}]{2}}%
{\\end{parcolumns}}`,
  dualColumnTwo: `\\newenvironment{双栏二}%
{\\begin{paracol}{2}}%
{\\end{paracol}}`
}

// 计算属性：控制启用状态
const mainEnabled = computed({
  get: () => props.modelValue.enabled,
  set: (value) => emit('update:modelValue', { 
    enabled: value,
    pdfcolparcolumnsEnabled: props.modelValue.pdfcolparcolumnsEnabled ?? true,
    paracolEnabled: props.modelValue.paracolEnabled ?? true,
    lengthsEnabled: props.modelValue.lengthsEnabled ?? true,
    commandsEnabled: props.modelValue.commandsEnabled ?? true,
    dualColumnEnvEnabled: props.modelValue.dualColumnEnvEnabled ?? true,
    dualColumnEnvTwoEnabled: props.modelValue.dualColumnEnvTwoEnabled ?? true
  })
})

const pdfcolparcolumnsEnabled = computed({
  get: () => props.modelValue.pdfcolparcolumnsEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    pdfcolparcolumnsEnabled: value
  })
})

const paracolEnabled = computed({
  get: () => props.modelValue.paracolEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    paracolEnabled: value
  })
})

const lengthsEnabled = computed({
  get: () => props.modelValue.lengthsEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    lengthsEnabled: value
  })
})

const commandsEnabled = computed({
  get: () => props.modelValue.commandsEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    commandsEnabled: value
  })
})

const dualColumnEnvEnabled = computed({
  get: () => props.modelValue.dualColumnEnvEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    dualColumnEnvEnabled: value
  })
})

const dualColumnEnvTwoEnabled = computed({
  get: () => props.modelValue.dualColumnEnvTwoEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    dualColumnEnvTwoEnabled: value
  })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  if (!mainEnabled.value) return ''
  
  const codes = []
  
  // 添加启用的宏包
  if (pdfcolparcolumnsEnabled.value) codes.push(packageTemplates.pdfcolparcolumns)
  if (paracolEnabled.value) codes.push(packageTemplates.paracol)
  
  // 添加启用的长度定义
  if (lengthsEnabled.value) codes.push(lengthTemplates)
  
  // 添加启用的命令定义
  if (commandsEnabled.value) codes.push(commandTemplates)
  
  // 添加启用的环境定义
  const envCodes = []
  if (dualColumnEnvEnabled.value) envCodes.push(environmentTemplates.dualColumn)
  if (dualColumnEnvTwoEnabled.value) envCodes.push(environmentTemplates.dualColumnTwo)
  
  if (envCodes.length > 0) {
    codes.push(...envCodes)
  }
  
  return codes.join('\n\n')
})

// 监听代码变化
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时触发代码变更事件
onMounted(() => {
  // 如果未设置enabled属性，则设置默认值
  if (Object.values(props.modelValue).every(v => v === undefined)) {
    emit('update:modelValue', { 
      enabled: true,
      pdfcolparcolumnsEnabled: true,
      paracolEnabled: true,
      lengthsEnabled: true,
      commandsEnabled: true,
      dualColumnEnvEnabled: true,
      dualColumnEnvTwoEnabled: true
    })
  }
  
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`ParallelTextPackage component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">对译环境</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="对译环境设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>对译环境设置</strong>
          <p>设置文档中的对译环境，包括双栏排版和栏宽调整命令</p>
          
          <el-checkbox 
            :model-value="mainEnabled" 
            @update:model-value="(val) => mainEnabled = Boolean(val)"
            label="启用对译环境设置" 
          />
          
          <div v-if="mainEnabled" style="margin-top: 20px;">
            <el-alert
              title="使用说明"
              description="以下宏包和自定义命令可以单独启用或禁用。"
              type="info"
              show-icon
              style="margin-bottom: 15px;"
            />
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>宏包选项</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="pdfcolparcolumnsEnabled" 
                  @update:model-value="(val) => pdfcolparcolumnsEnabled = Boolean(val)"
                  label="pdfcolparcolumns 宏包（提供带颜色支持的并排栏）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="paracolEnabled" 
                  @update:model-value="(val) => paracolEnabled = Boolean(val)"
                  label="paracol 宏包（提供并排文本环境）" 
                  style="display: block; margin-bottom: 8px;"
                />
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>长度定义</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="lengthsEnabled" 
                  @update:model-value="(val) => lengthsEnabled = Boolean(val)"
                  label="启用长度定义（栏间距、左右栏宽度等）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <div v-if="lengthsEnabled" style="margin-top: 10px;">
                  <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ lengthTemplates }}</pre>
                </div>
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>命令定义</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="commandsEnabled" 
                  @update:model-value="(val) => commandsEnabled = Boolean(val)"
                  label="启用自定义命令（栏宽调整、换栏等）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <div v-if="commandsEnabled" style="margin-top: 10px;">
                  <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ commandTemplates }}</pre>
                </div>
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>环境定义</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="dualColumnEnvEnabled" 
                  @update:model-value="(val) => dualColumnEnvEnabled = Boolean(val)"
                  label="双栏 环境（基于 parcolumns）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <div v-if="dualColumnEnvEnabled" style="margin-top: 10px;">
                  <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ environmentTemplates.dualColumn }}</pre>
                </div>
                
                <el-checkbox 
                  :model-value="dualColumnEnvTwoEnabled" 
                  @update:model-value="(val) => dualColumnEnvTwoEnabled = Boolean(val)"
                  label="双栏二 环境（基于 paracol）" 
                  style="display: block; margin-bottom: 8px; margin-top: 10px;"
                />
                
                <div v-if="dualColumnEnvTwoEnabled" style="margin-top: 10px;">
                  <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ environmentTemplates.dualColumnTwo }}</pre>
                </div>
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>完整代码预览</strong>
              <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace; margin-top: 10px;">{{ computedLatexCode }}</pre>
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