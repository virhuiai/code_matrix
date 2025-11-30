<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElInput, ElSelect, ElOption, ElRow, ElCol, ElDialog, ElButton } from 'element-plus'

const props = defineProps<{
  modelValue: {
    enabled: boolean
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { enabled: boolean }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 字体配置数据
const fontConfig = {
  // 默认启用英文字体设置
  defaultEnabled: true,
  
  // LaTeX 代码模板
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
]`
+ '\n' + `% 消除 \\t 命令的字体 warning
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

// 监听代码变化
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时触发代码变更事件
onMounted(() => {
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`EnglishFontSettings component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%;margin-top:10px;">英文字体设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="英文字体设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>英文字体设置</strong>
          <p>设置英文字体配置</p>
          
          <el-checkbox v-model="isEnabled" label="启用cm-unicode" />
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
