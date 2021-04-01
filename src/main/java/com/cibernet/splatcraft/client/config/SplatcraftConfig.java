package com.cibernet.splatcraft.client.config;

import com.cibernet.splatcraft.client.config.enums.InkAmountIndicator;
import com.cibernet.splatcraft.client.config.enums.PreventBobView;
import com.cibernet.splatcraft.client.config.enums.SquidFormKeyBehavior;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import me.andante.chord.client.config.EnumOption;
import me.andante.chord.client.config.Option;
import me.andante.chord.client.config.RangedOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SplatcraftConfig {
    public static RenderGroup RENDER = new RenderGroup();
    public static class RenderGroup {
        /**
         * Enable or disable having to hold stage barriers or voids to render.
         */
        public Option<Boolean> holdStageBarrierToRender = register(new Option<>("hold_stage_barrier_to_render", true));
        /**
         * How far away stage barriers or voids will render away from you.
         */
        public RangedOption<Integer> barrierRenderDistance = register(new RangedOption<>("barrier_render_distance", 16, 4, 80));
        /**
         * Enable or disable inked blocks' color layer being transparent.
         */
        public Option<Boolean> inkedBlocksColorLayerIsTransparent = register(new Option<>("inked_blocks_color_layer_is_transparent", true));
    }

    public static UIGroup UI = new UIGroup();
    public static class UIGroup {
        /**
         * Choose when view bobbing should occur when in squid form, if at all.
         */
        public EnumOption<PreventBobView> preventBobViewWhenSquid = register(new EnumOption<>("prevent_bob_view_when_squid", PreventBobView.class, PreventBobView.ALWAYS));
        /**
         * Enable or disable modifying the player's fov if in squid form.
         */
        public Option<Boolean> modifyFovForSquidForm = register(new Option<>("modify_fov_for_squid_form", true));
        /**
         * How much to modify the player's fov if in squid form.
         */
        public RangedOption<Integer> fovForSquidForm = register(new RangedOption<>("fov_for_squid_form", 15, -100, 100));
        /**
         * Enable or disable disabling the hotbar when in squid form.
         */
        public Option<Boolean> invisibleHotbarWhenSquid = register(new Option<>("invisible_hotbar_when_squid", true));
        /**
         * Enable or disable rendering the held item when the hotbar is set to be invisible in squid form.
         */
        public Option<Boolean> renderHeldItemWhenHotbarInvisible = register(new Option<>("render_held_item_when_hotbar_invisible", false));
        /**
         * How far the status bars should shift when the hotbar is invisible.
         */
        public Option<Integer> invisibleHotbarStatusBarsShift = register(new Option<>("invisible_hotbar_status_bars_shift", -22));
        /**
         * Enable or disable disabling the crosshair when in squid form.
         */
        public Option<Boolean> invisibleCrosshairWhenSquid = register(new Option<>("invisible_crosshair_when_squid", true));
        /**
         * Enable or disable rendering your player's paper doll when signalling.
         */
        public Option<Boolean> renderPaperDollWhenSignalling = register(new Option<>("render_paper_doll_when_signalling", true));
    }

    public static InkGroup INK = new InkGroup();
    public static class InkGroup {
        // /**
        //  * Enable or disable a weapon's durability bar's colour to match the player's ink colour.
        //  */
        // public Option dynamicInkDurabilityColor<Boolean> = register(new Option<>("dynamic_ink_durability_color", true));
        /**
         * Enable or disable an ink-colored crosshair when in squid form.
         */
        public Option<Boolean> inkColoredCrosshairWhenSquid = register(new Option<>("ink_colored_crosshair_when_squid", true));
        /**
         * Choose where the ink amount indicator is displayed, if at all.
         */
        public EnumOption<InkAmountIndicator> inkAmountIndicator = register(new EnumOption<>("ink_amount_indicator", InkAmountIndicator.class, InkAmountIndicator.HOTBAR));
        /**
         * Enable or disable the ink amount indicator always being visible if an ink tank is present.
         */
        public Option<Boolean> inkAmountIndicatorAlwaysVisible = register(new Option<>("ink_amount_indicator_always_visible", false));
        /**
         * Enable or disable the ink amount indicator displaying exclamation points when full or empty.
         */
        public Option<Boolean> inkAmountIndicatorExclamations = register(new Option<>("ink_amount_exclamations", false));
        /**
         * When the ink amount indicator low ink warning should display.
         */
        public RangedOption<Integer> inkAmountIndicatorExclamationsMin = register(new RangedOption<>("ink_amount_exclamations_min", 1, 1, 100));
        /**
         * When the ink amount indicator full ink warning should display.
         */
        public RangedOption<Integer> inkAmountIndicatorExclamationsMax = register(new RangedOption<>("ink_amount_exclamations_max", 92, 0, 100));
    }

    public static AccessibilityGroup ACCESSIBILITY = new AccessibilityGroup();
    public static class AccessibilityGroup {
        /**
         * Decide how the squid form key behaves.
         */
        public EnumOption<SquidFormKeyBehavior> squidFormKeyBehavior = register(new EnumOption<>("squid_form_key_behavior", SquidFormKeyBehavior.class, SquidFormKeyBehavior.HOLD));
        /**
         * Enable or disable color lock.
         */
        public Option<Boolean> colorLock = register(new Option<>("color_lock", false));
        /**
         * Your color when color lock is enabled.
         */
        public Option<Integer> colorLockFriendly = register(new Option<>("color_lock_friendly", ColorUtils.DEFAULT_COLOR_LOCK_FRIENDLY));
        /**
         * Others' color when color lock is enabled.
         */
        public Option<Integer> colorLockHostile = register(new Option<>("color_lock_hostile", ColorUtils.DEFAULT_COLOR_LOCK_HOSTILE));
    }

    private static <T extends Option<?>> T register(T option) {
        SplatcraftConfigManager.OPTIONS.add(option);
        return option;
    }
}
