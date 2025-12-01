<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider, ElAlert } from 'element-plus'

const props = defineProps<{
  modelValue: {
    enabled: boolean
    pifontEnabled: boolean
    mflogoEnabled: boolean
    texnamesEnabled: boolean
    bbdingEnabled: boolean
    amssymbEnabled: boolean
    enumerateEnabled: boolean
    enumitemEnabled: boolean
    paralistEnabled: boolean
    mdwlistEnabled: boolean
    manfntEnabled: boolean
    eurosymEnabled: boolean
    descNLEnabled: boolean
    descMLEnabled: boolean
    descPLEnabled: boolean
    dlEnabled: boolean
    daiquanEnabled: boolean
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { 
    enabled: boolean
    pifontEnabled: boolean
    mflogoEnabled: boolean
    texnamesEnabled: boolean
    bbdingEnabled: boolean
    amssymbEnabled: boolean
    enumerateEnabled: boolean
    enumitemEnabled: boolean
    paralistEnabled: boolean
    mdwlistEnabled: boolean
    manfntEnabled: boolean
    eurosymEnabled: boolean
    descNLEnabled: boolean
    descMLEnabled: boolean
    descPLEnabled: boolean
    dlEnabled: boolean
    daiquanEnabled: boolean
  }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 各宏包的LaTeX代码模板
const packageTemplates = {
  pifont: '\\usepackage{pifont}',
  mflogo: '\\usepackage{mflogo,metalogo}',
  texnames: '\\usepackage{texnames}',
  bbding: '\\usepackage{bbding}',
  amssymb: '\\usepackage{amssymb,latexsym,textcomp,mathrsfs,euscript,yhmath}',
  enumerate: '\\usepackage{enumerate}',
  enumitem: '\\usepackage{enumitem}',
  paralist: '\\usepackage[olditem,oldenum]{paralist}',
  mdwlist: '\\usepackage{mdwlist}',
  manfnt: '\\usepackage{manfnt}',
  eurosym: '\\usepackage{eurosym}'
}

// 自定义环境代码模板
const customEnvironmentTemplates = {
  descNL: `\\newenvironment{descNL}[1][2em]{%
\\begin{basedescript}{\\desclabelwidth{#1}\\desclabelstyle{\\nextlinelabel}}}%
{\\end{basedescript}}`,
  descML: `\\newenvironment{descML}[1][2em]{%
\\begin{basedescript}{\\desclabelwidth{#1}\\desclabelstyle{\\multilinelabel}}}%
{\\end{basedescript}}`,
  descPL: `\\newenvironment{descPL}[1][2em]{%
\\begin{basedescript}{\\desclabelwidth{#1}\\desclabelstyle{\\pushlabel}}}{\\end{basedescript}}`,
  dl: `\\newlength{\\basedescriptDesclabelwidth}
\\newenvironment{dl}[1][标签]{%
\\settowidth{\\basedescriptDesclabelwidth}{\\tt #1}
\\begin{basedescript}{%
\\renewcommand{\\makelabel}[1]{\\tt ##1}%
\\desclabelwidth{\\basedescriptDesclabelwidth}%
\\desclabelstyle{\\pushlabel}}}{\\end{basedescript}}`,
  daiquan: `\\newcounter{带圈文字}
\\newcommand\\带圈文字[1]{
\\protect\\setcounter{带圈文字}{171+#1}
\\protect\\ding{\\value{带圈文字}}}`
}

// 计算属性：控制启用状态
const mainEnabled = computed({
  get: () => props.modelValue.enabled,
  set: (value) => emit('update:modelValue', { 
    enabled: value,
    pifontEnabled: props.modelValue.pifontEnabled ?? true,
    mflogoEnabled: props.modelValue.mflogoEnabled ?? true,
    texnamesEnabled: props.modelValue.texnamesEnabled ?? true,
    bbdingEnabled: props.modelValue.bbdingEnabled ?? true,
    amssymbEnabled: props.modelValue.amssymbEnabled ?? true,
    enumerateEnabled: props.modelValue.enumerateEnabled ?? true,
    enumitemEnabled: props.modelValue.enumitemEnabled ?? true,
    paralistEnabled: props.modelValue.paralistEnabled ?? true,
    mdwlistEnabled: props.modelValue.mdwlistEnabled ?? true,
    manfntEnabled: props.modelValue.manfntEnabled ?? true,
    eurosymEnabled: props.modelValue.eurosymEnabled ?? true,
    descNLEnabled: props.modelValue.descNLEnabled ?? true,
    descMLEnabled: props.modelValue.descMLEnabled ?? true,
    descPLEnabled: props.modelValue.descPLEnabled ?? true,
    dlEnabled: props.modelValue.dlEnabled ?? true,
    daiquanEnabled: props.modelValue.daiquanEnabled ?? true
  })
})

const pifontEnabled = computed({
  get: () => props.modelValue.pifontEnabled ?? true,
  set: (value) => {
    const newValue = { ...props.modelValue, pifontEnabled: value }
    // 如果取消pifont宏包，则也取消依赖它的带圈文字命令
    if (!value) {
      newValue.daiquanEnabled = false
    }
    emit('update:modelValue', newValue)
  }
})

const mflogoEnabled = computed({
  get: () => props.modelValue.mflogoEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    mflogoEnabled: value
  })
})

const texnamesEnabled = computed({
  get: () => props.modelValue.texnamesEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    texnamesEnabled: value
  })
})

const bbdingEnabled = computed({
  get: () => props.modelValue.bbdingEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    bbdingEnabled: value
  })
})

const amssymbEnabled = computed({
  get: () => props.modelValue.amssymbEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    amssymbEnabled: value
  })
})

const enumerateEnabled = computed({
  get: () => props.modelValue.enumerateEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    enumerateEnabled: value
  })
})

const enumitemEnabled = computed({
  get: () => props.modelValue.enumitemEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    enumitemEnabled: value
  })
})

const paralistEnabled = computed({
  get: () => props.modelValue.paralistEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    paralistEnabled: value
  })
})

const mdwlistEnabled = computed({
  get: () => props.modelValue.mdwlistEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    mdwlistEnabled: value
  })
})

const manfntEnabled = computed({
  get: () => props.modelValue.manfntEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    manfntEnabled: value
  })
})

const eurosymEnabled = computed({
  get: () => props.modelValue.eurosymEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    eurosymEnabled: value
  })
})

// 自定义环境启用状态
const descNLEnabled = computed({
  get: () => props.modelValue.descNLEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    descNLEnabled: value
  })
})

const descMLEnabled = computed({
  get: () => props.modelValue.descMLEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    descMLEnabled: value
  })
})

const descPLEnabled = computed({
  get: () => props.modelValue.descPLEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    descPLEnabled: value
  })
})

const dlEnabled = computed({
  get: () => props.modelValue.dlEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    dlEnabled: value
  })
})

const daiquanEnabled = computed({
  get: () => props.modelValue.daiquanEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    daiquanEnabled: value
  })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  if (!mainEnabled.value) return ''
  
  const codes = []
  
  // 添加启用的宏包
  if (pifontEnabled.value) codes.push(packageTemplates.pifont)
  if (mflogoEnabled.value) codes.push(packageTemplates.mflogo)
  if (texnamesEnabled.value) codes.push(packageTemplates.texnames)
  if (bbdingEnabled.value) codes.push(packageTemplates.bbding)
  if (amssymbEnabled.value) codes.push(packageTemplates.amssymb)
  if (enumerateEnabled.value) codes.push(packageTemplates.enumerate)
  if (enumitemEnabled.value) codes.push(packageTemplates.enumitem)
  if (paralistEnabled.value) codes.push(packageTemplates.paralist)
  if (mdwlistEnabled.value) codes.push(packageTemplates.mdwlist)
  if (manfntEnabled.value) codes.push(packageTemplates.manfnt)
  if (eurosymEnabled.value) codes.push(packageTemplates.eurosym)
  
  // 添加启用的自定义环境
  const envCodes = []
  if (descNLEnabled.value) envCodes.push(customEnvironmentTemplates.descNL)
  if (descMLEnabled.value) envCodes.push(customEnvironmentTemplates.descML)
  if (descPLEnabled.value) envCodes.push(customEnvironmentTemplates.descPL)
  if (dlEnabled.value) envCodes.push(customEnvironmentTemplates.dl)
  if (daiquanEnabled.value && pifontEnabled.value) envCodes.push(customEnvironmentTemplates.daiquan)
  
  if (envCodes.length > 0) {
    codes.push('% 自定义描述环境')
    codes.push(...envCodes)
  }
  
  return codes.join('\n')
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
      pifontEnabled: true,
      mflogoEnabled: true,
      texnamesEnabled: true,
      bbdingEnabled: true,
      amssymbEnabled: true,
      enumerateEnabled: true,
      enumitemEnabled: true,
      paralistEnabled: true,
      mdwlistEnabled: true,
      manfntEnabled: true,
      eurosymEnabled: true,
      descNLEnabled: true,
      descMLEnabled: true,
      descPLEnabled: true,
      dlEnabled: true,
      daiquanEnabled: true
    })
  }
  
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`ListSymbolPackage component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">列表_符号设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="列表和符号设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>列表和符号设置</strong>
          <p>设置文档中的列表环境和符号包，包括各种列表格式和特殊符号</p>
          
          <el-checkbox 
            :model-value="mainEnabled" 
            @update:model-value="(val) => mainEnabled = Boolean(val)"
            label="启用列表和符号设置" 
          />
          
          <div v-if="mainEnabled" style="margin-top: 20px;">
            <el-alert
              title="使用说明"
              description="以下宏包和自定义环境可以单独启用或禁用。带圈文字命令依赖于pifont宏包，如果禁用了pifont宏包，带圈文字命令将不会被包含在输出中。"
              type="info"
              show-icon
              style="margin-bottom: 15px;"
            />
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>符号宏包</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="pifontEnabled" 
                  @update:model-value="(val) => pifontEnabled = Boolean(val)"
                  label="pifont 宏包（提供花体字符和装饰符号）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="mflogoEnabled" 
                  @update:model-value="(val) => mflogoEnabled = Boolean(val)"
                  label="mflogo/metalogo 宏包（提供各种 TeX 系统标志）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="texnamesEnabled" 
                  @update:model-value="(val) => texnamesEnabled = Boolean(val)"
                  label="texnames 宏包（提供 TeX 相关名称）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="bbdingEnabled" 
                  @update:model-value="(val) => bbdingEnabled = Boolean(val)"
                  label="bbding 宏包（提供装饰符号）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="amssymbEnabled" 
                  @update:model-value="(val) => amssymbEnabled = Boolean(val)"
                  label="amssymb 等宏包（提供数学符号）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="manfntEnabled" 
                  @update:model-value="(val) => manfntEnabled = Boolean(val)"
                  label="manfnt 宏包（提供手动符号）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="eurosymEnabled" 
                  @update:model-value="(val) => eurosymEnabled = Boolean(val)"
                  label="eurosym 宏包（提供欧元符号）" 
                  style="display: block; margin-bottom: 8px;"
                />
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>列表宏包</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="enumerateEnabled" 
                  @update:model-value="(val) => enumerateEnabled = Boolean(val)"
                  label="enumerate 宏包（增强枚举环境）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="enumitemEnabled" 
                  @update:model-value="(val) => enumitemEnabled = Boolean(val)"
                  label="enumitem 宏包（最灵活的列表定制）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="paralistEnabled" 
                  @update:model-value="(val) => paralistEnabled = Boolean(val)"
                  label="paralist 宏包（提供紧凑列表环境）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="mdwlistEnabled" 
                  @update:model-value="(val) => mdwlistEnabled = Boolean(val)"
                  label="mdwlist 宏包（列表环境扩展）" 
                  style="display: block; margin-bottom: 8px;"
                />
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>自定义环境</strong>
              <p style="margin-top: 5px; font-size: 14px;">以下是可以单独启用的自定义环境：</p>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="descNLEnabled" 
                  @update:model-value="(val) => descNLEnabled = Boolean(val)"
                  label="descNL - 下一行标签描述环境" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="descMLEnabled" 
                  @update:model-value="(val) => descMLEnabled = Boolean(val)"
                  label="descML - 多行标签描述环境" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="descPLEnabled" 
                  @update:model-value="(val) => descPLEnabled = Boolean(val)"
                  label="descPL - 推入标签描述环境" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="dlEnabled" 
                  @update:model-value="(val) => dlEnabled = Boolean(val)"
                  label="dl - 自定义描述环境" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="daiquanEnabled" 
                  @update:model-value="(val) => daiquanEnabled = Boolean(val)"
                  :disabled="!pifontEnabled"
                  label="带圈文字 - 带圈数字命令（需要 pifont 宏包）" 
                  style="display: block; margin-bottom: 8px;"
                />
              </div>
              
              <div style="margin-top: 15px;">
                <strong>自定义环境代码预览</strong>
                <div style="margin-top: 10px;">
                  <div v-if="descNLEnabled" style="margin-bottom: 10px;">
                    <div style="font-weight: bold;">descNL 环境:</div>
                    <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ customEnvironmentTemplates.descNL }}</pre>
                  </div>
                  
                  <div v-if="descMLEnabled" style="margin-bottom: 10px;">
                    <div style="font-weight: bold;">descML 环境:</div>
                    <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ customEnvironmentTemplates.descML }}</pre>
                  </div>
                  
                  <div v-if="descPLEnabled" style="margin-bottom: 10px;">
                    <div style="font-weight: bold;">descPL 环境:</div>
                    <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ customEnvironmentTemplates.descPL }}</pre>
                  </div>
                  
                  <div v-if="dlEnabled" style="margin-bottom: 10px;">
                    <div style="font-weight: bold;">dl 环境:</div>
                    <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ customEnvironmentTemplates.dl }}</pre>
                  </div>
                  
                  <div v-if="daiquanEnabled && pifontEnabled" style="margin-bottom: 10px;">
                    <div style="font-weight: bold;">带圈文字 命令:</div>
                    <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ customEnvironmentTemplates.daiquan }}</pre>
                  </div>
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