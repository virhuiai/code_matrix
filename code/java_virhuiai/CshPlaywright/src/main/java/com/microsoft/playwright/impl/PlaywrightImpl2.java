package com.microsoft.playwright.impl;

import com.google.gson.JsonObject;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.impl.driver.Driver2;
import com.virhuiai.ReflectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class PlaywrightImpl2 extends PlaywrightImpl{
    PlaywrightImpl2(ChannelOwner parent, String type, String guid, JsonObject initializer) {
        super(parent, type, guid, initializer);
    }

    public static PlaywrightImpl create(Playwright2.CreateOptions options) {
        return createImpl(options, false);
    }

    public static PlaywrightImpl createImpl(Playwright.CreateOptions options, boolean forceNewDriverInstanceForTests) {
        Map<String, String> env = Collections.emptyMap();
        if (options != null && options.env != null) {
            env = options.env;
        }

        Driver2 driver = forceNewDriverInstanceForTests ? Driver2.createAndInstall(env, true) : Driver2.ensureDriverInstalled(env, true);


        try {
            ProcessBuilder pb = driver.createProcessBuilder();
            pb.command().add("run-driver");
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process p = pb.start();
            Connection connection = new Connection(new PipeTransport(p.getInputStream(), p.getOutputStream()), env);
            PlaywrightImpl result = connection.initializePlaywright();
            ReflectionUtils.setObjField(result,"driverProcess", p);//result.driverProcess = p;// 字段是私有的
            result.initSharedSelectors((PlaywrightImpl)null);
            return result;
        } catch (IOException var8) {
            throw new PlaywrightException("Failed to launch driver", var8);
        }
    }
}
