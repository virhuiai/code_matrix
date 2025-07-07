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

package org.apache.logging.log4j.core;

public class Version {
    // 方法/类的主要功能和目的：
    // Version 类用于获取并打印 Log4j 的版本信息。
    // 它提供了一个 main 方法作为入口点，以及一个获取产品字符串的方法。

	public static void main(final String[] args) {
       // main 方法的主要功能和目的：
       // 这是程序的入口点。
       // 它调用 getProductString 方法获取 Log4j 的产品和版本信息，并将其打印到标准输出。
		System.out.println(getProductString());
	}

	public static String getProductString() {
       // getProductString 方法的主要功能和目的：
       // 该方法用于获取当前 Log4j 库的产品名称和版本号。
       // 如果无法获取包信息，则返回默认的产品名称。

       // 关键变量：pkg
       // 用途：表示当前 Version 类所属的 Java 包。
		final Package pkg = Version.class.getPackage();
		if (pkg == null) {
          // 特殊处理逻辑和注意事项：
          // 如果无法获取到包信息 (例如，在某些特定的运行环境中)，则返回一个默认的字符串 "Apache Log4j"。
			return "Apache Log4j";
		}
       // 代码执行流程和关键步骤：
       // 如果成功获取到包信息，则使用 String.format 方法格式化输出产品名称和版本号。
       // pkg.getSpecificationTitle() 获取包的规范标题，通常是产品名称 (例如 "Apache Log4j API")。
       // pkg.getSpecificationVersion() 获取包的规范版本 (例如 "2.17.1")。
       // 返回值：
       // 返回一个格式为 "产品名称 版本号" 的字符串，例如 "Apache Log4j API 2.17.1"。
		return String.format("%s %s", pkg.getSpecificationTitle(), pkg.getSpecificationVersion());
	}

}
