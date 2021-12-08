package net.splatcraft.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.mixin.TrackedDataHandlerRegistryAccessor;

import java.util.ArrayList;

public class SplatcraftTrackedDataHandlers {
    private static final ArrayList<TrackedDataHandler<?>> HANDLERS = new ArrayList<>();

    public static final TrackedDataHandler<InkColor> INK_COLOR = register(new IdentifiableTrackedDataHandler<>(InkColor::fromId));

    static {
        HANDLERS.forEach(TrackedDataHandlerRegistryAccessor.getDataHandlers()::add);
    }

    public static <T> TrackedDataHandler<T> register(TrackedDataHandler<T> handler) {
        HANDLERS.add(handler);
        return handler;
    }
}
