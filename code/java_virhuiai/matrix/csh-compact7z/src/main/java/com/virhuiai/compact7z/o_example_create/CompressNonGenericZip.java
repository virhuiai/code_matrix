package com.virhuiai.compact7z.o_example_create;

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

import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * ## Creating archives with the archive format specific API
 *
 *
 *
 * The archive format specific API provides easy access to the archive configuration methods (e.g. for setting the compression level). Also it uses archive format specific item description interfaces (like [IOutItemZip](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutItemZip.html) for Zip). Different archive formats support different archive item properties. Those interfaces provide access to the properties supported by the corresponding archive format, whether the unsupported properties remain hidden.
 *
 * Lets see how different archives can be created using archive format specific API
 *
 * ### Creating Zip archive using archive format specific API
 *
 * Creating Zip archive using archive format specific API was already presented in the "first steps". The key parts of the code are:
 *
 * -
 *
 * - Implementation of the [IOutCreateCallback](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutCreateCallback.html)<IOutItemCallbackZip> interface specifying the structure of the new archive. Also the progress of the compression/update operation get reported here.
 * - Creating an instance of the [IOutArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutArchive.html) interface and calling createArchive() method.
 */
public class CompressNonGenericZip {
    /**
     * The callback provides information about archive items.
     */
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItemZip> {

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

        public IOutItemZip getItemInformation(int index,
                                              OutItemFactory<IOutItemZip> outItemFactory) {
            int attr = PropID.AttributesBitMask.FILE_ATTRIBUTE_UNIX_EXTENSION;

            IOutItemZip item = outItemFactory.createOutItem();

            if (items[index].getContent() == null) {
                // Directory
                item.setPropertyIsDir(true);
                attr |= PropID.AttributesBitMask.FILE_ATTRIBUTE_DIRECTORY;
                attr |= 0x81ED << 16; // permissions: drwxr-xr-x
            } else {
                // File
                item.setDataSize((long) items[index].getContent().length);
                attr |= 0x81a4 << 16; // permissions: -rw-r--r--
            }
            item.setPropertyPath(items[index].getPath());

            item.setPropertyAttributes(attr);

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
            new CompressNonGenericZip().compress(args[0]);
            return;
        }
        System.out.println("Usage: java CompressNonGenericZip <archive>");
    }


    private void compress(String filename) {
        items = CompressArchiveStructure.create();

        boolean success = false;
        RandomAccessFile raf = null;
        IOutCreateArchiveZip outArchive = null;
        try {
            raf = new RandomAccessFile(filename, "rw");

            // Open out-archive object
            outArchive = SevenZip.openOutArchiveZip();

            // Configure archive
            outArchive.setLevel(5);

            // Create archive
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    items.length, new MyCreateCallback());

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
            System.out.println("Compression operation succeeded");
        }
    }
}