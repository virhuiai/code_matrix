package com.jgoodies.layout.layout;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.layout.layout.FormSpec;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/ColumnSpec.class */
public final class ColumnSpec extends FormSpec {
    public static final DefaultAlignment LEFT = FormSpec.LEFT_ALIGN;
    public static final DefaultAlignment CENTER = FormSpec.CENTER_ALIGN;
    public static final DefaultAlignment RIGHT = FormSpec.RIGHT_ALIGN;
    public static final DefaultAlignment FILL = FormSpec.FILL_ALIGN;
    public static final DefaultAlignment NONE = FormSpec.NO_ALIGN;
    public static final DefaultAlignment DEFAULT = FILL;
    private static final Map<String, ColumnSpec> CACHE = new HashMap();

    public ColumnSpec(DefaultAlignment defaultAlignment, Size size, double resizeWeight) {
        super(defaultAlignment, size, resizeWeight);
    }

    public ColumnSpec(Size size) {
        super(DEFAULT, size, FormSpec.NO_GROW);
    }

    private ColumnSpec(String encodedDescription) {
        super(DEFAULT, encodedDescription);
    }

    public static ColumnSpec createGap(ConstantSize gapWidth) {
        return new ColumnSpec(DEFAULT, gapWidth, FormSpec.NO_GROW);
    }

    public static ColumnSpec decode(String encodedColumnSpec) {
        return decode(encodedColumnSpec, LayoutMap.getRoot());
    }

    public static ColumnSpec decode(String encodedColumnSpec, LayoutMap layoutMap) {
        Preconditions.checkNotBlank(encodedColumnSpec, Messages.MUST_NOT_BE_BLANK, "encoded column specification");
        Preconditions.checkNotNull(layoutMap, Messages.MUST_NOT_BE_NULL, "LayoutMap");
        String trimmed = encodedColumnSpec.trim();
        String lower = trimmed.toLowerCase(Locale.ENGLISH);
        return decodeExpanded(layoutMap.expand(lower, true));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ColumnSpec decodeExpanded(String expandedTrimmedLowerCaseSpec) {
        ColumnSpec spec = CACHE.get(expandedTrimmedLowerCaseSpec);
        if (spec == null) {
            spec = new ColumnSpec(expandedTrimmedLowerCaseSpec);
            CACHE.put(expandedTrimmedLowerCaseSpec, spec);
        }
        return spec;
    }

    public static ColumnSpec[] decodeSpecs(String encodedColumnSpecs) {
        return decodeSpecs(encodedColumnSpecs, LayoutMap.getRoot());
    }

    public static ColumnSpec[] decodeSpecs(String encodedColumnSpecs, LayoutMap layoutMap) {
        return FormSpecParser.parseColumnSpecs(encodedColumnSpecs, layoutMap);
    }

    @Override // com.jgoodies.layout.layout.FormSpec
    protected boolean isHorizontal() {
        return true;
    }
}
