<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider } from 'element-plus'

const props = defineProps<{
  modelValue: {
    xcolor: {
      enabled: boolean
      dvipsnames: boolean
    }
    cprotect: boolean
    spverbatim: boolean
    fancyvrb: boolean
    fancyvrbEx: boolean
    xparse: boolean
    minted: {
      enabled: boolean
      newfloat: boolean
      cache: boolean
    }
    listings: boolean
    accsupp: boolean
    tcolorbox: {
      enabled: boolean
      listings: boolean
      skins: boolean
      breakable: boolean
      xparse: boolean
    }
  }
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 包配置数据
const packages = ref({
  xcolor: {
    enabled: props.modelValue.xcolor?.enabled !== undefined ? props.modelValue.xcolor.enabled : true,
    dvipsnames: props.modelValue.xcolor?.dvipsnames !== undefined ? props.modelValue.xcolor.dvipsnames : true
  },
  cprotect: props.modelValue.cprotect !== undefined ? props.modelValue.cprotect : true,
  spverbatim: props.modelValue.spverbatim !== undefined ? props.modelValue.spverbatim : true,
  fancyvrb: props.modelValue.fancyvrb !== undefined ? props.modelValue.fancyvrb : true,
  fancyvrbEx: props.modelValue.fancyvrbEx !== undefined ? props.modelValue.fancyvrbEx : true,
  xparse: props.modelValue.xparse !== undefined ? props.modelValue.xparse : true,
  minted: {
    enabled: props.modelValue.minted?.enabled !== undefined ? props.modelValue.minted.enabled : true,
    newfloat: props.modelValue.minted?.newfloat !== undefined ? props.modelValue.minted.newfloat : true,
    cache: props.modelValue.minted?.cache !== undefined ? props.modelValue.minted.cache : false
  },
  listings: props.modelValue.listings !== undefined ? props.modelValue.listings : true,
  accsupp: props.modelValue.accsupp !== undefined ? props.modelValue.accsupp : true,
  tcolorbox: {
    enabled: props.modelValue.tcolorbox?.enabled !== undefined ? props.modelValue.tcolorbox.enabled : true,
    listings: props.modelValue.tcolorbox?.listings !== undefined ? props.modelValue.tcolorbox.listings : true,
    skins: props.modelValue.tcolorbox?.skins !== undefined ? props.modelValue.tcolorbox.skins : true,
    breakable: props.modelValue.tcolorbox?.breakable !== undefined ? props.modelValue.tcolorbox.breakable : true,
    xparse: props.modelValue.tcolorbox?.xparse !== undefined ? props.modelValue.tcolorbox.xparse : true
  }
})

// LaTeX 代码模板
const latexTemplates = {
  xcolor: (options: any) => {
    const opts = []
    if (options.dvipsnames) opts.push('dvipsnames')
    return `\\usepackage${opts.length > 0 ? `[${opts.join(',')}]` : ''}{xcolor}`
  },
  cprotect: '\\usepackage{cprotect}',
  spverbatim: '\\usepackage{spverbatim}',
  fancyvrb: '\\usepackage{fancyvrb}\n\\newsavebox{\\vTmpOne}',
  fancyvrbEx: '\\usepackage{fancyvrb-ex}',
  xparse: '\\usepackage{xparse}',
  minted: (options: any) => {
    const opts = []
    if (options.newfloat) opts.push('newfloat')
    if (options.cache === false) opts.push('cache=false')
    return `\\usepackage${opts.length > 0 ? `[${opts.join(',')}]` : ''}{minted}`
  },
  listings: '\\usepackage{listings}',
  accsupp: '\\usepackage{accsupp}',
  tcolorbox: (options: any) => {
    let code = '\\usepackage{tcolorbox}'
    const libraries = []
    if (options.listings) libraries.push('listings')
    if (options.skins) libraries.push('skins')
    if (options.breakable) libraries.push('breakable')
    if (options.xparse) libraries.push('xparse')
    
    if (libraries.length > 0) {
      code += '\n\\tcbuselibrary{' + libraries.join(',') + '}'
    }
    return code
  }
}

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  const codes = []
  
  if (packages.value.xcolor.enabled) {
    codes.push(latexTemplates.xcolor(packages.value.xcolor))
  }
  
  if (packages.value.cprotect) {
    codes.push(latexTemplates.cprotect)
  }
  
  if (packages.value.spverbatim) {
    codes.push(latexTemplates.spverbatim)
  }
  
  if (packages.value.fancyvrb) {
    codes.push(latexTemplates.fancyvrb)
  }
  
  if (packages.value.fancyvrbEx) {
    codes.push(latexTemplates.fancyvrbEx)
  }
  
  if (packages.value.xparse) {
    codes.push(latexTemplates.xparse)
  }
  
  if (packages.value.minted.enabled) {
    codes.push(latexTemplates.minted(packages.value.minted))
  }
  
  if (packages.value.listings) {
    codes.push(latexTemplates.listings)
  }
  
  if (packages.value.accsupp) {
    codes.push(latexTemplates.accsupp)
  }
  
  if (packages.value.tcolorbox.enabled) {
    codes.push(latexTemplates.tcolorbox(packages.value.tcolorbox))
  }
  
  return codes.join('\n\n')
})

// 监听包选项变化
const updatePackage = (pkg: string, value: any) => {
  (packages.value as any)[pkg] = value
  emit('update:modelValue', { ...packages.value })
}

// 监听 xcolor 子选项变化
const updateXcolorDvipsnames = (value: boolean | string | number) => {
  packages.value.xcolor.dvipsnames = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

// 监听 minted 子选项变化
const updateMintedNewfloat = (value: boolean | string | number) => {
  packages.value.minted.newfloat = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

const updateMintedCache = (value: boolean | string | number) => {
  packages.value.minted.cache = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

// 监听 tcolorbox 子选项变化
const updateTcolorboxListings = (value: boolean | string | number) => {
  packages.value.tcolorbox.listings = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

const updateTcolorboxSkins = (value: boolean | string | number) => {
  packages.value.tcolorbox.skins = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

const updateTcolorboxBreakable = (value: boolean | string | number) => {
  packages.value.tcolorbox.breakable = Boolean(value)
  emit('update:modelValue', { ...packages.value })
}

const updateTcolorboxXparse = (value: boolean | string | number) => {
  packages.value.tcolorbox.xparse = Boolean(value)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">代码抄录宏包设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="代码抄录宏包设置"
      width="70%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>代码抄录宏包设置</strong>
          <p>配置代码抄录相关的宏包及其选项</p>
          
          <el-divider />
          
          <!-- Xcolor -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.xcolor.enabled" 
              @change="(val) => updatePackage('xcolor', { ...packages.xcolor, enabled: Boolean(val) })"
              label="xcolor - 颜色支持宏包"
            />
            
            <div v-if="packages.xcolor.enabled" style="margin-left: 20px; margin-top: 10px;">
              <el-checkbox 
                v-model="packages.xcolor.dvipsnames" 
                @change="updateXcolorDvipsnames"
                label="dvipsnames"
              />
            </div>
          </div>
          
          <!-- Cprotect -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.cprotect" 
              @change="(val) => updatePackage('cprotect', val)"
              label="cprotect - 保护命令宏包"
            />
          </div>
          
          <!-- Spverbatim -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.spverbatim" 
              @change="(val) => updatePackage('spverbatim', val)"
              label="spverbatim - 支持空格的 Verbatim 宏包"
            />
          </div>
          
          <!-- Fancyvrb -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.fancyvrb" 
              @change="(val) => updatePackage('fancyvrb', val)"
              label="fancyvrb - 增强的 Verbatim 宏包"
            />
          </div>
          
          <!-- Fancyvrb-ex -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.fancyvrbEx" 
              @change="(val) => updatePackage('fancyvrbEx', val)"
              label="fancyvrb-ex - Fancyvrb 扩展宏包"
            />
          </div>
          
          <!-- Xparse -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.xparse" 
              @change="(val) => updatePackage('xparse', val)"
              label="xparse - 新一代命令定义宏包"
            />
          </div>
          
          <!-- Minted -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.minted.enabled" 
              @change="(val) => updatePackage('minted', { ...packages.minted, enabled: Boolean(val) })"
              label="minted - 代码高亮宏包"
            />
            
            <div v-if="packages.minted.enabled" style="margin-left: 20px; margin-top: 10px;">
              <el-checkbox 
                v-model="packages.minted.newfloat" 
                @change="updateMintedNewfloat"
                label="newfloat"
              />
              <el-checkbox 
                v-model="packages.minted.cache" 
                @change="updateMintedCache"
                label="cache=false"
              />
            </div>
          </div>
          
          <!-- Listings -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.listings" 
              @change="(val) => updatePackage('listings', val)"
              label="listings - 代码环境宏包"
            />
          </div>
          
          <!-- Accsupp -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.accsupp" 
              @change="(val) => updatePackage('accsupp', val)"
              label="accsupp - 辅助支持宏包"
            />
          </div>
          
          <!-- Tcolorbox -->
          <div style="margin-bottom: 15px;">
            <el-checkbox 
              v-model="packages.tcolorbox.enabled" 
              @change="(val) => updatePackage('tcolorbox', { ...packages.tcolorbox, enabled: Boolean(val) })"
              label="tcolorbox - 彩色文本框宏包"
            />
            
            <div v-if="packages.tcolorbox.enabled" style="margin-left: 20px; margin-top: 10px;">
              <div>tcolorbox 库:</div>
              <el-checkbox 
                v-model="packages.tcolorbox.listings" 
                @change="updateTcolorboxListings"
                label="listings"
              />
              <el-checkbox 
                v-model="packages.tcolorbox.skins" 
                @change="updateTcolorboxSkins"
                label="skins"
              />
              <el-checkbox 
                v-model="packages.tcolorbox.breakable" 
                @change="updateTcolorboxBreakable"
                label="breakable"
              />
              <el-checkbox 
                v-model="packages.tcolorbox.xparse" 
                @change="updateTcolorboxXparse"
                label="xparse"
              />
            </div>
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