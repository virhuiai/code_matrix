package com.virhuiai.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 选项配置类，用于封装选项的所有属性
 *
 * 利用了 OptionConfig 的结构，使得代码更加统一和易于管理。
 */
public class OptionConfig {
    private final String optionName;

    private final Runnable addOptionMethod;
    private final Set<String> validValues;

    public OptionConfig(String optionName,
                        Runnable addOptionMethod, String... validValues) {
        this.optionName = optionName;

        this.addOptionMethod = addOptionMethod;
        this.validValues = validValues.length > 0
                ? Collections.unmodifiableSet(new HashSet<>(Arrays.asList(validValues)))
                : Collections.emptySet();
    }

    public String getOptionName() {
        return optionName;
    }



    public Runnable getAddOptionMethod() {
        return addOptionMethod;
    }

    public boolean isValidValue(String value) {
        return validValues.isEmpty() || validValues.contains(value.toLowerCase());
    }
}