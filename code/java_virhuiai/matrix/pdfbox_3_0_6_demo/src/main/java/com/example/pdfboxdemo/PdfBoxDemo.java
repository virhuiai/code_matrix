package com.example.pdfboxdemo;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfBoxDemo {
    public static void main(String[] args) {
        try {
            // Load the PDF file - 使用正确的PDDocument类型
            PDDocument document = Loader.loadPDF(new File("/Volumes/RamDisk/test.pdf"));

            // Check if document has pages
            if (document.getNumberOfPages() > 0) {
                // Get the first page
                PDPage page = document.getPage(0);
                System.out.println("Processing page: " + page.getMediaBox());

                // Extract text and its positions
                PDFTextStripper stripper = new PDFTextStripper() {
                    @Override
                    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                        for (TextPosition textPosition : textPositions) {
                            System.out.println("Text: '" + textPosition.getUnicode() + 
                                "', Font: " + textPosition.getFont().getName() +
                                ", Size: " + textPosition.getFontSize() +
                                ", Position: (" + textPosition.getX() + ", " + textPosition.getY() + ")");
                        }
                        super.writeString(text, textPositions);
                    }
                };
                
                stripper.setStartPage(1);
                stripper.setEndPage(1);
                String text = stripper.getText(document);
                System.out.println("=== Extracted Text ===");
                System.out.println(text);
                System.out.println("=====================");

                // Create a sample PDF
                createSamplePDF();
            } else {
                System.out.println("Document has no pages");
                // Create a sample PDF anyway
                createSamplePDF();
            }

            // Close the document
            document.close();
        } catch (IOException e) {
            System.err.println("Error processing PDF: " + e.getMessage());
            // Even if PDF loading fails, create a sample PDF
            createSamplePDF();
        }
    }

    // 创建示例PDF的方法
    private static void createSamplePDF() {
        try {
            System.out.println("Creating sample PDF...");
            
            // Create a new document
            PDDocument document = new PDDocument();
            
            // Create a new page
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            // Create a content stream for the page
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Add some text - 使用PDFBox 3.0.6正确的字体创建方式
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("PDFBox 3.0.6 Demo");
            contentStream.newLineAtOffset(0, -30);
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.showText("This is a sample PDF created with Apache PDFBox 3.0.6");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Created on: " + java.time.LocalDateTime.now());
            contentStream.endText();
            
            // Draw a rectangle
            contentStream.addRect(50, 600, 200, 100);
            contentStream.stroke();
            
            // Close the content stream
            contentStream.close();
            
            // Save the document
            File outputFile = new File("/Volumes/RamDisk/sample_output.pdf");
            document.save(outputFile);
            System.out.println("Sample PDF created successfully at: " + outputFile.getAbsolutePath());
            
            // Close the document
            document.close();
            
        } catch (IOException e) {
            System.err.println("Error creating sample PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}