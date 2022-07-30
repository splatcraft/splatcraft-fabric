package net.splatcraft.impl.registry;

import net.moddingplayground.frame.api.registries.v0.dynamic.EndDynamicRegistrySetupEntrypoint;
import net.moddingplayground.frame.api.registries.v0.dynamic.RegisterDynamicRegistryCallback;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.InkColors;
import net.splatcraft.api.registry.SplatcraftRegistry;

public final class SplatcraftRegistryImpl implements Splatcraft, SplatcraftRegistry, EndDynamicRegistrySetupEntrypoint {
    @Override
    public void afterDynamicRegistrySetup() {
        RegisterDynamicRegistryCallback.EVENT.register(context -> context.register(INK_COLOR, InkColors::initAndGetDefault, InkColor.CODEC, InkColor.CODEC));
    }
}
