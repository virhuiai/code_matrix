import { watch, onMounted, type ComputedRef } from 'vue'

export function setupCodeEmission(
  computedLatexCode: ComputedRef<string>,
  emit: (e: 'codeChange', value: string) => void,
  componentId?: number,
  componentName?: string
) {
  watch(computedLatexCode, (newCode) => {
    emit('codeChange', newCode)
  })
  onMounted(() => {
    emit('codeChange', computedLatexCode.value)
    if (componentId !== undefined && componentName) {
      console.log(`${componentName} component loaded successfully with ID: ${componentId}`)
    }
  })
}
