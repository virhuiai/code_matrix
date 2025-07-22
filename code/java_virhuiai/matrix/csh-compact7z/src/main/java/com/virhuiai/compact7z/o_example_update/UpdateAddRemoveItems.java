package com.virhuiai.compact7z.o_example_update;

// 导入必要的Java和SevenZipJBinding库，用于处理7z压缩文件的操作
import java.io.Closeable;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

// 中文注释：本类用于演示如何在7z压缩文件中添加和删除条目。
// 主要功能：通过重新索引实现条目的添加和删除操作，更新压缩文件内容。
// 注意事项：添加新条目需要增加条目总数，删除条目会导致后续索引发生偏移。

/**
 * ## Adding and removing archive items
 *
 * Now lets see how archive items can be added and removed. In order to remove an archive item a reindexing is necessary. In the previous snippet for each archive item the indexes in the old archive and the index in the new archive were the same. But after removing one item all consequent indexes in the new archive will change and will be less, that corresponding indexes in the old archive. Here is an example of removing an item C with index 2:`Index:          0      1      2      3      4 Old archive:   (A)    (B)    (C)    (D)    (E) New archive:   (A)    (B)    (D)    (E) `Here the index of D in the old archive is 3, but in the new archive is 2.
 *
 * In order to add a new item the count of items in archive passed to the [IOutArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutArchive.html).updateItems() method should be increased. In the callback the new item with the new index (that doesn't map to any old archive items) should be initialized exactly, like new items get initialized during a compression operation. The next snippet
 *
 * - Removes "info.txt" file
 * - Adds "data.dmp" file with 11 bytes of content
 */
// 中文注释：
// 类功能：实现7z压缩文件中条目的添加和删除。
// 主要操作：
// 1. 删除名为“info.txt”的文件。
// 2. 添加名为“data.dmp”的文件，内容为11字节。
// 执行流程：通过回调机制定义修改操作，重新索引条目并更新压缩文件。
// 注意事项：删除条目后，需调整索引映射；添加新条目需指定新索引和内容。
public class UpdateAddRemoveItems {
    /**
     * The callback defines the modification to be made.
     */
    // 中文注释：
    // 内部类功能：定义压缩文件更新操作的回调逻辑。
    // 实现接口：IOutCreateCallback，用于处理添加和删除条目的具体行为。
    // 主要功能：通过回调方法控制条目信息的生成和数据流的提供。
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItemAllFormats> {

        // 中文注释：
        // 方法功能：处理每次操作的结果。
        // 参数：operationResultOk - 操作是否成功的标志。
        // 注意事项：用于跟踪操作状态，异常时抛出SevenZipException。
        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
            // 中文注释：在此跟踪每个操作的结果。
        }

        // 中文注释：
        // 方法功能：设置操作的总进度。
        // 参数：total - 操作的总工作量。
        // 注意事项：用于跟踪整体进度，异常时抛出SevenZipException。
        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            // 中文注释：在此跟踪操作进度。
        }

        // 中文注释：
        // 方法功能：设置操作的当前完成进度。
        // 参数：complete - 当前已完成的工作量。
        // 注意事项：用于更新进度显示，异常时抛出SevenZipException。
        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            // 中文注释：在此跟踪操作进度。
        }

        // 中文注释：
        // 方法功能：根据索引生成条目信息，用于添加或保留条目。
        // 参数：
        // - index：新压缩文件中的条目索引。
        // - outItemFactory：用于创建条目对象的工厂。
        // 返回值：IOutItemAllFormats - 新条目或现有条目的信息对象。
        // 执行流程：
        // 1. 如果索引等于待添加条目索引，创建新条目并设置路径和数据大小。
        // 2. 如果索引小于待删除条目索引，直接映射到原索引。
        // 3. 如果索引大于或等于待删除条目索引，映射到原索引加1以跳过被删除条目。
        // 注意事项：确保索引映射正确以处理删除导致的偏移。
        public IOutItemAllFormats getItemInformation(int index,
                                                     OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            if (index == itemToAdd) {
                // Adding new item
                // 中文注释：添加新条目。
                IOutItemAllFormats outItem = outItemFactory.createOutItem();
                outItem.setPropertyPath(itemToAddPath);
                // 中文注释：设置新条目的路径为itemToAddPath。
                outItem.setDataSize((long) itemToAddContent.length);
                // 中文注释：设置新条目的数据大小为itemToAddContent的长度。

                return outItem;
                // 中文注释：返回新创建的条目对象。
            }

            // Remove item by changing the mapping "new index"->"old index"
            // 中文注释：通过更改“新索引”到“旧索引”的映射来删除条目。
            if (index < itemToRemove) {
                return outItemFactory.createOutItem(index);
                // 中文注释：对于小于待删除索引的条目，直接使用原索引创建条目对象。
            }
            return outItemFactory.createOutItem(index + 1);
            // 中文注释：对于大于或等于待删除索引的条目，使用原索引加1创建条目对象，以跳过被删除的条目。
        }

        // 中文注释：
        // 方法功能：为指定索引的条目提供数据流。
        // 参数：i - 新压缩文件中的条目索引。
        // 返回值：ISequentialInStream - 数据流对象，若非添加条目则返回null。
        // 执行流程：仅为待添加的条目提供数据流，其他情况返回null。
        // 注意事项：数据流使用ByteArrayStream封装待添加内容。
        public ISequentialInStream getStream(int i) throws SevenZipException {
            if (i != itemToAdd) {
                return null;
                // 中文注释：如果索引不是待添加条目，返回null。
            }
            return new ByteArrayStream(itemToAddContent, true);
            // 中文注释：为待添加条目创建并返回包含itemToAddContent的字节流。
        }
    }

    int itemToAdd; // New index of the item to add
    // 中文注释：待添加条目在新压缩文件中的索引。

    String itemToAddPath;
    // 中文注释：待添加条目的文件路径。

    byte[] itemToAddContent;
    // 中文注释：待添加条目的内容，存储为字节数组。

    int itemToRemove; // Old index of the item to be removed
    // 中文注释：待删除条目在原压缩文件中的索引。

    // 中文注释：
    // 方法功能：初始化更新操作的参数。
    // 参数：inArchive - 输入的压缩文件对象。
    // 执行流程：
    // 1. 设置待添加条目的索引为当前条目总数减1。
    // 2. 设置待添加条目的路径为“data.dmp”，内容为“dmp-content”的字节数组。
    // 3. 遍历压缩文件，查找“info.txt”的索引并设置为待删除索引。
    // 4. 如果未找到“info.txt”，抛出异常。
    // 注意事项：确保待删除条目存在，否则抛出运行时异常。
    private void initUpdate(IInArchive inArchive) throws SevenZipException {
        itemToAdd = inArchive.getNumberOfItems() - 1;
        // 中文注释：设置待添加条目的索引为当前条目总数减1。
        itemToAddPath = "data.dmp";
        // 中文注释：设置待添加条目的路径为“data.dmp”。
        itemToAddContent = "dmp-content".getBytes();
        // 中文注释：将字符串“dmp-content”转换为字节数组作为待添加条目内容。

        itemToRemove = -1;
        // 中文注释：初始化待删除条目索引为-1，表示未找到。
        for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
            if (inArchive.getProperty(i, PropID.PATH).equals("info.txt")) {
                itemToRemove = i;
                // 中文注释：找到“info.txt”时，记录其索引。
                break;
            }
        }
        if (itemToRemove == -1) {
            throw new RuntimeException("Item 'info.txt' not found");
            // 中文注释：如果未找到“info.txt”，抛出运行时异常。
        }
    }

    // 中文注释：
    // 方法功能：程序入口，处理命令行参数并启动压缩更新操作。
    // 参数：args - 命令行参数，包含输入和输出文件路径。
    // 执行流程：
    // 1. 检查参数长度是否为2。
    // 2. 如果参数正确，调用compress方法执行更新操作。
    // 3. 否则，打印使用说明。
    public static void main(String[] args) {
        if (args.length == 2) {
            new UpdateAddRemoveItems().compress(args[0], args[1]);
            // 中文注释：如果提供两个参数，调用compress方法进行更新。
            return;
        }
        System.out.println("Usage: java UpdateAddRemoveItems <in> <out>");
        // 中文注释：打印程序使用说明，要求提供输入和输出文件路径。
    }

    // 中文注释：
    // 方法功能：执行压缩文件的更新操作，包括添加和删除条目。
    // 参数：
    // - in：输入压缩文件路径。
    // - out：输出压缩文件路径。
    // 执行流程：
    // 1. 打开输入文件并创建输入流。
    // 2. 打开压缩文件并初始化更新参数。
    // 3. 打开输出文件并创建输出流。
    // 4. 获取输出压缩对象并调用updateItems方法执行更新。
    // 5. 处理异常并确保所有资源正确关闭。
    // 6. 根据操作结果打印成功或失败信息。
    // 注意事项：
    // - 使用Closeable列表管理所有需要关闭的资源。
    // - 捕获SevenZipException和其他异常，打印详细错误信息。
    // - 确保资源关闭顺序正确，避免资源泄漏。
    private void compress(String in, String out) {
        boolean success = false;
        // 中文注释：标志操作是否成功，初始为false。
        RandomAccessFile inRaf = null;
        // 中文注释：输入文件的随机访问文件对象。
        RandomAccessFile outRaf = null;
        // 中文注释：输出文件的随机访问文件对象。
        IInArchive inArchive;
        // 中文注释：输入压缩文件对象。
        IOutUpdateArchive<IOutItemAllFormats> outArchive = null;
        // 中文注释：输出压缩文件更新对象。
        List<Closeable> closeables = new ArrayList<Closeable>();
        // 中文注释：存储所有需要关闭的资源对象。
        try {
            // Open input file
            // 中文注释：打开输入文件。
            inRaf = new RandomAccessFile(in, "r");
            // 中文注释：以只读模式创建输入文件的随机访问对象。
            closeables.add(inRaf);
            // 中文注释：将输入文件对象添加到待关闭资源列表。
            IInStream inStream = new RandomAccessFileInStream(inRaf);
            // 中文注释：创建输入文件的随机访问输入流。

            // Open in-archive
            // 中文注释：打开输入压缩文件。
            inArchive = SevenZip.openInArchive(null, inStream);
            // 中文注释：使用SevenZip库打开输入压缩文件。
            closeables.add(inArchive);
            // 中文注释：将输入压缩文件对象添加到待关闭资源列表。

            initUpdate(inArchive);
            // 中文注释：初始化更新操作的参数，包括待添加和待删除条目。

            outRaf = new RandomAccessFile(out, "rw");
            // 中文注释：以读写模式创建输出文件的随机访问对象。
            closeables.add(outRaf);
            // 中文注释：将输出文件对象添加到待关闭资源列表。

            // Open out-archive object
            // 中文注释：打开输出压缩文件对象。
            outArchive = inArchive.getConnectedOutArchive();
            // 中文注释：获取与输入压缩文件关联的输出压缩更新对象。

            // Modify archive
            // 中文注释：修改压缩文件内容。
            outArchive.updateItems(new RandomAccessFileOutStream(outRaf),
                    inArchive.getNumberOfItems(), new MyCreateCallback());
            // 中文注释：调用updateItems方法，使用输出流、条目总数和回调对象执行更新操作。

            success = true;
            // 中文注释：操作成功，设置成功标志为true。
        } catch (SevenZipException e) {
            System.err.println("7z-Error occurs:");
            // 中文注释：捕获SevenZip异常，打印错误信息。
            // Get more information using extended method
            // 中文注释：使用扩展方法获取更多错误信息。
            e.printStackTraceExtended();
            // 中文注释：打印详细的SevenZip异常堆栈信息。
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
            // 中文注释：捕获其他异常，打印错误信息。
        } finally {
            for (int i = closeables.size() - 1; i >= 0; i--) {
                try {
                    closeables.get(i).close();
                    // 中文注释：逆序关闭所有资源对象。
                } catch (Throwable e) {
                    System.err.println("Error closing resource: " + e);
                    // 中文注释：捕获关闭资源时的异常，打印错误信息。
                    success = false;
                    // 中文注释：关闭资源失败，设置成功标志为false。
                }
            }
        }
        if (success) {
            System.out.println("Update successful");
            // 中文注释：如果操作成功，打印成功信息。
        }
    }
}