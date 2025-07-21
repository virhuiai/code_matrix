package com.virhuiai.compact7z.o_example_create;

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IOutCreateArchiveTar;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemTar;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;

import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * # Troubleshoot problems using tracing
 *
 * One of the weak sides of the 7-zip compression engine is a rather simple error reporting. If some provided data doesn't satisfy the compressor it fails without any descriptive error message. One way to get an clue is to use 7-Zip-JBinding tracing feature. Here is the code passing invalid data size for the item 1 and though failing.
 */
public class CompressWithError {
    /**
     * The callback provides information about archive items.
     */
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItemTar> {

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

        public IOutItemTar getItemInformation(int index,
                                              OutItemFactory<IOutItemTar> outItemFactory) {
            IOutItemTar item = outItemFactory.createOutItem();

            if (items[index].getContent() == null) {
                // Directory
                item.setPropertyIsDir(true);
            } else {
                // File
                item.setDataSize((long) items[index].getContent().length);
            }

            item.setPropertyPath(items[index].getPath());

            if (index == 1) {
                item.setDataSize(null); // Provide invalid data for item 1
            }

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
        if (args.length == 1) {
            new CompressWithError().compress(args[0]);
            return;
        }
        System.out.println("Usage: java CompressNonGenericTar <archive>");
    }


    private void compress(String filename) {
        items = CompressArchiveStructure.create();

        boolean success = false;
        RandomAccessFile raf = null;
        IOutCreateArchiveTar outArchive = null;
        try {
            raf = new RandomAccessFile(filename, "rw");

            // Open out-archive object
            outArchive = SevenZip.openOutArchiveTar();

            // Activate tracing
            outArchive.setTrace(true);

            // Create archive
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    items.length, new MyCreateCallback());

            success = true;
        } catch (SevenZipException e) {
            System.err.println("Tar-Error occurs:");
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
            System.out.println("Compression operation succeeded");
        }
    }
}