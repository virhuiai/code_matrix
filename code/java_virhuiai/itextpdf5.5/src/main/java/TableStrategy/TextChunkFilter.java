package TableStrategy;

/**
 * Specifies a filter for filtering {@link TextChunk} objects during text extraction
 * @see LocTextExtractionStrategy#getResultantText(TextChunkFilter)
 * @since 5.3.3
 */
// 文本块过滤器接口，用于在文本提取过程中过滤文本块
public interface TextChunkFilter{
    /**
     * @param textChunk the chunk to check
     * @return true if the chunk should be allowed
     */
    // 判断是否接受该文本块
    public boolean accept(TextChunk textChunk);
}
