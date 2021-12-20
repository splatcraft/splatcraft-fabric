package net.splatcraft.util;

import net.minecraft.util.math.Vec3f;

public class Color {
    private final int color;

    private Color(int color) {
        this.color = color;
    }

    public static Color of(int color) {
        return new Color(color);
    }

    public static Color ofRGB(float r, float g, float b) {
        return ofRGB(
            (int) (r * 255 + 0.5),
            (int) (g * 255 + 0.5),
            (int) (b * 255 + 0.5)
        );
    }

    public static Color ofRGB(int r, int g, int b) {
        return new Color(
            ((r & 0xFF) << 16) |
            ((g & 0xFF) << 8 ) |
            ((b & 0xFF)      )
        );
    }

    public static Color ofHSB(float hue, float saturation, float brightness) {
        return of(HSBtoRGB(hue, saturation, brightness));
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0 -> {
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                }
                case 1 -> {
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                }
                case 2 -> {
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                }
                case 3 -> {
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                }
                case 4 -> {
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                }
                case 5 -> {
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                }
            }
        }
        return (r << 16) | (g << 8) | b;
    }

    public int getColor() {
        return this.color;
    }

    public int getRed() {
        return this.color >> 16 & 0xFF;
    }

    public int getGreen() {
        return this.color >> 8 & 0xFF;
    }

    public int getBlue() {
        return this.color & 0xFF;
    }

    public Vec3f getVector() {
        return new Vec3f(this.getRed() / 255.0f, this.getGreen() / 255.0f, this.getBlue() / 255.0f);
    }

    /**
     * Returns a brighter color
     *
     * @param factor the higher the value, the brighter the color
     * @return the brighter color
     */
    public Color brighter(double factor) {
        int r = getRed(), g = getGreen(), b = getBlue();
        int i = (int) (1.0 / (1.0 - (1 / factor)));
        if (r == 0 && g == 0 && b == 0) {
            return ofRGB(i, i, i);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;
        return ofRGB(Math.min((int) (r / (1 / factor)), 255),
            Math.min((int) (g / (1 / factor)), 255),
            Math.min((int) (b / (1 / factor)), 255));
    }

    /**
     * Returns a darker color
     *
     * @param factor the higher the value, the darker the color
     * @return the darker color
     */
    public Color darker(double factor) {
        return ofRGB(Math.max((int) (getRed() * (1 / factor)), 0),
            Math.max((int) (getGreen() * (1 / factor)), 0),
            Math.max((int) (getBlue() * (1 / factor)), 0));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return this.color == ((Color) other).color;
    }

    @Override
    public int hashCode() {
        return this.color;
    }

    @Override
    public String toString() {
        return String.valueOf(this.color);
    }
}
