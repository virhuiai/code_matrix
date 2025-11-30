<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton } from 'element-plus'

const props = defineProps<{
  modelValue: {
    enabled: boolean
  }
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { enabled: boolean }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 包配置数据
const packageConfig = {
  // 默认启用插图和颜色设置
  defaultEnabled: true,
  
  // LaTeX 代码模板
  latexTemplate: `% 插图和颜色设置
%%%%%% grffile package extends the file name processing 
%%%%%% of package graphics to support a larger range of file names.
\\usepackage{graphicx,grffile}
%%%%%% set default figure placement to htbp
\\makeatletter
\\def\\fps@figure{htbp}
\\makeatother
%%%%%% 插图路径命令
%\\graphicspath{{}}

%%%%%% 浮动体宏包
\\usepackage{floatrow}
%%%%%% 旋转宏包
\\usepackage{rotating}

%\\includepdf[pages={3,5}]{..../13-1.pdf}；
%可使用合并命令\\includepdfmerge将多页外部文件自动缩小合并后插入一页之中。
\\usepackage{pdfpages}

\\usepackage{picinpar}
%\\begin{window}[行数，位置，{绕排对象}，{标题}] 绕排文本
%\\end{window}

%\\begin{figwindow}[行数，位置，{绕排对象}，{标题}] 绕排文本
%\\end{figwindow}

%\\begin{tabwindow}[行数，位置，{绕排对象}，{标题}] 绕排文本
%\\end{tabwindow}

%只是后两种可以在绕排对象的标题前自动添加标 题标志和分隔符号。

%右侧 imgRight
%\\begin{window}[0,r,{\\includegraphics[width=0.5\\textwidth]{图}},{}]
%
%\\end{window}

% \\usepackage[dvipsnames]{xcolor}
%-----------------------------------------------------------定义颜色---------------
\\usepackage{pgf-umlcd}`
}

// 计算属性：控制启用状态
const isEnabled = computed({
  get: () => props.modelValue.enabled,
  set: (value) => emit('update:modelValue', { enabled: value })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  return isEnabled.value ? packageConfig.latexTemplate : ''
})

// 监听代码变化
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时触发代码变更事件
onMounted(() => {
  emit('codeChange', computedLatexCode.value)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">插图和颜色</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="插图和颜色设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>插图和颜色设置</strong>
          <p>设置文档中的插图、浮动体、旋转和颜色相关功能</p>
          
          <el-checkbox v-model="isEnabled" label="启用插图和颜色设置" />
          
          <div v-if="isEnabled" style="margin-top: 20px;">
            <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace;">{{ packageConfig.latexTemplate }}</pre>
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