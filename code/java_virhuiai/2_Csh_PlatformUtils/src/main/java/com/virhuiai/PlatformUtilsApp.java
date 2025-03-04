package com.virhuiai;

import com.virhuiai.PlatformUtils.EnumPlatform;
import com.virhuiai.PlatformUtils.UnsupportedPlatformException;
import org.apache.commons.logging.Log;
/**
 * Hello world!
 *
 */
public class PlatformUtilsApp
{
    private static Log LOGGER = CshLogUtils.getLog(PlatformUtilsApp.class);
    public static void main( String[] args )
    {
        LOGGER.info("支持的平台：" + EnumPlatform.getSupported());//macosx-amd64

        LOGGER.info( "Hello World!" );
        try {
            EnumPlatform platform = EnumPlatform.getCurrentPlatform();

            LOGGER.info(platform.getIdentifier());//macosx-amd64
            LOGGER.info("isMac:" + platform.isMac());//macosx-amd64
            LOGGER.info("isAMD64():" + platform.isAMD64());//macosx-amd64

        } catch (UnsupportedPlatformException e) {
            LOGGER.error("sss", e);
            throw new RuntimeException(e);
        }
    }
}
