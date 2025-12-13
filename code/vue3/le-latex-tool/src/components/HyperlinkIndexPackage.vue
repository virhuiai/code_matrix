<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider, ElAlert, ElInput } from 'element-plus'
import { setupCodeEmission } from '../utils/code-emitter'
import { generateCodeFromPackageInfos, type PackageInfo } from '../utils/generic-packages-utils'

const props = defineProps<{
  modelValue: {
    variorefEnabled?: boolean
    imakeidxEnabled?: boolean
    splitidxEnabled?: boolean
    hyperrefEnabled?: boolean
    urlEnabled?: boolean
    pdfTitle?: string
  }
  componentId?: number
  externalTrigger?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { 
    variorefEnabled?: boolean, 
    imakeidxEnabled?: boolean, 
    splitidxEnabled?: boolean, 
    hyperrefEnabled?: boolean,
    urlEnabled?: boolean,
    pdfTitle?: string
  }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const isDialogOpen = ref(false)

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

const generateHyperrefTemplate = (pdfTitle: string) => {
  // 如果用户输入了PDF标题，则使用用户输入的标题，否则使用默认的\pdffilename命令
  const titleValue = pdfTitle ? pdfTitle : "标题标题标题";
  return `% 链接设置
\\usepackage[CJKbookmarks,bookmarksnumbered,bookmarksopen,
            pdftitle={${titleValue}},pdfauthor=virhuiai,
            colorlinks=true, pdfstartview=FitH,citecolor=blue,linktocpage,
            linkcolor=blue,urlcolor=blue,hyperindex=true]{hyperref}`;
};

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
    urlEnabled: props.modelValue.urlEnabled ?? true,
    pdfTitle: props.modelValue.pdfTitle ?? ""
  })
})

const imakeidxEnabled = computed({
  get: () => props.modelValue.imakeidxEnabled ?? false,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: props.modelValue.variorefEnabled ?? true,
    imakeidxEnabled: value,
    splitidxEnabled: false, // 互斥选项
    hyperrefEnabled: props.modelValue.hyperrefEnabled ?? true,
    urlEnabled: props.modelValue.urlEnabled ?? true,
    pdfTitle: props.modelValue.pdfTitle ?? ""
  })
})

const splitidxEnabled = computed({
  get: () => props.modelValue.splitidxEnabled ?? false,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: props.modelValue.variorefEnabled ?? true,
    imakeidxEnabled: false, // 互斥选项
    splitidxEnabled: value,
    hyperrefEnabled: props.modelValue.hyperrefEnabled ?? true,
    urlEnabled: props.modelValue.urlEnabled ?? true,
    pdfTitle: props.modelValue.pdfTitle ?? ""
  })
})

const hyperrefEnabled = computed({
  get: () => props.modelValue.hyperrefEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: props.modelValue.variorefEnabled ?? true,
    imakeidxEnabled: props.modelValue.imakeidxEnabled ?? false,
    splitidxEnabled: props.modelValue.splitidxEnabled ?? false,
    hyperrefEnabled: value,
    urlEnabled: props.modelValue.urlEnabled ?? true,
    pdfTitle: props.modelValue.pdfTitle ?? ""
  })
})

const urlEnabled = computed({
  get: () => props.modelValue.urlEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: props.modelValue.variorefEnabled ?? true,
    imakeidxEnabled: props.modelValue.imakeidxEnabled ?? false,
    splitidxEnabled: props.modelValue.splitidxEnabled ?? false,
    hyperrefEnabled: props.modelValue.hyperrefEnabled ?? true,
    urlEnabled: value,
    pdfTitle: props.modelValue.pdfTitle ?? ""
  })
})

// PDF标题输入值
const pdfTitle = computed({
  get: () => props.modelValue.pdfTitle ?? "",
  set: (value) => emit('update:modelValue', { 
    variorefEnabled: props.modelValue.variorefEnabled ?? true,
    imakeidxEnabled: props.modelValue.imakeidxEnabled ?? false,
    splitidxEnabled: props.modelValue.splitidxEnabled ?? false,
    hyperrefEnabled: props.modelValue.hyperrefEnabled ?? true,
    urlEnabled: props.modelValue.urlEnabled ?? true,
    pdfTitle: value
  })
})

// 计算属性：生成 LaTeX 代码（使用通用宏包工具）
const latexCode = computed(() => {
  const infos: PackageInfo[] = []

  if (variorefEnabled.value) {
    infos.push({ package: 'varioref' })
  }

  if (imakeidxEnabled.value) {
    infos.push({ 
      package: 'imakeidx', 
      options: ['noautomatic'],
      afterLines: [
        '\\makeindex[name=aut,title=人名索引,intoc]',
        '\\makeindex[name=loc,title=地名索引,intoc]',
        '\\makeindex[name=conc,title=概念索引,intoc]',
        '% 正文里使用',
        '%\\index[aut]{爱因斯坦}',
        '%\\index[loc]{北京}',
        '%\\index[conc]{相对论}',
        '% 打印索引（顺序随意）',
        '%\\printindex[aut]   % 标题自动用上面 title= 设置的',
        '%\\printindex[loc]',
        '%\\printindex[conc]'
      ]
    })
  }

  if (splitidxEnabled.value) {
    infos.push({ 
      package: 'splitidx', 
      options: ['splitindex'],
      afterLines: [
        '\\newindex{aut}{idxa}   % 人名索引',
        '\\newindex{loc}{idxb}   % 地名索引',
        '% 正文里使用',
        '%\\sindex[aut]{牛顿}',
        '%\\sindex[loc]{伦敦}',
        '% 打印',
        '%\\printsplitindex[aut]{人名索引}',
        '%\\printsplitindex[loc]{地名索引}'
      ]
    })
  }

  if (hyperrefEnabled.value) {
    const titleValue = pdfTitle.value ? `pdftitle={${pdfTitle.value}}` : 'pdftitle={标题标题标题}'
    infos.push({
      package: 'hyperref',
      options: [
        'CJKbookmarks',
        'bookmarksnumbered',
        'bookmarksopen',
        titleValue,
        'pdfauthor=virhuiai',
        'colorlinks=true',
        'pdfstartview=FitH',
        'citecolor=blue',
        'linktocpage',
        'linkcolor=blue',
        'urlcolor=blue',
        'hyperindex=true'
      ]
    })
  }

  if (urlEnabled.value) {
    infos.push({ 
      package: 'url',
      afterLines: ['\\urlstyle{same}']
    })
  }

  return generateCodeFromPackageInfos(infos)
})

if (props.modelValue.variorefEnabled === undefined || 
    props.modelValue.imakeidxEnabled === undefined || 
    props.modelValue.splitidxEnabled === undefined || 
    props.modelValue.hyperrefEnabled === undefined ||
    props.modelValue.urlEnabled === undefined ||
    props.modelValue.pdfTitle === undefined) {
  emit('update:modelValue', { 
    variorefEnabled: true,
    imakeidxEnabled: false,
    splitidxEnabled: false,
    hyperrefEnabled: true,
    urlEnabled: true,
    pdfTitle: ""
  })
}

setupCodeEmission(latexCode, emit, props.componentId, 'HyperlinkIndexPackage')

// 打开弹窗
const openDialog = () => {
  isDialogOpen.value = true
}

// 关闭弹窗
const closeDialog = () => {
  isDialogOpen.value = false
}

defineExpose({
  openDialog,
  closeDialog
})
</script>

<template>
  <div class="package-options-dialog">

    <!-- 弹窗 -->
    <el-dialog
      v-model="isDialogOpen"
      title="链接和索引设置"
      fullscreen
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <strong>链接和索引设置</strong>
              <p>设置文档中的链接和索引功能，包括超链接、参考文献链接以及各种索引</p>

              <div>
                <el-checkbox 
                  :model-value="variorefEnabled" 
                  @update:model-value="(val: boolean | string | number) => variorefEnabled = Boolean(val)"
                  label="启用 varioref 宏包（用于智能交叉引用）" 
                />
                <div v-if="variorefEnabled">
                  <pre class="code-preview-content">{{ variorefTemplate }}</pre>
                </div>
              </div>

              <el-divider />

              <div>
                <el-alert
                  title="索引宏包选择说明"
                  description="imakeidx 和 splitidx 是 multind 的现代替代品，提供了更好的功能和维护性。请注意这两个选项是互斥的，只能同时启用其中一个。"
                  type="info"
                  show-icon
                />

                <el-checkbox 
                  :model-value="imakeidxEnabled" 
                  @update:model-value="(val: boolean | string | number) => imakeidxEnabled = Boolean(val)"
                  label="启用 imakeidx 宏包（最简单、最推荐）" 
                />
                <div v-if="imakeidxEnabled">
                  <pre class="code-preview-content">{{ imakeidxTemplate }}</pre>
                </div>

                <el-checkbox 
                  :model-value="splitidxEnabled" 
                  @update:model-value="(val: boolean | string | number) => splitidxEnabled = Boolean(val)"
                  label="启用 splitidx 宏包（功能更强）" 
                />
                <div v-if="splitidxEnabled">
                  <pre class="code-preview-content">{{ splitidxTemplate }}</pre>
                </div>
              </div>

              <el-divider />

              <div>
                <el-checkbox 
                  :model-value="hyperrefEnabled" 
                  @update:model-value="(val: boolean | string | number) => hyperrefEnabled = Boolean(val)"
                  label="启用 hyperref 宏包（用于超链接）" 
                />
                <div v-if="hyperrefEnabled">
                  <div>
                    <label>PDF 标题：</label>
                    <el-input 
                      :model-value="pdfTitle" 
                      @input="(val) => pdfTitle = val"
                      placeholder="请输入PDF标题，留空则使用默认文件名"
                      size="small"
                    />
                  </div>
                  <pre class="code-preview-content">{{ generateHyperrefTemplate(pdfTitle) }}</pre>
                </div>
              </div>

              <el-divider />

              <div>
                <el-checkbox 
                  :model-value="urlEnabled" 
                  @update:model-value="(val: boolean | string | number) => urlEnabled = Boolean(val)"
                  label="启用 url 宏包（用于URL样式设置）" 
                />
                <div v-if="urlEnabled">
                  <pre class="code-preview-content">{{ urlTemplate }}</pre>
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
