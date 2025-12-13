<script setup lang="ts">
import { ref, defineProps, defineExpose } from 'vue'

const props = defineProps<{
  buttonLabel: string
  dialogTitle: string
  buttonFullWidth?: boolean
  dialogWidth?: string | number
  code?: string
  renderTriggerButton?: boolean
}>()

const visible = ref(false)

const openDialog = () => {
  visible.value = true
}

const closeDialog = () => {
  visible.value = false
}

defineExpose({ openDialog, closeDialog })
</script>

<template>
  <div class="package-options-dialog">
    <el-button 
      v-if="props.renderTriggerButton !== false"
      type="primary"
      size="small"
      round
      @click="openDialog"
    >
      {{ buttonLabel }}
    </el-button>

    <el-dialog
      v-model="visible"
      :title="dialogTitle"
      :before-close="closeDialog"
      :width="dialogWidth ?? '100%'"
    >
      <el-card shadow="hover">
        <div class="package-options-container">
          <div class="package-options-left">
            <slot name="left" />
          </div>
          <div class="package-options-right">
            <div class="code-preview">
              <pre class="code-preview-content"><slot name="right">{{ props.code }}</slot></pre>
            </div>
          </div>
        </div>
      </el-card>

      <template #footer>
        <slot name="footer" />
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 样式统一在全局样式文件中维护 */
</style>
