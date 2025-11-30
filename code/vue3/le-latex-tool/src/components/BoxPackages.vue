<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider } from 'element-plus'

const props = defineProps<{
  modelValue: {
    fancybox: boolean
    boxedminipage: boolean
    tikz: boolean
    tcolorbox: {
      enabled: boolean
      raster: boolean
      listings: boolean
      theorems: boolean
      skins: boolean
      xparse: boolean
      breakable: boolean
    }
    awesomebox: boolean
    mdframed: boolean
    framed: boolean
    changepage: boolean
  }
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 包配置数据 - 所有宏包默认选中
const packages = ref({
  fancybox: props.modelValue.fancybox !== undefined ? props.modelValue.fancybox : true,
  boxedminipage: props.modelValue.boxedminipage !== undefined ? props.modelValue.boxedminipage : true,
  tikz: props.modelValue.tikz !== undefined ? props.modelValue.tikz : true,
  tcolorbox: {
    enabled: props.modelValue.tcolorbox?.enabled !== undefined ? props.modelValue.tcolorbox.enabled : true,
    raster: props.modelValue.tcolorbox?.raster !== undefined ? props.modelValue.tcolorbox.raster : true,
    listings: props.modelValue.tcolorbox?.listings !== undefined ? props.modelValue.tcolorbox.listings : true,
    theorems: props.modelValue.tcolorbox?.theorems !== undefined ? props.modelValue.tcolorbox.theorems : true,
    skins: props.modelValue.tcolorbox?.skins !== undefined ? props.modelValue.tcolorbox.skins : true,
    xparse: props.modelValue.tcolorbox?.xparse !== undefined ? props.modelValue.tcolorbox.xparse : true,
    breakable: props.modelValue.tcolorbox?.breakable !== undefined ? props.modelValue.tcolorbox.breakable : true,
  },
  awesomebox: props.modelValue.awesomebox !== undefined ? props.modelValue.awesomebox : true,
  mdframed: props.modelValue.mdframed !== undefined ? props.modelValue.mdframed : true,
  framed: props.modelValue.framed !== undefined ? props.modelValue.framed : true,
  changepage: props.modelValue.changepage !== undefined ? props.modelValue.changepage : true
})

// LaTeX 代码模板 - 只生成引入宏包的代码
const latexTemplates = {
  fancybox: '\\usepackage{fancybox}',
  boxedminipage: '\\usepackage{boxedminipage}',
  tikz: '\\usepackage{tikz}',
  tcolorbox: (options: any) => {
    let code = '\\usepackage{tcolorbox}'
    const libraries = []
    if (options.raster) libraries.push('raster')
    if (options.listings) libraries.push('listings')
    if (options.theorems) libraries.push('theorems')
    if (options.skins) libraries.push('skins')
    if (options.xparse) libraries.push('xparse')
    if (options.breakable) libraries.push('breakable')
    
    if (libraries.length > 0) {
      code += '\n\\tcbuselibrary{' + libraries.join(',') + '}'
    }
    return code
  },
  awesomebox: '\\usepackage{awesomebox}',
  mdframed: '\\usepackage{mdframed}',
  framed: '\\usepackage{framed}',
  changepage: '\\usepackage{changepage}'
}

// 计算属性：生成 LaTeX 代码 - 只生成引入宏包的代码
const computedLatexCode = computed(() => {
  const codes = []
  
  // 添加启用的包
  if (packages.value.fancybox) {
    codes.push(latexTemplates.fancybox)
  }
  
  if (packages.value.boxedminipage) {
    codes.push(latexTemplates.boxedminipage)
  }
  
  if (packages.value.tikz) {
    codes.push(latexTemplates.tikz)
  }
  
  if (packages.value.tcolorbox.enabled) {
    codes.push(latexTemplates.tcolorbox(packages.value.tcolorbox))
  }
  
  if (packages.value.awesomebox) {
    codes.push(latexTemplates.awesomebox)
  }
  
  if (packages.value.mdframed) {
    codes.push(latexTemplates.mdframed)
  }
  
  if (packages.value.framed) {
    codes.push(latexTemplates.framed)
  }
  
  if (packages.value.changepage) {
    codes.push(latexTemplates.changepage)
  }
  
  return codes.join('\n\n')
})

// 监听包选项变化
const updatePackage = (pkg: string, value: any) => {
  (packages.value as any)[pkg] = value
  emit('update:modelValue', { ...packages.value })
}

// 监听 tcolorbox 子选项变化
const updateTcolorboxRaster = (value: boolean | string | number) => {
  packages.value.tcolorbox.raster = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

const updateTcolorboxListings = (value: boolean | string | number) => {
  packages.value.tcolorbox.listings = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

const updateTcolorboxTheorems = (value: boolean | string | number) => {
  packages.value.tcolorbox.theorems = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

const updateTcolorboxSkins = (value: boolean | string | number) => {
  packages.value.tcolorbox.skins = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

const updateTcolorboxXparse = (value: boolean | string | number) => {
  packages.value.tcolorbox.xparse = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

const updateTcolorboxBreakable = (value: boolean | string | number) => {
  packages.value.tcolorbox.breakable = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

// 监听代码变化
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时触发代码变更事件
onMounted(() => {
  emit('update:modelValue', { ...packages.value })
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">盒子宏包设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="盒子宏包设置"
      width="70%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>盒子宏包设置</strong>
          <p>配置各种盒子宏包及其选项</p>
          
          <el-divider />
          
          <!-- Fancybox -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.fancybox" 
              @change="(val) => updatePackage('fancybox', val)"
              label="fancybox - 盒子宏包，扩展 \fbox 命令"
            />
          </div>
          
          <!-- Boxedminipage -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.boxedminipage" 
              @change="(val) => updatePackage('boxedminipage', val)"
              label="boxedminipage - 盒子环境"
            />
          </div>
          
          <!-- Tikz -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.tikz" 
              @change="(val) => updatePackage('tikz', val)"
              label="tikz - 绘图宏包"
            />
          </div>
          
          <!-- Tcolorbox -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.tcolorbox.enabled" 
              @change="(val) => updatePackage('tcolorbox', {...packages.tcolorbox, enabled: val})"
              label="tcolorbox - 彩色文本框宏包"
            />
            
            <div v-if="packages.tcolorbox.enabled" style="margin-left: 20px; margin-top: 10px;">
              <div>tcolorbox 库:</div>
              <el-checkbox 
                v-model="packages.tcolorbox.raster" 
                @change="updateTcolorboxRaster"
                label="raster"
              />
              <el-checkbox 
                v-model="packages.tcolorbox.listings" 
                @change="updateTcolorboxListings"
                label="listings"
              />
              <el-checkbox 
                v-model="packages.tcolorbox.theorems" 
                @change="updateTcolorboxTheorems"
                label="theorems"
              />
              <el-checkbox 
                v-model="packages.tcolorbox.skins" 
                @change="updateTcolorboxSkins"
                label="skins"
              />
              <el-checkbox 
                v-model="packages.tcolorbox.xparse" 
                @change="updateTcolorboxXparse"
                label="xparse"
              />
              <el-checkbox 
                v-model="packages.tcolorbox.breakable" 
                @change="updateTcolorboxBreakable"
                label="breakable"
              />
            </div>
          </div>
          
          <!-- Awesomebox -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.awesomebox" 
              @change="(val) => updatePackage('awesomebox', val)"
              label="awesomebox - 图标盒子宏包"
            />
          </div>
          
          <!-- Mdframed -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.mdframed" 
              @change="(val) => updatePackage('mdframed', val)"
              label="mdframed - 框架环境宏包"
            />
          </div>
          
          <!-- Framed -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.framed" 
              @change="(val) => updatePackage('framed', val)"
              label="framed - 框架环境宏包"
            />
          </div>
          
          <!-- Changepage -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.changepage" 
              @change="(val) => updatePackage('changepage', val)"
              label="changepage - 页面调整宏包"
            />
          </div>
          
          <el-divider />
          
          <!-- 代码预览 -->
          <div v-if="computedLatexCode" style="margin-top: 20px;">
            <div><strong>生成的 LaTeX 代码:</strong></div>
            <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace; max-height: 300px;">{{ computedLatexCode }}</pre>
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