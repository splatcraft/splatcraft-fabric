package com.cibernet.splatcraft.client.config.enums;

public enum SquidFormKeyBehavior {
    TOGGLE,
    HOLD;

    protected static boolean isPressedLastTick = false;

    public boolean keyIsPressed(boolean wasPressed, boolean isPressed) {
        boolean localIsPressedLastTick = SquidFormKeyBehavior.isPressedLastTick;
        SquidFormKeyBehavior.isPressedLastTick = isPressed;

        if (this == SquidFormKeyBehavior.HOLD) {
            if (localIsPressedLastTick) {
                return !isPressed;
            } else {
                return isPressed;
            }
        } else if (this == SquidFormKeyBehavior.TOGGLE) {
            return wasPressed;
        }

        return false;
    }
}
