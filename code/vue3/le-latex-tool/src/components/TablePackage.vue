<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider, ElAlert } from 'element-plus'

const props = defineProps<{
  modelValue: {
    enabled: boolean
    longtableEnabled: boolean
    booktabsEnabled: boolean
    tabularxEnabled: boolean
    tabularyEnabled: boolean
    ltablexEnabled: boolean
    colortblEnabled: boolean
    multirowEnabled: boolean
    arrayEnabled: boolean
    dcolumnEnabled: boolean
    slashboxEnabled: boolean
    arydshlnEnabled: boolean
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { 
    enabled: boolean
    longtableEnabled: boolean
    booktabsEnabled: boolean
    tabularxEnabled: boolean
    tabularyEnabled: boolean
    ltablexEnabled: boolean
    colortblEnabled: boolean
    multirowEnabled: boolean
    arrayEnabled: boolean
    dcolumnEnabled: boolean
    slashboxEnabled: boolean
    arydshlnEnabled: boolean
  }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 各宏包的LaTeX代码模板
const packageTemplates = {
  longtable: `% 表格大都采用 longtable 环境编制，( 另一个跨页的是 \\usepackage{supertabular})
\\usepackage{longtable}`,
  booktabs: `% 表格线使用 booktabs 宏包提供的三种画线命令绘制;
\\usepackage{booktabs}`,
  tabularx: `% 并且调用 tabularx 宏包，用以增强 longtable 环境中列格式的功能。
\\usepackage{tabularx}`,
  tabulary: `% 总宽可设、列宽自动确定 \\begin{tabulary}{宽度}{列格式} \\end{tabulary} % 列格式有 L、C、R 和 J 
\\usepackage{tabulary}`,
  ltablex: `\\usepackage{ltablex}%扩展了 tabularx 环境功能，能自动分页，可用于排版跨页长表格。`,
  colortbl: `% 在14color中。。。[table] 
\\usepackage{colortbl}`,
  multirow: `% 跨行表格宏包multirow
\\usepackage{multirow}`,
  array: `% Frank Mittelbach 等人编写的 array 宏包
% 对 3 个表格环境(tabular tabular* array)的制表功能做了重大改进和扩展
% 主要是增加和增强了列格式功能，
% 还增加了许多表格参数的调整功能。
\\usepackage{array}`,
  dcolumn: `% dcolumn小数点对齐宏包，
% 它专为tabular和array表格环境定义了
% 一个用于对齐小数点或逗号等标点符号的列格式选项
\\usepackage{dcolumn}`,
  slashbox: `% 对角线宏包slashbox
\\usepackage{slashbox}`,
  arydshln: `% 虚线表格宏包arydshln
%如果已经调用或是还需要调用与表格有关的 array、colortab、colortbl 或 longtable 宏包，
%应将 arydshln 的调用命令放在这些宏包的调用命令之后，否则可能会造成莫名的错误
\\usepackage{arydshln}`
}

// 计算属性：控制启用状态
const mainEnabled = computed({
  get: () => props.modelValue.enabled,
  set: (value) => emit('update:modelValue', { 
    enabled: value,
    longtableEnabled: props.modelValue.longtableEnabled ?? true,
    booktabsEnabled: props.modelValue.booktabsEnabled ?? true,
    tabularxEnabled: props.modelValue.tabularxEnabled ?? true,
    tabularyEnabled: props.modelValue.tabularyEnabled ?? true,
    ltablexEnabled: props.modelValue.ltablexEnabled ?? true,
    colortblEnabled: props.modelValue.colortblEnabled ?? true,
    multirowEnabled: props.modelValue.multirowEnabled ?? true,
    arrayEnabled: props.modelValue.arrayEnabled ?? true,
    dcolumnEnabled: props.modelValue.dcolumnEnabled ?? true,
    slashboxEnabled: props.modelValue.slashboxEnabled ?? true,
    arydshlnEnabled: props.modelValue.arydshlnEnabled ?? true
  })
})

const longtableEnabled = computed({
  get: () => props.modelValue.longtableEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    longtableEnabled: value
  })
})

const booktabsEnabled = computed({
  get: () => props.modelValue.booktabsEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    booktabsEnabled: value
  })
})

const tabularxEnabled = computed({
  get: () => props.modelValue.tabularxEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    tabularxEnabled: value
  })
})

const tabularyEnabled = computed({
  get: () => props.modelValue.tabularyEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    tabularyEnabled: value
  })
})

const ltablexEnabled = computed({
  get: () => props.modelValue.ltablexEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    ltablexEnabled: value
  })
})

const colortblEnabled = computed({
  get: () => props.modelValue.colortblEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    colortblEnabled: value
  })
})

const multirowEnabled = computed({
  get: () => props.modelValue.multirowEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    multirowEnabled: value
  })
})

const arrayEnabled = computed({
  get: () => props.modelValue.arrayEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    arrayEnabled: value
  })
})

const dcolumnEnabled = computed({
  get: () => props.modelValue.dcolumnEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    dcolumnEnabled: value
  })
})

const slashboxEnabled = computed({
  get: () => props.modelValue.slashboxEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    slashboxEnabled: value
  })
})

const arydshlnEnabled = computed({
  get: () => props.modelValue.arydshlnEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    arydshlnEnabled: value
  })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  if (!mainEnabled.value) return ''
  
  const codes = []
  
  // 添加启用的宏包
  if (longtableEnabled.value) codes.push(packageTemplates.longtable)
  if (booktabsEnabled.value) codes.push(packageTemplates.booktabs)
  if (tabularxEnabled.value) codes.push(packageTemplates.tabularx)
  if (tabularyEnabled.value) codes.push(packageTemplates.tabulary)
  if (ltablexEnabled.value) codes.push(packageTemplates.ltablex)
  if (colortblEnabled.value) codes.push(packageTemplates.colortbl)
  if (multirowEnabled.value) codes.push(packageTemplates.multirow)
  if (arrayEnabled.value) codes.push(packageTemplates.array)
  if (dcolumnEnabled.value) codes.push(packageTemplates.dcolumn)
  if (slashboxEnabled.value) codes.push(packageTemplates.slashbox)
  if (arydshlnEnabled.value) codes.push(packageTemplates.arydshln)
  
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
      longtableEnabled: true,
      booktabsEnabled: true,
      tabularxEnabled: true,
      tabularyEnabled: true,
      ltablexEnabled: true,
      colortblEnabled: true,
      multirowEnabled: true,
      arrayEnabled: true,
      dcolumnEnabled: true,
      slashboxEnabled: true,
      arydshlnEnabled: true
    })
  }
  
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`TablePackage component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">表格</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="表格设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>表格设置</strong>
          <p>设置文档中的表格相关功能，包括长表格、表格线、列格式等功能</p>
          
          <el-checkbox 
            :model-value="mainEnabled" 
            @update:model-value="(val) => mainEnabled = Boolean(val)"
            label="启用表格设置" 
          />
          
          <div v-if="mainEnabled" style="margin-top: 20px;">
            <el-alert
              title="使用说明"
              description="以下宏包可以单独启用或禁用。"
              type="info"
              show-icon
              style="margin-bottom: 15px;"
            />
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>基础表格宏包</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="longtableEnabled" 
                  @update:model-value="(val) => longtableEnabled = Boolean(val)"
                  label="longtable 宏包（跨页长表格）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="booktabsEnabled" 
                  @update:model-value="(val) => booktabsEnabled = Boolean(val)"
                  label="booktabs 宏包（专业表格线）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="tabularxEnabled" 
                  @update:model-value="(val) => tabularxEnabled = Boolean(val)"
                  label="tabularx 宏包（增强列格式）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="tabularyEnabled" 
                  @update:model-value="(val) => tabularyEnabled = Boolean(val)"
                  label="tabulary 宏包（自动调整列宽）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="ltablexEnabled" 
                  @update:model-value="(val) => ltablexEnabled = Boolean(val)"
                  label="ltablex 宏包（自动分页的 tabularx）" 
                  style="display: block; margin-bottom: 8px;"
                />
              </div>
              
              <div v-if="longtableEnabled" style="margin-top: 10px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.longtable }}</pre>
              </div>
              
              <div v-if="booktabsEnabled" style="margin-top: 10px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.booktabs }}</pre>
              </div>
              
              <div v-if="tabularxEnabled" style="margin-top: 10px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.tabularx }}</pre>
              </div>
              
              <div v-if="tabularyEnabled" style="margin-top: 10px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.tabulary }}</pre>
              </div>
              
              <div v-if="ltablexEnabled" style="margin-top: 10px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.ltablex }}</pre>
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>表格增强宏包</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="colortblEnabled" 
                  @update:model-value="(val) => colortblEnabled = Boolean(val)"
                  label="colortbl 宏包（彩色表格）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="multirowEnabled" 
                  @update:model-value="(val) => multirowEnabled = Boolean(val)"
                  label="multirow 宏包（跨行表格）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="arrayEnabled" 
                  @update:model-value="(val) => arrayEnabled = Boolean(val)"
                  label="array 宏包（增强表格功能）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="dcolumnEnabled" 
                  @update:model-value="(val) => dcolumnEnabled = Boolean(val)"
                  label="dcolumn 宏包（小数点对齐）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="slashboxEnabled" 
                  @update:model-value="(val) => slashboxEnabled = Boolean(val)"
                  label="slashbox 宏包（对角线表格）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="arydshlnEnabled" 
                  @update:model-value="(val) => arydshlnEnabled = Boolean(val)"
                  label="arydshln 宏包（虚线表格）" 
                  style="display: block; margin-bottom: 8px;"
                />
              </div>
              
              <div v-if="colortblEnabled" style="margin-top: 10px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.colortbl }}</pre>
              </div>
              
              <div v-if="multirowEnabled" style="margin-top: 10px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.multirow }}</pre>
              </div>
              
              <div v-if="arrayEnabled" style="margin-top: 10px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.array }}</pre>
              </div>
              
              <div v-if="dcolumnEnabled" style="margin-top: 10px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.dcolumn }}</pre>
              </div>
              
              <div v-if="slashboxEnabled" style="margin-top: 10px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.slashbox }}</pre>
              </div>
              
              <div v-if="arydshlnEnabled" style="margin-top: 10px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.arydshln }}</pre>
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