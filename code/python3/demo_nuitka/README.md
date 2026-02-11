# Simple GUI App with Nuitka

这是一个使用 Tkinter 构建的简单 GUI 应用程序示例，演示如何使用 Nuitka 进行编译打包。

## 项目结构

- [simple_gui_app.py](simple_gui_app.py): 主应用程序源代码
- [NUITKA_COMPILATION_GUIDE.md](NUITKA_COMPILATION_GUIDE.md): 详细的 Nuitka 编译指南
- [README.md](README.md): 项目说明文件

## 功能特性

- 输入姓名并点击"问候"按钮显示欢迎消息
- "清除"按钮清空输入框
- "关于"按钮显示应用程序信息
- 响应式布局，适应窗口大小变化

## 如何运行

直接使用 Python 运行：

```bash
python3 simple_gui_app.py
```

## 如何编译

### 在 Windows 上

```cmd
python -m nuitka ^
    --standalone ^
    --onefile ^
    --enable-plugin=tk-inter ^
    --windows-disable-console ^
    simple_gui_app.py
```

### 在 Linux 上

```bash
python3 -m nuitka \
    --standalone \
    --onefile \
    --enable-plugin=tk-inter \
    simple_gui_app.py
```

### 在 macOS 上（重要！）

由于 macOS 上的 Apple Python 不支持 Nuitka 的独立模式，您需要先安装独立的 Python 版本（如 CPython）：

1. 从 [Python官网](https://www.python.org/downloads/macos/) 下载并安装 Python
2. 使用新安装的 Python 来编译：

```bash
# 使用完整路径或确保终端使用的是新安装的 Python
/usr/local/bin/python3 -m nuitka \
    --standalone \
    --onefile \
    --enable-plugin=tk-inter \
    --macos-disable-console \
    simple_gui_app.py
```

或者使用 `which python3` 命令找到 Python 的安装路径。

## 编译选项说明

- `--standalone`: 创建独立应用程序，包含所有依赖
- `--onefile`: 将应用打包为单个可执行文件
- `--enable-plugin=tk-inter`: 使 Nuitka 正确处理 Tkinter 依赖
- `--windows-disable-console` 或 `--macos-disable-console`: 编译后隐藏控制台窗口

## 注意事项

1. 编译过程可能需要几分钟时间
2. 生成的可执行文件只能在相同操作系统上运行
3. 在 macOS 上必须使用非 Apple 的 Python 版本进行编译
4. 编译后的文件大小可能较大，因为它包含了 Python 解释器

## 更多信息

请参阅 [NUITKA_COMPILATION_GUIDE.md](NUITKA_COMPILATION_GUIDE.md) 获取更详细的编译说明和故障排除方法。