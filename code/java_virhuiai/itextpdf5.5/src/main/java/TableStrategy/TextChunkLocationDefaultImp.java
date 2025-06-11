package TableStrategy;

import com.itextpdf.text.pdf.parser.Vector;

// 文本块位置的默认实现
public class TextChunkLocationDefaultImp implements TextChunkLocation {

    /** the starting location of the chunk */
    private final Vector startLocation;  // 文本块的起始位置
    /** the ending location of the chunk */
    private final Vector endLocation;    // 文本块的结束位置
    /** unit vector in the orientation of the chunk */
    private final Vector orientationVector;  // 文本块方向的单位向量
    /** the orientation as a scalar for quick sorting */
    private final int orientationMagnitude;  // 方向的标量值，用于快速排序
    /** perpendicular distance to the orientation unit vector (i.e. the Y position in an unrotated coordinate system)
     * we round to the nearest integer to handle the fuzziness of comparing floats */
    // 到方向单位向量的垂直距离（相当于未旋转坐标系中的Y位置）
    // 四舍五入到最近的整数以处理浮点数比较的模糊性
    private final int distPerpendicular;
    /** distance of the start of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system) */
    private final float distParallelStart;  // 文本块起始点平行于方向单位向量的距离（相当于X位置）
    /** distance of the end of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system) */
    private final float distParallelEnd;    // 文本块结束点平行于方向单位向量的距离
    /** the width of a single space character in the font of the chunk */
    private final float charSpaceWidth;     // 该文本块字体中单个空格字符的宽度

    // 构造函数，计算文本块的各种位置属性
    public TextChunkLocationDefaultImp(Vector startLocation, Vector endLocation, float charSpaceWidth) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.charSpaceWidth = charSpaceWidth;

        // 计算方向向量
        Vector oVector = endLocation.subtract(startLocation);
        if (oVector.length() == 0) {
            oVector = new Vector(1, 0, 0);  // 如果起始和结束位置相同，使用默认方向
        }
        orientationVector = oVector.normalize();  // 归一化为单位向量
        // 计算方向的数值表示（使用反正切函数）
        orientationMagnitude = (int)(Math.atan2(orientationVector.get(Vector.I2), orientationVector.get(Vector.I1))*1000);

        // see http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
        // the two vectors we are crossing are in the same plane, so the result will be purely
        // in the z-axis (out of plane) direction, so we just take the I3 component of the result
        // 计算到原点的垂直距离
        // 使用叉积计算点到直线的距离（参考Wolfram MathWorld的公式）
        Vector origin = new Vector(0,0,1);
        distPerpendicular = (int)(startLocation.subtract(origin)).cross(orientationVector).get(Vector.I3);

        // 计算平行距离
        distParallelStart = orientationVector.dot(startLocation);
        distParallelEnd = orientationVector.dot(endLocation);
    }

    // 获取方向数值
    public int orientationMagnitude() {return orientationMagnitude;}
    // 获取垂直距离
    public int distPerpendicular() {return distPerpendicular;}
    // 获取平行起始距离
    public float distParallelStart() {return distParallelStart; }
    // 获取平行结束距离
    public float distParallelEnd() { return distParallelEnd;}

    /**
     * @return the start location of the text
     */
    // 获取文本的起始位置
    public Vector getStartLocation(){
        return startLocation;
    }

    /**
     * @return the end location of the text
     */
    // 获取文本的结束位置
    public Vector getEndLocation(){
        return endLocation;
    }

    /**
     * @return the width of a single space character as rendered by this chunk
     */
    // 获取空格字符的宽度
    public float getCharSpaceWidth() {
        return charSpaceWidth;
    }

    /**
     * @param as the location to compare to
     * @return true is this location is on the the same line as the other
     */
    // 判断是否与另一个位置在同一行
    // 通过比较方向和垂直距离来判断
    public boolean sameLine(TextChunkLocation as){
        return orientationMagnitude() == as.orientationMagnitude() && distPerpendicular() == as.distPerpendicular();
    }

    /**
     * Computes the distance between the end of 'other' and the beginning of this chunk
     * in the direction of this chunk's orientation vector.  Note that it's a bad idea
     * to call this for chunks that aren't on the same line and orientation, but we don't
     * explicitly check for that condition for performance reasons.
     * @param other
     * @return the number of spaces between the end of 'other' and the beginning of this chunk
     */
    // 计算两个文本块之间的距离
    // 返回'other'的结束位置到当前块开始位置的距离
    public float distanceFromEndOf(TextChunkLocation other){
        float distance = distParallelStart() - other.distParallelEnd();
        return distance;
    }

    // 判断是否在单词边界
    // 如果两个文本块之间的距离大于半个空格宽度，则认为在单词边界
    public boolean isAtWordBoundary(TextChunkLocation previous){
        float dist = distanceFromEndOf(previous);

        if (dist < 0) {
            dist = previous.distanceFromEndOf(this);

            //The situation when the chunks intersect. We don't need to add space in this case
            // 文本块相交的情况，此时不需要添加空格
            if (dist < 0) {
                return false;
            }
        }
        return dist > getCharSpaceWidth() / 2.0f;
    }

    // 比较方法，用于排序
    // 首先按方向排序，然后按垂直距离，最后按水平距离
    public int compareTo(TextChunkLocation other) {
        if (this == other) return 0; // not really needed, but just in case

        int rslt;
        rslt = compareInts(orientationMagnitude(), other.orientationMagnitude());
        if (rslt != 0) return rslt;

        rslt = compareInts(distPerpendicular(), other.distPerpendicular());
        if (rslt != 0) return rslt;

        return Float.compare(distParallelStart(), other.distParallelStart());
    }

    /**
     *
     * @param int1
     * @param int2
     * @return comparison of the two integers
     */
    // 比较两个整数的辅助方法
    private static int compareInts(int int1, int int2){
        return int1 == int2 ? 0 : int1 < int2 ? -1 : 1;
    }
}