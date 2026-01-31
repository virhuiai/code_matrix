#!/bin/bash

# PDF表格解析Demo运行脚本

echo "==================================="
echo "PDF表格解析Demo运行脚本"
echo "==================================="

# 设置PDF文件路径
PDF_FILE="/Volumes/RamDisk/test.pdf"

echo "目标PDF文件: $PDF_FILE"

# 检查PDF文件是否存在
if [ ! -f "$PDF_FILE" ]; then
    echo "错误: PDF文件不存在!"
    echo "请确保文件 $PDF_FILE 存在"
    exit 1
fi

echo "PDF文件存在，开始编译和运行..."

# 编译项目
echo ""
echo "1. 编译项目..."
mvn compile
if [ $? -ne 0 ]; then
    echo "编译失败!"
    exit 1
fi

echo ""
echo "2. 运行基础解析器..."
mvn exec:java -Dexec.mainClass="com.virhuiai.demo.PdfTableParserDemo"

echo ""
echo "3. 运行高级解析器..."
mvn exec:java -Dexec.mainClass="com.virhuiai.demo.AdvancedPdfTableParser"

echo ""
echo "==================================="
echo "运行完成!"
echo "==================================="