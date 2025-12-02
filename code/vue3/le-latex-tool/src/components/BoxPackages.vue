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

// 计算属性：生成 LaTeX 代码（使用工具函数）
const computedLatexCode = computed(() => {
  const infos: BoxPackageInfo[] = []

  if (packages.value.fancybox) {
    infos.push({ package: 'fancybox' })
  }

  if (packages.value.boxedminipage) {
    infos.push({ package: 'boxedminipage' })
  }

  if (packages.value.tikz) {
    infos.push({ package: 'tikz' })
  }

  if (packages.value.tcolorbox.enabled) {
    const libs: string[] = []
    if (packages.value.tcolorbox.raster) libs.push('raster')
    if (packages.value.tcolorbox.listings) libs.push('listings')
    if (packages.value.tcolorbox.theorems) libs.push('theorems')
    if (packages.value.tcolorbox.skins) libs.push('skins')
    if (packages.value.tcolorbox.xparse) libs.push('xparse')
    if (packages.value.tcolorbox.breakable) libs.push('breakable')
    infos.push({ package: 'tcolorbox', libraries: libs })
  }

  if (packages.value.awesomebox) {
    infos.push({ package: 'awesomebox' })
  }

  if (packages.value.mdframed) {
    infos.push({ package: 'mdframed' })
  }

  if (packages.value.framed) {
    infos.push({ package: 'framed' })
  }

  if (packages.value.changepage) {
    infos.push({ package: 'changepage' })
  }

  return generateCodeFromBoxPackageInfos(infos)
})

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

setupCodeEmission(computedLatexCode, emit, props.componentId, 'BoxPackages')

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
      :before-close="closeDialog"
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
                    v-model="packages.fancybox" 
                    @change="(val: boolean | string | number) => updatePackage('fancybox', Boolean(val))"
                    label="fancybox - 盒子宏包，扩展 \\fbox 命令"
                    class="package-option-item"
                  />
                  <el-checkbox 
                    v-model="packages.boxedminipage" 
                    @change="(val: boolean | string | number) => updatePackage('boxedminipage', Boolean(val))"
                    label="boxedminipage - 盒子环境"
                    class="package-option-item"
                  />
                  <el-checkbox 
                    v-model="packages.tikz" 
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
                    v-model="packages.tcolorbox.enabled" 
                    @change="(val: boolean | string | number) => updatePackage('tcolorbox', {...packages.tcolorbox, enabled: Boolean(val)})"
                    label="启用 tcolorbox"
                    class="package-option-item"
                  />
                  <div v-if="packages.tcolorbox.enabled">
                    <el-checkbox 
                      v-model="packages.tcolorbox.raster" 
                      @change="updateTcolorboxRaster"
                      label="raster"
                      class="package-option-item"
                    />
                    <el-checkbox 
                      v-model="packages.tcolorbox.listings" 
                      @change="updateTcolorboxListings"
                      label="listings"
                      class="package-option-item"
                    />
                    <el-checkbox 
                      v-model="packages.tcolorbox.theorems" 
                      @change="updateTcolorboxTheorems"
                      label="theorems"
                      class="package-option-item"
                    />
                    <el-checkbox 
                      v-model="packages.tcolorbox.skins" 
                      @change="updateTcolorboxSkins"
                      label="skins"
                      class="package-option-item"
                    />
                    <el-checkbox 
                      v-model="packages.tcolorbox.xparse" 
                      @change="updateTcolorboxXparse"
                      label="xparse"
                      class="package-option-item"
                    />
                    <el-checkbox 
                      v-model="packages.tcolorbox.breakable" 
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
                    v-model="packages.awesomebox" 
                    @change="(val: boolean | string | number) => updatePackage('awesomebox', Boolean(val))"
                    label="awesomebox - 图标盒子宏包"
                    class="package-option-item"
                  />
                  <el-checkbox 
                    v-model="packages.mdframed" 
                    @change="(val: boolean | string | number) => updatePackage('mdframed', Boolean(val))"
                    label="mdframed - 框架环境宏包"
                    class="package-option-item"
                  />
                  <el-checkbox 
                    v-model="packages.framed" 
                    @change="(val: boolean | string | number) => updatePackage('framed', Boolean(val))"
                    label="framed - 框架环境宏包"
                    class="package-option-item"
                  />
                  <el-checkbox 
                    v-model="packages.changepage" 
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
                <pre class="code-preview-content">{{ computedLatexCode }}</pre>
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
