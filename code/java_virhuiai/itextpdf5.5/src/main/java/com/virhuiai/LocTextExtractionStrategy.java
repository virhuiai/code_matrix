package com.virhuiai;



import TableStrategy.TextChunkLocation;
import TableStrategy.TextChunkLocationDefaultImp;
import com.itextpdf.text.pdf.parser.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * <b>Development preview</b> - this class (and all of the parser classes) are still experiencing
 * heavy development, and are subject to change both behavior and interface.
 * <br>
 * A text extraction renderer that keeps track of relative position of text on page
 * The resultant text will be relatively consistent with the physical layout that most
 * PDF files have on screen.
 * <br>
 * This renderer keeps track of the orientation and distance (both perpendicular
 * and parallel) to the unit vector of the orientation.  Text is ordered by
 * orientation, then perpendicular, then parallel distance.  Text with the same
 * perpendicular distance, but different parallel distance is treated as being on
 * the same line.
 * <br>
 * This renderer also uses a simple strategy based on the font metrics to determine if
 * a blank space should be inserted into the output.
 *
 * @since   5.0.2
 */
// 基于位置的文本提取策略类，用于从PDF中提取文本并保持其物理布局
// 该类会跟踪文本在页面上的相对位置，使提取的文本保持与PDF显示时相似的布局
public class LocTextExtractionStrategy implements TextExtractionStrategy {

    /** set to true for debugging */
    static boolean DUMP_STATE = false;  // 调试标志，设为true时会输出调试信息

    /** a summary of all found text */
    // 存储所有找到的文本块，每个TextChunk代表一个文本片段及其位置信息
    private final List<TextChunk> locationalResult = new ArrayList<TextChunk>();

    // 文本块位置策略，用于创建文本块的位置信息
    private final TextChunkLocationStrategy tclStrat;


    /**
     * Creates a new text extraction renderer.
     */
    // 创建一个新的文本提取渲染器，使用默认的位置策略
    public LocTextExtractionStrategy() {
        this(new TextChunkLocationStrategy() {
            public TextChunkLocation createLocation(TextRenderInfo renderInfo, LineSegment baseline) {
                return new TextChunkLocationDefaultImp(baseline.getStartPoint(), baseline.getEndPoint(), renderInfo.getSingleSpaceWidth());
            }
        });
    }

    /**
     * Creates a new text extraction renderer, with a custom strategy for
     * creating new TextChunkLocation objects based on the input of the
     * TextRenderInfo.
     * @param strat the custom strategy
     */
    // 使用自定义策略创建文本提取渲染器
    // 允许用户自定义如何根据TextRenderInfo创建TextChunkLocation对象
    public LocTextExtractionStrategy(TextChunkLocationStrategy strat) {
        tclStrat = strat;
    }

    /**
     * @see com.itextpdf.text.pdf.parser.RenderListener#beginTextBlock()
     */
    // 文本块开始时的回调，这里不做任何处理
    public void beginTextBlock(){
    }

    /**
     * @see com.itextpdf.text.pdf.parser.RenderListener#endTextBlock()
     */
    // 文本块结束时的回调，这里不做任何处理
    public void endTextBlock(){
    }

    /**
     * @param str
     * @return true if the string starts with a space character, false if the string is empty or starts with a non-space character
     */
    // 检查字符串是否以空格开始
    private boolean startsWithSpace(String str){
        if (str.length() == 0) return false;
        return str.charAt(0) == ' ';
    }

    /**
     * @param str
     * @return true if the string ends with a space character, false if the string is empty or ends with a non-space character
     */
    // 检查字符串是否以空格结尾
    private boolean endsWithSpace(String str){
        if (str.length() == 0) return false;
        return str.charAt(str.length()-1) == ' ';
    }

    /**
     * Filters the provided list with the provided filter
     * @param textChunks a list of all TextChunks that this strategy found during processing
     * @param filter the filter to apply.  If null, filtering will be skipped.
     * @return the filtered list
     * @since 5.3.3
     */
    // 使用提供的过滤器过滤文本块列表
    // 如果filter为null，则返回原列表
    private List<TextChunk> filterTextChunks(List<TextChunk> textChunks, TextChunkFilter filter){
        if (filter == null)
            return textChunks;

        List<TextChunk> filtered = new ArrayList<TextChunk>();
        for (TextChunk textChunk : textChunks) {
            if (filter.accept(textChunk))
                filtered.add(textChunk);
        }
        return filtered;
    }

    /**
     * Determines if a space character should be inserted between a previous chunk and the current chunk.
     * This method is exposed as a callback so subclasses can fine time the algorithm for determining whether a space should be inserted or not.
     * By default, this method will insert a space if the there is a gap of more than half the font space character width between the end of the
     * previous chunk and the beginning of the current chunk.  It will also indicate that a space is needed if the starting point of the new chunk
     * appears *before* the end of the previous chunk (i.e. overlapping text).
     * @param chunk the new chunk being evaluated
     * @param previousChunk the chunk that appeared immediately before the current chunk
     * @return true if the two chunks represent different words (i.e. should have a space between them).  False otherwise.
     */
    // 判断两个文本块之间是否应该插入空格
    // 如果两个块之间的距离大于半个空格字符的宽度，或者文本重叠，则返回true
    protected boolean isChunkAtWordBoundary(TextChunk chunk, TextChunk previousChunk){
        return chunk.getLocation().isAtWordBoundary(previousChunk.getLocation());
    }

    /**
     * Gets text that meets the specified filter
     * If multiple text extractions will be performed for the same page (i.e. for different physical regions of the page),
     * filtering at this level is more efficient than filtering using {@link FilteredRenderListener} - but not nearly as powerful
     * because most of the RenderInfo state is not captured in {@link TextChunk}
     * @param chunkFilter the filter to to apply
     * @return the text results so far, filtered using the specified filter
     */
    // 获取符合指定过滤器条件的文本
    // 这是提取文本的核心方法，它会将文本块按位置排序并组合成最终的文本
    public String getResultantText(TextChunkFilter chunkFilter){
        if (DUMP_STATE) dumpState();  // 如果开启调试，输出状态信息

        // 过滤文本块
        List<TextChunk> filteredTextChunks = filterTextChunks(locationalResult, chunkFilter);
        // 根据位置对文本块进行排序（按方向、垂直距离、水平距离的顺序）
        Collections.sort(filteredTextChunks);

        StringBuilder sb = new StringBuilder();
        TextChunk lastChunk = null;
        for (TextChunk chunk : filteredTextChunks) {

            if (lastChunk == null){
                // 第一个文本块，直接添加
                sb.append(chunk.text);
            } else {
                if (chunk.sameLine(lastChunk)){
                    // 同一行的文本
                    // we only insert a blank space if the trailing character of the previous string wasn't a space, and the leading character of the current string isn't a space
                    // 只有当前一个字符串不以空格结尾，且当前字符串不以空格开始时，才插入空格
                    if (isChunkAtWordBoundary(chunk, lastChunk) && !startsWithSpace(chunk.text) && !endsWithSpace(lastChunk.text))
                        sb.append(' ');

                    sb.append(chunk.text);
                } else {
                    // 不同行的文本，插入换行符
                    sb.append('\n');
                    sb.append(chunk.text);
                }
            }
            lastChunk = chunk;
        }

        return sb.toString();
    }

    /**
     * Returns the result so far.
     * @return  a String with the resulting text.
     */
    // 返回到目前为止的提取结果，不使用任何过滤器
    public String getResultantText(){
        return getResultantText(null);
    }

    /** Used for debugging only */
    // 调试方法，输出所有文本块的诊断信息
    private void dumpState(){
        for (TextChunk location : locationalResult) {
            location.printDiagnostics();
            System.out.println();
        }
    }

    /**
     *
     * @see com.itextpdf.text.pdf.parser.RenderListener#renderText(com.itextpdf.text.pdf.parser.TextRenderInfo)
     */
    // 处理文本渲染信息的核心方法
    // 当PDF解析器遇到文本时会调用此方法
    public void renderText(TextRenderInfo renderInfo) {
        LineSegment segment = renderInfo.getBaseline();
        if (renderInfo.getRise() != 0){ // remove the rise from the baseline - we do this because the text from a super/subscript render operations should probably be considered as part of the baseline of the text the super/sub is relative to
            // 处理上标或下标文本，将其调整到基线位置
            Matrix riseOffsetTransform = new Matrix(0, -renderInfo.getRise());
            segment = segment.transformBy(riseOffsetTransform);
        }
        // 创建文本块并添加到结果列表中
        TextChunk tc = new TextChunk(renderInfo.getText(), tclStrat.createLocation(renderInfo, segment));
        locationalResult.add(tc);
    }

    // 文本块位置策略接口，用于创建文本块的位置信息
    public static interface TextChunkLocationStrategy {
        TextChunkLocation createLocation(TextRenderInfo renderInfo, LineSegment baseline);
    }






    /**
     * Represents a chunk of text, it's orientation, and location relative to the orientation vector
     */
    // 表示一个文本块，包含文本内容及其位置信息
    public static class TextChunk implements Comparable<TextChunk>{
        /** the text of the chunk */
        private final String text;      // 文本内容
        private final TextChunkLocation location;  // 位置信息

        // 使用起始和结束位置构造文本块
        public TextChunk(String string, Vector startLocation, Vector endLocation, float charSpaceWidth) {
            this(string, new TextChunkLocationDefaultImp(startLocation, endLocation, charSpaceWidth));
        }

        // 使用位置对象构造文本块
        public TextChunk(String string, TextChunkLocation loc) {
            this.text = string;
            this.location = loc;
        }

        /**
         * @return the text captured by this chunk
         */
        // 获取文本内容
        public String getText(){
            return text;
        }

        /**
         * @return an object holding location data about this TextChunk
         */
        // 获取位置信息对象
        public TextChunkLocation getLocation() {
            return location;
        }

        /**
         * @return the start location of the text
         */
        // 获取文本的起始位置
        public Vector getStartLocation(){
            return location.getStartLocation();
        }
        /**
         * @return the end location of the text
         */
        // 获取文本的结束位置
        public Vector getEndLocation(){
            return location.getEndLocation();
        }

        /**
         * @return the width of a single space character as rendered by this chunk
         */
        // 获取空格字符的宽度
        public float getCharSpaceWidth() {
            return location.getCharSpaceWidth();
        }

        /**
         * Computes the distance between the end of 'other' and the beginning of this chunk
         * in the direction of this chunk's orientation vector.  Note that it's a bad idea
         * to call this for chunks that aren't on the same line and orientation, but we don't
         * explicitly check for that condition for performance reasons.
         * @param other the other {@link TextChunk}
         * @return the number of spaces between the end of 'other' and the beginning of this chunk
         */
        // 计算与另一个文本块的距离
        public float distanceFromEndOf(TextChunk other){
            return location.distanceFromEndOf(other.location);
        }

        // 打印诊断信息，用于调试
        private void printDiagnostics(){
            System.out.println("Text (@" + location.getStartLocation() + " -> " + location.getEndLocation() + "): " + text);
            System.out.println("orientationMagnitude: " + location.orientationMagnitude());
            System.out.println("distPerpendicular: " + location.distPerpendicular());
            System.out.println("distParallel: " + location.distParallelStart());
        }

        /**
         * Compares based on orientation, perpendicular distance, then parallel distance
         * @param rhs the other object
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        // 比较方法，委托给位置对象进行比较
        public int compareTo(TextChunk rhs) {
            return location.compareTo(rhs.location);
        }

        // 判断是否在同一行
        private boolean sameLine(TextChunk lastChunk) {
            return getLocation().sameLine(lastChunk.getLocation());
        }
    }



    /**
     * no-op method - this renderer isn't interested in image events
     * @see com.itextpdf.text.pdf.parser.RenderListener#renderImage(com.itextpdf.text.pdf.parser.ImageRenderInfo)
     * @since 5.0.1
     */
    // 空操作方法 - 这个渲染器不处理图像事件
    public void renderImage(ImageRenderInfo renderInfo) {
        // do nothing
    }

    /**
     * Specifies a filter for filtering {@link TextChunk} objects during text extraction
     * @see LocTextExtractionStrategy#getResultantText(TextChunkFilter)
     * @since 5.3.3
     */
    // 文本块过滤器接口，用于在文本提取过程中过滤文本块
    public static interface TextChunkFilter{
        /**
         * @param textChunk the chunk to check
         * @return true if the chunk should be allowed
         */
        // 判断是否接受该文本块
        public boolean accept(TextChunk textChunk);
    }
}