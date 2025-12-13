<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider, ElAlert } from 'element-plus'
import { setupCodeEmission } from '../utils/code-emitter'
import { generateCodeFromPackageInfos, type PackageInfo } from '../utils/generic-packages-utils'

const props = defineProps<{
  modelValue: {
    enabled?: boolean
    longtableEnabled?: boolean
    booktabsEnabled?: boolean
    tabularxEnabled?: boolean
    tabularyEnabled?: boolean
    ltablexEnabled?: boolean
    colortblEnabled?: boolean
    multirowEnabled?: boolean
    arrayEnabled?: boolean
    dcolumnEnabled?: boolean
    arydshlnEnabled?: boolean
    makecellEnabled?: boolean
    hhlineEnabled?: boolean
  }
  componentId?: number
  externalTrigger?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { 
    enabled?: boolean
    longtableEnabled?: boolean
    booktabsEnabled?: boolean
    tabularxEnabled?: boolean
    tabularyEnabled?: boolean
    ltablexEnabled?: boolean
    colortblEnabled?: boolean
    multirowEnabled?: boolean
    arrayEnabled?: boolean
    dcolumnEnabled?: boolean
    arydshlnEnabled?: boolean
    makecellEnabled?: boolean
    hhlineEnabled?: boolean
  }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const isDialogOpen = ref(false)

// 选项说明与示例（JS 配置化）
const optionDocs: Record<string, { desc: string; example: string }> = {
  longtable: {
    desc: '跨页长表格环境',
    example: '\\begin{longtable}{ccc}\n...\\end{longtable}'
  },
  booktabs: {
    desc: '专业表格线，提供\\toprule/\\midrule/\\bottomrule',
    example: '\\usepackage{booktabs}\n\\begin{tabular}{cc}\n\\toprule\nA & B \\\\ \\midrule\n1 & 2 \\\\ \\bottomrule\n\\end{tabular}'
  },
  tabularx: {
    desc: '增强列格式，X 列可自动伸缩',
    example: '\\begin{tabularx}{\\textwidth}{|X|X|}...\\end{tabularx}'
  },
  tabulary: {
    desc: '自动调整列宽以适配内容',
    example: '\\begin{tabulary}{\\textwidth}{LL}...\\end{tabulary}'
  },
  ltablex: {
    desc: '结合 longtable 与 tabularx，实现可分页的伸缩列',
    example: '\\begin{ltablex}{\\textwidth}{|X|X|}...\\end{ltablex}'
  },
  colortbl: {
    desc: '为表格提供背景色与彩色线条',
    example: '\\rowcolor{lightgray} A & B'
  },
  multirow: {
    desc: '跨行合并单元格',
    example: '\\multirow{2}{*}{A} & B \\\\ \\cline{2-2} & C'
  },
  array: {
    desc: '增强列类型与对齐控制',
    example: '\\newcolumntype{Y}{>{\\centering\\arraybackslash}X}'
  },
  dcolumn: {
    desc: '按小数点对齐数字列',
    example: '\\begin{tabular}{|D{.}{.}{2}|} 3.14 \\ 2.7 \\end{tabular}'
  },
  makecell: {
    desc: '提供表头斜线与多行单元格排版',
    example: '\\makecell{行一\\\\行二}'
  },
  hhline: {
    desc: '更灵活的横线绘制（配合 multirow 可做斜线表头）',
    example: '\\begin{tabular}{|c|c|}\\hhline{|==|} A & B \\ \\end{tabular}'
  },
  arydshln: {
    desc: '绘制虚线表格线',
    example: '\\begin{tabular}{:c:|:c:} A & B \\ \\end{tabular}'
  }
};
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
    arydshlnEnabled: props.modelValue.arydshlnEnabled ?? true,
    makecellEnabled: props.modelValue.makecellEnabled ?? false,
    hhlineEnabled: props.modelValue.hhlineEnabled ?? false
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

const arydshlnEnabled = computed({
  get: () => props.modelValue.arydshlnEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    arydshlnEnabled: value
  })
})

const makecellEnabled = computed({
  get: () => props.modelValue.makecellEnabled ?? false,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    makecellEnabled: value
  })
})

const hhlineEnabled = computed({
  get: () => props.modelValue.hhlineEnabled ?? false,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    hhlineEnabled: value
  })
})

// 计算属性：生成 LaTeX 代码（使用通用宏包工具）
const latexCode = computed(() => {
  if (!mainEnabled.value) return ''

  const infos: PackageInfo[] = []

  // 基础与增强表格宏包
  if (longtableEnabled.value) infos.push({ package: 'longtable' })
  if (booktabsEnabled.value) infos.push({ package: 'booktabs' })
  if (tabularxEnabled.value) infos.push({ package: 'tabularx' })
  if (tabularyEnabled.value) infos.push({ package: 'tabulary' })
  if (ltablexEnabled.value) infos.push({ package: 'ltablex' })
  if (colortblEnabled.value) infos.push({ package: 'colortbl' })
  if (multirowEnabled.value) infos.push({ package: 'multirow' })
  if (arrayEnabled.value) infos.push({ package: 'array' })
  if (dcolumnEnabled.value) infos.push({ package: 'dcolumn' })

  // 斜线表头相关
  if (makecellEnabled.value) {
    infos.push({ package: 'makecell', afterLines: ['\\renewcommand\\theadfont{\\bfseries}'] })
  }
  if (hhlineEnabled.value) {
    infos.push({ package: 'hhline' })
  }

  // 建议 arydshln 放在相关宏包之后
  if (arydshlnEnabled.value) infos.push({ package: 'arydshln' })

  return generateCodeFromPackageInfos(infos)
})

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
    arydshlnEnabled: true,
    makecellEnabled: false,
    hhlineEnabled: false
  })
}

setupCodeEmission(latexCode, emit, props.componentId, 'TablePackage')

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
  hideDialog,
  optionDocs
})
</script>

<template>
  <div>
    <el-button v-if="!props.externalTrigger" type="primary" size="small" round @click="showDialog">表格设置</el-button>
    <el-dialog v-model="isDialogOpen" title="表格设置" :before-close="hideDialog">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <strong>表格设置</strong>
            <el-alert title="说明" description="以下宏包可独立启用或禁用。斜线表头可选 makecell 或 hhline + multirow。" type="info" show-icon />
            <el-divider />
            <el-checkbox :model-value="mainEnabled" @update:model-value="(val: boolean | string | number) => mainEnabled = Boolean(val)" label="启用表格设置" />
            <template v-if="mainEnabled">
              <el-divider />
              <strong>基础表格宏包</strong>
              <div>
                <el-checkbox :model-value="longtableEnabled" @update:model-value="(val: boolean | string | number) => longtableEnabled = Boolean(val)" label="longtable（跨页长表格）" />
                <el-checkbox :model-value="booktabsEnabled" @update:model-value="(val: boolean | string | number) => booktabsEnabled = Boolean(val)" label="booktabs（专业表格线）" />
                <el-checkbox :model-value="tabularxEnabled" @update:model-value="(val: boolean | string | number) => tabularxEnabled = Boolean(val)" label="tabularx（增强列格式）" />
                <el-checkbox :model-value="tabularyEnabled" @update:model-value="(val: boolean | string | number) => tabularyEnabled = Boolean(val)" label="tabulary（自动调整列宽）" />
                <el-checkbox :model-value="ltablexEnabled" @update:model-value="(val: boolean | string | number) => ltablexEnabled = Boolean(val)" label="ltablex（分页的 tabularx）" />
              </div>
              <el-divider />
              <strong>表格增强宏包</strong>
              <div>
                <el-checkbox :model-value="colortblEnabled" @update:model-value="(val: boolean | string | number) => colortblEnabled = Boolean(val)" label="colortbl（彩色表格）" />
                <el-checkbox :model-value="multirowEnabled" @update:model-value="(val: boolean | string | number) => multirowEnabled = Boolean(val)" label="multirow（跨行）" />
                <el-checkbox :model-value="arrayEnabled" @update:model-value="(val: boolean | string | number) => arrayEnabled = Boolean(val)" label="array（增强表格功能）" />
                <el-checkbox :model-value="dcolumnEnabled" @update:model-value="(val: boolean | string | number) => dcolumnEnabled = Boolean(val)" label="dcolumn（小数点对齐）" />
                <el-divider />
                <strong>斜线表头选项</strong>
                <el-checkbox :model-value="makecellEnabled" @update:model-value="(val: boolean | string | number) => makecellEnabled = Boolean(val)" label="makecell（常用方案）" />
                <el-checkbox :model-value="hhlineEnabled" @update:model-value="(val: boolean | string | number) => hhlineEnabled = Boolean(val)" label="hhline + multirow（原生）" />
                <el-checkbox :model-value="arydshlnEnabled" @update:model-value="(val: boolean | string | number) => arydshlnEnabled = Boolean(val)" label="arydshln（虚线表格）" />
              </div>
            </template>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <strong>代码预览</strong>
            <el-divider />
            <el-scrollbar max-height="60vh">
              <pre>{{ latexCode }}</pre>
            </el-scrollbar>
          </el-card>
        </el-col>
      </el-row>
      <template #footer></template>
    </el-dialog>
  </div>
</template>
