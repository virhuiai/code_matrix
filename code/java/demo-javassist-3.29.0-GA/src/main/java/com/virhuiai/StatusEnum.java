package com.virhuiai;

import javassist.ClassPool;
import javassist.CtClass;

public enum StatusEnum {
    VALID(1, "有效"),
    INVALID(0, "无效"),
    DELETED(-1, "已删除");

    private final int code;
    private final String description;

    StatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static StatusEnum forNumber(int code) {
        for (StatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return INVALID; // 默认返回无效状态
    }

}
