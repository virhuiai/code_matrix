package com.virhuiai.compact7z.o_example_create;

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
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
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItem7z>, ICryptoGetTextPassword {
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

        public IOutItem7z getItemInformation(int index,
                                             OutItemFactory<IOutItem7z> outItemFactory) {
            IOutItem7z item = outItemFactory.createOutItem();

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

        public String cryptoGetTextPassword() throws SevenZipException {
            return password;
        }
    }

    private Item[] items;
    private String password;

    public static void main(String[] args) {
//        if (args.length  == 2) {
//            new CompressWithPassword().compress("/Volumes/RamDisk/out.7z", "123");
//            return;
//        }
        new CompressWithPassword().compress("/Volumes/RamDisk/out.7z", "123");
        System.out.println("Usage: java CompressWithPassword <archive> <pass>");
    }

    private void compress(String filename, String pass) {
        items = CompressArchiveStructure.create();
        password = pass;

        boolean success = false;
        RandomAccessFile raf = null;
        IOutCreateArchive7z outArchive = null;
        try {
            raf = new RandomAccessFile(filename, "rw");

            // Open out-archive object
            outArchive = SevenZip.openOutArchive7z();

            // Header encryption is only available for 7z
            outArchive.setHeaderEncryption(true);

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