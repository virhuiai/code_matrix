package com.jgoodies.layout.layout;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.internal.CommonFormats;
import com.jgoodies.framework.completion.Suggest;
import com.jgoodies.layout.layout.FormLayout;
import com.jgoodies.layout.layout.FormSpec;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Locale;
import java.util.StringTokenizer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/CellConstraints.class */
public final class CellConstraints implements Cloneable, Serializable {
    public static final Alignment DEFAULT = new Alignment("default", 2);
    public static final Alignment FILL = new Alignment("fill", 2);
    public static final Alignment LEFT = new Alignment("left", 0);
    public static final Alignment RIGHT = new Alignment("right", 0);
    public static final Alignment CENTER = new Alignment("center", 2);
    public static final Alignment TOP = new Alignment("top", 1);
    public static final Alignment BOTTOM = new Alignment("bottom", 1);
    private static final Alignment[] VALUES = {DEFAULT, FILL, LEFT, RIGHT, CENTER, TOP, BOTTOM};
    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
    public int gridX;
    public int gridY;
    public int gridWidth;
    public int gridHeight;
    public Alignment hAlign;
    public Alignment vAlign;
    public Insets insets;
    public Boolean honorsVisibility;

    public CellConstraints() {
        this(1, 1);
    }

    public CellConstraints(int gridX, int gridY) {
        this(gridX, gridY, 1, 1);
    }

    public CellConstraints(int gridX, int gridY, Alignment hAlign, Alignment vAlign) {
        this(gridX, gridY, 1, 1, hAlign, vAlign, EMPTY_INSETS);
    }

    public CellConstraints(int gridX, int gridY, int gridWidth, int gridHeight) {
        this(gridX, gridY, gridWidth, gridHeight, DEFAULT, DEFAULT);
    }

    public CellConstraints(int gridX, int gridY, int gridWidth, int gridHeight, Alignment hAlign, Alignment vAlign) {
        this(gridX, gridY, gridWidth, gridHeight, hAlign, vAlign, EMPTY_INSETS);
    }

    public CellConstraints(int gridX, int gridY, int gridWidth, int gridHeight, Alignment hAlign, Alignment vAlign, Insets insets) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.hAlign = hAlign;
        this.vAlign = vAlign;
        this.insets = insets;
        if (gridX <= 0) {
            throw new IndexOutOfBoundsException("The grid x must be a positive number.");
        }
        if (gridY <= 0) {
            throw new IndexOutOfBoundsException("The grid y must be a positive number.");
        }
        if (gridWidth <= 0) {
            throw new IndexOutOfBoundsException("The grid width must be a positive number.");
        }
        if (gridHeight <= 0) {
            throw new IndexOutOfBoundsException("The grid height must be a positive number.");
        }
        Preconditions.checkNotNull(hAlign, Messages.MUST_NOT_BE_NULL, "horizontal alignment");
        Preconditions.checkNotNull(vAlign, Messages.MUST_NOT_BE_NULL, "vertical alignment");
        ensureValidOrientations(hAlign, vAlign);
    }

    public CellConstraints(String encodedConstraints) {
        this();
        initFromConstraints(encodedConstraints);
    }

    public CellConstraints translate(int dx, int dy) {
        return new CellConstraints(this.gridX + dx, this.gridY + dy, this.gridWidth, this.gridHeight, this.hAlign, this.vAlign, this.insets);
    }

    public CellConstraints xy(int col, int row) {
        return xywh(col, row, 1, 1);
    }

    public CellConstraints xy(int col, int row, String encodedAlignments) {
        return xywh(col, row, 1, 1, encodedAlignments);
    }

    public CellConstraints xy(int col, int row, Alignment colAlign, Alignment rowAlign) {
        return xywh(col, row, 1, 1, colAlign, rowAlign);
    }

    public CellConstraints xyw(int col, int row, int colSpan) {
        return xywh(col, row, colSpan, 1, DEFAULT, DEFAULT);
    }

    public CellConstraints xyw(int col, int row, int colSpan, String encodedAlignments) {
        return xywh(col, row, colSpan, 1, encodedAlignments);
    }

    public CellConstraints xyw(int col, int row, int colSpan, Alignment colAlign, Alignment rowAlign) {
        return xywh(col, row, colSpan, 1, colAlign, rowAlign);
    }

    public CellConstraints xywh(int col, int row, int colSpan, int rowSpan) {
        return xywh(col, row, colSpan, rowSpan, DEFAULT, DEFAULT);
    }

    public CellConstraints xywh(int col, int row, int colSpan, int rowSpan, String encodedAlignments) {
        CellConstraints result = xywh(col, row, colSpan, rowSpan);
        result.setAlignments(encodedAlignments, true);
        return result;
    }

    public CellConstraints xywh(int col, int row, int colSpan, int rowSpan, Alignment colAlign, Alignment rowAlign) {
        this.gridX = col;
        this.gridY = row;
        this.gridWidth = colSpan;
        this.gridHeight = rowSpan;
        this.hAlign = colAlign;
        this.vAlign = rowAlign;
        ensureValidOrientations(this.hAlign, this.vAlign);
        return this;
    }

    public CellConstraints rc(int row, int col) {
        return rchw(row, col, 1, 1);
    }

    public CellConstraints rc(int row, int col, String encodedAlignments) {
        return rchw(row, col, 1, 1, encodedAlignments);
    }

    public CellConstraints rc(int row, int col, Alignment rowAlign, Alignment colAlign) {
        return rchw(row, col, 1, 1, rowAlign, colAlign);
    }

    public CellConstraints rcw(int row, int col, int colSpan) {
        return rchw(row, col, 1, colSpan, DEFAULT, DEFAULT);
    }

    public CellConstraints rcw(int row, int col, int colSpan, String encodedAlignments) {
        return rchw(row, col, 1, colSpan, encodedAlignments);
    }

    public CellConstraints rcw(int row, int col, int colSpan, Alignment rowAlign, Alignment colAlign) {
        return rchw(row, col, 1, colSpan, rowAlign, colAlign);
    }

    public CellConstraints rchw(int row, int col, int rowSpan, int colSpan) {
        return rchw(row, col, rowSpan, colSpan, DEFAULT, DEFAULT);
    }

    public CellConstraints rchw(int row, int col, int rowSpan, int colSpan, String encodedAlignments) {
        CellConstraints result = rchw(row, col, rowSpan, colSpan);
        result.setAlignments(encodedAlignments, false);
        return result;
    }

    public CellConstraints rchw(int row, int col, int rowSpan, int colSpan, Alignment rowAlign, Alignment colAlign) {
        return xywh(col, row, colSpan, rowSpan, colAlign, rowAlign);
    }

    private void initFromConstraints(String encodedConstraints) {
        StringTokenizer tokenizer = new StringTokenizer(encodedConstraints, " ,");
        int argCount = tokenizer.countTokens();
        Preconditions.checkArgument(argCount == 2 || argCount == 4 || argCount == 6, "You must provide 2, 4 or 6 arguments.");
        Integer nextInt = decodeInt(tokenizer.nextToken());
        Preconditions.checkArgument(nextInt != null, "First cell constraint element must be a number.");
        this.gridX = nextInt.intValue();
        Preconditions.checkArgument(this.gridX > 0, "The grid x must be a positive number.");
        Integer nextInt2 = decodeInt(tokenizer.nextToken());
        Preconditions.checkArgument(nextInt2 != null, "Second cell constraint element must be a number.");
        this.gridY = nextInt2.intValue();
        Preconditions.checkArgument(this.gridY > 0, "The grid y must be a positive number.");
        if (!tokenizer.hasMoreTokens()) {
            return;
        }
        String token = tokenizer.nextToken();
        Integer nextInt3 = decodeInt(token);
        if (nextInt3 != null) {
            this.gridWidth = nextInt3.intValue();
            if (this.gridWidth <= 0) {
                throw new IndexOutOfBoundsException("The grid width must be a positive number.");
            }
            Integer nextInt4 = decodeInt(tokenizer.nextToken());
            if (nextInt4 == null) {
                throw new IllegalArgumentException("Fourth cell constraint element must be like third.");
            }
            this.gridHeight = nextInt4.intValue();
            if (this.gridHeight <= 0) {
                throw new IndexOutOfBoundsException("The grid height must be a positive number.");
            }
            if (!tokenizer.hasMoreTokens()) {
                return;
            } else {
                token = tokenizer.nextToken();
            }
        }
        this.hAlign = decodeAlignment(token);
        this.vAlign = decodeAlignment(tokenizer.nextToken());
        ensureValidOrientations(this.hAlign, this.vAlign);
    }

    private void setAlignments(String encodedAlignments, boolean horizontalThenVertical) {
        StringTokenizer tokenizer = new StringTokenizer(encodedAlignments, " ,");
        Alignment first = decodeAlignment(tokenizer.nextToken());
        Alignment second = decodeAlignment(tokenizer.nextToken());
        this.hAlign = horizontalThenVertical ? first : second;
        this.vAlign = horizontalThenVertical ? second : first;
        ensureValidOrientations(this.hAlign, this.vAlign);
    }

    private static Integer decodeInt(String token) {
        try {
            return Integer.decode(token);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Alignment decodeAlignment(String encodedAlignment) {
        return Alignment.valueOf(encodedAlignment);
    }

    public void ensureValidGridBounds(int colCount, int rowCount) {
        if (this.gridX <= 0) {
            throw new IndexOutOfBoundsException("The column index " + this.gridX + " must be positive.");
        }
        if (this.gridX > colCount) {
            throw new IndexOutOfBoundsException("The column index " + this.gridX + " must be less than or equal to " + colCount + ".");
        }
        if ((this.gridX + this.gridWidth) - 1 > colCount) {
            throw new IndexOutOfBoundsException("The grid width " + this.gridWidth + " must be less than or equal to " + ((colCount - this.gridX) + 1) + ".");
        }
        if (this.gridY <= 0) {
            throw new IndexOutOfBoundsException("The row index " + this.gridY + " must be positive.");
        }
        if (this.gridY > rowCount) {
            throw new IndexOutOfBoundsException("The row index " + this.gridY + " must be less than or equal to " + rowCount + ".");
        }
        if ((this.gridY + this.gridHeight) - 1 > rowCount) {
            throw new IndexOutOfBoundsException("The grid height " + this.gridHeight + " must be less than or equal to " + ((rowCount - this.gridY) + 1) + ".");
        }
    }

    private static void ensureValidOrientations(Alignment horizontalAlignment, Alignment verticalAlignment) {
        if (!horizontalAlignment.isHorizontal()) {
            throw new IllegalArgumentException("The horizontal alignment must be one of: left, center, right, fill, default.");
        }
        if (!verticalAlignment.isVertical()) {
            throw new IllegalArgumentException("The vertical alignment must be one of: top, center, bottom, fill, default.");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBounds(Component c, FormLayout layout, Rectangle cellBounds, FormLayout.Measure minWidthMeasure, FormLayout.Measure minHeightMeasure, FormLayout.Measure prefWidthMeasure, FormLayout.Measure prefHeightMeasure, FormLayout.ComponentSizeCache componentSizeCache) {
        ColumnSpec colSpec = this.gridWidth == 1 ? layout.getColumnSpec(this.gridX) : null;
        RowSpec rowSpec = this.gridHeight == 1 ? layout.getRowSpec(this.gridY) : null;
        Alignment concreteHAlign = concreteAlignment(this.hAlign, colSpec);
        Alignment concreteVAlign = concreteAlignment(this.vAlign, rowSpec);
        Insets concreteInsets = this.insets != null ? this.insets : EMPTY_INSETS;
        int cellX = cellBounds.x + concreteInsets.left;
        int cellY = cellBounds.y + concreteInsets.top;
        int cellW = (cellBounds.width - concreteInsets.left) - concreteInsets.right;
        int cellH = (cellBounds.height - concreteInsets.top) - concreteInsets.bottom;
        int compW = componentSize(c, colSpec, cellW, minWidthMeasure, prefWidthMeasure, componentSizeCache);
        int compH = componentSize(c, rowSpec, cellH, minHeightMeasure, prefHeightMeasure, componentSizeCache);
        int x = origin(concreteHAlign, cellX, cellW, compW);
        int y = origin(concreteVAlign, cellY, cellH, compH);
        int w = extent(concreteHAlign, cellW, compW);
        int h = extent(concreteVAlign, cellH, compH);
        c.setBounds(x, y, w, h);
    }

    private static Alignment concreteAlignment(Alignment cellAlignment, FormSpec formSpec) {
        if (formSpec == null) {
            return cellAlignment == DEFAULT ? FILL : cellAlignment;
        }
        return usedAlignment(cellAlignment, formSpec);
    }

    private static Alignment usedAlignment(Alignment cellAlignment, FormSpec formSpec) {
        if (cellAlignment != DEFAULT) {
            return cellAlignment;
        }
        FormSpec.DefaultAlignment defaultAlignment = formSpec.getDefaultAlignment();
        if (defaultAlignment == FormSpec.FILL_ALIGN) {
            return FILL;
        }
        if (defaultAlignment == ColumnSpec.LEFT) {
            return LEFT;
        }
        if (defaultAlignment == FormSpec.CENTER_ALIGN) {
            return CENTER;
        }
        if (defaultAlignment == ColumnSpec.RIGHT) {
            return RIGHT;
        }
        if (defaultAlignment == RowSpec.TOP) {
            return TOP;
        }
        return BOTTOM;
    }

    private static int componentSize(Component component, FormSpec formSpec, int cellSize, FormLayout.Measure minMeasure, FormLayout.Measure prefMeasure, FormLayout.ComponentSizeCache componentSizeCache) {
        if (formSpec == null) {
            return prefMeasure.sizeOf(component, componentSizeCache);
        }
        if (formSpec.getSize() == ComponentSize.MINIMUM) {
            return minMeasure.sizeOf(component, componentSizeCache);
        }
        if (formSpec.getSize() == ComponentSize.PREFERRED) {
            return prefMeasure.sizeOf(component, componentSizeCache);
        }
        return Math.min(cellSize, prefMeasure.sizeOf(component, componentSizeCache));
    }

    private static int origin(Alignment alignment, int cellOrigin, int cellSize, int componentSize) {
        if (alignment == RIGHT || alignment == BOTTOM) {
            return (cellOrigin + cellSize) - componentSize;
        }
        if (alignment == CENTER) {
            return cellOrigin + ((cellSize - componentSize) / 2);
        }
        return cellOrigin;
    }

    private static int extent(Alignment alignment, int cellSize, int componentSize) {
        return alignment == FILL ? cellSize : componentSize;
    }

    public Object clone() {
        try {
            CellConstraints c = (CellConstraints) super.clone();
            c.insets = (Insets) this.insets.clone();
            return c;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CellConstraints");
        builder.append("[x=");
        builder.append(this.gridX);
        builder.append("; y=");
        builder.append(this.gridY);
        builder.append("; w=");
        builder.append(this.gridWidth);
        builder.append("; h=");
        builder.append(this.gridHeight);
        builder.append("; hAlign=");
        builder.append(this.hAlign);
        builder.append("; vAlign=");
        builder.append(this.vAlign);
        if (!EMPTY_INSETS.equals(this.insets)) {
            builder.append("; insets=");
            builder.append(this.insets);
        }
        builder.append("; honorsVisibility=");
        builder.append(this.honorsVisibility);
        builder.append(']');
        return builder.toString();
    }

    public String toShortString() {
        return toShortString(null);
    }

    public String toShortString(FormLayout layout) {
        StringBuilder builder = new StringBuilder("(");
        builder.append(formatInt(this.gridX));
        builder.append(CommonFormats.COMMA_DELIMITER);
        builder.append(formatInt(this.gridY));
        builder.append(CommonFormats.COMMA_DELIMITER);
        builder.append(formatInt(this.gridWidth));
        builder.append(CommonFormats.COMMA_DELIMITER);
        builder.append(formatInt(this.gridHeight));
        builder.append(", \"");
        builder.append(this.hAlign.abbreviation());
        if (this.hAlign == DEFAULT && layout != null) {
            builder.append('=');
            ColumnSpec colSpec = this.gridWidth == 1 ? layout.getColumnSpec(this.gridX) : null;
            builder.append(concreteAlignment(this.hAlign, colSpec).abbreviation());
        }
        builder.append(CommonFormats.COMMA_DELIMITER);
        builder.append(this.vAlign.abbreviation());
        if (this.vAlign == DEFAULT && layout != null) {
            builder.append('=');
            RowSpec rowSpec = this.gridHeight == 1 ? layout.getRowSpec(this.gridY) : null;
            builder.append(concreteAlignment(this.vAlign, rowSpec).abbreviation());
        }
        builder.append("\"");
        if (!EMPTY_INSETS.equals(this.insets)) {
            builder.append(CommonFormats.COMMA_DELIMITER);
            builder.append(this.insets);
        }
        if (this.honorsVisibility != null) {
            builder.append(this.honorsVisibility.booleanValue() ? "honors visibility" : "ignores visibility");
        }
        builder.append(')');
        return builder.toString();
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/CellConstraints$Alignment.class */
    public static final class Alignment implements Serializable {
        private static final int HORIZONTAL = 0;
        private static final int VERTICAL = 1;
        private static final int BOTH = 2;
        private final transient String name;
        private final transient int orientation;
        private static int nextOrdinal = 0;
        private final int ordinal;

        private Alignment(String name, int orientation) {
            int i = nextOrdinal;
            nextOrdinal = i + VERTICAL;
            this.ordinal = i;
            this.name = name;
            this.orientation = orientation;
        }

        static Alignment valueOf(String nameOrAbbreviation) {
            String str = nameOrAbbreviation.toLowerCase(Locale.ENGLISH);
            boolean z = -1;
            switch (str.hashCode()) {
                case -1383228885:
                    if (str.equals("bottom")) {
                        z = 13;
                        break;
                    }
                    break;
                case -1364013995:
                    if (str.equals("center")) {
                        z = 5;
                        break;
                    }
                    break;
                case 98:
                    if (str.equals("b")) {
                        z = 12;
                        break;
                    }
                    break;
                case 99:
                    if (str.equals("c")) {
                        z = 4;
                        break;
                    }
                    break;
                case Suggest.DEFAULT_CAPACITY /* 100 */:
                    if (str.equals("d")) {
                        z = false;
                        break;
                    }
                    break;
                case 102:
                    if (str.equals("f")) {
                        z = 2;
                        break;
                    }
                    break;
                case 108:
                    if (str.equals("l")) {
                        z = 6;
                        break;
                    }
                    break;
                case 114:
                    if (str.equals("r")) {
                        z = 8;
                        break;
                    }
                    break;
                case 116:
                    if (str.equals("t")) {
                        z = 10;
                        break;
                    }
                    break;
                case 115029:
                    if (str.equals("top")) {
                        z = 11;
                        break;
                    }
                    break;
                case 3143043:
                    if (str.equals("fill")) {
                        z = 3;
                        break;
                    }
                    break;
                case 3317767:
                    if (str.equals("left")) {
                        z = 7;
                        break;
                    }
                    break;
                case 108511772:
                    if (str.equals("right")) {
                        z = 9;
                        break;
                    }
                    break;
                case 1544803905:
                    if (str.equals("default")) {
                        z = VERTICAL;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                case VERTICAL /* 1 */:
                    return CellConstraints.DEFAULT;
                case true:
                case true:
                    return CellConstraints.FILL;
                case AnimatedLabel.RIGHT /* 4 */:
                case true:
                    return CellConstraints.CENTER;
                case true:
                case true:
                    return CellConstraints.LEFT;
                case AnimatedLabel.DEFAULT_FONT_EXTRA_SIZE /* 8 */:
                case true:
                    return CellConstraints.RIGHT;
                case true:
                case true:
                    return CellConstraints.TOP;
                case true:
                case true:
                    return CellConstraints.BOTTOM;
                default:
                    throw new IllegalArgumentException("Invalid alignment " + nameOrAbbreviation + ". Must be one of: left, center, right, top, bottom, fill, default, l, c, r, t, b, f, d.");
            }
        }

        public String toString() {
            return this.name;
        }

        public char abbreviation() {
            return this.name.charAt(0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isHorizontal() {
            return this.orientation != VERTICAL;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isVertical() {
            return this.orientation != 0;
        }

        private Object readResolve() {
            return CellConstraints.VALUES[this.ordinal];
        }
    }

    private static String formatInt(int number) {
        String str = Integer.toString(number);
        return number < 10 ? " " + str : str;
    }
}
