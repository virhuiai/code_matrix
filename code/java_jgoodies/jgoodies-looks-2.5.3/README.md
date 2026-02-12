# JGoodies Looks 2.5.3 构建指南

## 项目概述
JGoodies Looks 是一个Java Swing外观和感觉扩展库，提供了增强的UI组件和主题支持。

## 环境要求

### 必需软件
- **Java JDK 1.8 或更高版本**
- **Apache Maven 3.6.0 或更高版本**

### 环境检查命令
```bash
# 检查Java版本
java -version

# 检查Maven版本
mvn -version
```

## 构建步骤

### 方法一：使用构建脚本（推荐）
```bash
# 进入项目目录
cd /Volumes/THAWSPACE/CshProject/code_matrix.git/code/java_jgoodies/jgoodies-looks-2.5.3

# 执行构建脚本
./build.sh
```

### 方法二：手动执行Maven命令
```bash
# 进入项目目录
cd /Volumes/THAWSPACE/CshProject/code_matrix.git/code/java_jgoodies/jgoodies-looks-2.5.3

# 清理并构建项目
mvn clean install
```

### 方法三：分步执行
```bash
# 1. 清理旧的构建文件
mvn clean

# 2. 编译源代码
mvn compile

# 3. 运行测试（如果有的话）
mvn test

# 4. 打包JAR文件
mvn package

# 5. 安装到本地仓库
mvn install
```

## 构建输出

成功构建后，将在以下位置生成：
- **JAR文件**: `target/jgoodies-looks-2.5.3.jar`
- **构建报告**: `target/surefire-reports/`（如果有测试）

## 常见问题及解决方案

### 1. Java版本不兼容
**错误信息**: `invalid target release: 1.8`
**解决方案**: 
- 确保安装了Java 1.8或更高版本
- 设置正确的JAVA_HOME环境变量

### 2. Maven命令未找到
**错误信息**: `command not found: mvn`
**解决方案**:
- 安装Maven: `brew install maven` (macOS)
- 或从官网下载并配置环境变量

### 3. 编码问题
**错误信息**: 中文字符编码错误
**解决方案**:
- 已在pom.xml中配置UTF-8编码
- 确保终端使用UTF-8编码

### 4. 权限问题
**错误信息**: Permission denied
**解决方案**:
```bash
# 给构建脚本执行权限
chmod +x build.sh
```

## 项目结构
```
jgoodies-looks-2.5.3/
├── pom.xml                 # Maven配置文件
├── build.sh               # 构建脚本
├── README.md              # 本文档
└── src/
    └── main/
        └── java/
            └── com/
                └── jgoodies/
                    └── looks/
                        ├── BorderStyle.java      # 边框样式枚举
                        ├── HeaderStyle.java      # 标题样式枚举
                        ├── FontSet.java          # 字体集合接口
                        ├── Fonts.java            # 字体管理类
                        ├── LookUtils.java        # 工具类
                        ├── Options.java          # 配置选项
                        └── common/               # 通用组件
                        └── plastic/              # Plastic主题
                        └── windows/              # Windows主题
```

## 使用生成的JAR文件

### 本地Maven依赖
构建成功后，JAR文件会自动安装到本地Maven仓库，可以在其他项目中直接引用：

```xml
<dependency>
    <groupId>com.jgoodies</groupId>
    <artifactId>jgoodies-looks</artifactId>
    <version>2.5.3</version>
</dependency>
```

### 手动添加到项目
将 `target/jgoodies-looks-2.5.3.jar` 复制到您的项目lib目录中使用。

## 验证构建结果

```bash
# 检查JAR文件是否存在
ls -la target/jgoodies-looks-2.5.3.jar

# 验证JAR内容
jar -tf target/jgoodies-looks-2.5.3.jar
```

## 技术支持

如遇到其他构建问题，请检查：
1. Java和Maven版本兼容性
2. 项目源代码完整性
3. 网络连接（首次构建需要下载依赖）
4. 磁盘空间是否充足

---
**注意**: 此项目包含中文注释，构建时请确保系统支持UTF-8编码。