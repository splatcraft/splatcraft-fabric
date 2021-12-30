package net.splatcraft.inkcolor;

public enum InkType {
    NORMAL, GLOWING;

    public static InkType safeValueOf(String name) {
        try { return InkType.valueOf(name); } catch (IllegalArgumentException ignored) {}
        return InkType.values()[0];
    }
}
