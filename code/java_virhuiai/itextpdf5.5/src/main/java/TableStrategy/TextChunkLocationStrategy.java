package TableStrategy;


import com.itextpdf.text.pdf.parser.LineSegment;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

// 文本块位置策略接口，用于创建文本块的位置信息
public interface TextChunkLocationStrategy {
    TextChunkLocation createLocation(TextRenderInfo renderInfo, LineSegment baseline);
}