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
  // 默认启用表格设置
  defaultEnabled: true,
  
  // LaTeX 代码模板
  latexTemplate: `% 表格设置
%%%%%%    可用于排版表格的环境很多，但在一篇论文中不宜使用多种表格环境，%
%%%%%%    以免在调整与标题或上文间距时相互影响。

% 1. 表格大都采用 longtable 环境编制，( 另一个跨页的是 \\usepackage{supertabular})
\\usepackage{longtable}
% 2. 表格线使用 booktabs 宏包提供的三种画线命令绘制;
\\usepackage{booktabs}
% 这两种宏包分别提供有调整表格与上下文间距、表格与标题间距和表格左右位置的命令;
% 3. 并且调用 tabularx 宏包，用以增强 longtable 环境中列格式的功能。
\\usepackage{tabularx}
\\usepackage{tabulary}%总宽可设、列宽自动确定 \\begin{tabulary}{宽度}{列格式} \\end{tabulary} % 列格式有 L、C、R 和 J 
\\usepackage{ltablex}%扩展了 tabularx 环境功能，能自动分页，可用于排版跨页长表格。
% 在14color中。。。[table] 
\\usepackage{colortbl}
\\usepackage{bigstrut}
%%%%%% 跨行表格宏包multirow
\\usepackage{multirow}

%%%%%%  Frank Mittelbach 等人编写的 array 宏包
%%%%%%  对 3 个表格环境(tabular tabular* array)的制表功能做了重大改进和扩展
%%%%%% 主要是增加和增强了列格式功能，
%%%%%% 还增加了许多表格参数的调整功能。
\\usepackage{array}
%%%%%%    dcolumn小数点对齐宏包，
%%%%%%    它专为tabular和array表格环境定义了
%%%%%%    一个用于对齐小数点或逗号等标点符号的列格式选项
\\usepackage{dcolumn}
%%%%%% 彩色表格宏包colortbl
%在调用 xcolor 宏包时，添加 table 选项就可以加载 colortbl
\\usepackage{colortbl}
%%%%%%    对角线宏包slashbox
\\usepackage{slashbox}

%%%%%% 虚线表格宏包arydshln
%如果已经调用或是还需要调用与表格有关的 array、colortab、colortbl 或 longtable 宏包，
%应将 arydshln 的调用命令放在这些宏包的调用命令之后，否则可能会造成莫名的错误
\\usepackage{arydshln}

%////////bigstrut


%\\begin{longtable*}%
%@{\\extracolsep{\\fill}}*{}{>{\\tt}l}@{}
%    %\\caption{}\\label{tbl:}%
%    \\\\\\toprule[1pt]
%    %\\multicolumn{1}{c}{计数器名} &
%    %\\multicolumn{1}{c}{用途} \\\\\\midrule
%    %chapter        & 章序号计数器\\\\
%    %section        & 节序号计数器\\\\
%    %subsubsection  & 小小节序号计数器\\\\
%    %\\bottomrule[1pt]
%\\end{longtable*}`
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
          
          <el-checkbox v-model="isEnabled" label="启用表格设置" />
          
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