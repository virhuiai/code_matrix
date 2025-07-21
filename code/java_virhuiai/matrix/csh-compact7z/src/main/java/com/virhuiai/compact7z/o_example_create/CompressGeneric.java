package com.virhuiai.compact7z.o_example_create;

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutFeatureSetLevel;
import net.sf.sevenzipjbinding.IOutFeatureSetMultithreading;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;

import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * ## Creating archives with the generic API
 *
 * The one of the great features of the 7-Zip (and though of the 7-Zip-JBinding) is the ability to write archive format independent code supporting most or even all of the archive formats, supported by 7-Zip. The following code snippet accepts the required archive format as the first parameter compressing the test data in the specified archive format.The key steps to write a generic compression code are
 *
 * -
 *
 * - Use [IOutItemAllFormats](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutItemAllFormats.html) interface instead of the one of the archive specific interfaces, like [IOutItem7z](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutItem7z.html)
 * - Create out-archive object using generic create method [SevenZip](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/SevenZip.html).openOutArchive([ArchiveFormat](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/ArchiveFormat.html))
 */
public class CompressGeneric {
    /**
     * The callback provides information about archive items.
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
                                                     OutItemFactory<IOutItemAllFormats> outItemFactory) {
            IOutItemAllFormats item = outItemFactory.createOutItem();

            if (items[index].getContent() == null) {
                // Directory
                item.setPropertyIsDir(true);
            } else {
                // File
                item.setDataSize((long) items[index].getContent().length);
            }

            item.setPropertyPath(items[index].getPath());

            return item;
        }

        public ISequentialInStream getStream(int i) throws SevenZipException {
            if (items[i].getContent() == null) {
                return null;
            }
            return new ByteArrayStream(items[i].getContent(), true);
        }
    }

    private Item[] items;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java CompressGeneric "
                    + "<archive-format> <archive> <count-of-files>");
            for (ArchiveFormat af : ArchiveFormat.values()) {
                if (af.isOutArchiveSupported()) {
                    System.out.println("Supported formats: " + af.name());
                }
            }
            return;
        }

        int itemsCount = Integer.valueOf(args[2]);
        new CompressGeneric().compress(args[0], args[1], itemsCount);
    }


    private void compress(String filename, String fmtName, int count) {
        items = CompressArchiveStructure.create();

        boolean success = false;
        RandomAccessFile raf = null;
        IOutCreateArchive<IOutItemAllFormats> outArchive = null;
        ArchiveFormat archiveFormat = ArchiveFormat.valueOf(fmtName);
        try {
            raf = new RandomAccessFile(filename, "rw");

            // Open out-archive object
            outArchive = SevenZip.openOutArchive(archiveFormat);

            // Configure archive
            if (outArchive instanceof IOutFeatureSetLevel) {
                ((IOutFeatureSetLevel) outArchive).setLevel(5);
            }
            if (outArchive instanceof IOutFeatureSetMultithreading) {
                ((IOutFeatureSetMultithreading) outArchive).setThreadCount(2);
            }

            // Create archive
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    count, new MyCreateCallback());

            success = true;
        } catch (SevenZipException e) {
            System.err.println("7z-Error occurs:");
            // Get more information using extended method
            e.printStackTraceExtended();
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            if (outArchive != null) {
                try {
                    outArchive.close();
                } catch (IOException e) {
                    System.err.println("Error closing archive: " + e);
                    success = false;
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    success = false;
                }
            }
        }
        if (success) {
            System.out.println(archiveFormat.getMethodName()
                    + " archive with " + count + " item(s) created");
        }
    }
}