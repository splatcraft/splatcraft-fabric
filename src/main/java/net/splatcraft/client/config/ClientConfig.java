package net.splatcraft.client.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.Identifier;
import net.moddingplayground.frame.api.config.v0.Config;
import net.moddingplayground.frame.api.config.v0.option.BooleanOption;
import net.moddingplayground.frame.api.config.v0.option.ColorOption;
import net.moddingplayground.frame.api.config.v0.option.EnumOption;
import net.moddingplayground.frame.api.config.v0.option.IntOption;
import net.moddingplayground.frame.api.config.v0.option.Option;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.config.enums.HealthInkOverlay;
import net.splatcraft.client.config.enums.PreventBobView;
import net.splatcraft.client.keybind.ChangeSquidFormKeyBehavior;

import java.io.File;

@Environment(EnvType.CLIENT)
public class ClientConfig extends Config {
    public static final ClientConfig INSTANCE = new ClientConfig(createFile("%1$s/%1$s-client".formatted(Splatcraft.MOD_ID))).load();

    public final BooleanOption colorLock = add("color_lock", BooleanOption.of(false));
    public final ColorOption colorLockFriendly = add("color_lock_friendly", ColorOption.of(0xDEA801));
    public final ColorOption colorLockHostile = add("color_lock_hostile", ColorOption.of(0x4717A9));

    public final EnumOption<ChangeSquidFormKeyBehavior> changeSquidKeyBehavior = add("change_squid_form_key_behavior", EnumOption.of(ChangeSquidFormKeyBehavior.class, ChangeSquidFormKeyBehavior.HOLD));

    public final BooleanOption inkSplashParticleOnTravel = add("ink_splash_particle_on_travel", BooleanOption.of(true));
    public final BooleanOption inkSquidSoulParticleOnDeath = add("ink_squid_soul_particle_on_death", BooleanOption.of(true));

    public final EnumOption<HealthInkOverlay> healthInkOverlay = add("health_ink_overlay", EnumOption.of(HealthInkOverlay.class, HealthInkOverlay.ON));
    public final EnumOption<PreventBobView> preventBobViewWhenSquid = add("prevent_bob_view_when_squid", EnumOption.of(PreventBobView.class, PreventBobView.ALWAYS));
    public final BooleanOption cancelCapeRenderWithInkTank = add("cancel_cape_render_with_ink_tank", BooleanOption.of(true));
    public final IntOption stageBlockRadius = add("stage_block_radius", IntOption.of(2, 2, 4));

    public final BooleanOption optimiseDesync = add("optimise_desync", BooleanOption.of(false));

    private <T, O extends Option<T>> O add(String id, O option) {
        return this.add(new Identifier(Splatcraft.MOD_ID, id), option);
    }

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
