package com.jgoodies.animation.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Random;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/renderer/FanRenderer.class */
public final class FanRenderer implements AnimationRenderer {
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private final Triangle[] triangles;
    private Point2D origin;
    private double rotation;

    public FanRenderer(Triangle[] triangles) {
        this.triangles = triangles;
    }

    public FanRenderer(int triangleCount, Color baseColor) {
        this(createSectors(triangleCount, baseColor));
    }

    public static Triangle[] createSectors(int count, Color baseColor) {
        Triangle[] result = new Triangle[count];
        double sectorAngle = 6.283185307179586d / count;
        for (int i = 0; i < count; i++) {
            double rotation = (i * sectorAngle) + (((RANDOM.nextFloat() - 0.5d) * 3.141592653589793d) / 10.0d);
            double angle = sectorAngle * (0.2d + (RANDOM.nextFloat() * 0.4d));
            result[i] = new Triangle(rotation, angle, nextColor(baseColor));
        }
        return result;
    }

    private static Color nextColor(Color baseColor) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), hsb);
        float brightness = 0.8f + (RANDOM.nextFloat() * 0.2f);
        return Color.getHSBColor(hsb[0], hsb[1], brightness);
    }

    public Point2D getOrigin() {
        return this.origin;
    }

    public void setOrigin(Point2D origin) {
        this.origin = origin;
    }

    public double getRotation() {
        return this.rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    @Override // com.jgoodies.animation.renderer.AnimationRenderer
    public void render(Graphics2D g2, int width, int height) {
        double radius = Math.sqrt((width * width) + (height * height));
        Point2D p = getOrigin() != null ? getOrigin() : getDefaultOrigin(width, height);
        g2.translate(p.getX(), p.getY());
        g2.rotate(this.rotation);
        for (Triangle element : this.triangles) {
            element.render(g2, radius);
        }
        g2.rotate(-this.rotation);
        g2.translate(-p.getX(), -p.getY());
    }

    private static Point2D getDefaultOrigin(int width, int height) {
        return new Point2D.Double(width * 0.75d, height * 0.75d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/renderer/FanRenderer$Triangle.class */
    public static final class Triangle {
        private final double aRotation;
        private final double angle;
        private final Color color;

        private Triangle(double rotation, double angle, Color color) {
            this.aRotation = rotation;
            this.angle = angle;
            this.color = color;
        }

        private static Shape createPolygon(double rotation, double angle, double radius) {
            double startAngle = rotation - (angle / 2.0d);
            double stopAngle = startAngle + angle;
            double hypothenusis = radius / Math.cos(angle / 2.0d);
            float x1 = (float) (0.0f - (hypothenusis * Math.cos(startAngle)));
            float y1 = (float) (0.0f - (hypothenusis * Math.sin(startAngle)));
            float x2 = (float) (0.0f - (hypothenusis * Math.cos(stopAngle)));
            float y2 = (float) (0.0f - (hypothenusis * Math.sin(stopAngle)));
            GeneralPath polygon = new GeneralPath(0, 3);
            polygon.moveTo(0.0f, 0.0f);
            polygon.lineTo(x1, y1);
            polygon.lineTo(x2, y2);
            polygon.closePath();
            return polygon;
        }

        void render(Graphics2D g2, double radius) {
            g2.setColor(this.color);
            g2.fill(createPolygon(this.aRotation, this.angle, radius));
        }
    }
}
