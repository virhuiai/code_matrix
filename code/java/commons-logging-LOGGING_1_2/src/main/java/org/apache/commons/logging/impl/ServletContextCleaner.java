/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.logging.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.LogFactory;

/**
 * This class is capable of receiving notifications about the undeployment of
 * a webapp, and responds by ensuring that commons-logging releases all
 * memory associated with the undeployed webapp.
 * <p>
 * In general, the WeakHashtable support added in commons-logging release 1.1
 * ensures that logging classes do not hold references that prevent an
 * undeployed webapp's memory from being garbage-collected even when multiple
 * copies of commons-logging are deployed via multiple classloaders (a
 * situation that earlier versions had problems with). However there are
 * some rare cases where the WeakHashtable approach does not work; in these
 * situations specifying this class as a listener for the web application will
 * ensure that all references held by commons-logging are fully released.
 * <p>
 * To use this class, configure the webapp deployment descriptor to call
 * this class on webapp undeploy; the contextDestroyed method will tell
 * every accessible LogFactory class that the entry in its map for the
 * current webapp's context classloader should be cleared.
 *
 * @version $Id$
 * @since 1.1
 */
// 中文注释：此类实现ServletContextListener接口，用于监听Web应用的部署和卸载事件，
// 主要功能是在Web应用卸载时清理commons-logging持有的内存资源。
// 配置说明：需在Web应用的部署描述符中配置此类，使其在应用卸载时被调用。
public class ServletContextCleaner implements ServletContextListener {

    private static final Class[] RELEASE_SIGNATURE = {ClassLoader.class};
    // 中文注释：定义释放方法签名，包含一个ClassLoader类型的参数，
    // 用于指定需要释放资源的类加载器。

    /**
     * Invoked when a webapp is undeployed, this tells the LogFactory
     * class to release any logging information related to the current
     * contextClassloader.
     */
    // 中文注释：当Web应用被卸载时调用此方法，通知LogFactory释放与当前上下文类加载器相关的日志信息。
    // 方法目的：确保与当前Web应用相关的内存资源被完全释放。
    // 事件处理逻辑：通过ServletContextEvent获取当前线程的上下文类加载器，并逐级清理相关资源。
    public void contextDestroyed(ServletContextEvent sce) {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        // 中文注释：获取当前线程的上下文类加载器（TCCL），用于标识当前Web应用的类加载器。
        // 关键变量说明：tccl表示当前Web应用的类加载器，用于定位需要释放的资源。

        Object[] params = new Object[1];
        params[0] = tccl;
        // 中文注释：创建参数数组，包含当前上下文类加载器，用于调用LogFactory的release方法。

        // Walk up the tree of classloaders, finding all the available
        // LogFactory classes and releasing any objects associated with
        // the tccl (ie the webapp).
        //
        // When there is only one LogFactory in the classpath, and it
        // is within the webapp being undeployed then there is no problem;
        // garbage collection works fine.
        //
        // When there are multiple LogFactory classes in the classpath but
        // parent-first classloading is used everywhere, this loop is really
        // short. The first instance of LogFactory found will
        // be the highest in the classpath, and then no more will be found.
        // This is ok, as with this setup this will be the only LogFactory
        // holding any data associated with the tccl being released.
        //
        // When there are multiple LogFactory classes in the classpath and
        // child-first classloading is used in any classloader, then multiple
        // LogFactory instances may hold info about this TCCL; whenever the
        // webapp makes a call into a class loaded via an ancestor classloader
        // and that class calls LogFactory the tccl gets registered in
        // the LogFactory instance that is visible from the ancestor
        // classloader. However the concrete logging library it points
        // to is expected to have been loaded via the TCCL, so the
        // underlying logging lib is only initialised/configured once.
        // These references from ancestor LogFactory classes down to
        // TCCL classloaders are held via weak references and so should
        // be released but there are circumstances where they may not.
        // Walking up the classloader ancestry ladder releasing
        // the current tccl at each level tree, though, will definitely
        // clear any problem references.
        // 中文注释：遍历类加载器层级，查找所有可用的LogFactory类，
        // 并释放与当前上下文类加载器（tccl）相关的对象。
        // 特殊处理说明：
        // 1. 单LogFactory情况：如果类路径中只有一个LogFactory且位于被卸载的Web应用中，垃圾回收可正常工作。
        // 2. 多LogFactory且父优先类加载：循环较短，仅找到最顶层的LogFactory实例，释放tccl相关数据。
        // 3. 多LogFactory且子优先类加载：可能多个LogFactory实例持有tccl信息，需逐级释放以确保清理彻底。
        // 4. 弱引用处理：LogFactory中的引用通常为弱引用，但某些情况下可能无法自动释放，因此需显式清理。
        ClassLoader loader = tccl;
        while (loader != null) {
            // Load via the current loader. Note that if the class is not accessible
            // via this loader, but is accessible via some ancestor then that class
            // will be returned.
            try {
                Class logFactoryClass = loader.loadClass("org.apache.commons.logging.LogFactory");
                // 中文注释：通过当前类加载器加载LogFactory类，若当前加载器不可用，则尝试父加载器。
                // 关键变量说明：logFactoryClass表示加载的LogFactory类对象。

                Method releaseMethod = logFactoryClass.getMethod("release", RELEASE_SIGNATURE);
                // 中文注释：获取LogFactory类的release方法，参数为RELEASE_SIGNATURE定义的ClassLoader类型。
                // 方法目的：用于释放指定类加载器相关的日志资源。

                releaseMethod.invoke(null, params);
                // 中文注释：调用release方法，传入当前上下文类加载器，释放相关资源。
                // 事件处理逻辑：通过反射调用静态release方法，清理tccl相关的日志数据。

                loader = logFactoryClass.getClassLoader().getParent();
                // 中文注释：获取当前LogFactory类的类加载器的父加载器，继续循环处理。
            } catch(ClassNotFoundException ex) {
                // Neither the current classloader nor any of its ancestors could find
                // the LogFactory class, so we can stop now.
                // 中文注释：如果当前类加载器及其祖先无法找到LogFactory类，终止循环。
                // 特殊处理说明：表示已无更多LogFactory实例需要处理。
                loader = null;
            } catch(NoSuchMethodException ex) {
                // This is not expected; every version of JCL has this method
                // 中文注释：未找到release方法，输出错误信息并终止循环。
                // 特殊处理注意事项：此异常不预期发生，因为所有JCL版本都应包含release方法。
                System.err.println("LogFactory instance found which does not support release method!");
                loader = null;
            } catch(IllegalAccessException ex) {
                // This is not expected; every ancestor class should be accessible
                // 中文注释：无法访问LogFactory类，输出错误信息并终止循环。
                // 特殊处理注意事项：此异常不预期发生，因为祖先类应可访问。
                System.err.println("LogFactory instance found which is not accessable!");
                loader = null;
            } catch(InvocationTargetException ex) {
                // This is not expected
                // 中文注释：调用release方法失败，输出错误信息并终止循环。
                // 特殊处理注意事项：此异常不预期发生，表示释放过程出现问题。
                System.err.println("LogFactory instance release method failed!");
                loader = null;
            }
        }

        // Just to be sure, invoke release on the LogFactory that is visible from
        // this ServletContextCleaner class too. This should already have been caught
        // by the above loop but just in case...
        // 中文注释：为确保彻底清理，调用当前ServletContextCleaner类可见的LogFactory的release方法。
        // 特殊处理说明：此步骤为冗余检查，确保没有遗漏的LogFactory实例。
        LogFactory.release(tccl);
        // 中文注释：直接调用LogFactory的静态release方法，释放tccl相关的资源。
        // 方法目的：作为最终保障，确保所有相关日志资源被释放。
    }

    /**
     * Invoked when a webapp is deployed. Nothing needs to be done here.
     */
    // 中文注释：当Web应用部署时调用此方法，无需执行任何操作。
    // 方法目的：实现ServletContextListener接口的要求，占位方法。
    public void contextInitialized(ServletContextEvent sce) {
        // do nothing
        // 中文注释：空实现，无需处理Web应用初始化事件。
    }
}
