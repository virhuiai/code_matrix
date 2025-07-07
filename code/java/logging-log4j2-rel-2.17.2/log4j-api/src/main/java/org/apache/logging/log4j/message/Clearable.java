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
package org.apache.logging.log4j.message;

/**
 * {@link Clearable} objects may be reset to a reusable state.
 * Clearable 对象可以重置为可重用状态。
 *
 * This type should be combined into {@link ReusableMessage} as a default method for 3.0.
 * 该类型在 3.0 版本中应作为默认方法合并到 {@link ReusableMessage} 中。
 *
 * @since 2.11.1
 */
interface Clearable {

    /**
     * Resets the object to a clean state.
     * 将对象重置为干净状态。
     */
    void clear();

}
