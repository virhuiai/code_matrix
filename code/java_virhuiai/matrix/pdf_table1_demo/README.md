# PDF表格解析Demo

这个项目演示了如何使用`pdf-table`库来解析PDF文件中的表格内容。

## ⚠️ 重要依赖说明

**OpenCV依赖**: `pdf-table`库需要OpenCV 3.4.2才能正常工作！

### OpenCV安装步骤

1. **下载OpenCV**:
   - 访问: https://github.com/opencv/opencv/releases/tag/3.4.2
   - 下载适合您系统的版本

2. **Windows系统**:
   ```
   1. 解压下载的OpenCV包
   2. 将 <opencv_dir>\build\java\x64 添加到系统PATH环境变量
   ```

3. **Linux/Mac系统**:
   ```
   1. 解压OpenCV包
   2. 设置LD_LIBRARY_PATH指向OpenCV的Java库目录
   ```

## 项目结构

```
src/main/java/com/virhuiai/demo/
├── PdfTableParserDemo.java      # 基础PDF表格解析器
└── AdvancedPdfTableParser.java  # 高级PDF表格解析器
```

## 依赖说明

项目使用以下主要依赖：

- `com.github.rostrovsky:pdf-table:1.0.0` - 核心表格解析库
- `org.apache.pdfbox:pdfbox:2.0.27` - PDF处理基础库
- `org.slf4j:slf4j-simple:1.7.36` - 日志支持

## 使用方法

### 方法1: 使用运行脚本（推荐）

```bash
# 确保已安装OpenCV并设置好环境变量
./run-demo.sh
```

### 方法2: 手动编译运行

```bash
# 编译项目
mvn compile

# 运行基础解析器
mvn exec:java -Dexec.mainClass="com.virhuiai.demo.PdfTableParserDemo"

# 运行高级解析器
mvn exec:java -Dexec.mainClass="com.virhuiai.demo.AdvancedPdfTableParser"
```

## 输出示例

程序会输出类似以下格式的信息：

```
开始解析PDF文件: /Volumes/RamDisk/test.pdf
==================================================
正在解析第1页...
解析成功!
页码: 1
行数: 3

表格内容详情:
------------------------------
第 1 行 (3 列):
  第1个单元格 [1,1]: 姓名
  第2个单元格 [1,2]: 年龄  
  第3个单元格 [1,3]: 城市

第 2 行 (3 列):
  第4个单元格 [2,1]: 张三
  第5个单元格 [2,2]: 25
  第6个单元格 [2,3]: 北京

第 3 行 (3 列):
  第7个单元格 [3,1]: 李四
  第8个单元格 [3,2]: 30
  第9个单元格 [3,3]: 上海

表格解析完成! 总共 9 个单元格
```

## 配置说明

### 修改PDF文件路径

在两个Java文件中修改以下行：

```java
String pdfFilePath = "/Volumes/RamDisk/test.pdf"; // 修改为你的PDF文件路径
```

### 支持的表格格式

该解析器可以处理：
- ✅ 规则表格（有明确边框）
- ✅ 不规则表格
- ✅ 跨页表格（只解析第一页）
- ✅ 合并单元格的表格
- ❌ 手写表格或图片表格

## 常见问题解答

### Q: 编译时报错"程序包ru.codechap.pdf.table不存在"？
A: 确保Maven依赖已正确下载，运行`mvn clean install`

### Q: 运行时报错找不到OpenCV库？
A: 按照上面的说明正确安装和配置OpenCV

### Q: 解析结果不准确？
A: 可以尝试调整PdfTableReader的设置参数：

```java
PdfTableSettings settings = PdfTableSettings.getBuilder()
    .setCannyFiltering(true)
    .setCannyApertureSize(5)
    .setCannyThreshold1(40)
    .setCannyThreshold2(190.5)
    .setPdfRenderingDpi(160)
    .build();
    
PdfTableReader reader = new PdfTableReader(settings);
```

### Q: 只想解析特定页码？
A: 修改代码中的页码参数：
```java
ParsedTablePage page = reader.parsePdfTablePage(pdfDoc, 页码); // 页码从1开始
```

## 故障排除

如果遇到问题，请按以下顺序检查：

1. **文件路径**: 确认PDF文件路径正确且文件存在
2. **OpenCV依赖**: 确保OpenCV 3.4.2已正确安装和配置
3. **Java版本**: 确保使用Java 8或更高版本
4. **Maven依赖**: 运行`mvn clean install`重新下载依赖
5. **权限问题**: 确保有读取PDF文件的权限

## 扩展功能

你可以基于这些demo进一步开发：

- 📤 解析多页PDF中的所有表格
- 📊 导出表格数据到CSV/Excel格式
- 🔍 添加表格数据分析功能
- ⚙️ 支持更多表格格式的识别
- 🌐 创建Web界面进行表格解析

## API参考

主要使用的类和方法：

- `PdfTableReader`: 核心表格读取器
- `parsePdfTablePage(PDDocument doc, int pageNum)`: 解析指定页码的表格
- `ParsedTablePage`: 解析后的表格页面对象
- `ParsedTableRow`: 表格行对象
- `getRow(int index)`: 获取指定行
- `getCell(int index)`: 获取指定单元格内容

## 许可证

本项目仅供学习和参考使用。