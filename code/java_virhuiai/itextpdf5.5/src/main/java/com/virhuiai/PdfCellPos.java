package com.virhuiai;


public class PdfCellPos {
    private double yTop;
    private double yBtm;
    private double xLeft;
    private double xRight;

    // 私有构造函数，只允许通过Builder创建实例
    private PdfCellPos(Builder builder) {
        this.yTop = builder.yTop;
        this.yBtm = builder.yBtm;
        this.xLeft = builder.xLeft;
        this.xRight = builder.xRight;
    }

    // 默认构造函数（如果需要保持兼容性）
    public PdfCellPos() {
    }

    public double getyTop() {
        return yTop;
    }

    public void setyTop(double yTop) {
        this.yTop = yTop;
    }

    public double getyBtm() {
        return yBtm;
    }

    public void setyBtm(double yBtm) {
        this.yBtm = yBtm;
    }

    public double getxLeft() {
        return xLeft;
    }

    public void setxLeft(double xLeft) {
        this.xLeft = xLeft;
    }

    public double getxRight() {
        return xRight;
    }

    public void setxRight(double xRight) {
        this.xRight = xRight;
    }

    // 静态方法返回Builder实例
    public static Builder builder() {
        return new Builder();
    }

    // Builder内部类
    public static class Builder {
        private double yTop;
        private double yBtm;
        private double xLeft;
        private double xRight;

        public Builder yTop(double yTop) {
            this.yTop = yTop;
            return this;
        }

        public Builder yBtm(double yBtm) {
            this.yBtm = yBtm;
            return this;
        }

        public Builder xLeft(double xLeft) {
            this.xLeft = xLeft;
            return this;
        }

        public Builder xRight(double xRight) {
            this.xRight = xRight;
            return this;
        }

        // 构建PdfCellPos实例
        public PdfCellPos build() {
            return new PdfCellPos(this);
        }
    }

    @Override
    public String toString() {
        return "PdfCellPos{" +
                "yTop=" + yTop +
                ", yBtm=" + yBtm +
                ", xLeft=" + xLeft +
                ", xRight=" + xRight +
                '}';
    }
}