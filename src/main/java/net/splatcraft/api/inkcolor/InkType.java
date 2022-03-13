package net.splatcraft.api.inkcolor;

public enum InkType {
    NORMAL, GLOWING;

    private static final InkType[] VALUES = InkType.values();

    public static InkType safeValueOf(String name) {
        try { return InkType.valueOf(name); } catch (IllegalArgumentException ignored) {}
        return VALUES[0];
    }

    public static InkType safeIndexOf(int index) {
        try { return VALUES[index]; } catch (ArrayIndexOutOfBoundsException ignored) {}
        return VALUES[0];
    }
}
