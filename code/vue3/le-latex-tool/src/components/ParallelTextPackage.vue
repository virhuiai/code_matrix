<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, onMounted } from 'vue'
import { generateCodeFromPackageInfos, type PackageInfo } from '../utils/generic-packages-utils'
import { setupCodeEmission } from '../utils/code-emitter'

const props = defineProps<{
  modelValue: {
    enabled?: boolean
    pdfcolparcolumnsEnabled?: boolean
    paracolEnabled?: boolean
    lengthsEnabled?: boolean
    commandsEnabled?: boolean
    dualColumnEnvEnabled?: boolean
    dualColumnEnvTwoEnabled?: boolean
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { 
    enabled?: boolean
    pdfcolparcolumnsEnabled?: boolean
    paracolEnabled?: boolean
    lengthsEnabled?: boolean
    commandsEnabled?: boolean
    dualColumnEnvEnabled?: boolean
    dualColumnEnvTwoEnabled?: boolean
  }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const isDialogOpen = ref(false)

 

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

// 计算属性：生成 LaTeX 代码（使用通用工具 + 追加自定义定义块）
const latexCode = computed(() => {
  if (!mainEnabled.value) return ''

  const infos: PackageInfo[] = []
  if (pdfcolparcolumnsEnabled.value) {
    infos.push({ package: 'pdfcolparcolumns' })
  }
  if (paracolEnabled.value) {
    infos.push({ package: 'paracol' })
  }

  const pkgLines = generateCodeFromPackageInfos(infos)

  const extraBlocks: string[] = []
  if (lengthsEnabled.value) extraBlocks.push(lengthTemplates)
  if (commandsEnabled.value) extraBlocks.push(commandTemplates)

  const envBlocks: string[] = []
  if (dualColumnEnvEnabled.value) envBlocks.push(environmentTemplates.dualColumn)
  if (dualColumnEnvTwoEnabled.value) envBlocks.push(environmentTemplates.dualColumnTwo)
  if (envBlocks.length > 0) extraBlocks.push(envBlocks.join('\n\n'))

  return [pkgLines, ...extraBlocks].filter(Boolean).join('\n\n')
})

setupCodeEmission(latexCode, emit, props.componentId, 'ParallelTextPackage')

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
  
})

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
    <!-- 触发弹窗的按钮 -->
    <el-button type="primary" @click="showDialog" style="width: 100%; margin-top: 10px;">对译环境</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="isDialogOpen"
      title="对译环境设置"
      :before-close="hideDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>对译环境设置</strong>
          <p>设置文档中的对译环境，包括双栏排版和栏宽调整命令</p>

          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <el-alert
                title="使用说明"
                description="以下宏包和自定义命令可以单独启用或禁用。"
                type="info"
                show-icon
                style="margin-bottom: 15px;"
              />

              <div class="package-section">
                <el-checkbox 
                  :model-value="mainEnabled" 
                  @update:model-value="(val: boolean | string | number) => mainEnabled = Boolean(val)"
                  label="启用对译环境设置" 
                />
              </div>

              <div v-if="mainEnabled">
                <el-divider />

                <!-- 宏包选项 -->
                <div class="package-section">
                  <strong>宏包选项</strong>
                  <div style="margin-top: 10px; margin-left: 12px;">
                    <el-checkbox 
                      :model-value="pdfcolparcolumnsEnabled" 
                      @update:model-value="(val: boolean | string | number) => pdfcolparcolumnsEnabled = Boolean(val)"
                      label="pdfcolparcolumns 宏包（提供带颜色支持的并排栏）" 
                      class="package-option-item"
                    />
                    <el-checkbox 
                      :model-value="paracolEnabled" 
                      @update:model-value="(val: boolean | string | number) => paracolEnabled = Boolean(val)"
                      label="paracol 宏包（提供并排文本环境）" 
                      class="package-option-item"
                    />
                  </div>
                </div>

                <el-divider />

                <!-- 长度定义 -->
                <div class="package-section">
                  <strong>长度定义</strong>
                  <div style="margin-top: 10px; margin-left: 12px;">
                    <el-checkbox 
                      :model-value="lengthsEnabled" 
                      @update:model-value="(val: boolean | string | number) => lengthsEnabled = Boolean(val)"
                      label="启用长度定义（栏间距、左右栏宽度等）" 
                      class="package-option-item"
                    />
                  </div>
                </div>

                <el-divider />

                <!-- 命令定义 -->
                <div class="package-section">
                  <strong>命令定义</strong>
                  <div style="margin-top: 10px; margin-left: 12px;">
                    <el-checkbox 
                      :model-value="commandsEnabled" 
                      @update:model-value="(val: boolean | string | number) => commandsEnabled = Boolean(val)"
                      label="启用自定义命令（栏宽调整、换栏等）" 
                      class="package-option-item"
                    />
                  </div>
                </div>

                <el-divider />

                <!-- 环境定义 -->
                <div class="package-section">
                  <strong>环境定义</strong>
                  <div style="margin-top: 10px; margin-left: 12px;">
                    <el-checkbox 
                      :model-value="dualColumnEnvEnabled" 
                      @update:model-value="(val: boolean | string | number) => dualColumnEnvEnabled = Boolean(val)"
                      label="双栏 环境（基于 parcolumns）" 
                      class="package-option-item"
                    />
                    <el-checkbox 
                      :model-value="dualColumnEnvTwoEnabled" 
                      @update:model-value="(val: boolean | string | number) => dualColumnEnvTwoEnabled = Boolean(val)"
                      label="双栏二 环境（基于 paracol）" 
                      class="package-option-item"
                    />
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
