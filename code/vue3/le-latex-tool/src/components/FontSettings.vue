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
interface FontType {
  value: string
  label: string
  command: string
}

const fontTypes: FontType[] = [
  { value: 'main', label: '主字体', command: 'setCJKmainfont' },
  { value: 'sans', label: '无衬线字体', command: 'setCJKsansfont' }, 
  { value: 'mono', label: '等宽字体', command: 'setCJKmonofont' }
]

// 字体选项，按类型分组
interface FontOption {
  label: string
  path: string
  filename: string
}

const fontOptions: Record<string, FontOption[]> = {
  main: [
    {
      label: '方正书宋GBK',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/',
      filename: 'FangZhengShuSong-GBK-1.ttf'
    },
    {
      label: '方正书宋繁体',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正FanTi/',
      filename: 'FangZhengShuSongFanTi-1.ttf'
    }
    // 添加新的简体字体选项
    ,{
      label: '方正楷体简体',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正JianTi/',
      filename: 'FangZhengShuSongJianTi-1.ttf'
    }
  ],
  sans: [
    {
      label: '方正黑体GBK',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/',
      filename: 'FangZhengHeiTi-GBK-1.ttf'
    },
    {
      label: '方正黑体繁体',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正FanTi/',
      filename: 'FangZhengHeiTiFanTi-1.ttf'
    }
    // 添加新的简体字体选项
    ,{
      label: '方正雅黑简体',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正JianTi/',
      filename: 'FangZhengHeiTiJianTi-1.ttf'
    }
  ],
  mono: [
    {
      label: '方正仿宋GBK',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/',
      filename: 'FangZhengFangSong-GBK-1.ttf'
    },
    {
      label: '方正仿宋繁体',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正FanTi/',
      filename: 'FangZhengFangSongFanTi-1.ttf'
    }
    // 添加新的简体字体选项
    ,{
      label: '方正宋体简体',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正JianTi/',
      filename: 'FangZhengFangSongJianTi-1.ttf'
    }
  ]
}

const fontSettings = ref([
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
])

// 获取特定类型的字体设置
const getFontByType = (type: string) => {
  return fontSettings.value.find(font => font.type === type) || { 
    type, 
    path: '', 
    filename: '' 
  }
}

// 更新特定字体类型的设置
const updateFontSetting = (type: string, field: 'path' | 'filename' | 'label', value: string) => {
  const index = fontSettings.value.findIndex(font => font.type === type)
  if (index !== -1) {
    fontSettings.value[index][field as keyof typeof fontSettings.value[number]] = value
  } else {
    // 如果该类型不存在，则添加新项
    fontSettings.value.push({ type, path: '', filename: '', label: '' } as any)
    const lastIndex = fontSettings.value.length - 1
    fontSettings.value[lastIndex][field as keyof typeof fontSettings.value[number]] = value
  }
  emit('update:modelValue', { fonts: [...fontSettings.value] })
}

// 处理字体选择变更
const handleFontChange = (type: string, option: { path: string; filename: string; label: string }) => {
  updateFontSetting(type, 'path', option.path)
  updateFontSetting(type, 'filename', option.filename)
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
    fontSettings.value = [
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
    emit('update:modelValue', { fonts: [...fontSettings.value] })
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
              <el-col :span="24">
                <el-select 
                  :model-value="getFontByType(fontType.value).filename"
                  @change="(val) => handleFontChange(fontType.value, val)"
                  placeholder="请选择字体"
                  style="width: 100%"
                >
                  <el-option
                    v-for="(option, index) in fontOptions[fontType.value as keyof typeof fontOptions]"
                    :key="index"
                    :label="option.label"
                    :value="option"
                  />
                </el-select>
              </el-col>
            </el-row>
            
            <!-- 显示路径和文件名，并允许编辑 -->
            <el-row :gutter="20" style="margin-top: 10px;">
              <el-col :span="12">
                <el-input
                  :model-value="getFontByType(fontType.value).path"
                  placeholder="字体路径"
                  @update:model-value="(val) => updateFontSetting(fontType.value, 'path', val)"
                />
              </el-col>
              <el-col :span="12">
                <el-input
                  :model-value="getFontByType(fontType.value).filename"
                  placeholder="字体文件名"
                  @update:model-value="(val) => updateFontSetting(fontType.value, 'filename', val)"
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