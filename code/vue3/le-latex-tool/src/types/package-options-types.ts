// PackageOptions 组件相关类型定义

export interface OptionInfo {
  command: string
  options: string
  package: string
}

export interface PackageConfig {
  packageName: string
  title: string
  items: Array<{ key: string; label: string; desc?: string; example?: string }>
  optionsMap: Record<string, string>
}
