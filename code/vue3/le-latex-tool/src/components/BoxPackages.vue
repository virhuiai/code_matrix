<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { generateCodeFromBoxPackageInfos, BoxPackageInfo } from '../utils/box-packages-utils'
import { setupCodeEmission } from '../utils/code-emitter'

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
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const isDialogOpen = ref(false)

// 包配置数据 - 所有宏包默认选中
const packageOptions = ref({
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

// 计算属性：生成 LaTeX 代码（使用工具函数）
const latexCode = computed(() => {
  const infos: BoxPackageInfo[] = []

  if (packageOptions.value.fancybox) {
    infos.push({ package: 'fancybox' })
  }

  if (packageOptions.value.boxedminipage) {
    infos.push({ package: 'boxedminipage' })
  }

  if (packageOptions.value.tikz) {
    infos.push({ package: 'tikz' })
  }

  if (packageOptions.value.tcolorbox.enabled) {
    const libs: string[] = []
    if (packageOptions.value.tcolorbox.raster) libs.push('raster')
    if (packageOptions.value.tcolorbox.listings) libs.push('listings')
    if (packageOptions.value.tcolorbox.theorems) libs.push('theorems')
    if (packageOptions.value.tcolorbox.skins) libs.push('skins')
    if (packageOptions.value.tcolorbox.xparse) libs.push('xparse')
    if (packageOptions.value.tcolorbox.breakable) libs.push('breakable')
    infos.push({ package: 'tcolorbox', libraries: libs })
  }

  if (packageOptions.value.awesomebox) {
    infos.push({ package: 'awesomebox' })
  }

  if (packageOptions.value.mdframed) {
    infos.push({ package: 'mdframed' })
  }

  if (packageOptions.value.framed) {
    infos.push({ package: 'framed' })
  }

  if (packageOptions.value.changepage) {
    infos.push({ package: 'changepage' })
  }

  return generateCodeFromBoxPackageInfos(infos)
})

const updatePackage = (pkg: string, value: any) => {
  (packageOptions.value as any)[pkg] = value
  emit('update:modelValue', { ...packageOptions.value })
}

// 监听 tcolorbox 子选项变化
const updateTcolorboxRaster = (value: boolean | string | number) => {
  packageOptions.value.tcolorbox.raster = Boolean(value)
  emit('update:modelValue', { ...packageOptions.value })
}

const updateTcolorboxListings = (value: boolean | string | number) => {
  packageOptions.value.tcolorbox.listings = Boolean(value)
  emit('update:modelValue', { ...packageOptions.value })
}

const updateTcolorboxTheorems = (value: boolean | string | number) => {
  packageOptions.value.tcolorbox.theorems = Boolean(value)
  emit('update:modelValue', { ...packageOptions.value })
}

const updateTcolorboxSkins = (value: boolean | string | number) => {
  packageOptions.value.tcolorbox.skins = Boolean(value)
  emit('update:modelValue', { ...packageOptions.value })
}

const updateTcolorboxXparse = (value: boolean | string | number) => {
  packageOptions.value.tcolorbox.xparse = Boolean(value)
  emit('update:modelValue', { ...packageOptions.value })
}

const updateTcolorboxBreakable = (value: boolean | string | number) => {
  packageOptions.value.tcolorbox.breakable = Boolean(value)
  emit('update:modelValue', { ...packageOptions.value })
}

setupCodeEmission(latexCode, emit, props.componentId, 'BoxPackages')

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
  hideDialog
})
</script>

<template>
  <div class="package-options-dialog">
    <!-- 触发弹窗的按钮 -->
    <el-button type="primary" @click="showDialog">盒子宏包设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="isDialogOpen"
      title="盒子宏包设置"
      :before-close="hideDialog"
    >
      <el-card shadow="hover">
        <div>
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <div class="package-section">
                <strong>基础盒子宏包</strong>
                <div class="package-options-list">
                  <el-checkbox 
                    v-model="packageOptions.fancybox" 
                    @change="(val: boolean | string | number) => updatePackage('fancybox', Boolean(val))"
                    label="fancybox - 盒子宏包，扩展 \\fbox 命令"
                    class="package-option-item"
                  />
                  <el-checkbox 
                    v-model="packageOptions.boxedminipage" 
                    @change="(val: boolean | string | number) => updatePackage('boxedminipage', Boolean(val))"
                    label="boxedminipage - 盒子环境"
                    class="package-option-item"
                  />
                  <el-checkbox 
                    v-model="packageOptions.tikz" 
                    @change="(val: boolean | string | number) => updatePackage('tikz', Boolean(val))"
                    label="tikz - 绘图宏包"
                    class="package-option-item"
                  />
                </div>
              </div>

              <div class="package-section">
                <strong>tcolorbox 库</strong>
                <div class="package-options-list">
                  <el-checkbox 
                    v-model="packageOptions.tcolorbox.enabled" 
                    @change="(val: boolean | string | number) => updatePackage('tcolorbox', {...packageOptions.tcolorbox, enabled: Boolean(val)})"
                    label="启用 tcolorbox"
                    class="package-option-item"
                  />
                  <div v-if="packageOptions.tcolorbox.enabled">
                    <el-checkbox 
                      v-model="packageOptions.tcolorbox.raster" 
                      @change="updateTcolorboxRaster"
                      label="raster"
                      class="package-option-item"
                    />
                    <el-checkbox 
                      v-model="packageOptions.tcolorbox.listings" 
                      @change="updateTcolorboxListings"
                      label="listings"
                      class="package-option-item"
                    />
                    <el-checkbox 
                      v-model="packageOptions.tcolorbox.theorems" 
                      @change="updateTcolorboxTheorems"
                      label="theorems"
                      class="package-option-item"
                    />
                    <el-checkbox 
                      v-model="packageOptions.tcolorbox.skins" 
                      @change="updateTcolorboxSkins"
                      label="skins"
                      class="package-option-item"
                    />
                    <el-checkbox 
                      v-model="packageOptions.tcolorbox.xparse" 
                      @change="updateTcolorboxXparse"
                      label="xparse"
                      class="package-option-item"
                    />
                    <el-checkbox 
                      v-model="packageOptions.tcolorbox.breakable" 
                      @change="updateTcolorboxBreakable"
                      label="breakable"
                      class="package-option-item"
                    />
                  </div>
                </div>
              </div>

              <div class="package-section">
                <strong>其他盒子宏包</strong>
                <div class="package-options-list">
                  <el-checkbox 
                    v-model="packageOptions.awesomebox" 
                    @change="(val: boolean | string | number) => updatePackage('awesomebox', Boolean(val))"
                    label="awesomebox - 图标盒子宏包"
                    class="package-option-item"
                  />
                  <el-checkbox 
                    v-model="packageOptions.mdframed" 
                    @change="(val: boolean | string | number) => updatePackage('mdframed', Boolean(val))"
                    label="mdframed - 框架环境宏包"
                    class="package-option-item"
                  />
                  <el-checkbox 
                    v-model="packageOptions.framed" 
                    @change="(val: boolean | string | number) => updatePackage('framed', Boolean(val))"
                    label="framed - 框架环境宏包"
                    class="package-option-item"
                  />
                  <el-checkbox 
                    v-model="packageOptions.changepage" 
                    @change="(val: boolean | string | number) => updatePackage('changepage', Boolean(val))"
                    label="changepage - 页面调整宏包"
                    class="package-option-item"
                  />
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
