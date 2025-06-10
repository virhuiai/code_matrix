package com.virhuiai;

public class PdfCellTextPos extends PdfCellPos {
    private String text;

    // 私有构造函数，只允许通过Builder创建实例
    private PdfCellTextPos(Builder builder) {
        // 调用父类的setter方法设置继承的属性
        super.setyTop(builder.getyTop());
        super.setyBtm(builder.getyBtm());
        super.setxLeft(builder.getxLeft());
        super.setxRight(builder.getxRight());
        this.text = builder.text;
    }

    // 默认构造函数（如果需要保持兼容性）
    public PdfCellTextPos() {
        super();
    }

    // 只需要text字段的getter和setter
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // 静态方法返回Builder实例
    public static Builder builder() {
        return new Builder();
    }

    // Builder内部类
    public static class Builder extends PdfCellPos.Builder{

        private String text;

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        // 构建PdfCellTextPos实例
        public PdfCellTextPos build() {
            return new PdfCellTextPos(this);
        }
    }

    @Override
    public String toString() {
        return "PdfCellTextPos{" +
                "yTop=" + getyTop() +
                ", yBtm=" + getyBtm() +
                ", xLeft=" + getxLeft() +
                ", xRight=" + getxRight() +
                ", text='" + text + '\'' +
                '}';
    }
}