package com.virhuiai.compact7z.o_example_update;

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
public class UpdateAddRemoveItems {
    /**
     * The callback defines the modification to be made.
     */
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItemAllFormats> {

        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
        }

        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
        }

        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
        }

        public IOutItemAllFormats getItemInformation(int index,
                                                     OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            if (index == itemToAdd) {
                // Adding new item
                IOutItemAllFormats outItem = outItemFactory.createOutItem();
                outItem.setPropertyPath(itemToAddPath);
                outItem.setDataSize((long) itemToAddContent.length);

                return outItem;
            }

            // Remove item by changing the mapping "new index"->"old index"
            if (index < itemToRemove) {
                return outItemFactory.createOutItem(index);
            }
            return outItemFactory.createOutItem(index + 1);
        }

        public ISequentialInStream getStream(int i) throws SevenZipException {
            if (i != itemToAdd) {
                return null;
            }
            return new ByteArrayStream(itemToAddContent, true);
        }
    }

    int itemToAdd; // New index of the item to add
    String itemToAddPath;
    byte[] itemToAddContent;

    int itemToRemove; // Old index of the item to be removed

    private void initUpdate(IInArchive inArchive) throws SevenZipException {
        itemToAdd = inArchive.getNumberOfItems() - 1;
        itemToAddPath = "data.dmp";
        itemToAddContent = "dmp-content".getBytes();

        itemToRemove = -1;
        for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
            if (inArchive.getProperty(i, PropID.PATH).equals("info.txt")) {
                itemToRemove = i;
                break;
            }
        }
        if (itemToRemove == -1) {
            throw new RuntimeException("Item 'info.txt' not found");
        }
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            new UpdateAddRemoveItems().compress(args[0], args[1]);
            return;
        }
        System.out.println("Usage: java UpdateAddRemoveItems <in> <out>");
    }

    private void compress(String in, String out) {
        boolean success = false;
        RandomAccessFile inRaf = null;
        RandomAccessFile outRaf = null;
        IInArchive inArchive;
        IOutUpdateArchive<IOutItemAllFormats> outArchive = null;
        List<Closeable> closeables = new ArrayList<Closeable>();
        try {
            // Open input file
            inRaf = new RandomAccessFile(in, "r");
            closeables.add(inRaf);
            IInStream inStream = new RandomAccessFileInStream(inRaf);

            // Open in-archive
            inArchive = SevenZip.openInArchive(null, inStream);
            closeables.add(inArchive);

            initUpdate(inArchive);

            outRaf = new RandomAccessFile(out, "rw");
            closeables.add(outRaf);

            // Open out-archive object
            outArchive = inArchive.getConnectedOutArchive();

            // Modify archive
            outArchive.updateItems(new RandomAccessFileOutStream(outRaf),
                    inArchive.getNumberOfItems(), new MyCreateCallback());

            success = true;
        } catch (SevenZipException e) {
            System.err.println("7z-Error occurs:");
            // Get more information using extended method
            e.printStackTraceExtended();
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            for (int i = closeables.size() - 1; i >= 0; i--) {
                try {
                    closeables.get(i).close();
                } catch (Throwable e) {
                    System.err.println("Error closing resource: " + e);
                    success = false;
                }
            }
        }
        if (success) {
            System.out.println("Update successful");
        }
    }
}