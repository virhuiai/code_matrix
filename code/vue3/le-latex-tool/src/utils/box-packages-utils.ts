export interface BoxPackageInfo {
  package: string
  libraries?: string[]
}

/**
 * 根据 BoxPackageInfo 数组生成 LaTeX 代码
 * @param infos BoxPackageInfo 类型的数组
 * @returns 生成的 LaTeX 代码字符串
 */
export const generateCodeFromBoxPackageInfos = (infos: BoxPackageInfo[]): string => {
  if (!Array.isArray(infos)) return ''

  return infos
    .map(info => {
      const pkgLine = `\\usepackage{${info.package}}`
      const libLine = info.libraries && info.libraries.length > 0
        ? `\n\\tcbuselibrary{${info.libraries.join(',')}}`
        : ''
      return `${pkgLine}${libLine}`
    })
    .join('\n\n')
}