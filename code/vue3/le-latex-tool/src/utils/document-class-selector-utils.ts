import { DocumentClassInfo } from '../types/document-class-selector-types';

/**
 * 根据DocumentClassInfo对象生成LaTeX代码
 * @param documentClassInfo DocumentClassInfo类型的对象
 * @returns 生成的LaTeX代码字符串
 */
export const generateCodeFromDocumentClassInfo = (documentClassInfo: DocumentClassInfo): string => {
  // 确保documentClassInfo存在且有效
  if (!documentClassInfo || !documentClassInfo.documentClass) {
    return '';
  }

  return `\\documentclass[${documentClassInfo.options}]{${documentClassInfo.documentClass}}`;
};