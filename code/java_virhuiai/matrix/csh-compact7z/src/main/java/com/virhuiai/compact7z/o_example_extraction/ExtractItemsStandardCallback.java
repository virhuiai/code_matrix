package com.virhuiai.compact7z.o_example_extraction;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

/**
 * Standard interface
 * The following two examples show how to perform the same extracting task using standard interface. As in the previous example, the simple checksum will be calculated in order to consume the extracted data.
 *
 * First example traverse the entire archive and uses call back method to choose on the fly whether proceed with the extracting of an item or not. The second example prepare a list of items for extracting.
 *
 * Extracting archive using standard interface and call back method as a filter
 */
public class ExtractItemsStandardCallback {
    public static class MyExtractCallback implements IArchiveExtractCallback {
        private int hash = 0;
        private int size = 0;
        private int index;
        private boolean skipExtraction;
        private IInArchive inArchive;

        public MyExtractCallback(IInArchive inArchive) {
            this.inArchive = inArchive;
        }

        public ISequentialOutStream getStream(int index,
                                              ExtractAskMode extractAskMode) throws SevenZipException {
            this.index = index;
            skipExtraction = (Boolean) inArchive
                    .getProperty(index, PropID.IS_FOLDER);
            if (skipExtraction || extractAskMode != ExtractAskMode.EXTRACT) {
                return null;
            }
            return new ISequentialOutStream() {
                public int write(byte[] data) throws SevenZipException {
                    hash ^= Arrays.hashCode(data);
                    size += data.length;
                    return data.length; // Return amount of proceed data
                }
            };
        }

        public void prepareOperation(ExtractAskMode extractAskMode)
                throws SevenZipException {
        }

        public void setOperationResult(ExtractOperationResult
                                               extractOperationResult) throws SevenZipException {
            if (skipExtraction) {
                return;
            }
            if (extractOperationResult != ExtractOperationResult.OK) {
                System.err.println("Extraction error");
            } else {
                System.out.println(String.format("%9X | %10s | %s", hash, size,//
                        inArchive.getProperty(index, PropID.PATH)));
                hash = 0;
                size = 0;
            }
        }

        public void setCompleted(long completeValue) throws SevenZipException {
        }

        public void setTotal(long total) throws SevenZipException {
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java ExtractItemsStandard <arch-name>");
            return;
        }
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(args[0], "r");
            inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    new RandomAccessFileInStream(randomAccessFile));

            System.out.println("   Hash   |    Size    | Filename");
            System.out.println("----------+------------+---------");

            int[] in = new int[inArchive.getNumberOfItems()];
            for (int i = 0; i < in.length; i++) {
                in[i] = i;
            }
            inArchive.extract(in, false, // Non-test mode
                    new MyExtractCallback(inArchive));
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                }
            }
        }
    }
}