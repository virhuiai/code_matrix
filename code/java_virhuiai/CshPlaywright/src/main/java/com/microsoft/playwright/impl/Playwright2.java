package com.microsoft.playwright.impl;

import com.microsoft.playwright.Playwright;

public interface Playwright2 extends Playwright {

    static Playwright create(CreateOptions options) {
        return PlaywrightImpl2.create(options);
    }

    static Playwright create() {
        return create((CreateOptions)null);
    }
}
