<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider, ElAlert } from 'element-plus'

const props = defineProps<{
  modelValue: {
    variorefEnabled: boolean
    imakeidxEnabled: boolean
    splitidxEnabled: boolean
    hyperrefEnabled: boolean
    urlEnabled: boolean
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { 
    variorefEnabled: boolean, 
    imakeidxEnabled: boolean, 
    splitidxEnabled: boolean, 
    hyperrefEnabled: boolean,
    urlEnabled: boolean
  }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// LaTeX 代码模板
const variorefTemplate = `\\usepackage{varioref}`

const imakeidxTemplate = `% 使用 imakeidx 宏包（推荐）
\\usepackage[noautomatic]{imakeidx}   % 禁止自动运行 makeindex

\\makeindex[name=aut,title=人名索引,intoc]
\\makeindex[name=loc,title=地名索引,intoc]
\\makeindex[name=conc,title=概念索引,intoc]

% 正文里使用
%\\index[aut]{爱因斯坦}
%\\index[loc]{北京}
%\\index[conc]{相对论}

% 打印索引（顺序随意）
%\\printindex[aut]   % 标题自动用上面 title= 设置的
%\\printindex[loc]
%\\printindex[conc]`

const splitidxTemplate = `% 使用 splitidx 宏包（功能更强）
\\usepackage[splitindex]{splitidx}

\\newindex{aut}{idxa}   % 人名索引
\\newindex{loc}{idxb}   % 地名索引

% 正文里使用
%\\sindex[aut]{牛顿}
%\\sindex[loc]{伦敦}

% 打印
%\\printsplitindex[aut]{人名索引}
%\\printsplitindex[loc]{地名索引}`

const hyperrefTemplate = `% 链接设置
\\usepackage[CJKbookmarks,bookmarksnumbered,bookmarksopen,
            pdftitle={\\pdffilename},pdfauthor=virhuiai,
            colorlinks=true, pdfstartview=FitH,citecolor=blue,linktocpage,
            linkcolor=blue,urlcolor=blue,hyperindex=true]{hyperref}`

const urlTemplate = `% url样式设置
\\usepackage{url}
\\urlstyle{same}`

// 计算属性：控制启用状态
const variorefEnabled = computed({
  get: () => props.modelValue.variorefEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: value, 
    imakeidxEnabled: props.modelValue.imakeidxEnabled ?? false,
    splitidxEnabled: props.modelValue.splitidxEnabled ?? false,
    hyperrefEnabled: props.modelValue.hyperrefEnabled ?? true,
    urlEnabled: props.modelValue.urlEnabled ?? true
  })
})

const imakeidxEnabled = computed({
  get: () => props.modelValue.imakeidxEnabled ?? false,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: props.modelValue.variorefEnabled ?? true,
    imakeidxEnabled: value,
    splitidxEnabled: false, // 互斥选项
    hyperrefEnabled: props.modelValue.hyperrefEnabled ?? true,
    urlEnabled: props.modelValue.urlEnabled ?? true
  })
})

const splitidxEnabled = computed({
  get: () => props.modelValue.splitidxEnabled ?? false,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: props.modelValue.variorefEnabled ?? true,
    imakeidxEnabled: false, // 互斥选项
    splitidxEnabled: value,
    hyperrefEnabled: props.modelValue.hyperrefEnabled ?? true,
    urlEnabled: props.modelValue.urlEnabled ?? true
  })
})

const hyperrefEnabled = computed({
  get: () => props.modelValue.hyperrefEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: props.modelValue.variorefEnabled ?? true,
    imakeidxEnabled: props.modelValue.imakeidxEnabled ?? false,
    splitidxEnabled: props.modelValue.splitidxEnabled ?? false,
    hyperrefEnabled: value,
    urlEnabled: props.modelValue.urlEnabled ?? true
  })
})

const urlEnabled = computed({
  get: () => props.modelValue.urlEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: props.modelValue.variorefEnabled ?? true,
    imakeidxEnabled: props.modelValue.imakeidxEnabled ?? false,
    splitidxEnabled: props.modelValue.splitidxEnabled ?? false,
    hyperrefEnabled: props.modelValue.hyperrefEnabled ?? true,
    urlEnabled: value
  })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  const codes = []
  if (variorefEnabled.value) {
    codes.push(variorefTemplate)
  }
  if (imakeidxEnabled.value) {
    codes.push(imakeidxTemplate)
  }
  if (splitidxEnabled.value) {
    codes.push(splitidxTemplate)
  }
  if (hyperrefEnabled.value) {
    codes.push(hyperrefTemplate)
  }
  if (urlEnabled.value) {
    codes.push(urlTemplate)
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
  if (props.modelValue.variorefEnabled === undefined || 
      props.modelValue.imakeidxEnabled === undefined || 
      props.modelValue.splitidxEnabled === undefined || 
      props.modelValue.hyperrefEnabled === undefined ||
      props.modelValue.urlEnabled === undefined) {
    emit('update:modelValue', { 
      variorefEnabled: true,
      imakeidxEnabled: false,
      splitidxEnabled: false,
      hyperrefEnabled: true,
      urlEnabled: true
    })
  }
  
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`HyperlinkIndexPackage component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">链接和索引设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="链接和索引设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>链接和索引设置</strong>
          <p>设置文档中的链接和索引功能，包括超链接、参考文献链接以及各种索引</p>
          
          <div style="margin-top: 20px;">
            <el-checkbox 
              :model-value="variorefEnabled" 
              @update:model-value="(val) => variorefEnabled = Boolean(val)"
              label="启用 varioref 宏包（用于智能交叉引用）" 
            />
            
            <div v-if="variorefEnabled" style="margin-top: 10px; margin-left: 20px;">
              <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ variorefTemplate }}</pre>
            </div>
          </div>
          
          <el-divider />
          
          <div style="margin-top: 20px;">
            <el-alert
              title="索引宏包选择说明"
              description="imakeidx 和 splitidx 是 multind 的现代替代品，提供了更好的功能和维护性。请注意这两个选项是互斥的，只能同时启用其中一个。"
              type="info"
              show-icon
              style="margin-bottom: 15px;"
            />
            
            <el-checkbox 
              :model-value="imakeidxEnabled" 
              @update:model-value="(val) => imakeidxEnabled = Boolean(val)"
              label="启用 imakeidx 宏包（最简单、最推荐）" 
            />
            
            <div v-if="imakeidxEnabled" style="margin-top: 10px; margin-left: 20px;">
              <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ imakeidxTemplate }}</pre>
            </div>
            
            <el-checkbox 
              :model-value="splitidxEnabled" 
              @update:model-value="(val) => splitidxEnabled = Boolean(val)"
              label="启用 splitidx 宏包（功能更强）" 
              style="margin-top: 10px;"
            />
            
            <div v-if="splitidxEnabled" style="margin-top: 10px; margin-left: 20px;">
              <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ splitidxTemplate }}</pre>
            </div>
          </div>
          
          <el-divider />
          
          <div style="margin-top: 20px;">
            <el-checkbox 
              :model-value="hyperrefEnabled" 
              @update:model-value="(val) => hyperrefEnabled = Boolean(val)"
              label="启用 hyperref 宏包（用于超链接）" 
            />
            
            <div v-if="hyperrefEnabled" style="margin-top: 10px; margin-left: 20px;">
              <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ hyperrefTemplate }}</pre>
            </div>
          </div>
          
          <el-divider />
          
          <div style="margin-top: 20px;">
            <el-checkbox 
              :model-value="urlEnabled" 
              @update:model-value="(val) => urlEnabled = Boolean(val)"
              label="启用 url 宏包（用于URL样式设置）" 
            />
            
            <div v-if="urlEnabled" style="margin-top: 10px; margin-left: 20px;">
              <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ urlTemplate }}</pre>
            </div>
          </div>
          
          <div style="margin-top: 20px;">
            <strong>完整代码预览</strong>
            <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace; margin-top: 10px;">{{ computedLatexCode }}</pre>
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