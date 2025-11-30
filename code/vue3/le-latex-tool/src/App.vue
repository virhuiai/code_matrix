<script setup lang="ts">
import { ref } from 'vue'
import { ElContainer, ElHeader, ElAside, ElMain, ElFooter, ElTabs, ElTabPane, ElCard } from 'element-plus'


const activeTab = ref('tab1')
</script>

<template>
  <el-container style="height: 100vh;">
    <!-- 顶部标题区域 -->
    <el-header style="background-color: #409eff; color: white; display: flex; align-items: center;">
      <h1>LE LaTeX 工具</h1>
    </el-header>
    
    <!-- 下方主要内容区域 -->
    <el-container> 
      <!-- 左侧选项卡 -->
      <el-aside width="250px" style="background-color: #f5f5f5; padding: 10px;">
        <el-tabs v-model="activeTab" type="border-card">
          <el-tab-pane label="选项" name="tab1">
            <div style="display: flex; flex-direction: column; gap: 10px;">

              <el-card shadow="hover">
                <div style="display: flex; flex-direction: column; gap: 5px;">
                  <strong>PassOptionsToPackage</strong>
                  
<!-- \PassOptionsToPackage{AutoFakeBold=true,AutoFakeSlant=true}{xeCJK}
\PassOptionsToPackage{no-math}{fontspec} -->

                </div>
              </el-card>

              <el-card shadow="hover">
                <div style="display: flex; flex-direction: column; gap: 5px;">
                  <strong>文档设置</strong>
                  <p style="font-size: 0.9em; color: #666;">设置文档类型、页面大小等</p>
                </div>
              </el-card>
              
              <el-card shadow="hover">
                <div style="display: flex; flex-direction: column; gap: 5px;">
                  <strong>公式编辑</strong>
                  <p style="font-size: 0.9em; color: #666;">插入和编辑数学公式</p>
                </div>
              </el-card>
              
              <el-card shadow="hover">
                <div style="display: flex; flex-direction: column; gap: 5px;">
                  <strong>图表生成</strong>
                  <p style="font-size: 0.9em; color: #666;">创建和自定义图表</p>
                </div>
              </el-card>


<!-- 0_导言区通用内容.tex

\input{1_中文宏包.tex}
\input{1_2字体.tex}
\input{2_morewrites.tex} 
\input{2_2_盒子设置.tex}
\input{2_3_首行放大.tex} 
\input{3_抄录设置.tex}
\input{3_1行距和空格设置.tex}
\input{3_2_版面设置16K.tex}
\input{3_3版式.tex}
\input{6_标题格式设置.tex}
\input{8_目录格式设置.tex} 
\input{10_链接_索引设置.tex}
\input{11_列表_符号设置.tex}
\input{12_对译环境.tex}
\input{12_2_多栏.tex}
\input{13_注释.tex}
\input{14_插图_颜色设置.tex}
\input{15_表格设置.tex}

1_2字体.tex
1_中文宏包.tex
2_2_盒子设置.tex
2_3_首行放大.tex
2_morewrites.tex
3_1行距和空格设置.tex
3_2_版面设置16K.tex
3_3版式.tex
3_抄录设置_minted.tex
3_抄录设置_tcb.tex
3_抄录设置.tex
6_标题格式设置.tex
8_目录格式设置.tex
10_链接_索引设置.tex
11_列表_符号设置.tex
12_2_多栏.tex
12_对译环境.tex
13_注释.tex
14_插图_颜色设置.tex
15_表格设置.tex
16_marginPic.tex -->
              
            </div>
          </el-tab-pane>
          <el-tab-pane label="常见模板" name="tab2"></el-tab-pane>
          <!-- <el-tab-pane label="选项三" name="tab3"></el-tab-pane>
          <el-tab-pane label="选项四" name="tab4"></el-tab-pane> -->
        </el-tabs>
      </el-aside>
      
      <!-- 中间主要内容显示区域 -->
      <el-main style="background-color: #ffffff; padding: 20px;">
        <div style="height: 100%; border: 1px dashed #ccc; display: flex; justify-content: center; align-items: center;">
          <p>在此处显示生成的内容（只读）</p>
        </div>
      </el-main>
      
      <!-- 右侧属性设置区域 -->
      <el-aside width="250px" style="background-color: #f5f5f5; padding: 10px;">
        <div style="padding: 20px;">
          <h3>属性设置</h3>
          <div v-if="activeTab === 'tab1'">
            <p>选项一的属性设置</p>
          </div>
          <div v-else-if="activeTab === 'tab2'">
            <p>选项二的属性设置</p>
          </div>
          <div v-else-if="activeTab === 'tab3'">
            <p>选项三的属性设置</p>
          </div>
          <div v-else-if="activeTab === 'tab4'">
            <p>选项四的属性设置</p>
          </div>
        </div>
      </el-aside>
    </el-container>
    
    <!-- 底部 -->
    <el-footer style="background-color: #eee; height: 40px; display: flex; align-items: center; justify-content: center;">
      <span>LE LaTeX 工具 ©2023</span>
    </el-footer>
  </el-container>
</template>

<style scoped>
.el-header h1 {
  margin: 0;
  font-size: 1.5em;
}

.el-aside {
  overflow-y: auto;
}

.el-tabs {
  height: 100%;
}
</style>