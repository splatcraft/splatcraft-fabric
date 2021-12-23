package net.splatcraft.client.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.config.enums.PreventBobView;
import net.splatcraft.client.keybind.ChangeSquidFormKeyBehavior;
import net.splatcraft.config.Config;
import net.splatcraft.config.option.BooleanOption;
import net.splatcraft.config.option.ColorOption;
import net.splatcraft.config.option.EnumOption;
import net.splatcraft.config.option.IntOption;

import java.io.File;

@Environment(EnvType.CLIENT)
public class ClientConfig extends Config {
    public static final ClientConfig INSTANCE = new ClientConfig(createFile("%1$s/%1$s-client".formatted(Splatcraft.MOD_ID)));
    static {
        INSTANCE.load();
    }

    public final EnumOption<ChangeSquidFormKeyBehavior> changeSquidKeyBehavior = add("change_squid_form_key_behavior", EnumOption.of(ChangeSquidFormKeyBehavior.class, ChangeSquidFormKeyBehavior.HOLD));
    public final BooleanOption instantSquidFormChangeClient = add("instant_squid_form_change_client", BooleanOption.of(true));

    public final EnumOption<PreventBobView> preventBobViewWhenSquid = add("prevent_bob_view_when_squid", EnumOption.of(PreventBobView.class, PreventBobView.ALWAYS));
    public final IntOption stageBlockRadius = add("stage_block_radius", IntOption.of(2, 2, 4));

    public final BooleanOption colorLock = add("color_lock", BooleanOption.of(false));
    public final ColorOption colorLockFriendly = add("color_lock_friendly", ColorOption.of(0xDEA801));
    public final ColorOption colorLockHostile = add("color_lock_hostile", ColorOption.of(0x4717A9));

    public final BooleanOption inkSplashParticleOnTravel = add("ink_splash_particle_on_travel", BooleanOption.of(true));
    public final BooleanOption inkSquidSoulParticleOnDeath = add("ink_squid_soul_particle_on_death", BooleanOption.of(true));

    private ClientConfig(File file) {
        super(file);
    }

    @Override
    public void save() {
        super.save();
        WorldRenderer worldRenderer = MinecraftClient.getInstance().worldRenderer;
        if (worldRenderer != null) worldRenderer.reload();
    }
}
