package com.virhuiai.compact7z;

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
public class ExtractItemsStandardCallback implements IArchiveExtractCallback {

    private int hash = 0;
    private int size = 0;
    private int index;
    private boolean skipExtraction;
    private IInArchive inArchive;

    public ExtractItemsStandardCallback(IInArchive inArchive) {
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
