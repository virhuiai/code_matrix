package com.microsoft.playwright.impl.driver;

import com.microsoft.playwright.impl.driver.Driver;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DriverJar2 extends Driver2 {
    private static final String PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = "PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD";
    private static final String SELENIUM_REMOTE_URL = "SELENIUM_REMOTE_URL";
    static final String PLAYWRIGHT_NODEJS_PATH = "PLAYWRIGHT_NODEJS_PATH";
    private final Path driverTempDir;
    private Path preinstalledNodePath;

    public DriverJar2() throws IOException {
        String alternativeTmpdir = System.getProperty("playwright.driver.tmpdir");
        String prefix = "playwright-java-";
        this.driverTempDir = alternativeTmpdir == null ? Files.createTempDirectory(prefix) : Files.createTempDirectory(Paths.get(alternativeTmpdir), prefix);
        this.driverTempDir.toFile().deleteOnExit();
        String nodePath = System.getProperty("playwright.nodejs.path");
        if (nodePath != null) {
            this.preinstalledNodePath = Paths.get(nodePath);
            if (!Files.exists(this.preinstalledNodePath, new LinkOption[0])) {
                throw new RuntimeException("Invalid Node.js path specified: " + nodePath);
            }
        }

        logMessage("created DriverJar: " + this.driverTempDir);
    }

    protected void initialize(Boolean installBrowsers) throws Exception {
        if (this.preinstalledNodePath == null && this.env.containsKey("PLAYWRIGHT_NODEJS_PATH")) {
            this.preinstalledNodePath = Paths.get((String)this.env.get("PLAYWRIGHT_NODEJS_PATH"));
            if (!Files.exists(this.preinstalledNodePath, new LinkOption[0])) {
                throw new RuntimeException("Invalid Node.js path specified: " + this.preinstalledNodePath);
            }
        } else if (this.preinstalledNodePath != null) {
            this.env.put("PLAYWRIGHT_NODEJS_PATH", this.preinstalledNodePath.toString());
        }

        this.extractDriverToTempDir();
        logMessage("extracted driver from jar to " + this.driverPath());
        if (installBrowsers) {
            this.installBrowsers(this.env);
        }

    }

    private void installBrowsers(Map<String, String> env) throws IOException, InterruptedException {
        String skip = (String)env.get("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD");
        if (skip == null) {
            skip = System.getenv("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD");
        }

        if (skip != null && !"0".equals(skip) && !"false".equals(skip)) {
            System.out.println("Skipping browsers download because `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD` env variable is set");
        } else if (env.get("SELENIUM_REMOTE_URL") == null && System.getenv("SELENIUM_REMOTE_URL") == null) {
            Path driver = this.driverPath();
            if (!Files.exists(driver, new LinkOption[0])) {
                throw new RuntimeException("Failed to find driver: " + driver);
            } else {
                ProcessBuilder pb = this.createProcessBuilder();
                pb.command().add("install");
                pb.redirectError(Redirect.INHERIT);
                pb.redirectOutput(Redirect.INHERIT);
                Process p = pb.start();
                boolean result = p.waitFor(10L, TimeUnit.MINUTES);
                if (!result) {
                    p.destroy();
                    throw new RuntimeException("Timed out waiting for browsers to install");
                } else if (p.exitValue() != 0) {
                    throw new RuntimeException("Failed to install browsers, exit code: " + p.exitValue());
                }
            }
        } else {
            logMessage("Skipping browsers download because `SELENIUM_REMOTE_URL` env variable is set");
        }
    }

    private static boolean isExecutable(Path filePath) {
        String name = filePath.getFileName().toString();
        return name.endsWith(".sh") || name.endsWith(".exe") || !name.contains(".");
    }

    private FileSystem initFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        } catch (FileSystemAlreadyExistsException var3) {
            return null;
        }
    }

    public static URI getDriverResourceURI() throws URISyntaxException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResource("driver/" + platformDir()).toURI();
    }

    void extractDriverToTempDir() throws URISyntaxException, IOException {
        URI originalUri = getDriverResourceURI();
        URI uri = this.maybeExtractNestedJar(originalUri);
        FileSystem fileSystem = "jar".equals(uri.getScheme()) ? this.initFileSystem(uri) : null;

        try {
            Path srcRoot = Paths.get(uri);
            Path srcRootDefaultFs = Paths.get(srcRoot.toString());
            Files.walk(srcRoot).forEach((fromPath) -> {
                if (this.preinstalledNodePath != null) {
                    String fileName = fromPath.getFileName().toString();
                    if ("node.exe".equals(fileName) || "node".equals(fileName)) {
                        return;
                    }
                }

                Path relative = srcRootDefaultFs.relativize(Paths.get(fromPath.toString()));
                Path toPath = this.driverTempDir.resolve(relative.toString());

                try {
                    if (Files.isDirectory(fromPath, new LinkOption[0])) {
                        Files.createDirectories(toPath);
                    } else {
                        Files.copy(fromPath, toPath);
                        if (isExecutable(toPath)) {
                            toPath.toFile().setExecutable(true, true);
                        }
                    }

                    toPath.toFile().deleteOnExit();
                } catch (IOException var8) {
                    throw new RuntimeException("Failed to extract driver from " + uri + ", full uri: " + originalUri, var8);
                }
            });
        } catch (Throwable var7) {
            if (fileSystem != null) {
                try {
                    fileSystem.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }
            }

            throw var7;
        }

        if (fileSystem != null) {
            fileSystem.close();
        }

    }

    private URI maybeExtractNestedJar(URI uri) throws URISyntaxException {
        if (!"jar".equals(uri.getScheme())) {
            return uri;
        } else {
            String JAR_URL_SEPARATOR = "!/";
            String[] parts = uri.toString().split("!/");
            if (parts.length != 3) {
                return uri;
            } else {
                String innerJar = String.join("!/", parts[0], parts[1]);
                URI jarUri = new URI(innerJar);

                try {
                    FileSystem fs = FileSystems.newFileSystem(jarUri, Collections.emptyMap());

                    URI var9;
                    try {
                        Path fromPath = Paths.get(jarUri);
                        Path toPath = this.driverTempDir.resolve(fromPath.getFileName().toString());
                        Files.copy(fromPath, toPath);
                        toPath.toFile().deleteOnExit();
                        var9 = new URI("jar:" + toPath.toUri() + "!/" + parts[2]);
                    } catch (Throwable var11) {
                        if (fs != null) {
                            try {
                                fs.close();
                            } catch (Throwable var10) {
                                var11.addSuppressed(var10);
                            }
                        }

                        throw var11;
                    }

                    if (fs != null) {
                        fs.close();
                    }

                    return var9;
                } catch (IOException var12) {
                    throw new RuntimeException("Failed to extract driver's nested .jar from " + jarUri + "; full uri: " + uri, var12);
                }
            }
        }
    }

    private static String platformDir() {
        String name = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();
        if (name.contains("windows")) {
            return "win32_x64";
        } else if (name.contains("linux")) {
            return arch.equals("aarch64") ? "linux-arm64" : "linux";
        } else if (name.contains("mac os x")) {
            return "mac";
        } else {
            throw new RuntimeException("Unexpected os.name value: " + name);
        }
    }

    protected Path driverDir() {
        return this.driverTempDir;
    }
}

