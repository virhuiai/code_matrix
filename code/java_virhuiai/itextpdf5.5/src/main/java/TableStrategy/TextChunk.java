package TableStrategy;

import com.itextpdf.text.pdf.parser.Vector;

/**
 * Represents a chunk of text, it's orientation, and location relative to the orientation vector
 */
// 表示一个文本块，包含文本内容及其位置信息
public  class TextChunk implements Comparable<TextChunk>{
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
    public void printDiagnostics(){
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
    public boolean sameLine(TextChunk lastChunk) {
        return getLocation().sameLine(lastChunk.getLocation());
    }
}