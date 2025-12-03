<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider, ElAlert } from 'element-plus'
import { setupCodeEmission } from '../utils/code-emitter'

const props = defineProps<{
  modelValue: {
    enabled?: boolean
    graphicxEnabled?: boolean
    floatrowEnabled?: boolean
    rotatingEnabled?: boolean
    pdfpagesEnabled?: boolean
    picinparEnabled?: boolean
    xcolorEnabled?: boolean
    pgfUmlcdEnabled?: boolean
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { 
    enabled?: boolean
    graphicxEnabled?: boolean
    floatrowEnabled?: boolean
    rotatingEnabled?: boolean
    pdfpagesEnabled?: boolean
    picinparEnabled?: boolean
    xcolorEnabled?: boolean
    pgfUmlcdEnabled?: boolean
  }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 各宏包的LaTeX代码模板
const packageTemplates = {
  graphicx: `% grffile package extends the file name processing 
% of package graphics to support a larger range of file names.
\\usepackage{graphicx,grffile}
% set default figure placement to htbp
\\makeatletter
\\def\\fps@figure{htbp}
\\makeatother
% 插图路径命令
%\\graphicspath{{}}`,
  floatrow: `% 浮动体宏包
\\usepackage{floatrow}`,
  rotating: `% 旋转宏包
\\usepackage{rotating}`,
  pdfpages: `%\\includepdf[pages={3,5}]{..../13-1.pdf}；
%可使用合并命令\\includepdfmerge将多页外部文件自动缩小合并后插入一页之中。
\\usepackage{pdfpages}`,
  picinpar: `%\\begin{window}[行数，位置，{绕排对象}，{标题}] 绕排文本
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
\\usepackage{picinpar}`,
  xcolor: `\\usepackage[dvipsnames]{xcolor}
%-----------------------------------------------------------定义颜色---------------`,
  pgfUmlcd: '\\usepackage{pgf-umlcd}'
}

// 计算属性：控制启用状态
const mainEnabled = computed({
  get: () => props.modelValue.enabled,
  set: (value) => emit('update:modelValue', { 
    enabled: value,
    graphicxEnabled: props.modelValue.graphicxEnabled ?? true,
    floatrowEnabled: props.modelValue.floatrowEnabled ?? true,
    rotatingEnabled: props.modelValue.rotatingEnabled ?? true,
    pdfpagesEnabled: props.modelValue.pdfpagesEnabled ?? true,
    picinparEnabled: props.modelValue.picinparEnabled ?? true,
    xcolorEnabled: props.modelValue.xcolorEnabled ?? true,
    pgfUmlcdEnabled: props.modelValue.pgfUmlcdEnabled ?? true
  })
})

const graphicxEnabled = computed({
  get: () => props.modelValue.graphicxEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    graphicxEnabled: value
  })
})

const floatrowEnabled = computed({
  get: () => props.modelValue.floatrowEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    floatrowEnabled: value
  })
})

const rotatingEnabled = computed({
  get: () => props.modelValue.rotatingEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    rotatingEnabled: value
  })
})

const pdfpagesEnabled = computed({
  get: () => props.modelValue.pdfpagesEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    pdfpagesEnabled: value
  })
})

const picinparEnabled = computed({
  get: () => props.modelValue.picinparEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    picinparEnabled: value
  })
})

const xcolorEnabled = computed({
  get: () => props.modelValue.xcolorEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    xcolorEnabled: value
  })
})

const pgfUmlcdEnabled = computed({
  get: () => props.modelValue.pgfUmlcdEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    ...props.modelValue,
    pgfUmlcdEnabled: value
  })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  if (!mainEnabled.value) return ''
  
  const codes = []
  
  // 添加启用的宏包
  if (graphicxEnabled.value) codes.push(packageTemplates.graphicx)
  if (floatrowEnabled.value) codes.push(packageTemplates.floatrow)
  if (rotatingEnabled.value) codes.push(packageTemplates.rotating)
  if (pdfpagesEnabled.value) codes.push(packageTemplates.pdfpages)
  if (picinparEnabled.value) codes.push(packageTemplates.picinpar)
  if (xcolorEnabled.value) codes.push(packageTemplates.xcolor)
  if (pgfUmlcdEnabled.value) codes.push(packageTemplates.pgfUmlcd)
  
  return codes.join('\n\n')
})

if (Object.values(props.modelValue).every(v => v === undefined)) {
  emit('update:modelValue', { 
    enabled: true,
    graphicxEnabled: true,
    floatrowEnabled: true,
    rotatingEnabled: true,
    pdfpagesEnabled: true,
    picinparEnabled: true,
    xcolorEnabled: true,
    pgfUmlcdEnabled: true
  })
}

setupCodeEmission(computedLatexCode, emit, props.componentId, 'FigureColorPackage')

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
          
          <el-checkbox 
            :model-value="mainEnabled" 
            @update:model-value="(val: boolean | string | number) => mainEnabled = Boolean(val)"
            label="启用插图和颜色设置" 
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
              <strong>图形和插图宏包</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="graphicxEnabled" 
                  @update:model-value="(val: boolean | string | number) => graphicxEnabled = Boolean(val)"
                  label="graphicx/grffile 宏包（提供图像插入功能）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <div v-if="graphicxEnabled" style="margin-top: 10px;">
                  <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.graphicx }}</pre>
                </div>
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>浮动体和旋转宏包</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="floatrowEnabled" 
                  @update:model-value="(val: boolean | string | number) => floatrowEnabled = Boolean(val)"
                  label="floatrow 宏包（浮动体环境）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <el-checkbox 
                  :model-value="rotatingEnabled" 
                  @update:model-value="(val: boolean | string | number) => rotatingEnabled = Boolean(val)"
                  label="rotating 宏包（旋转对象）" 
                  style="display: block; margin-bottom: 8px;"
                />
              </div>
              
              <div v-if="floatrowEnabled" style="margin-top: 10px; margin-left: 20px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.floatrow }}</pre>
              </div>
              
              <div v-if="rotatingEnabled" style="margin-top: 10px; margin-left: 20px;">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.rotating }}</pre>
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>PDF 页面处理</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="pdfpagesEnabled" 
                  @update:model-value="(val: boolean | string | number) => pdfpagesEnabled = Boolean(val)"
                  label="pdfpages 宏包（插入 PDF 页面）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <div v-if="pdfpagesEnabled" style="margin-top: 10px;">
                  <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.pdfpages }}</pre>
                </div>
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>绕排文本宏包</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="picinparEnabled" 
                  @update:model-value="(val: boolean | string | number) => picinparEnabled = Boolean(val)"
                  label="picinpar 宏包（图片绕排文本）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <div v-if="picinparEnabled" style="margin-top: 10px;">
                  <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.picinpar }}</pre>
                </div>
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>颜色宏包</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="xcolorEnabled" 
                  @update:model-value="(val: boolean | string | number) => xcolorEnabled = Boolean(val)"
                  label="xcolor 宏包（颜色支持）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <div v-if="xcolorEnabled" style="margin-top: 10px;">
                  <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.xcolor }}</pre>
                </div>
              </div>
            </div>
            
            <el-divider />
            
            <div style="margin-top: 20px;">
              <strong>UML 图宏包</strong>
              <div style="margin-top: 10px; margin-left: 20px;">
                <el-checkbox 
                  :model-value="pgfUmlcdEnabled" 
                  @update:model-value="(val: boolean | string | number) => pgfUmlcdEnabled = Boolean(val)"
                  label="pgf-umlcd 宏包（绘制 UML 类图）" 
                  style="display: block; margin-bottom: 8px;"
                />
                
                <div v-if="pgfUmlcdEnabled" style="margin-top: 10px;">
                  <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ packageTemplates.pgfUmlcd }}</pre>
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
