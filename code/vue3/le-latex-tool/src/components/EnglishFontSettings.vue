<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { setupCodeEmission } from '../utils/code-emitter'

const props = defineProps<{
  modelValue: {
    enabled: boolean
  }
  componentId?: number
  externalTrigger?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { enabled: boolean }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 字体配置数据
const fontConfig = {
  latexTemplate: `%
%  cd /usr/local/texlive/2022/texmf-dist/fonts/opentype/public/cm-unicode/
%  cd /Volumes/THAWSPACE/Soft.Ok/texlive/2024/texmf-dist/fonts/opentype/public/cm-unicode/
%  \\setmainfont{ } % 论文中西文部分默认使用的字体。
% %通常到 Word 2003 为止，这里的默认字体都会是 Times New Roman。Linux 下也有同名字体。
\\setmainfont{cmun}[
  Extension       = .otf,
  UprightFont     = *rm,
  ItalicFont      = *ti,
  SlantedFont     = *sl,
  BoldFont        = *bx,
  BoldItalicFont  = *bi,
  BoldSlantedFont = *bl,
]
%\\setsansfont{ } %是西文默认无衬线字体。一般可能出现在大标题等显眼的位置。
\\setsansfont{cmun}[
  Extension      = .otf,
  UprightFont    = *ss,
  ItalicFont     = *si,
  BoldFont       = *sx,
  BoldItalicFont = *so,
]
%\\setmonofont{ }%是西文默认的等宽字体。一般用于排版程序代码。
%Courier 或者 Courier New 是常见的 Word 选项。Linux 下一般会有 Courier，但很少能看见 Courier New。
\\setmonofont{cmun}[
  Extension      = .otf,
  UprightFont    = *btl,% light version
  ItalicFont     = *bto,%  light version
  BoldFont       = *tb,
  BoldItalicFont = *tx,
]
% 消除 \\t 命令的字体 warning
\\AtBeginDocument{%}
  \\renewcommand*\\t[1]{{\\edef\\restore@font{\\the\\font}\\usefont{OML}{cmm}{m}{it}\\accent"7F\\restore@font#1}}
}`
}

// 计算属性：控制启用状态
const isEnabled = computed({
  get: () => props.modelValue.enabled,
  set: (value) => emit('update:modelValue', { enabled: value })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  return isEnabled.value ? fontConfig.latexTemplate : ''
})

setupCodeEmission(computedLatexCode, emit, props.componentId, 'EnglishFontSettings')

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

    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="英文字体设置"
      fullscreen
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <strong>英文字体设置</strong>
              <p>设置英文字体配置</p>
              <el-checkbox v-model="isEnabled" label="启用cm-unicode" />
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
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="closeDialog">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
