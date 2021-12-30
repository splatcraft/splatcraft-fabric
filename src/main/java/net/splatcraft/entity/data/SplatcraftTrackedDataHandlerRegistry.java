package net.splatcraft.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkType;
import net.splatcraft.mixin.TrackedDataHandlerRegistryAccessor;

import java.util.ArrayList;

public class SplatcraftTrackedDataHandlerRegistry {
    private static final ArrayList<TrackedDataHandler<?>> HANDLERS = new ArrayList<>();

    public static final TrackedDataHandler<InkColor> INK_COLOR = register(new IdentifiableTrackedDataHandler<>(InkColor::fromId));
    public static final TrackedDataHandler<InkType> INK_TYPE = register(new EnumTrackedDataHandler<>(InkType.class));

    static {
        HANDLERS.forEach(TrackedDataHandlerRegistryAccessor.getDataHandlers()::add);
    }

    public static <T> TrackedDataHandler<T> register(TrackedDataHandler<T> handler) {
        HANDLERS.add(handler);
        return handler;
    }
}
