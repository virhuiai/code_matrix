<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider } from 'element-plus'
import { generateCodeFromPackageInfos, PackageInfo } from '../utils/generic-packages-utils'
import { setupCodeEmission } from '../utils/code-emitter'

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
  componentId?: number
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

// 选项说明与示例
const optionDocs = {
  xcolor: {
    desc: '提供颜色支持，可选 dvipsnames 预定义颜色集',
    example: '\\usepackage[dvipsnames]{xcolor}\n\\textcolor{BrickRed}{Hello}'
  },
  xcolorDvipsnames: {
    desc: '启用 dvipsnames 预定义颜色名称',
    example: '\\textcolor{BrickRed}{示例文本}'
  },
  cprotect: {
    desc: '保护命令在浮动环境中的内容，避免被展开破坏',
    example: '\\cprotect\\caption{包含特殊命令的标题}'
  },
  spverbatim: {
    desc: '支持空格的 verbatim 环境，保留空白字符',
    example: '\\begin{spverbatim}\ncode with  spaces\n\\end{spverbatim}'
  },
  fancyvrb: {
    desc: '增强的 verbatim，支持边框、行号等',
    example: '\\begin{Verbatim}[frame=single]\n...\\end{Verbatim}'
  },
  fancyvrbEx: {
    desc: 'fancyvrb 的扩展功能集合',
    example: '% 使用 fancyvrb-ex 扩展特性'
  },
  xparse: {
    desc: '提供新一代命令定义接口',
    example: '\\NewDocumentCommand{\\foo}{m}{#1}'
  },
  minted: {
    desc: '基于 Pygments 的代码高亮，需要 -shell-escape',
    example: '\\begin{minted}{python}\nprint(1)\n\\end{minted}'
  },
  mintedNewfloat: {
    desc: '将 minted 定义为浮动体，支持题注',
    example: '\\usepackage[newfloat]{minted}'
  },
  mintedCache: {
    desc: '关闭缓存便于调试，或启用以提升性能',
    example: '\\usepackage[cache=false]{minted}'
  },
  listings: {
    desc: '标准代码环境宏包，支持多语言格式化',
    example: '\\begin{lstlisting}\n...\n\\end{lstlisting}'
  },
  accsupp: {
    desc: '可访问性辅助支持，如设置实际文本',
    example: '\\BeginAccSupp{ActualText=hello}Text\\EndAccSupp'
  },
  tcolorbox: {
    desc: '彩色文本框，支持丰富样式',
    example: '\\begin{tcolorbox}内容\\end{tcolorbox}'
  },
  tcolorboxListings: {
    desc: '启用 tcolorbox 的 listings 库',
    example: '\\tcbuselibrary{listings}'
  },
  tcolorboxSkins: {
    desc: '启用 tcolorbox 的 skins 库',
    example: '\\tcbuselibrary{skins}'
  },
  tcolorboxBreakable: {
    desc: '允许 tcolorbox 在分页时断开',
    example: '\\begin{tcolorbox}[breakable]...\\end{tcolorbox}'
  },
  tcolorboxXparse: {
    desc: '启用 tcolorbox 的 xparse 库',
    example: '\\tcbuselibrary{xparse}'
  }
}

// 计算属性：生成 LaTeX 代码（使用通用工具）
const computedLatexCode = computed(() => {
  const infos: PackageInfo[] = []

  if (packages.value.xcolor.enabled) {
    const opts: string[] = []
    if (packages.value.xcolor.dvipsnames) opts.push('dvipsnames')
    infos.push({ package: 'xcolor', options: opts })
  }

  if (packages.value.cprotect) {
    infos.push({ package: 'cprotect' })
  }

  if (packages.value.spverbatim) {
    infos.push({ package: 'spverbatim' })
  }

  if (packages.value.fancyvrb) {
    infos.push({ package: 'fancyvrb', afterLines: ['\\newsavebox{\\vTmpOne}'] })
  }

  if (packages.value.fancyvrbEx) {
    infos.push({ package: 'fancyvrb-ex' })
  }

  if (packages.value.xparse) {
    infos.push({ package: 'xparse' })
  }

  if (packages.value.minted.enabled) {
    const opts: string[] = []
    if (packages.value.minted.newfloat) opts.push('newfloat')
    if (packages.value.minted.cache === false) opts.push('cache=false')
    infos.push({ package: 'minted', options: opts })
  }

  if (packages.value.listings) {
    infos.push({ package: 'listings' })
  }

  if (packages.value.accsupp) {
    infos.push({ package: 'accsupp' })
  }

  if (packages.value.tcolorbox.enabled) {
    const libs: string[] = []
    if (packages.value.tcolorbox.listings) libs.push('listings')
    if (packages.value.tcolorbox.skins) libs.push('skins')
    if (packages.value.tcolorbox.breakable) libs.push('breakable')
    if (packages.value.tcolorbox.xparse) libs.push('xparse')
    const after: string[] = libs.length > 0 ? [`\\tcbuselibrary{${libs.join(',')}}`] : []
    infos.push({ package: 'tcolorbox', afterLines: after })
  }

  return generateCodeFromPackageInfos(infos)
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

setupCodeEmission(computedLatexCode, emit, props.componentId, 'CodeListingPackage')

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
    <!-- 触发弹窗的按钮 -->
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">代码抄录宏包设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="代码抄录宏包设置"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <strong>代码抄录宏包设置</strong>
              <p>配置代码抄录相关的宏包及其选项</p>
              <el-divider />

              <!-- Xcolor -->
              <div class="package-section">
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
                  <div class="option-doc" style="margin-top: 8px;">
                    <div>{{ optionDocs.xcolor.desc }}</div>
                    <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.xcolor.example }}</pre>
                    <div v-if="packages.xcolor.dvipsnames" style="margin-top:8px;">
                      <div>{{ optionDocs.xcolorDvipsnames.desc }}</div>
                      <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.xcolorDvipsnames.example }}</pre>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Cprotect -->
              <div class="package-section">
                <el-checkbox 
                  v-model="packages.cprotect" 
                  @change="(val) => updatePackage('cprotect', val)"
                  label="cprotect - 保护命令宏包"
                />
                <div v-if="packages.cprotect" class="option-doc" style="margin-top: 8px; margin-left: 20px;">
                  <div>{{ optionDocs.cprotect.desc }}</div>
                  <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.cprotect.example }}</pre>
                </div>
              </div>

              <!-- Spverbatim -->
              <div class="package-section">
                <el-checkbox 
                  v-model="packages.spverbatim" 
                  @change="(val) => updatePackage('spverbatim', val)"
                  label="spverbatim - 支持空格的 Verbatim 宏包"
                />
                <div v-if="packages.spverbatim" class="option-doc" style="margin-top: 8px; margin-left: 20px;">
                  <div>{{ optionDocs.spverbatim.desc }}</div>
                  <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.spverbatim.example }}</pre>
                </div>
              </div>

              <!-- Fancyvrb -->
              <div class="package-section">
                <el-checkbox 
                  v-model="packages.fancyvrb" 
                  @change="(val) => updatePackage('fancyvrb', val)"
                  label="fancyvrb - 增强的 Verbatim 宏包"
                />
                <div v-if="packages.fancyvrb" class="option-doc" style="margin-top: 8px; margin-left: 20px;">
                  <div>{{ optionDocs.fancyvrb.desc }}</div>
                  <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.fancyvrb.example }}</pre>
                </div>
              </div>

              <!-- Fancyvrb-ex -->
              <div class="package-section">
                <el-checkbox 
                  v-model="packages.fancyvrbEx" 
                  @change="(val) => updatePackage('fancyvrbEx', val)"
                  label="fancyvrb-ex - Fancyvrb 扩展宏包"
                />
                <div v-if="packages.fancyvrbEx" class="option-doc" style="margin-top: 8px; margin-left: 20px;">
                  <div>{{ optionDocs.fancyvrbEx.desc }}</div>
                  <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.fancyvrbEx.example }}</pre>
                </div>
              </div>

              <!-- Xparse -->
              <div class="package-section">
                <el-checkbox 
                  v-model="packages.xparse" 
                  @change="(val) => updatePackage('xparse', val)"
                  label="xparse - 新一代命令定义宏包"
                />
                <div v-if="packages.xparse" class="option-doc" style="margin-top: 8px; margin-left: 20px;">
                  <div>{{ optionDocs.xparse.desc }}</div>
                  <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.xparse.example }}</pre>
                </div>
              </div>

              <!-- Minted -->
              <div class="package-section">
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
                  <div class="option-doc" style="margin-top: 8px;">
                    <div>{{ optionDocs.minted.desc }}</div>
                    <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.minted.example }}</pre>
                    <div v-if="packages.minted.newfloat" style="margin-top:8px;">
                      <div>{{ optionDocs.mintedNewfloat.desc }}</div>
                      <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.mintedNewfloat.example }}</pre>
                    </div>
                    <div v-if="packages.minted.cache === false" style="margin-top:8px;">
                      <div>{{ optionDocs.mintedCache.desc }}</div>
                      <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.mintedCache.example }}</pre>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Listings -->
              <div class="package-section">
                <el-checkbox 
                  v-model="packages.listings" 
                  @change="(val) => updatePackage('listings', val)"
                  label="listings - 代码环境宏包"
                />
                <div v-if="packages.listings" class="option-doc" style="margin-top: 8px; margin-left: 20px;">
                  <div>{{ optionDocs.listings.desc }}</div>
                  <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.listings.example }}</pre>
                </div>
              </div>

              <!-- Accsupp -->
              <div class="package-section">
                <el-checkbox 
                  v-model="packages.accsupp" 
                  @change="(val) => updatePackage('accsupp', val)"
                  label="accsupp - 辅助支持宏包"
                />
                <div v-if="packages.accsupp" class="option-doc" style="margin-top: 8px; margin-left: 20px;">
                  <div>{{ optionDocs.accsupp.desc }}</div>
                  <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.accsupp.example }}</pre>
                </div>
              </div>

              <!-- Tcolorbox -->
              <div class="package-section">
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
                  <div class="option-doc" style="margin-top: 8px;">
                    <div>{{ optionDocs.tcolorbox.desc }}</div>
                    <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.tcolorbox.example }}</pre>
                    <div v-if="packages.tcolorbox.listings" style="margin-top:8px;">
                      <div>{{ optionDocs.tcolorboxListings.desc }}</div>
                      <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.tcolorboxListings.example }}</pre>
                    </div>
                    <div v-if="packages.tcolorbox.skins" style="margin-top:8px;">
                      <div>{{ optionDocs.tcolorboxSkins.desc }}</div>
                      <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.tcolorboxSkins.example }}</pre>
                    </div>
                    <div v-if="packages.tcolorbox.breakable" style="margin-top:8px;">
                      <div>{{ optionDocs.tcolorboxBreakable.desc }}</div>
                      <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.tcolorboxBreakable.example }}</pre>
                    </div>
                    <div v-if="packages.tcolorbox.xparse" style="margin-top:8px;">
                      <div>{{ optionDocs.tcolorboxXparse.desc }}</div>
                      <pre style="background:#f5f5f5;padding:10px;border-radius:4px;white-space:pre-wrap;">{{ optionDocs.tcolorboxXparse.example }}</pre>
                    </div>
                  </div>
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
