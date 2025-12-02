// DocumentClassSelector 组件相关类型定义

export interface DocumentClassConfig {
  className: string
  label: string
  description: string
}

export interface ClassOptionConfig {
  key: string
  label: string
}

export interface DocumentClassInfo {
  command: string
  options: string
  documentClass: string
}