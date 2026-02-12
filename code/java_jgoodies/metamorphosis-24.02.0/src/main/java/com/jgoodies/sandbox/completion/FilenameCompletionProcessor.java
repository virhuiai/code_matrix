package com.jgoodies.sandbox.completion;

import com.jgoodies.common.base.Strings;
import com.jgoodies.search.Completion;
import com.jgoodies.search.CompletionProcessor;
import com.jgoodies.search.CompletionPublisher;
import com.jgoodies.search.CompletionState;
import java.io.File;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/completion/FilenameCompletionProcessor.class */
public final class FilenameCompletionProcessor implements CompletionProcessor {
    private Mode mode;
    private boolean allowsRelativePaths = false;
    private boolean ignoresCase = true;

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/completion/FilenameCompletionProcessor$Mode.class */
    public enum Mode {
        FILES_ONLY,
        DIRECTORIES_ONLY,
        FILES_AND_DIRECTORIES
    }

    public FilenameCompletionProcessor(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode newMode) {
        this.mode = newMode;
    }

    public boolean getAllowsRelativePaths() {
        return this.allowsRelativePaths;
    }

    public void setAllowsRelativePaths(boolean relative) {
        this.allowsRelativePaths = relative;
    }

    public boolean getIgnoresCase() {
        return this.ignoresCase;
    }

    public void setIgnoresCase(boolean ignoreCase) {
        this.ignoresCase = ignoreCase;
    }

    public boolean isAutoActivatable(String content, int caretPosition) {
        return false;
    }

    public boolean search(String pathPrefix, int caretPosition, CompletionPublisher publisher, CompletionState state) {
        File contentFile = new File(pathPrefix);
        File parentFile = contentFile.isDirectory() ? contentFile : contentFile.getParentFile();
        if (parentFile != null && parentFile.isDirectory()) {
            if (!this.allowsRelativePaths && !parentFile.isAbsolute()) {
                return true;
            }
            String parentPath = parentFile.getPath();
            String childPrefix = pathPrefix.substring(parentPath.length());
            if (childPrefix.length() > 0 && childPrefix.charAt(0) == File.separatorChar) {
                childPrefix = childPrefix.substring(1);
            }
            File[] children = parentFile.listFiles();
            if (children == null) {
                return false;
            }
            for (File child : children) {
                if (matches(childPrefix, child)) {
                    new Completion.Builder().replacementText(child.getPath(), new Object[0]).publish(publisher);
                }
            }
            return true;
        }
        return true;
    }

    private boolean matches(String childPrefix, File child) {
        if (this.mode == Mode.DIRECTORIES_ONLY && child.isFile()) {
            return false;
        }
        if (this.mode == Mode.FILES_ONLY && child.isDirectory()) {
            return false;
        }
        String childName = child.getName();
        if (this.ignoresCase) {
            return Strings.startsWithIgnoreCase(childName, childPrefix);
        }
        return childName.startsWith(childPrefix);
    }
}
