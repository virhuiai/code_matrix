package com.virhuiai.compact7z.o_example_extraction;

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

/**
 * Standard vs Simple interface
 * The 7-Zip-JBinding library offers two different interfaces: standard and simple. The standard interface directly maps all native 7-Zip library methods providing C++ like interface. The simple interface is a try to provide more Java-like easy to use interface. It was designed either for quick coding and easy start and not for feature richness nor for performance.
 *
 * The following examples will be presented in both standard and simple interface beginning with as simple interface. Please note, that the full performance can only be reached with the standard interface.
 *
 *
 * Quering items in archive
 * Simple interface
 * This example shows how to print a list of items of archive using simple interface.
 */
public class ListItemsSimple {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java ListItemsSimple <archive-name>");
            return;
        }
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(args[0], "r");
            inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    new RandomAccessFileInStream(randomAccessFile));

            // Getting simple interface of the archive inArchive
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();

            System.out.println("   Size   | Compr.Sz. | Filename");
            System.out.println("----------+-----------+---------");

            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                System.out.println(String.format("%9s | %9s | %s", //
                        item.getSize(),
                        item.getPackedSize(),
                        item.getPath()));
            }
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