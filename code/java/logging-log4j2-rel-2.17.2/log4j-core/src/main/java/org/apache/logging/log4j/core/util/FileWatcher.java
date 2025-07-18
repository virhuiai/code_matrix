/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.util;

import java.io.File;

/**
 * Watches for changes in a {@link File} and performs an action when the file is modified.
 * 监视文件变化，并在文件被修改时执行相应操作。
 *
 * @see WatchManager
 * @see WatchManager 文件监视管理器的参考。
 */
public interface FileWatcher {

    /**
     * Called when a {@link WatchManager} detects that the given {@link File} changed.
     * 当 {@link WatchManager} 检测到给定 {@link File} 发生变化时调用此方法。
     *
     * @param file
     *            the file that changed.
     * 发生变化的文件。
     * @see WatchManager
     * @see WatchManager 文件监视管理器的参考。
     */
    void fileModified(File file);
}
