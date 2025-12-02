import { OptionInfo } from '../types/package-options-types';

/**
 * 根据OptionInfo数组生成LaTeX代码
 * @param optionInfos OptionInfo类型的数组
 * @returns 生成的LaTeX代码字符串
 */
export const generateCodeFromOptionInfos = (optionInfos: OptionInfo[]): string => {
  // 确保optionInfos是一个数组
  if (!Array.isArray(optionInfos)) {
    return '';
  }

  return optionInfos
    .map(info => `\\PassOptionsToPackage{${info.options}}{${info.package}}`)
    .join('\n');
};