package com.virhuiai;


import java.util.List;
import java.util.Locale;

public class CommonRuntimeException extends RuntimeException  {

    public CommonRuntimeException(String code, Throwable e, List<Locale> locales, Object... params) {
        super(e);
    }

    public CommonRuntimeException(String code, Throwable e, Object... params) {
        this(code, e, (List)null, params);
    }

    public CommonRuntimeException(String code, Object... params) {
        this(code, (Throwable)null, params);
    }

}