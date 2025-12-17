<script setup>
import { ref } from 'vue'

const exampleColorDtx = ref('')
const targetMathcolorDtx = ref('')
const translatedResult = ref('')

// 生成翻译提示词的函数
function translateDtx() {
  // 根据用户需求，生成用于翻译的提示词
  translatedResult.value = `根据 example.txt 中翻译模式的说明，以及${exampleColorDtx}-en翻译成${exampleColorDtx}.dtx的效果，对 ${targetMathcolorDtx}.dtx 文件进行中文翻译。全部翻译到文档结尾：
0.\\begin{macrocode} 和 \\end{macrocode} 环境包围的不需要翻译，两个|包围的一句话也不用翻译，保持
1.按段翻译
2.章节翻译时注意：章节标题，英文原文要添加 \\savecounters , 章节标题，中文翻译要添加 \\restorecounters 。
2.对于latex列表的翻译，在每个 item 的末尾添加一行中文翻译，保持示例代码不变，仅补充注释行
3.技术术语和命令保留了英文形式。
4.翻译直到整个文件结束。
5.同时注意不翻译changes命令。

运行 xelatex -output-directory=/Volumes/RamDisk ./${targetMathcolorDtx}.dtx 验证是否成功`
}
</script>

<template>
  <el-card shadow="hover">
    <el-text tag="b">DTX文件翻译生成器</el-text>
    <el-divider />
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <el-text>示例文件 (color.dtx)</el-text>
          </template>
          <el-input
            v-model="exampleColorDtx"
            type="textarea"
            :autosize="{ minRows: 10, maxRows: 20 }"
            placeholder="请输入示例color.dtx文件内容"
          />
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <el-text>目标文件 (mathcolor.dtx)</el-text>
          </template>
          <el-input
            v-model="targetMathcolorDtx"
            type="textarea"
            :autosize="{ minRows: 10, maxRows: 20 }"
            placeholder="请输入待翻译的mathcolor.dtx文件内容"
          />
        </el-card>
      </el-col>
    </el-row>
    
    <el-space direction="vertical" alignment="normal" style="width: 100%; margin-top: 20px;">
      <el-button type="primary" @click="translateDtx">生成翻译提示词</el-button>
      
      <el-card shadow="never" v-if="translatedResult">
        <template #header>
          <el-text>翻译提示词</el-text>
        </template>
        <el-input
          v-model="translatedResult"
          type="textarea"
          :autosize="{ minRows: 15, maxRows: 30 }"
          placeholder="翻译提示词将显示在这里"
        />
      </el-card>
      
      <el-alert
        title="使用说明"
        type="info"
        description="根据示例文件中的翻译模式，生成用于翻译目标文件的提示词。提示词包含详细的翻译规则和注意事项。"
        show-icon
      />
    </el-space>
  </el-card>
</template>

<style scoped>
</style>