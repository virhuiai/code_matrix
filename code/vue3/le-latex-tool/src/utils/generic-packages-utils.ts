export interface PackageInfo {
  package: string
  options?: string[]
  afterLines?: string[]
}

// 生成通用的 \usepackage 代码，以及附加的后续命令（如库加载）
export function generateCodeFromPackageInfos(infos: PackageInfo[]): string {
  if (!infos || infos.length === 0) return ''
  const lines: string[] = []
  for (const info of infos) {
    const opts = (info.options && info.options.length > 0) ? `[${info.options.join(',')}]` : ''
    lines.push(`\\usepackage${opts}{${info.package}}`)
    if (info.afterLines && info.afterLines.length > 0) {
      lines.push(...info.afterLines)
    }
  }
  return lines.join('\n\n')
}