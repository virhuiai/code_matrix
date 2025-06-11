package TableStrategy;

import com.itextpdf.text.pdf.parser.Vector;

// 文本块位置接口，定义了位置相关的操作
public  interface TextChunkLocation extends Comparable<TextChunkLocation> {

    float distParallelEnd();    // 平行于方向向量的结束距离

    float distParallelStart();  // 平行于方向向量的开始距离

    int distPerpendicular();    // 垂直于方向向量的距离

    float getCharSpaceWidth();  // 空格字符的宽度

    Vector getEndLocation();    // 结束位置

    Vector getStartLocation();  // 开始位置

    int orientationMagnitude(); // 方向的数值表示

    boolean sameLine(TextChunkLocation as);  // 是否在同一行

    float distanceFromEndOf(TextChunkLocation other);  // 与其他位置的距离

    boolean isAtWordBoundary(TextChunkLocation previous);  // 是否在单词边界
}