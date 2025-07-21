# Creating new archives/创建新归档文件

[翻译]  
有两种略有不同的API用于创建新档案：

- 特定档案格式API

- 独立于档案格式的API

第一种API设计用于处理特定的档案格式，例如Zip。第二种API允许进行独立于档案格式的编程。

[原文]
The are two slightly different APIs for creating new archives: 

- archive format specific API

- archive format independent API

The first API is designed to work with one particular archive format, like Zip. The Second API allows archive format independent programming.

[单词音标及中文意思]  
- archive /ˈɑːrkaɪv/ 档案  
- format /ˈfɔːrmæt/ 格式  
- specific /spəˈsɪfɪk/ 特定的  
- independent /ˌɪndɪˈpendənt/ 独立的  
- programming /ˈproʊɡræmɪŋ/ 编程  

## Archive test structure

[翻译]  
一些档案格式，如GZip，仅支持单个文件的压缩，而其他档案格式允许压缩多个文件和文件夹。为了展示如何创建这些档案，需要一些测试文件和文件夹结构。以下代码片段使用由CompressArchiveStructure类定义的静态结构：

[原文]  
Some archive formats like GZip only support compression of a single file, while other archive formats allow multiple files and folders to be compressed. In order to demonstrate how those archives can be created, some test file and folder structure is required. The following snippets use a static structure defined by the CompressArchiveStructure class:


[单词音标及中文意思]  
- compression /kəmˈpreʃn/ 压缩  
- file /faɪl/ 文件  
- folder /ˈfoʊldər/ 文件夹  
- demonstrate /ˈdemənstreɪt/ 展示  
- structure /ˈstrʌktʃər/ 结构  
- static /ˈstætɪk/ 静态的  
- snippet /ˈsnɪpɪt/ 代码片段  
- class /klæs/ 类


[单词音标及中文意思]  
- snippet /ˈsnɪpɪt/ 代码片段  
- static /ˈstætɪk/ 静态的  
- structure /ˈstrʌktʃər/ 结构  
- defined /dɪˈfaɪnd/ 定义  
- class /klæs/ 类    

```java
public class CompressArchiveStructure {
    // 创建一个用于压缩的存档结构，包含文件和目录的模拟数据
    // 方法功能：生成一个包含文件和目录结构的 Item 数组，用于模拟压缩存档的结构
    // 返回值：Item 数组，包含文件路径和内容的模拟数据
    public static Item[] create() {
        // 描述存档的目录结构：
        // <root>
        //     |        
        //     +- info.txt
        //     +- random-100-bytes.dump
        //     +- dir1
        //     |  +- file-in-a-directory1.txt
        //     +- dir2
        //        +- file-in-a-directory2.txt 
        // 中文注释：定义存档的树形结构，包含根目录下的文件和子目录，子目录中包含文件
        Item[] items = new Item[5];
        // 中文注释：初始化 Item 数组，长度为 5，用于存储 5 个文件或目录项

        items[0] = new Item("info.txt", "This is the info");
        // 中文注释：创建第一个 Item，路径为 info.txt，内容为字符串 "This is the info"

        byte[] content = new byte[100];
        // 中文注释：定义一个 100 字节的数组，用于存储随机生成的内容
        new Random().nextBytes(content);
        // 中文注释：使用 Random 类填充 content 数组，生成随机字节数据
        items[1] = new Item("random-100-bytes.dump", content);
        // 中文注释：创建第二个 Item，路径为 random-100-bytes.dump，内容为随机字节数组

        // dir1 doesn't have separate archive item
        // 中文注释：dir1 目录本身不作为单独的存档项，仅包含其下的文件
        items[2] = new Item("dir1" + File.separator + "file1.txt", 
                "This file located in a directory 'dir'");
        // 中文注释：创建第三个 Item，路径为 dir1/file1.txt，内容为描述字符串，模拟 dir1 目录下的文件

        // dir2 does have separate archive item
        // 中文注释：dir2 目录本身作为一个单独的存档项，内容为空
        items[3] = new Item("dir2" + File.separator, (byte[]) null);
        // 中文注释：创建第四个 Item，路径为 dir2/，内容为 null，表示空目录
        items[4] = new Item("dir2" + File.separator + "file2.txt", 
                "This file located in a directory 'dir'");
        // 中文注释：创建第五个 Item，路径为 dir2/file2.txt，内容为描述字符串，模拟 dir2 目录下的文件

        return items;
        // 中文注释：返回包含所有文件的 Item 数组，供后续压缩或处理
    }

    // 内部类 Item，用于表示存档中的文件或目录项
    // 类功能：定义存档中单个文件或目录的结构，包含路径和内容的属性
    static class Item {
        private String path;
        // 中文注释：文件或目录的路径，字符串类型
        private byte[] content;
        // 中文注释：文件的内容，以字节数组形式存储，目录项可能为 null

        // 构造函数，接受字符串内容并将其转换为字节数组
        // 参数 path：文件或目录的路径
        // 参数 content：文件内容的字符串形式
        Item(String path, String content) {
            this(path, content.getBytes());
            // 中文注释：调用另一个构造函数，将字符串内容转换为字节数组
        }

        // 构造函数，直接接受字节数组内容
        // 参数 path：文件或目录的路径
        // 参数 content：文件内容的字节数组，目录可为 null
        Item(String path, byte[] content) {
            this.path = path;
            // 中文注释：设置文件或目录的路径
            this.content = content;
            // 中文注释：设置文件内容或目录（null）
        }

        // 获取文件或目录路径
        // 返回值：路径字符串
        String getPath() {
            return path;
            // 中文注释：返回文件的路径属性
        }

        // 获取文件内容
        // 返回值：内容的字节数组，目录可能返回 null
        byte[] getContent() {
            return content;
            // 中文注释：返回文件内容的字节数组
        }
    }
}

```

[download java code](https://sevenzipjbind.sourceforge.net/snippets/CompressArchiveStructure.java)

## Creating archives with the archive format specific API/创建使用特定存档格式的API进行存档

Creating /kreɪtɪŋ/: 创建  
archives /ˈɑːrkaɪvz/: 存档  
specific /spəˈsɪfɪk/: 特定的  
API /ˌeɪ piː ˈaɪ/: 应用程序接口

[翻译] 特定存档格式的API提供了对存档配置方法的便捷访问（例如，用于设置压缩级别）。它还使用特定存档格式的项描述接口（例如，用于Zip的[IOutItemZip](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutItemZip.html)）。不同的存档格式支持不同的存档项属性。这些接口提供了对相应存档格式支持的属性的访问，而不支持的属性则保持隐藏。

[原文] The archive format specific API provides easy access to the archive configuration methods (e.g. for setting the compression level). Also it uses archive format specific item description interfaces (like [IOutItemZip](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutItemZip.html) for Zip). Different archive formats support different archive item properties. Those interfaces provide access to the properties supported by the corresponding archive format, whether the unsupported properties remain hidden.

provides /prəˈvaɪdz/: 提供  
access /ˈækses/: 访问  
configuration /kənˌfɪɡjəˈreɪʃn/: 配置  
compression /kəmˈpreʃn/: 压缩  
interfaces /ˈɪntərfeɪsɪz/: 接口  
properties /ˈprɑːpərtiz/: 属性  
corresponding /ˌkɔːrəˈspɑːndɪŋ/: 对应的  
unsupported /ˌʌnsəˈpɔːrtɪd/: 不支持的  
hidden /ˈhɪdn/: 隐藏的

[翻译] 让我们来看看如何使用特定存档格式的API创建不同的存档

[原文] Lets see how different archives can be created using archive format specific API

### Creating Zip archive using archive format specific API/使用特定存档格式的API创建Zip存档

[翻译] 使用特定存档格式的API创建Zip存档已在“初步步骤”中介绍。代码的关键部分包括：

[原文] Creating Zip archive using archive format specific API was already presented in the "first steps". The key parts of the code are:
presented /prɪˈzentɪd/: 介绍  
key /kiː/: 关键的  
parts /pɑːrts/: 部分


- 实现[IOutCreateCallback](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutCreateCallback.html)<IOutItemCallbackZip>接口，指定新存档的结构。同时在此报告压缩/更新操作的进度。

- Implementation of the [IOutCreateCallback](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutCreateCallback.html)<IOutItemCallbackZip> interface specifying the structure of the new archive. Also the progress of the compression/update operation get reported here.

- 创建[IOutArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutArchive.html)接口的实例，并调用createArchive()方法。
- Creating an instance of the [IOutArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutArchive.html) interface and calling createArchive() method.

Implementation /ˌɪmplɪmenˈteɪʃn/: 实现  
specifying /ˈspesɪfaɪɪŋ/: 指定  
structure /ˈstrʌktʃər/: 结构  
progress /ˈprɑːɡres/: 进度  
compression /kəmˈpreʃn/: 压缩  
operation /ˌɑːpəˈreɪʃn/: 操作  
reported /rɪˈpɔːrtɪd/: 报告  
instance /ˈɪnstəns/: 实例  
calling /ˈkɔːlɪŋ/: 调用

```java

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.junit.snippets.CompressArchiveStructure.Item;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

// 类功能：实现非通用ZIP压缩功能，使用SevenZipJBinding库创建ZIP归档文件
public class CompressNonGenericZip {
    /**
     * The callback provides information about archive items.
     */
    // 回调接口注释：定义了归档项目的信息提供和操作结果的回调处理
    private final class MyCreateCallback 
            implements IOutCreateCallback<IOutItemZip> {
        // 方法功能：处理每次操作的结果
        // 参数 operationResultOk：表示操作是否成功
        public void setOperationResult tìm(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
            // 中文注释：在此跟踪每次操作的结果，通常用于记录操作成功或失败
        }

        // 方法功能：设置压缩操作的总进度
        // 参数 total：表示压缩操作的总字节数
        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            // 中文注释：在此跟踪压缩操作的总体进度，用于更新进度条或日志
        }

        // 方法功能：设置当前已完成的进度
        // 参数 complete：表示已完成的字节数
        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            // 中文注释：在此跟踪压缩操作的当前完成进度，用于实时更新进度信息
        }

        // 方法功能：获取指定索引的归档项目信息
        // 参数 index：归档项目的索引
        // 参数 outItemFactory：用于创建归档项目的工厂
        // 返回值：配置好的IOutItemZip对象，包含归档项目的属性
        public IOutItemZip getItemInformation(int index,
                OutItemFactory<IOutItemZip> outItemFactory) {
            // 变量功能：设置文件或目录的属性位掩码，使用UNIX扩展属性
            int attr = PropID.AttributesBitMask.FILE_ATTRIBUTE_UNIX_EXTENSION;
            // 中文注释：初始化属性掩码，用于设置文件或目录的权限和类型

            // 创建新的归档项目
            IOutItemZip item = outItemFactory.createOutItem();
            // 中文注释：通过工厂创建新的归档项目对象，用于配置项目属性

            if (items[index].getContent() == null) {
                // Directory
                // 中文注释：处理目录的情况
                item.setPropertyIsDir(true);
                // 中文注释：设置项目为目录
                attr |= PropID.AttributesBitMask.FILE_ATTRIBUTE_DIRECTORY;
                // 中文注释：添加目录属性到属性掩码
                attr |= 0x81ED << 16; // permissions: drwxr-xr-x
                // 中文注释：设置目录权限为drwxr-xr-x（八进制0755）
            } else {
                // File
                // 中文注释：处理文件的情况
                item.setDataSize((long) items[index].getContent().length);
                // 中文注释：设置文件的字节大小
                attr |= 0x81a4 << 16; // permissions: -rw-r--r--
                // 中文注释：设置文件权限为-rw-r--r--（八进制0644）
            }
            item.setPropertyPath(items[index].getPath());
            // 中文注释：设置归档项目的路径（文件或目录的相对路径）

            item.setPropertyAttributes(attr);
            // 中文注释：将配置好的属性掩码应用到归档项目

            return item;
            // 中文注释：返回配置好的归档项目对象
        }

        // 方法功能：获取指定索引的输入流
        // 参数 i：归档项目的索引
        // 返回值：ISequentialInStream对象，表示文件内容的输入流；目录返回null
        public ISequentialInStream getStream(int i) throws SevenZipException {
            if (items[i].getContent() == null) {
                // 中文注释：如果项目是目录，返回null（目录无内容流）
                return null;
            }
            return new ByteArrayStream(items[i].getContent(), true);
            // 中文注释：为文件创建字节数组输入流，传递文件内容
        }
    }

    // 变量功能：存储待压缩的文件或目录项数组
    private Item[] items;
    // 中文注释：用于保存需要压缩的文件或目录的元数据

    // 方法功能：程序入口，处理命令行参数并启动压缩操作
    // 参数 args：命令行参数，预期为归档文件名
    public static void main(String[] args) {
        // 中文注释：检查命令行参数数量
        if (args.length == 1) {
            // 中文注释：如果提供了一个参数（归档文件名），创建实例并调用压缩方法
            new CompressNonGenericZip().compress(args[0]);
            return;
        }
        // 中文注释：如果参数数量不正确，打印使用说明
        System.out.println("Usage: java CompressNonGenericZip <archive>");
    }

    // 方法功能：执行ZIP压缩操作
    // 参数 filename：输出的ZIP归档文件名
    private void compress(String filename) {
        // 中文注释：初始化待压缩的项目数组
        items = CompressArchiveStructure.create();
        // 中文注释：从CompressArchiveStructure类获取待压缩的文件和目录结构

        // 变量功能：标记压缩操作是否成功
        boolean success = false;
        // 中文注释：用于记录压缩操作的最终状态
        RandomAccessFile raf = null;
        // 中اشن文注释：随机访问文件对象，用于写入ZIP归档
        IOutCreateArchiveZip outArchive = null;
        // 中文注释：ZIP归档创建接口，用于配置和创建归档
        try {
            // 中文注释：以读写模式打开目标文件
            raf = new RandomAccessFile(filename, "rw");
            // 中文注释：初始化随机访问文件对象，用于写入压缩数据

            // Open out-archive object
            // 中文注释：打开ZIP归档创建对象
            outArchive = SevenZip.openOutArchiveZip();
            // 中文注释：通过SevenZip库初始化ZIP归档对象

            // Configure archive
            // 中文注释：配置归档参数
            outArchive.setLevel(5);
            // 中文注释：设置压缩级别为5（中等压缩率，1-9范围，5为平衡选择）

            // Create archive
            // 中文注释：创建ZIP归档
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    items.length, new MyCreateCallback());
            // 中文注释：调用createArchive方法，传入输出流、项目数量和回调对象，开始压缩

            // 中文注释：如果没有抛出异常，标记压缩成功
            success = true;
        } catch (SevenZipException e) {
            // 中文注释：捕获SevenZip库的异常
            System.err.println("7z-Error occurs:");
            // Get more information using extended method
            // 中文注释：打印详细的SevenZip异常信息
            e.printStackTraceExtended();
        } catch (Exception e) {
            // 中文注释：捕获其他异常
            System.err.println("Error occurs: " + e);
            // 中文注释：打印通用异常信息
        } finally {
            // 中文注释：清理资源，确保归档和文件正确关闭
            if (outArchive != null) {
                try {
                    // 中文注释：尝试关闭归档对象
                    outArchive.close();
                } catch (IOException e) {
                    // 中文注释：捕获关闭归档时的异常
                    System.err.println("Error closing archive: " + e);
                    // 中文注释：关闭失败，标记操作失败
                    success = false;
                }
            }
            if (raf != null) {
                try {
                    // 中文注释：尝试关闭文件
                    raf.close();
                } catch (IOException e) {
                    // 中文注释：捕获关闭文件时的异常
                    System.err.println("Error closing file: " + e);
                    // 中文注释：关闭失败，标记操作失败
                    success = false;
                }
            }
        }
        // 中文注释：根据操作结果打印最终状态
        if (success) {
            System.out.println("Compression operation succeeded");
            // 中文注释：如果成功，打印压缩成功的消息
        }
    }
}
```

[download java code](https://sevenzipjbind.sourceforge.net/snippets/CompressNonGenericZip.java)

If you run this program with (on Linux)

```
$ java -cp ‹path-to-lib›\sevenzipjbinding.jar;              \             ‹path-to-lib›\sevenzipjbinding-Windows-x86.jar;. \        CompressNonGenericZip compressed.zip
```

you will get the output

```
Compression operation succeeded
```

The archive file compressed.zip should be created. It contains files and folders specified in the [CompressArchiveStructure](https://sevenzipjbind.sourceforge.net/compression_snippets.html#archive-test-structure) class.

```
$ 7z l compressed.zipListing archive: compressed.zip--Path = compressed.zipType = zipPhysical Size = 718   Date      Time    Attr         Size   Compressed  Name------------------- ----- ------------ ------------  ------------------------2015-09-09 08:56:42 .....           16           16  info.txt2015-09-09 08:56:42 .....          100          100  random-100-bytes.dump2015-09-09 08:56:42 .....           38           38  dir1/file1.txt2015-09-09 08:56:42 D....            0            0  dir22015-09-09 08:56:42 .....           38           38  dir2/file2.txt------------------- ----- ------------ ------------  ------------------------                                   192          192  4 files, 1 folders
```

### Creating 7-Zip archive using archive format specific API

Creating 7z archive is a little bit easer that creating Zip archives. The main difference is the implementation of the MyCreateCallback.getItemInformation() method. 7z doesn't need relative complex calculation of the attribute property providing a nice default behavior.



```
import java.io.IOException;import java.io.RandomAccessFile;import net.sf.sevenzipjbinding.IOutCreateArchive7z;import net.sf.sevenzipjbinding.IOutCreateCallback;import net.sf.sevenzipjbinding.IOutItem7z;import net.sf.sevenzipjbinding.ISequentialInStream;import net.sf.sevenzipjbinding.SevenZip;import net.sf.sevenzipjbinding.SevenZipException;import net.sf.sevenzipjbinding.impl.OutItemFactory;import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;import net.sf.sevenzipjbinding.junit.snippets.CompressArchiveStructure.Item;import net.sf.sevenzipjbinding.util.ByteArrayStream;public class CompressNonGeneric7z {    /**     * The callback provides information about archive items.     */    private final class MyCreateCallback             implements IOutCreateCallback<IOutItem7z> {        public void setOperationResult(boolean operationResultOk)                throws SevenZipException {            // Track each operation result here        }        public void setTotal(long total) throws SevenZipException {            // Track operation progress here        }        public void setCompleted(long complete) throws SevenZipException {            // Track operation progress here        }        public IOutItem7z getItemInformation(int index,                OutItemFactory<IOutItem7z> outItemFactory) {            IOutItem7z item = outItemFactory.createOutItem();            if (items[index].getContent() == null) {                // Directory                item.setPropertyIsDir(true);            } else {                // File                item.setDataSize((long) items[index].getContent().length);            }            item.setPropertyPath(items[index].getPath());            return item;        }        public ISequentialInStream getStream(int i) throws SevenZipException {            if (items[i].getContent() == null) {                return null;            }            return new ByteArrayStream(items[i].getContent(), true);        }    }    private Item[] items;    public static void main(String[] args) {        if (args.length == 1) {            new CompressNonGeneric7z().compress(args[0]);            return;        }        System.out.println("Usage: java CompressNonGeneric7z <archive>");    }    private void compress(String filename) {        items = CompressArchiveStructure.create();        boolean success = false;        RandomAccessFile raf = null;        IOutCreateArchive7z outArchive = null;        try {            raf = new RandomAccessFile(filename, "rw");            // Open out-archive object            outArchive = SevenZip.openOutArchive7z();            // Configure archive            outArchive.setLevel(5);            outArchive.setSolid(true);            // Create archive            outArchive.createArchive(new RandomAccessFileOutStream(raf),                    items.length, new MyCreateCallback());            success = true;        } catch (SevenZipException e) {            System.err.println("7z-Error occurs:");            // Get more information using extended method            e.printStackTraceExtended();        } catch (Exception e) {            System.err.println("Error occurs: " + e);        } finally {            if (outArchive != null) {                try {                    outArchive.close();                } catch (IOException e) {                    System.err.println("Error closing archive: " + e);                    success = false;                }            }            if (raf != null) {                try {                    raf.close();                } catch (IOException e) {                    System.err.println("Error closing file: " + e);                    success = false;                }            }        }        if (success) {            System.out.println("Compression operation succeeded");        }    }}
```

[download java code](https://sevenzipjbind.sourceforge.net/snippets/CompressNonGeneric7z.java)

For instructions on how to running the snippet and check the results see [Creating Zip archive using archive format specific API](https://sevenzipjbind.sourceforge.net/compression_snippets.html#create-specific-api-zip).

### Creating Tar archive using archive format specific API

Creating tar archives is pretty much the same, as creating 7z archives, since the default values for most properties are good enough in most cases. Note, that the tar archive format do have attribute property. But due to the Unix-nature of the tar, it was renamed to PosixAttributes. Also the meaning of the bits is different.



```
import java.io.IOException;import java.io.RandomAccessFile;import net.sf.sevenzipjbinding.IOutCreateArchiveTar;import net.sf.sevenzipjbinding.IOutCreateCallback;import net.sf.sevenzipjbinding.IOutItemTar;import net.sf.sevenzipjbinding.ISequentialInStream;import net.sf.sevenzipjbinding.SevenZip;import net.sf.sevenzipjbinding.SevenZipException;import net.sf.sevenzipjbinding.impl.OutItemFactory;import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;import net.sf.sevenzipjbinding.junit.snippets.CompressArchiveStructure.Item;import net.sf.sevenzipjbinding.util.ByteArrayStream;public class CompressNonGenericTar {    /**     * The callback provides information about archive items.     */    private final class MyCreateCallback             implements IOutCreateCallback<IOutItemTar> {        public void setOperationResult(boolean operationResultOk)                throws SevenZipException {            // Track each operation result here        }        public void setTotal(long total) throws SevenZipException {            // Track operation progress here        }        public void setCompleted(long complete) throws SevenZipException {            // Track operation progress here        }        public IOutItemTar getItemInformation(int index,                OutItemFactory<IOutItemTar> outItemFactory) {            IOutItemTar item = outItemFactory.createOutItem();            if (items[index].getContent() == null) {                // Directory                item.setPropertyIsDir(true);            } else {                // File                item.setDataSize((long) items[index].getContent().length);            }            item.setPropertyPath(items[index].getPath());            return item;        }        public ISequentialInStream getStream(int i) throws SevenZipException {            if (items[i].getContent() == null) {                return null;            }            return new ByteArrayStream(items[i].getContent(), true);        }    }    private Item[] items;    public static void main(String[] args) {        if (args.length == 1) {            new CompressNonGenericTar().compress(args[0]);            return;        }        System.out.println("Usage: java CompressNonGenericTar <archive>");    }    private void compress(String filename) {        items = CompressArchiveStructure.create();        boolean success = false;        RandomAccessFile raf = null;        IOutCreateArchiveTar outArchive = null;        try {            raf = new RandomAccessFile(filename, "rw");            // Open out-archive object            outArchive = SevenZip.openOutArchiveTar();            // No configuration methods for Tar            // Create archive            outArchive.createArchive(new RandomAccessFileOutStream(raf),                    items.length, new MyCreateCallback());            success = true;        } catch (SevenZipException e) {            System.err.println("Tar-Error occurs:");            // Get more information using extended method            e.printStackTraceExtended();        } catch (Exception e) {            System.err.println("Error occurs: " + e);        } finally {            if (outArchive != null) {                try {                    outArchive.close();                } catch (IOException e) {                    System.err.println("Error closing archive: " + e);                    success = false;                }            }            if (raf != null) {                try {                    raf.close();                } catch (IOException e) {                    System.err.println("Error closing file: " + e);                    success = false;                }            }        }        if (success) {            System.out.println("Compression operation succeeded");        }    }}
```

[download java code](https://sevenzipjbind.sourceforge.net/snippets/CompressNonGenericTar.java)

For instructions on how to running the snippet and check the results see [Creating Zip archive using archive format specific API](https://sevenzipjbind.sourceforge.net/compression_snippets.html#create-specific-api-zip).

### Creating GZip archive using archive format specific API

GZip format is a stream archive format meaning, that it can only compress a single file. This simplifies the programming quite a bit. In the following snippet a single message passed through the second command line parameter get compressed. Note, that like non-stream archive formats GZip also supports optional Path and LastModificationTime properties for the single archive item.



```
import java.io.IOException;import java.io.RandomAccessFile;import net.sf.sevenzipjbinding.IOutCreateArchiveGZip;import net.sf.sevenzipjbinding.IOutCreateCallback;import net.sf.sevenzipjbinding.IOutItemGZip;import net.sf.sevenzipjbinding.ISequentialInStream;import net.sf.sevenzipjbinding.SevenZip;import net.sf.sevenzipjbinding.SevenZipException;import net.sf.sevenzipjbinding.impl.OutItemFactory;import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;import net.sf.sevenzipjbinding.util.ByteArrayStream;public class CompressNonGenericGZip {    /**     * The callback provides information about archive items.     */    private final class MyCreateCallback             implements IOutCreateCallback<IOutItemGZip> {        public void setOperationResult(boolean operationResultOk)                throws SevenZipException {            // Track each operation result here        }        public void setTotal(long total) throws SevenZipException {            // Track operation progress here        }        public void setCompleted(long complete) throws SevenZipException {            // Track operation progress here        }        public IOutItemGZip getItemInformation(int index,                OutItemFactory<IOutItemGZip> outItemFactory) {            IOutItemGZip item = outItemFactory.createOutItem();            item.setDataSize((long) content.length);            return item;        }        public ISequentialInStream getStream(int i) throws SevenZipException {            return new ByteArrayStream(content, true);        }    }    byte[] content;    public static void main(String[] args) {        if (args.length == 1) {            new CompressNonGenericGZip().compress(args[0]);            return;        }        System.out.println("Usage: java CompressNonGenericGZip <archive>");    }    private void compress(String filename) {        boolean success = false;        RandomAccessFile raf = null;        IOutCreateArchiveGZip outArchive = null;        content = CompressArchiveStructure.create()[0].getContent();        try {            raf = new RandomAccessFile(filename, "rw");            // Open out-archive object            outArchive = SevenZip.openOutArchiveGZip();            // Configure archive            outArchive.setLevel(5);            // Create archive            outArchive.createArchive(new RandomAccessFileOutStream(raf),                    1, new MyCreateCallback());            success = true;        } catch (SevenZipException e) {            System.err.println("GZip-Error occurs:");            // Get more information using extended method            e.printStackTraceExtended();        } catch (Exception e) {            System.err.println("Error occurs: " + e);        } finally {            if (outArchive != null) {                try {                    outArchive.close();                } catch (IOException e) {                    System.err.println("Error closing archive: " + e);                    success = false;                }            }            if (raf != null) {                try {                    raf.close();                } catch (IOException e) {                    System.err.println("Error closing file: " + e);                    success = false;                }            }        }        if (success) {            System.out.println("Compression operation succeeded");        }    }}
```

[download java code](https://sevenzipjbind.sourceforge.net/snippets/CompressNonGenericGZip.java)

For instructions on how to running the snippet and check the results see [Creating Zip archive using archive format specific API](https://sevenzipjbind.sourceforge.net/compression_snippets.html#create-specific-api-zip).

### Creating BZip2 archive using archive format specific API

BZip2 is like GZip a stream archive format. It compresses single archive item supporting no additional archive item properties at all.



```
import java.io.IOException;import java.io.RandomAccessFile;import net.sf.sevenzipjbinding.IOutCreateArchiveBZip2;import net.sf.sevenzipjbinding.IOutCreateCallback;import net.sf.sevenzipjbinding.IOutItemBZip2;import net.sf.sevenzipjbinding.ISequentialInStream;import net.sf.sevenzipjbinding.SevenZip;import net.sf.sevenzipjbinding.SevenZipException;import net.sf.sevenzipjbinding.impl.OutItemFactory;import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;import net.sf.sevenzipjbinding.util.ByteArrayStream;public class CompressNonGenericBZip2 {    /**     * The callback provides information about archive items.     */    private final class MyCreateCallback             implements IOutCreateCallback<IOutItemBZip2> {        public void setOperationResult(boolean operationResultOk)                throws SevenZipException {            // Track each operation result here        }        public void setTotal(long total) throws SevenZipException {            // Track operation progress here        }        public void setCompleted(long complete) throws SevenZipException {            // Track operation progress here        }        public IOutItemBZip2 getItemInformation(int index,                OutItemFactory<IOutItemBZip2> outItemFactory) {            IOutItemBZip2 item = outItemFactory.createOutItem();            item.setDataSize((long) content.length);            return item;        }        public ISequentialInStream getStream(int i) throws SevenZipException {            return new ByteArrayStream(content, true);        }    }    byte[] content;    public static void main(String[] args) {        if (args.length == 1) {            new CompressNonGenericBZip2().compress(args[0]);            return;        }        System.out.println("Usage: java CompressNonGenericBZip2 <archive>");    }    private void compress(String filename) {        boolean success = false;        RandomAccessFile raf = null;        IOutCreateArchiveBZip2 outArchive = null;        content = CompressArchiveStructure.create()[0].getContent();        try {            raf = new RandomAccessFile(filename, "rw");            // Open out-archive object            outArchive = SevenZip.openOutArchiveBZip2();            // Configure archive            outArchive.setLevel(5);            // Create archive            outArchive.createArchive(new RandomAccessFileOutStream(raf),                    1, new MyCreateCallback());            success = true;        } catch (SevenZipException e) {            System.err.println("BZip2-Error occurs:");            // Get more information using extended method            e.printStackTraceExtended();        } catch (Exception e) {            System.err.println("Error occurs: " + e);        } finally {            if (outArchive != null) {                try {                    outArchive.close();                } catch (IOException e) {                    System.err.println("Error closing archive: " + e);                    success = false;                }            }            if (raf != null) {                try {                    raf.close();                } catch (IOException e) {                    System.err.println("Error closing file: " + e);                    success = false;                }            }        }        if (success) {            System.out.println("Compression operation succeeded");        }    }}
```

[download java code](https://sevenzipjbind.sourceforge.net/snippets/CompressNonGenericBZip2.java)

For instructions on how to running the snippet and check the results see [Creating Zip archive using archive format specific API](https://sevenzipjbind.sourceforge.net/compression_snippets.html#create-specific-api-zip).

## Creating archives with the generic API

The one of the great features of the 7-Zip (and though of the 7-Zip-JBinding) is the ability to write archive format independent code supporting most or even all of the archive formats, supported by 7-Zip. The following code snippet accepts the required archive format as the first parameter compressing the test data in the specified archive format.The key steps to write a generic compression code are

-

- Use [IOutItemAllFormats](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutItemAllFormats.html) interface instead of the one of the archive specific interfaces, like [IOutItem7z](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutItem7z.html)
- Create out-archive object using generic create method [SevenZip](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/SevenZip.html).openOutArchive([ArchiveFormat](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/ArchiveFormat.html))



```
import java.io.IOException;import java.io.RandomAccessFile;import net.sf.sevenzipjbinding.ArchiveFormat;import net.sf.sevenzipjbinding.IOutCreateArchive;import net.sf.sevenzipjbinding.IOutCreateCallback;import net.sf.sevenzipjbinding.IOutFeatureSetLevel;import net.sf.sevenzipjbinding.IOutFeatureSetMultithreading;import net.sf.sevenzipjbinding.IOutItemAllFormats;import net.sf.sevenzipjbinding.ISequentialInStream;import net.sf.sevenzipjbinding.SevenZip;import net.sf.sevenzipjbinding.SevenZipException;import net.sf.sevenzipjbinding.impl.OutItemFactory;import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;import net.sf.sevenzipjbinding.junit.snippets.CompressArchiveStructure.Item;import net.sf.sevenzipjbinding.util.ByteArrayStream;public class CompressGeneric {    /**     * The callback provides information about archive items.     */    private final class MyCreateCallback             implements IOutCreateCallback<IOutItemAllFormats> {        public void setOperationResult(boolean operationResultOk)                throws SevenZipException {            // Track each operation result here        }        public void setTotal(long total) throws SevenZipException {            // Track operation progress here        }        public void setCompleted(long complete) throws SevenZipException {            // Track operation progress here        }        public IOutItemAllFormats getItemInformation(int index,                OutItemFactory<IOutItemAllFormats> outItemFactory) {            IOutItemAllFormats item = outItemFactory.createOutItem();            if (items[index].getContent() == null) {                // Directory                item.setPropertyIsDir(true);            } else {                // File                item.setDataSize((long) items[index].getContent().length);            }            item.setPropertyPath(items[index].getPath());            return item;        }        public ISequentialInStream getStream(int i) throws SevenZipException {            if (items[i].getContent() == null) {                return null;            }            return new ByteArrayStream(items[i].getContent(), true);        }    }    private Item[] items;    public static void main(String[] args) {        if (args.length != 3) {            System.out.println("Usage: java CompressGeneric "                     + "<archive-format> <archive> <count-of-files>");            for (ArchiveFormat af : ArchiveFormat.values()) {                if (af.isOutArchiveSupported()) {                    System.out.println("Supported formats: " + af.name());                }            }            return;        }        int itemsCount = Integer.valueOf(args[2]);        new CompressGeneric().compress(args[0], args[1], itemsCount);    }    private void compress(String filename, String fmtName, int count) {        items = CompressArchiveStructure.create();        boolean success = false;        RandomAccessFile raf = null;        IOutCreateArchive<IOutItemAllFormats> outArchive = null;        ArchiveFormat archiveFormat = ArchiveFormat.valueOf(fmtName);        try {            raf = new RandomAccessFile(filename, "rw");            // Open out-archive object            outArchive = SevenZip.openOutArchive(archiveFormat);            // Configure archive            if (outArchive instanceof IOutFeatureSetLevel) {                ((IOutFeatureSetLevel) outArchive).setLevel(5);            }            if (outArchive instanceof IOutFeatureSetMultithreading) {                ((IOutFeatureSetMultithreading) outArchive).setThreadCount(2);            }            // Create archive            outArchive.createArchive(new RandomAccessFileOutStream(raf),                    count, new MyCreateCallback());            success = true;        } catch (SevenZipException e) {            System.err.println("7z-Error occurs:");            // Get more information using extended method            e.printStackTraceExtended();        } catch (Exception e) {            System.err.println("Error occurs: " + e);        } finally {            if (outArchive != null) {                try {                    outArchive.close();                } catch (IOException e) {                    System.err.println("Error closing archive: " + e);                    success = false;                }            }            if (raf != null) {                try {                    raf.close();                } catch (IOException e) {                    System.err.println("Error closing file: " + e);                    success = false;                }            }        }        if (success) {            System.out.println(archiveFormat.getMethodName()                     + " archive with " + count + " item(s) created");        }    }}
```

[download java code](https://sevenzipjbind.sourceforge.net/snippets/CompressGeneric.java)

Now you can run the snippet with different parameters creating archives with different formats. The last parameter specifies, how many archive items from the [CompressArchiveStructure](https://sevenzipjbind.sourceforge.net/compression_snippets.html#archive-test-structure) should be compressed. This number should be between 1 and 5 for 7z, Zip and Tar, and must be 1 for the stream formats GZip and BZip2.

- To create a 7z archive with 5 items:

  ```
  C:\Test> java -cp ... CompressGeneric SEVEN_ZIP compressed_generic.zip 57z archive with 5 item(s) created
  ```

- To create a Zip archive with 5 items:

  ```
  C:\Test> java -cp ... CompressGeneric ZIP       compressed_generic.zip 5Zip archive with 5 item(s) created
  ```

- To create a Tar archive with 5 items:

  ```
  C:\Test> java -cp ... CompressGeneric TAR       compressed_generic.tar 5Tar archive with 5 item(s) created
  ```

- To create a GZip archive with 1 item:

  ```
  C:\Test> java -cp ... CompressGeneric GZIP      compressed_generic.zip 1GZip archive with 1 item(s) created
  ```

- To create a BZip2 archive with 1 item:

  ```
  C:\Test> java -cp ... CompressGeneric BZIP2     compressed_generic.bz2 1BZip2 archive with 1 item(s) created
  ```

Also a bunch of the compressed_generic.* archives should be created with the corresponding contents.

## Creating password protected archives

Depending on the archive format it's possible to create and update password protected archives or even encrypt the archive header. Encrypting archive items secures the content of the item leaving the item meta data (like archive item name, size, ...) unprotected. The header encryption solves this problem by encrypting the entire archive.

To create a password protected archive the create- or update-callback class should additionally implement the [ICryptoGetTextPassword](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/ICryptoGetTextPassword.html) interface. The cryptoGetTextPassword() method of the interface is called to get the password for the encryption. The header encryption can be activated using [IOutFeatureSetEncryptHeader](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutFeatureSetEncryptHeader.html).setHeaderEncryption() method of the outArchive object, if the outArchive object implements the [IOutFeatureSetEncryptHeader](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutFeatureSetEncryptHeader.html) interface an though supports the header encryption.

The following snippet takes the password as a second argument of the main() method. It uses the password to encrypt archive items. It also activates the header encryption. It's supported by the selected 7z algorithm.

```
import java.io.IOException;import java.io.RandomAccessFile;import net.sf.sevenzipjbinding.ICryptoGetTextPassword;import net.sf.sevenzipjbinding.IOutCreateArchive7z;import net.sf.sevenzipjbinding.IOutCreateCallback;import net.sf.sevenzipjbinding.IOutItem7z;import net.sf.sevenzipjbinding.ISequentialInStream;import net.sf.sevenzipjbinding.SevenZip;import net.sf.sevenzipjbinding.SevenZipException;import net.sf.sevenzipjbinding.impl.OutItemFactory;import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;import net.sf.sevenzipjbinding.junit.snippets.CompressArchiveStructure.Item;import net.sf.sevenzipjbinding.util.ByteArrayStream;public class CompressWithPassword {    /**     * The callback provides information about archive items.     *     * It also implements     * <ul>     * <li>{@link ICryptoGetTextPassword}     * </ul>     * to provide a password for encryption.     */    private final class MyCreateCallback             implements IOutCreateCallback<IOutItem7z>, ICryptoGetTextPassword {        public void setOperationResult(boolean operationResultOk)                throws SevenZipException {            // Track each operation result here        }        public void setTotal(long total) throws SevenZipException {            // Track operation progress here        }        public void setCompleted(long complete) throws SevenZipException {            // Track operation progress here        }        public IOutItem7z getItemInformation(int index,                OutItemFactory<IOutItem7z> outItemFactory) {            IOutItem7z item = outItemFactory.createOutItem();            if (items[index].getContent() == null) {                // Directory                item.setPropertyIsDir(true);            } else {                // File                item.setDataSize((long) items[index].getContent().length);            }            item.setPropertyPath(items[index].getPath());            return item;        }        public ISequentialInStream getStream(int i) throws SevenZipException {            if (items[i].getContent() == null) {                return null;            }            return new ByteArrayStream(items[i].getContent(), true);        }        public String cryptoGetTextPassword() throws SevenZipException {            return password;        }    }    private Item[] items;    private String password;    public static void main(String[] args) {        if (args.length  == 2) {            new CompressWithPassword().compress(args[0], args[1]);            return;        }        System.out.println("Usage: java CompressWithPassword <archive> <pass>");    }    private void compress(String filename, String pass) {        items = CompressArchiveStructure.create();        password = pass;        boolean success = false;        RandomAccessFile raf = null;        IOutCreateArchive7z outArchive = null;        try {            raf = new RandomAccessFile(filename, "rw");            // Open out-archive object            outArchive = SevenZip.openOutArchive7z();            // Header encryption is only available for 7z            outArchive.setHeaderEncryption(true);            // Create archive            outArchive.createArchive(new RandomAccessFileOutStream(raf),                    items.length, new MyCreateCallback());            success = true;        } catch (SevenZipException e) {            System.err.println("7z-Error occurs:");            // Get more information using extended method            e.printStackTraceExtended();        } catch (Exception e) {            System.err.println("Error occurs: " + e);        } finally {            if (outArchive != null) {                try {                    outArchive.close();                } catch (IOException e) {                    System.err.println("Error closing archive: " + e);                    success = false;                }            }            if (raf != null) {                try {                    raf.close();                } catch (IOException e) {                    System.err.println("Error closing file: " + e);                    success = false;                }            }        }        if (success) {            System.out.println("Compression operation succeeded");        }    }}
```

[download java code](https://sevenzipjbind.sourceforge.net/snippets/CompressWithPassword.java)

You can test the snippet like this:

```
C:\Test> java -cp ... CompressWithPassword compressed_with_pass.zip my-passCompression operation succeeded
```



# Modifying existing archives

7-Zip-JBinding provides API for archive modification. Especially by small changes the modification of an archive is much faster compared to the extraction and the consequent compression. The archive modification API (like the compression API) offers archive format specific and archive format independent variants. The process of the modification of an existing archive contains following steps:

-

- Open existing archive obtaining an instance of the [IInArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IInArchive.html)
- Call [IInArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IInArchive.html).getConnectedOutArchive() to get an instance of the [IOutUpdateArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutUpdateArchive.html)
- Optionally cast it to an archive format specific API interface like [IOutUpdateArchiveZip](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutUpdateArchiveZip.html)
- Call [IOutUpdateArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutUpdateArchive.html).updateItems() passing the new count of items and a callback implementing the modification

The following snippets show the modification process in details using archive format independent API. The archive to be modified is one of the Zip, 7z or Tar archives created by the corresponding compression snippets on this page. The structure of those archives is specified in the [CompressArchiveStructure](https://sevenzipjbind.sourceforge.net/compression_snippets.html#archive-test-structure) class.

## Altering existing archive items

The first snippet modifies one existing item with index 2 (info.txt, 16 bytes):It changes the path (name of the item) to the "info2.txt"It changes the content to the "More Info!" (10 bytes)



```
import java.io.Closeable;import java.io.RandomAccessFile;import java.util.ArrayList;import java.util.List;import net.sf.sevenzipjbinding.IInArchive;import net.sf.sevenzipjbinding.IInStream;import net.sf.sevenzipjbinding.IOutCreateCallback;import net.sf.sevenzipjbinding.IOutItemAllFormats;import net.sf.sevenzipjbinding.IOutUpdateArchive;import net.sf.sevenzipjbinding.ISequentialInStream;import net.sf.sevenzipjbinding.SevenZip;import net.sf.sevenzipjbinding.SevenZipException;import net.sf.sevenzipjbinding.impl.OutItemFactory;import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;import net.sf.sevenzipjbinding.util.ByteArrayStream;public class UpdateAlterItems {    /**     * The callback defines the modification to be made.     */    private final class MyCreateCallback             implements IOutCreateCallback<IOutItemAllFormats> {        public void setOperationResult(boolean operationResultOk)                throws SevenZipException {            // Track each operation result here        }        public void setTotal(long total) throws SevenZipException {            // Track operation progress here        }        public void setCompleted(long complete) throws SevenZipException {            // Track operation progress here        }        public IOutItemAllFormats getItemInformation(int index,                OutItemFactory<IOutItemAllFormats> outItemFactory)                 throws SevenZipException {            if (index != 2) {                // Keep properties and content                return outItemFactory.createOutItem(index);            }            IOutItemAllFormats item;            item = outItemFactory.createOutItemAndCloneProperties(index);            // Change property PATH            item.setUpdateIsNewProperties(true);            item.setPropertyPath("info2.txt");            // Change content            item.setUpdateIsNewData(true);            item.setDataSize((long) NEW_CONTENT.length);            return item;        }        public ISequentialInStream getStream(int i) throws SevenZipException {            if (i != 2) {                return null;            }            return new ByteArrayStream(NEW_CONTENT, true);        }    }    static final byte[] NEW_CONTENT = "More Info!".getBytes();    public static void main(String[] args) {        if (args.length == 2) {            new UpdateAlterItems().compress(args[0], args[1]);            return;        }        System.out.println("Usage: java UpdateAlterItems <in-arc> <out-arc>");    }    private void compress(String in, String out) {        boolean success = false;        RandomAccessFile inRaf = null;        RandomAccessFile outRaf = null;        IInArchive inArchive;        IOutUpdateArchive<IOutItemAllFormats> outArchive = null;        List<Closeable> closeables = new ArrayList<Closeable>();        try {            // Open input file            inRaf = new RandomAccessFile(in, "r");            closeables.add(inRaf);            IInStream inStream = new RandomAccessFileInStream(inRaf);            // Open in-archive            inArchive = SevenZip.openInArchive(null, inStream);            closeables.add(inArchive);            outRaf = new RandomAccessFile(out, "rw");            closeables.add(outRaf);            // Open out-archive object            outArchive = inArchive.getConnectedOutArchive();            // Modify archive            outArchive.updateItems(new RandomAccessFileOutStream(outRaf),                    inArchive.getNumberOfItems(), new MyCreateCallback());            success = true;        } catch (SevenZipException e) {            System.err.println("7z-Error occurs:");            // Get more information using extended method            e.printStackTraceExtended();        } catch (Exception e) {            System.err.println("Error occurs: " + e);        } finally {            for (int i = closeables.size() - 1; i >= 0; i--) {                try {                    closeables.get(i).close();                } catch (Throwable e) {                    System.err.println("Error closing resource: " + e);                    success = false;                }            }        }        if (success) {            System.out.println("Update successful");        }    }}
```

[download java code](https://sevenzipjbind.sourceforge.net/snippets/UpdateAlterItems.java)

If you run this program with (on Linux)

```
$ java -cp ‹path-to-lib›\sevenzipjbinding.jar;              \            ‹path-to-lib›\sevenzipjbinding-Windows-x86.jar;. \        UpdateAlterItems /testdata/snippets/to-update.7z updated.7z
```

you will get the output

```
Update successful
```

Now lets look at original and modified archives:

```
$ 7z l /testdata/snippets/to-update.7z...   Date      Time    Attr         Size   Compressed  Name------------------- ----- ------------ ------------  ------------------------2015-09-14 07:57:09 .....           38          159  dir1/file1.txt2015-09-14 07:57:09 .....           38               dir2/file2.txt2015-09-14 07:57:09 .....           16               info.txt2015-09-14 07:57:09 .....          100               random-100-bytes.dump2015-09-14 07:57:09 D....            0            0  dir2/------------------- ----- ------------ ------------  ------------------------                                   192          159  4 files, 1 folders$ 7z l updated.7z   Date      Time    Attr         Size   Compressed  Name------------------- ----- ------------ ------------  ------------------------2015-09-14 07:57:09 .....           38          151  dir1/file1.txt2015-09-14 07:57:09 .....           38               dir2/file2.txt2015-09-14 07:57:09 .....          100               random-100-bytes.dump2015-09-14 07:57:09 .....           10           16  info2.txt2015-09-14 07:57:09 D....            0            0  dir2/------------------- ----- ------------ ------------  ------------------------                                   186          167  4 files, 1 folders
```

As you can see, the file "info.txt" (16 bytes) was replaces with the file "info2.txt" (10 bytes).

## Adding and removing archive items

Now lets see how archive items can be added and removed. In order to remove an archive item a reindexing is necessary. In the previous snippet for each archive item the indexes in the old archive and the index in the new archive were the same. But after removing one item all consequent indexes in the new archive will change and will be less, that corresponding indexes in the old archive. Here is an example of removing an item C with index 2:`Index:          0      1      2      3      4 Old archive:   (A)    (B)    (C)    (D)    (E) New archive:   (A)    (B)    (D)    (E) `Here the index of D in the old archive is 3, but in the new archive is 2.

In order to add a new item the count of items in archive passed to the [IOutArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutArchive.html).updateItems() method should be increased. In the callback the new item with the new index (that doesn't map to any old archive items) should be initialized exactly, like new items get initialized during a compression operation. The next snippet

- Removes "info.txt" file
- Adds "data.dmp" file with 11 bytes of content

```
import java.io.Closeable;import java.io.RandomAccessFile;import java.util.ArrayList;import java.util.List;import net.sf.sevenzipjbinding.IInArchive;import net.sf.sevenzipjbinding.IInStream;import net.sf.sevenzipjbinding.IOutCreateCallback;import net.sf.sevenzipjbinding.IOutItemAllFormats;import net.sf.sevenzipjbinding.IOutUpdateArchive;import net.sf.sevenzipjbinding.ISequentialInStream;import net.sf.sevenzipjbinding.PropID;import net.sf.sevenzipjbinding.SevenZip;import net.sf.sevenzipjbinding.SevenZipException;import net.sf.sevenzipjbinding.impl.OutItemFactory;import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;import net.sf.sevenzipjbinding.util.ByteArrayStream;public class UpdateAddRemoveItems {    /**     * The callback defines the modification to be made.     */    private final class MyCreateCallback             implements IOutCreateCallback<IOutItemAllFormats> {        public void setOperationResult(boolean operationResultOk)                throws SevenZipException {            // Track each operation result here        }        public void setTotal(long total) throws SevenZipException {            // Track operation progress here        }        public void setCompleted(long complete) throws SevenZipException {            // Track operation progress here        }        public IOutItemAllFormats getItemInformation(int index,                OutItemFactory<IOutItemAllFormats> outItemFactory)                 throws SevenZipException {            if (index == itemToAdd) {                // Adding new item                IOutItemAllFormats outItem = outItemFactory.createOutItem();                outItem.setPropertyPath(itemToAddPath);                outItem.setDataSize((long) itemToAddContent.length);                return outItem;            }            // Remove item by changing the mapping "new index"->"old index"            if (index < itemToRemove) {                return outItemFactory.createOutItem(index);            }            return outItemFactory.createOutItem(index + 1);        }        public ISequentialInStream getStream(int i) throws SevenZipException {            if (i != itemToAdd) {                return null;            }            return new ByteArrayStream(itemToAddContent, true);        }    }    int itemToAdd; // New index of the item to add    String itemToAddPath;    byte[] itemToAddContent;    int itemToRemove; // Old index of the item to be removed    private void initUpdate(IInArchive inArchive) throws SevenZipException {        itemToAdd = inArchive.getNumberOfItems() - 1;        itemToAddPath = "data.dmp";        itemToAddContent = "dmp-content".getBytes();        itemToRemove = -1;        for (int i = 0; i < inArchive.getNumberOfItems(); i++) {            if (inArchive.getProperty(i, PropID.PATH).equals("info.txt")) {                itemToRemove = i;                break;            }        }        if (itemToRemove == -1) {            throw new RuntimeException("Item 'info.txt' not found");        }    }    public static void main(String[] args) {        if (args.length == 2) {            new UpdateAddRemoveItems().compress(args[0], args[1]);            return;        }        System.out.println("Usage: java UpdateAddRemoveItems <in> <out>");    }    private void compress(String in, String out) {        boolean success = false;        RandomAccessFile inRaf = null;        RandomAccessFile outRaf = null;        IInArchive inArchive;        IOutUpdateArchive<IOutItemAllFormats> outArchive = null;        List<Closeable> closeables = new ArrayList<Closeable>();        try {            // Open input file            inRaf = new RandomAccessFile(in, "r");            closeables.add(inRaf);            IInStream inStream = new RandomAccessFileInStream(inRaf);            // Open in-archive            inArchive = SevenZip.openInArchive(null, inStream);            closeables.add(inArchive);            initUpdate(inArchive);            outRaf = new RandomAccessFile(out, "rw");            closeables.add(outRaf);            // Open out-archive object            outArchive = inArchive.getConnectedOutArchive();            // Modify archive            outArchive.updateItems(new RandomAccessFileOutStream(outRaf),                    inArchive.getNumberOfItems(), new MyCreateCallback());            success = true;        } catch (SevenZipException e) {            System.err.println("7z-Error occurs:");            // Get more information using extended method            e.printStackTraceExtended();        } catch (Exception e) {            System.err.println("Error occurs: " + e);        } finally {            for (int i = closeables.size() - 1; i >= 0; i--) {                try {                    closeables.get(i).close();                } catch (Throwable e) {                    System.err.println("Error closing resource: " + e);                    success = false;                }            }        }        if (success) {            System.out.println("Update successful");        }    }}
```

[download java code](https://sevenzipjbind.sourceforge.net/snippets/UpdateAddRemoveItems.java)

If you run this program with (on Linux)

```
$ java -cp ‹path-to-lib›\sevenzipjbinding.jar;              \            ‹path-to-lib›\sevenzipjbinding-Windows-x86.jar;. \        UpdateAddRemoveItems ‹git›/testdata/snippets/to-update.7z updated.7z
```

you will get the output

```
Update successful
```

Now lets look at original and modified archives:

```
$ 7z l /testdata/snippets/to-update.7z...   Date      Time    Attr         Size   Compressed  Name------------------- ----- ------------ ------------  ------------------------2015-09-14 07:57:09 .....           38          159  dir1/file1.txt2015-09-14 07:57:09 .....           38               dir2/file2.txt2015-09-14 07:57:09 .....           16               info.txt2015-09-14 07:57:09 .....          100               random-100-bytes.dump2015-09-14 07:57:09 D....            0            0  dir2/------------------- ----- ------------ ------------  ------------------------                                   192          159  4 files, 1 folders$ 7z l updated.7z...   Date      Time    Attr         Size   Compressed  Name------------------- ----- ------------ ------------  ------------------------2015-09-14 07:57:09 .....           38          151  dir1/file1.txt2015-09-14 07:57:09 .....           38               dir2/file2.txt2015-09-14 07:57:09 .....          100               random-100-bytes.dump2015-09-16 21:43:52 .....           11           16  data.dmp2015-09-14 07:57:09 D....            0            0  dir2/------------------- ----- ------------ ------------  ------------------------                                   187          167  4 files, 1 folders
```

As you can see, the file info.txt is gone and the file data.dmp (11 bytes) appears in the archive.

# Troubleshoot problems using tracing

One of the weak sides of the 7-zip compression engine is a rather simple error reporting. If some provided data doesn't satisfy the compressor it fails without any descriptive error message. One way to get an clue is to use 7-Zip-JBinding tracing feature. Here is the code passing invalid data size for the item 1 and though failing.



```
import java.io.IOException;import java.io.RandomAccessFile;import net.sf.sevenzipjbinding.IOutCreateArchiveTar;import net.sf.sevenzipjbinding.IOutCreateCallback;import net.sf.sevenzipjbinding.IOutItemTar;import net.sf.sevenzipjbinding.ISequentialInStream;import net.sf.sevenzipjbinding.SevenZip;import net.sf.sevenzipjbinding.SevenZipException;import net.sf.sevenzipjbinding.impl.OutItemFactory;import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;import net.sf.sevenzipjbinding.junit.snippets.CompressArchiveStructure.Item;import net.sf.sevenzipjbinding.util.ByteArrayStream;public class CompressWithError {    /**     * The callback provides information about archive items.     */    private final class MyCreateCallback             implements IOutCreateCallback<IOutItemTar> {        public void setOperationResult(boolean operationResultOk)                throws SevenZipException {            // Track each operation result here        }        public void setTotal(long total) throws SevenZipException {            // Track operation progress here        }        public void setCompleted(long complete) throws SevenZipException {            // Track operation progress here        }        public IOutItemTar getItemInformation(int index,                OutItemFactory<IOutItemTar> outItemFactory) {            IOutItemTar item = outItemFactory.createOutItem();            if (items[index].getContent() == null) {                // Directory                item.setPropertyIsDir(true);            } else {                // File                item.setDataSize((long) items[index].getContent().length);            }            item.setPropertyPath(items[index].getPath());            if (index == 1) {                item.setDataSize(null); // Provide invalid data for item 1            }            return item;        }        public ISequentialInStream getStream(int i) throws SevenZipException {            if (items[i].getContent() == null) {                return null;            }            return new ByteArrayStream(items[i].getContent(), true);        }    }    private Item[] items;    public static void main(String[] args) {        if (args.length == 1) {            new CompressWithError().compress(args[0]);            return;        }        System.out.println("Usage: java CompressNonGenericTar <archive>");    }    private void compress(String filename) {        items = CompressArchiveStructure.create();        boolean success = false;        RandomAccessFile raf = null;        IOutCreateArchiveTar outArchive = null;        try {            raf = new RandomAccessFile(filename, "rw");            // Open out-archive object            outArchive = SevenZip.openOutArchiveTar();            // Activate tracing            outArchive.setTrace(true);            // Create archive            outArchive.createArchive(new RandomAccessFileOutStream(raf),                    items.length, new MyCreateCallback());            success = true;        } catch (SevenZipException e) {            System.err.println("Tar-Error occurs:");            // Get more information using extended method            e.printStackTraceExtended();        } catch (Exception e) {            System.err.println("Error occurs: " + e);        } finally {            if (outArchive != null) {                try {                    outArchive.close();                } catch (IOException e) {                    System.err.println("Error closing archive: " + e);                    success = false;                }            }            if (raf != null) {                try {                    raf.close();                } catch (IOException e) {                    System.err.println("Error closing file: " + e);                    success = false;                }            }        }        if (success) {            System.out.println("Compression operation succeeded");        }    }}
```

[download java code](https://sevenzipjbind.sourceforge.net/snippets/CompressWithError.java)

If you run this program you will get the following error message printed to the System.err:

```
0x80070057 (Invalid argument). Error creating 'tar' archive with 5 items
```

The error message provides no useful information for finding the bug. But since the snippet enables tracing by calling [IOutArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutArchive.html).setTrace(true), the trace log get printed to the System.out.

```
Compressing 5 itemsGet update info (new data: true) (new props: true) (old index: -1) (index: 0)Get property 'propertyIsDir' (index: 0)Get property 'propertyPosixAttributes' (index: 0)Get property 'propertyLastModificationTime' (index: 0)Get property 'propertyPath' (index: 0)Get property 'propertyUser' (index: 0)Get property 'propertyGroup' (index: 0)Get property 'dataSize' (index: 0)Get update info (new data: true) (new props: true) (old index: -1) (index: 1)Get property 'propertyIsDir' (index: 1)Get property 'propertyPosixAttributes' (index: 1)Get property 'propertyLastModificationTime' (index: 1)Get property 'propertyPath' (index: 1)Get property 'propertyUser' (index: 1)Get property 'propertyGroup' (index: 1)Get property 'dataSize' (index: 1)
```

[//]: # (As you see, the tracing stops just after 7-zip retrieved the size of the data for the item 1. This suggests, that the value for the size of the data of the item 1 may cause the failure. In this small example, like in most other cases, this will help to find the problem.)