package com.microsoft.playwright.impl.driver;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Driver2 {
    protected final Map<String, String> env = new LinkedHashMap();
    private static Driver2 instance;

    public Driver2() {
    }

    public static synchronized Driver2 ensureDriverInstalled(Map<String, String> env, Boolean installBrowsers) {
        if (instance == null) {
            instance = createAndInstall(env, installBrowsers);
        }

        return instance;
    }

    private void initialize(Map<String, String> env, Boolean installBrowsers) throws Exception {
        this.env.putAll(env);
        this.initialize(installBrowsers);
    }

    protected abstract void initialize(Boolean installBrowsers) throws Exception;

    public Path driverPath() {
        String cliFileName = System.getProperty("os.name").toLowerCase().contains("windows") ? "playwright.cmd" : "playwright.sh";
        return this.driverDir().resolve(cliFileName);
    }

    public ProcessBuilder createProcessBuilder() {
        ProcessBuilder pb = new ProcessBuilder(this.driverPath().toString());
        pb.environment().putAll(this.env);
        pb.environment().put("PW_LANG_NAME", "java");
        pb.environment().put("PW_LANG_NAME_VERSION", getMajorJavaVersion());
        String version = Driver2.class.getPackage().getImplementationVersion();
        if (version != null) {
            pb.environment().put("PW_CLI_DISPLAY_VERSION", version);
        }

        return pb;
    }

    private static String getMajorJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            return version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            return dot != -1 ? version.substring(0, dot) : version;
        }
    }

    public static Driver2 createAndInstall(Map<String, String> env, Boolean installBrowsers) {
        try {
            Driver2 instance = newInstance();
            logMessage("initializing Driver2");
            instance.initialize(env, installBrowsers);
            logMessage("Driver2 initialized.");
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Driver2", e);
        }
    }

    private static Driver2 newInstance() throws Exception {
        String pathFromProperty = System.getProperty("playwright.cli.dir");
        if (pathFromProperty != null) {
            return new PreinstalledDriver(Paths.get(pathFromProperty));
        } else {
            return new DriverJar2();
        }
    }

    protected abstract Path driverDir();

    protected static void logMessage(String message) {
        DriverLogging.logWithTimestamp("pw:install " + message);
    }

    private static class PreinstalledDriver extends Driver2 {
        private final Path driverDir;

        PreinstalledDriver(Path driverDir) {
            logMessage("created PreinstalledDriver: " + driverDir);
            this.driverDir = driverDir;
        }

        protected void initialize(Boolean installBrowsers) {
        }

        protected Path driverDir() {
            return this.driverDir;
        }
    }
}
