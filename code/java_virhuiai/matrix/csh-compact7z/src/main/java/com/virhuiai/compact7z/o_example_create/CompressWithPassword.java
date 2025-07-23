package com.virhuiai.compact7z.o_example_create;

// 导入必要的Java IO包，用于处理文件输入输出
import java.io.IOException;
// 导入RandomAccessFile类，用于随机访问文件
import java.io.RandomAccessFile;

// 导入SevenZipJBinding相关接口和类，用于7z压缩功能
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
// 导入用于创建7z档案的接口
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
// 导入创建档案时的回调接口
import net.sf.sevenzipjbinding.IOutCreateCallback;
// 导入7z档案项目的接口
import net.sf.sevenzipjbinding.IOutItem7z;
// 导入顺序输入流的接口
import net.sf.sevenzipjbinding.ISequentialInStream;
// 导入SevenZip核心类
import net.sf.sevenzipjbinding.SevenZip;
// 导入SevenZip异常类
import net.sf.sevenzipjbinding.SevenZipException;
// 导入用于创建档案项目的工厂类
import net.sf.sevenzipjbinding.impl.OutItemFactory;
// 导入用于输出流的实现类
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
// 导入用于处理字节数组流的工具类
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * ## Creating password protected archives
 *
 * Depending on the archive format it's possible to create and update password protected archives or even encrypt the archive header. Encrypting archive items secures the content of the item leaving the item meta data (like archive item name, size, ...) unprotected. The header encryption solves this problem by encrypting the entire archive.
 *
 * To create a password protected archive the create- or update-callback class should additionally implement the [ICryptoGetTextPassword](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/ICryptoGetTextPassword.html) interface. The cryptoGetTextPassword() method of the interface is called to get the password for the encryption. The header encryption can be activated using [IOutFeatureSetEncryptHeader](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutFeatureSetEncryptHeader.html).setHeaderEncryption() method of the outArchive object, if the outArchive object implements the [IOutFeatureSetEncryptHeader](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutFeatureSetEncryptHeader.html) interface an though supports the header encryption.
 *
 * The following snippet takes the password as a second argument of the main() method. It uses the password to encrypt archive items. It also activates the header encryption. It's supported by the selected 7z algorithm.
 */
 // 类的主要功能：创建带密码保护的7z压缩档案
 // 实现方式：通过SevenZipJBinding库，结合密码加密和头部加密功能
 // 注意事项：仅7z格式支持头部加密，需确保回调类实现ICryptoGetTextPassword接口
public class CompressWithPassword {
    /**
     * The callback provides information about archive items.
     *
     * It also implements
     * <ul>
     * <li>{@link ICryptoGetTextPassword}
     * </ul>
     * to provide a password for encryption.
     */
    // 内部类MyCreateCallback的主要功能：提供压缩档案项目的元信息及加密密码
    // 实现接口：IOutCreateCallback（提供档案项目信息），ICryptoGetTextPassword（提供加密密码）
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItem7z>, ICryptoGetTextPassword {
        // 方法功能：处理每次压缩操作的结果
        // 参数说明：operationResultOk表示操作是否成功
        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
            // 中文注释：跟踪每次压缩操作的结果，通常用于记录操作状态或错误
        }

        // 方法功能：设置压缩任务的总进度
        // 参数说明：total表示总的压缩数据量
        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            // 中文注释：记录压缩任务的总数据量，用于显示总体进度
        }

        // 方法功能：设置已完成的压缩进度
        // 参数说明：complete表示已完成的压缩数据量
        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            // 中文注释：记录当前已完成的压缩数据量，用于更新进度显示
        }

        // 方法功能：获取指定索引的档案项目信息
        // 参数说明：index表示项目索引，outItemFactory用于创建档案项目对象
        // 返回值：IOutItem7z对象，包含档案项目的元信息
        public IOutItem7z getItemInformation(int index,
                                             OutItemFactory<IOutItem7z> outItemFactory) {
            // 创建新的档案项目对象
            IOutItem7z item = outItemFactory.createOutItem();
            // 中文注释：使用工厂类创建新的IOutItem7z对象，用于存储档案项目的元数据

            if (items[index].getContent() == null) {
                // Directory
                // 中文注释：如果项目内容为空，说明是目录
                item.setPropertyIsDir(true);
                // 中文注释：设置项目属性为目录
            } else {
                // File
                // 中文注释：如果项目内容不为空，说明是文件
                item.setDataSize((long) items[index].getContent().length);
                // 中文注释：设置文件的字节大小
            }

            item.setPropertyPath(items[index].getPath());
            // 中文注释：设置档案项目的路径信息

            return item;
            // 中文注释：返回配置好的档案项目对象
        }

        // 方法功能：为指定索引的项目提供输入流
        // 参数说明：i表示项目索引
        // 返回值：ISequentialInStream对象，用于读取项目内容；若为目录则返回null
        public ISequentialInStream getStream(int i) throws SevenZipException {
            if (items[i].getContent() == null) {
                return null;
                // 中文注释：如果项目是目录，返回null表示无需输入流
            }
            return new ByteArrayStream(items[i].getContent(), true);
            // 中文注释：为文件创建字节数组流，用于读取文件内容
        }

        // 方法功能：提供加密密码
        // 返回值：用于加密的密码字符串
        public String cryptoGetTextPassword() throws SevenZipException {
            return password;
            // 中文注释：返回存储在类变量中的密码，用于档案加密
        }
    }

    // 关键变量：存储待压缩的档案项目数组
    private Item[] items;
    // 关键变量：存储用户提供的加密密码
    private String password;

    // 主方法：程序入口
    // 参数说明：args包含命令行参数，预期为档案路径和密码
    public static void main(String[] args) {
        // 中文注释：程序入口，创建带密码的压缩档案
//        if (args.length  == 2) {
//            new CompressWithPassword().compress("/Volumes/RamDisk/out.7z", "123");
//            return;
//        }
        // 中文注释：检查参数数量，若为2则调用compress方法，否则使用默认参数
        new CompressWithPassword().compress("/Volumes/RamDisk/out.7z", "123");
        // 中文注释：使用默认档案路径和密码调用压缩方法
        System.out.println("Usage: java CompressWithPassword <archive> <pass>");
        // 中文注释：打印程序使用说明
    }

    // 方法功能：执行压缩操作，创建带密码保护的7z档案
    // 参数说明：filename为输出档案路径，pass为加密密码
    private void compress(String filename, String pass) {
        // 初始化档案项目数组
        items = CompressArchiveStructure.create();
        // 中文注释：创建待压缩的档案项目结构
        // 设置加密密码
        password = pass;
        // 中文注释：将传入的密码存储到类变量

        // 关键变量：标记压缩操作是否成功
        boolean success = false;
        // 关键变量：随机访问文件对象
        RandomAccessFile raf = null;
        // 关键变量：7z档案创建对象
        IOutCreateArchive7z outArchive = null;
        // 中文注释：定义压缩操作的状态变量和资源对象
        try {
            // 初始化随机访问文件
            raf = new RandomAccessFile(filename, "rw");
            // 中文注释：以读写模式打开指定的输出档案文件

            // Open out-archive object
            // 打开档案创建对象
            outArchive = SevenZip.openOutArchive7z();
            // 中文注释：初始化7z档案创建对象

            // Header encryption is only available for 7z
            // 仅7z支持头部加密
            outArchive.setHeaderEncryption(true);
            // 中文注释：启用档案头部加密，保护元数据

            // Create archive
            // 创建档案
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    items.length, new MyCreateCallback());
            // 中文注释：调用创建档案方法，传入输出流、项目数量和回调对象

            // 标记压缩成功
            success = true;
            // 中文注释：如果没有异常，设置成功标志
        } catch (SevenZipException e) {
            // 处理SevenZip异常
            System.err.println("7z-Error occurs:");
            // 中文注释：捕获SevenZip相关异常并打印错误信息
            // Get more information using extended method
            e.printStackTraceExtended();
            // 中文注释：打印详细的SevenZip异常堆栈信息
        } catch (Exception e) {
            // 处理其他异常
            System.err.println("Error occurs: " + e);
            // 中文注释：捕获其他异常并打印错误信息
        } finally {
            // 清理资源
            if (outArchive != null) {
                try {
                    outArchive.close();
                    // 中文注释：关闭档案对象
                } catch (IOException e) {
                    System.err.println("Error closing archive: " + e);
                    // 中文注释：捕获关闭档案时的IO异常
                    success = false;
                    // 中文注释：设置失败标志
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                    // 中文注释：关闭随机访问文件
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    // 中文注释：捕获关闭文件时的IO异常
                    success = false;
                    // 中文注释：设置失败标志
                }
            }
        }
        // 输出压缩结果
        if (success) {
            System.out.println("Compression operation succeeded");
            // 中文注释：如果成功，打印压缩成功的消息
        }
    }
}