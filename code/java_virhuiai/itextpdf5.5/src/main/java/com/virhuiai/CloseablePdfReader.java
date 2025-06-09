package com.virhuiai;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.ReaderProperties;
import com.itextpdf.text.pdf.security.ExternalDecryptionProcess;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.cert.Certificate;

public class CloseablePdfReader extends PdfReader implements Closeable {

    /**
     * CloseablePdfReader
     * @param filename
     * @throws IOException
     */
    public CloseablePdfReader(String filename) throws IOException {
        super(filename);
    }

    /**
     * CloseablePdfReader
     * @param properties
     * @param filename
     * @throws IOException
     */
    public CloseablePdfReader(ReaderProperties properties, String filename) throws IOException {
        super(properties, filename);
    }

    /**
     * CloseablePdfReader
     * @param filename
     * @param ownerPassword
     * @throws IOException
     */
    public CloseablePdfReader(String filename, byte[] ownerPassword) throws IOException {
        super(filename, ownerPassword);
    }

    /**
     * CloseablePdfReader
     * @param filename
     * @param ownerPassword
     * @param partial
     * @throws IOException
     */
    public CloseablePdfReader(String filename, byte[] ownerPassword, boolean partial) throws IOException {
        super(filename, ownerPassword, partial);
    }

    /**
     * CloseablePdfReader
     * @param pdfIn
     * @throws IOException
     */
    public CloseablePdfReader(byte[] pdfIn) throws IOException {
        super(pdfIn);
    }

    /**
     * CloseablePdfReader
     * @param pdfIn
     * @param ownerPassword
     * @throws IOException
     */
    public CloseablePdfReader(byte[] pdfIn, byte[] ownerPassword) throws IOException {
        super(pdfIn, ownerPassword);
    }

    /**
     * CloseablePdfReader
     * @param filename
     * @param certificate
     * @param certificateKey
     * @param certificateKeyProvider
     * @throws IOException
     */
    public CloseablePdfReader(String filename, Certificate certificate, Key certificateKey, String certificateKeyProvider) throws IOException {
        super(filename, certificate, certificateKey, certificateKeyProvider);
    }

    /**
     * CloseablePdfReader
     * @param filename
     * @param certificate
     * @param externalDecryptionProcess
     * @throws IOException
     */
    public CloseablePdfReader(String filename, Certificate certificate, ExternalDecryptionProcess externalDecryptionProcess) throws IOException {
        super(filename, certificate, externalDecryptionProcess);
    }

    /**
     * CloseablePdfReader
     * @param pdfIn
     * @param certificate
     * @param externalDecryptionProcess
     * @throws IOException
     */
    public CloseablePdfReader(byte[] pdfIn, Certificate certificate, ExternalDecryptionProcess externalDecryptionProcess) throws IOException {
        super(pdfIn, certificate, externalDecryptionProcess);
    }

    /**
     * CloseablePdfReader
     * @param inputStream
     * @param certificate
     * @param externalDecryptionProcess
     * @throws IOException
     */
    public CloseablePdfReader(InputStream inputStream, Certificate certificate, ExternalDecryptionProcess externalDecryptionProcess) throws IOException {
        super(inputStream, certificate, externalDecryptionProcess);
    }

    /**
     * CloseablePdfReader
     * @param url
     * @throws IOException
     */
    public CloseablePdfReader(URL url) throws IOException {
        super(url);
    }

    /**
     * CloseablePdfReader
     * @param url
     * @param ownerPassword
     * @throws IOException
     */
    public CloseablePdfReader(URL url, byte[] ownerPassword) throws IOException {
        super(url, ownerPassword);
    }

    /**
     * CloseablePdfReader
     * @param is
     * @param ownerPassword
     * @throws IOException
     */
    public CloseablePdfReader(InputStream is, byte[] ownerPassword) throws IOException {
        super(is, ownerPassword);
    }

    /**
     * CloseablePdfReader
     * @param is
     * @throws IOException
     */
    public CloseablePdfReader(InputStream is) throws IOException {
        super(is);
    }

    /**
     * CloseablePdfReader
     * @param properties
     * @param is
     * @throws IOException
     */
    public CloseablePdfReader(ReaderProperties properties, InputStream is) throws IOException {
        super(properties, is);
    }

    /**
     * CloseablePdfReader
     * @param properties
     * @param raf
     * @throws IOException
     */
    public CloseablePdfReader(ReaderProperties properties, RandomAccessFileOrArray raf) throws IOException {
        super(properties, raf);
    }

    /**
     * CloseablePdfReader
     * @param raf
     * @param ownerPassword
     * @throws IOException
     */
    public CloseablePdfReader(RandomAccessFileOrArray raf, byte[] ownerPassword) throws IOException {
        super(raf, ownerPassword);
    }

    /**
     * CloseablePdfReader
     * @param raf
     * @param ownerPassword
     * @param partial
     * @throws IOException
     */
    public CloseablePdfReader(RandomAccessFileOrArray raf, byte[] ownerPassword, boolean partial) throws IOException {
        super(raf, ownerPassword, partial);
    }

    /**
     * CloseablePdfReader
     * @param reader
     */
    public CloseablePdfReader(PdfReader reader) {
        super(reader);
    }

//    @Override
//    public void close(){
//        super.close();
//    }
}
