package com.virhuiai.compact7z;

import com.virhuiai.log.codec.CharsetConverter;

interface IConvertStringToOriginal {
    default String convertStringToOriginal(String inputStr){
        return CharsetConverter.convertToOriginal(inputStr);
    }
}
