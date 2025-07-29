package com.virhuiai.compact7z;

import com.virhuiai.log.codec.CharsetConverter;
// 导入 CharsetConverter 类，用于处理字符编码转换
// 中文注释：CharsetConverter 提供静态方法来检测和转换字符串或字节数组的编码

interface IConvertStringToOriginal {
    // 接口声明：定义 IConvertStringToOriginal 接口，提供字符串编码转换功能
    // 中文注释：接口功能：为实现类提供将乱码字符串转换为正确编码字符串的默认方法
    // 中文注释：目的：解决字符串因错误编码（如 ISO-8859-1）导致的乱码问题，特别是在处理压缩文件（如 ZIP）的文件名时
    // 中文注释：注意事项：依赖 CharsetConverter 类执行实际的编码检测和转换

    default String convertStringToOriginal(String inputStr){
        // 默认方法：将输入字符串转换为原始编码的正确字符串
        // 中文注释：方法功能：将可能因错误编码导致乱码的输入字符串转换为正确的字符串
        // 中文注释：参数：
        // 中文注释：  - inputStr：输入字符串，可能包含乱码（例如由 GBK 字节按 ISO-8859-1 错误解码）
        // 中文注释：返回值：正确的字符串，基于检测到的原始编码解码
        // 中文注释：执行流程：
        // 中文注释：  1. 调用 CharsetConverter.convertToOriginal 方法处理输入字符串
        // 中文注释：  2. 返回转换后的正确字符串
        // 中文注释：特殊处理逻辑：
        // 中文注释：  - 如果 CharsetConverter 无法检测编码或转换失败，返回原始输入字符串
        // 中文注释：注意事项：
        // 中文注释：  - 方法依赖 CharsetConverter 的实现，需确保其正确配置（如 juniversalchardet 依赖）
        // 中文注释：  - 适用于处理压缩文件（如 ZIP）中的文件名乱码问题
        return CharsetConverter.convertToOriginal(inputStr);
        // 调用 CharsetConverter 的 convertToOriginal 方法执行编码转换
        // 中文注释：委托 CharsetConverter 处理字符串的编码检测和转换，返回结果
    }
}
