package com.virhuiai;


import java.util.List;
import java.util.Locale;

public class CommonRuntimeException extends RuntimeException  {
    private static final long serialVersionUID = 12345L;
    public static final String log001 = "无法找到异常信息，code={0}，locale={1}";
    private String code;
    private Object[] params;
    private List<Locale> locales;

    public CommonRuntimeException(String code, Throwable e, List<Locale> locales, Object... params) {
        super(e);
        this.code = code;
        this.params = params;
        this.locales = locales;
    }

    public CommonRuntimeException(String code, Throwable e, Object... params) {
        this(code, e, (List)null, params);
    }

    public CommonRuntimeException(String code, Object... params) {
        this(code, (Throwable)null, params);
    }

    public CommonRuntimeException(Throwable e) {
        this((String)null, e);
    }

    public CommonRuntimeException() {
    }
}