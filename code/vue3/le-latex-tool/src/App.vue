<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElContainer, ElHeader, ElAside, ElMain, ElFooter, ElTabs, ElTabPane, ElCard, ElCheckbox } from 'element-plus'

const activeTab = ref('tab1')

// 定义三个选项的默认值，都默认选中
const autoFakeBold = ref(true)
const autoFakeSlant = ref(true)
const noMath = ref(true)

// 计算属性，用于拼接这三个选项值
const computedOptions = computed(() => {
  const options = []
  if (autoFakeBold.value) options.push('AutoFakeBold=true')
  if (autoFakeSlant.value) options.push('AutoFakeSlant=true')
  if (noMath.value) options.push('no-math')
  return options
})
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
                  
                  <!-- 添加三个选项，针对 AutoFakeBold AutoFakeSlant no-math 是否选中，默认是选中 -->
                  <el-checkbox v-model="autoFakeBold" label="AutoFakeBold" />
                  <el-checkbox v-model="autoFakeSlant" label="AutoFakeSlant" />
                  <el-checkbox v-model="noMath" label="no-math" />
                  
                  <!-- 显示计算后的选项值 -->
                  <div style="margin-top: 10px; padding: 5px; background-color: #f0f0f0; border-radius: 4px;">
                    <div>\PassOptionsToPackage{</div>
                    <div style="margin-left: 10px;">{{ computedOptions.join(', ') }}</div>
                    <div>} xeCJK</div>
                    <div>\PassOptionsToPackage{</div>
                    <div style="margin-left: 10px;">{{ noMath ? 'no-math' : '' }}</div>
                    <div>} fontspec</div>
                  </div>

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