package com.jgoodies.layout.layout;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.layout.layout.FormSpec;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/RowSpec.class */
public final class RowSpec extends FormSpec {
    public static final DefaultAlignment TOP = FormSpec.TOP_ALIGN;
    public static final DefaultAlignment CENTER = FormSpec.CENTER_ALIGN;
    public static final DefaultAlignment BOTTOM = FormSpec.BOTTOM_ALIGN;
    public static final DefaultAlignment FILL = FormSpec.FILL_ALIGN;
    public static final DefaultAlignment DEFAULT = CENTER;
    private static final Map<String, RowSpec> CACHE = new HashMap();

    public RowSpec(DefaultAlignment defaultAlignment, Size size, double resizeWeight) {
        super(defaultAlignment, size, resizeWeight);
    }

    public RowSpec(Size size) {
        super(DEFAULT, size, FormSpec.NO_GROW);
    }

    private RowSpec(String encodedDescription) {
        super(DEFAULT, encodedDescription);
    }

    public static RowSpec createGap(ConstantSize gapHeight) {
        return new RowSpec(DEFAULT, gapHeight, FormSpec.NO_GROW);
    }

    public static RowSpec decode(String encodedRowSpec) {
        return decode(encodedRowSpec, LayoutMap.getRoot());
    }

    public static RowSpec decode(String encodedRowSpec, LayoutMap layoutMap) {
        Preconditions.checkNotBlank(encodedRowSpec, Messages.MUST_NOT_BE_BLANK, "encoded row specification");
        Preconditions.checkNotNull(layoutMap, Messages.MUST_NOT_BE_NULL, "LayoutMap");
        String trimmed = encodedRowSpec.trim();
        String lower = trimmed.toLowerCase(Locale.ENGLISH);
        return decodeExpanded(layoutMap.expand(lower, false));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static RowSpec decodeExpanded(String expandedTrimmedLowerCaseSpec) {
        RowSpec spec = CACHE.get(expandedTrimmedLowerCaseSpec);
        if (spec == null) {
            spec = new RowSpec(expandedTrimmedLowerCaseSpec);
            CACHE.put(expandedTrimmedLowerCaseSpec, spec);
        }
        return spec;
    }

    public static RowSpec[] decodeSpecs(String encodedRowSpecs) {
        return decodeSpecs(encodedRowSpecs, LayoutMap.getRoot());
    }

    public static RowSpec[] decodeSpecs(String encodedRowSpecs, LayoutMap layoutMap) {
        return FormSpecParser.parseRowSpecs(encodedRowSpecs, layoutMap);
    }

    @Override // com.jgoodies.layout.layout.FormSpec
    protected boolean isHorizontal() {
        return false;
    }
}
