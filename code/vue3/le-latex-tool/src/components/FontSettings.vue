<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElInput, ElSelect, ElOption, ElRow, ElCol } from 'element-plus'

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

// 字体类型选项
const fontTypes = [
  { value: 'main', label: '主字体', command: 'setCJKmainfont' },
  { value: 'sans', label: '无衬线字体', command: 'setCJKsansfont' },
  { value: 'mono', label: '等宽字体', command: 'setCJKmonofont' }
]

// \setCJKmainfont[Path=/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/]{FangZhengShuSong-GBK-1.ttf}
// \setCJKsansfont[Path=/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/]{FangZhengHeiTi-GBK-1.ttf}
// \setCJKmonofont[Path=/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/]{FangZhengFangSong-GBK-1.ttf}
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

// 更新特定字体类型的设置
const updateFontSetting = (type: string, field: 'path' | 'filename', value: string) => {
  const index = fontSettings.value.findIndex(font => font.type === type)
  if (index !== -1) {
    fontSettings.value[index][field] = value
    emit('update:modelValue', { fonts: [...fontSettings.value] })
  }
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
</script>

<template>
  <el-card shadow="hover">
    <div>
      <strong>字体设置</strong>
      <p>设置中文字体路径</p>
      
      <el-row :gutter="20" v-for="font in fontSettings" :key="font.type" style="margin-bottom: 15px;">
        <el-col :span="6">
          <el-select 
            v-model="font.type" 
            placeholder="选择字体类型" 
            style="width: 100%"
            disabled
          >
            <el-option
              v-for="item in fontTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-col>
        <el-col :span="12">
          <el-input
            v-model="font.path"
            placeholder="请输入字体文件路径"
            clearable
            @input="updateFontSetting(font.type, 'path', $event)"
          />
        </el-col>
        <el-col :span="6">
          <el-input
            v-model="font.filename"
            placeholder="字体文件名"
            clearable
            @input="updateFontSetting(font.type, 'filename', $event)"
          />
        </el-col>
      </el-row>
    </div>
  </el-card>
</template>