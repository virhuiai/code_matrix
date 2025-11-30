<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElInput, ElSelect, ElOption, ElRow, ElCol, ElDialog, ElButton } from 'element-plus'

const props = defineProps<{
  modelValue: {
    fonts: Array<{
      type: string
      path: string
      filename: string
    }>
  }
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { fonts: Array<{ type: string; path: string; filename: string }> }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 字体类型选项
const fontTypes = [
  { value: 'main', label: '主字体', command: 'setCJKmainfont' },
  { value: 'sans', label: '无衬线字体', command: 'setCJKsansfont' },
  { value: 'mono', label: '等宽字体', command: 'setCJKmonofont' }
]

// 默认字体配置
const defaultFonts = [
  {
    type: 'main',
    path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/',
    filename: 'FangZhengShuSong-GBK-1.ttf'
  },
  {
    type: 'sans',
    path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/',
    filename: 'FangZhengHeiTi-GBK-1.ttf'
  },
  {
    type: 'mono',
    path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/',
    filename: 'FangZhengFangSong-GBK-1.ttf'
  }
]

const fontSettings = ref([...defaultFonts])

// 获取特定类型的字体设置
const getFontByType = (type: string) => {
  return fontSettings.value.find(font => font.type === type) || { 
    type, 
    path: '', 
    filename: '' 
  }
}

// 更新特定字体类型的设置
const updateFontSetting = (type: string, field: 'path' | 'filename', value: string) => {
  const index = fontSettings.value.findIndex(font => font.type === type)
  if (index !== -1) {
    fontSettings.value[index][field] = value
  } else {
    // 如果该类型不存在，则添加新项
    fontSettings.value.push({ type, [field]: value, path: '', filename: '' } as any)
    // 确保其他字段有默认值
    if (field === 'path') {
      const lastIndex = fontSettings.value.length - 1
      fontSettings.value[lastIndex].filename = ''
    } else if (field === 'filename') {
      const lastIndex = fontSettings.value.length - 1
      fontSettings.value[lastIndex].path = ''
    }
  }
  emit('update:modelValue', { fonts: [...fontSettings.value] })
}

const computedLatexCode = computed(() => {
  return fontSettings.value.map(font => {
    const fontType = fontTypes.find(type => type.value === font.type)
    if (!fontType || !font.path || !font.filename) return ''
    return `\\${fontType.command}[Path=${font.path}]{${font.filename}}`
  }).filter(code => code !== '').join('\n')
})

watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
  if (newVal.fonts && newVal.fonts.length > 0) {
    fontSettings.value = [...newVal.fonts]
  }
}, { deep: true })

onMounted(() => {
  // 如果没有初始值，则使用默认配置
  if (!props.modelValue.fonts || props.modelValue.fonts.length === 0) {
    fontSettings.value = [...defaultFonts]
    emit('update:modelValue', { fonts: [...defaultFonts] })
  } else {
    fontSettings.value = [...props.modelValue.fonts]
  }
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
    <el-button type="primary" @click="openDialog">字体设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="字体设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>字体设置</strong>
          <p>设置中文字体路径</p>
          
          <div v-for="fontType in fontTypes" :key="fontType.value" style="margin-bottom: 20px;">
            <h4>{{ fontType.label }}</h4>
            <el-row :gutter="20">
              <el-col :span="18">
                <el-input
                  :model-value="getFontByType(fontType.value).path"
                  placeholder="请输入字体文件路径"
                  clearable
                  @update:model-value="updateFontSetting(fontType.value, 'path', $event)"
                />
              </el-col>
              <el-col :span="6">
                <el-input
                  :model-value="getFontByType(fontType.value).filename"
                  placeholder="字体文件名"
                  clearable
                  @update:model-value="updateFontSetting(fontType.value, 'filename', $event)"
                />
              </el-col>
            </el-row>
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