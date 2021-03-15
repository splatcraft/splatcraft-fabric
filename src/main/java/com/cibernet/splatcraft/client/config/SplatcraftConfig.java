package com.cibernet.splatcraft.client.config;

import com.cibernet.splatcraft.client.config.enums.InkAmountIndicator;
import com.cibernet.splatcraft.client.config.enums.PreventBobView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SplatcraftConfig {
    public static RenderGroup RENDER = new RenderGroup();

    public static class RenderGroup {
        /**
         * Enable or disable having to hold stage barriers or voids to render.
         */
        public Option<Boolean> holdStageBarrierToRender = new Option<>("hold_stage_barrier_to_render", true);
        /**
         * How far away stage barriers or voids will render away from you.
         */
        public RangedOption<Integer> barrierRenderDistance = new RangedOption<>("barrier_render_distance", 16, 4, 80);
        /**
         * Enable or disable inked blocks' color layer being transparent.
         */
        public Option<Boolean> inkedBlocksColorLayerIsTransparent = new Option<>("inked_blocks_color_layer_is_transparent", true);
    }

    public static UIGroup UI = new UIGroup();
    public static class UIGroup {
        /**
         * Choose when view bobbing should occur when in squid form, if at all.
         */
        public EnumOption<PreventBobView> preventBobViewWhenSquid = new EnumOption<>("prevent_bob_view_when_squid", PreventBobView.class, PreventBobView.ALWAYS);
        /**
         * Enable or disable modifying the player's fov if in squid form.
         */
        public Option<Boolean> modifyFovForSquidForm = new Option<>("modify_fov_for_squid_form", true);
        /**
         * How much to modify the player's fov if in squid form.
         */
        public RangedOption<Integer> fovForSquidForm = new RangedOption<>("fov_for_squid_form", 15, -100, 100);
        /**
         * Enable or disable disabling the hotbar when in squid form.
         */
        public Option<Boolean> invisibleHotbarWhenSquid = new Option<>("invisible_hotbar_when_squid", true);
        /**
         * Enable or disable rendering the held item when the hotbar is set to be invisible in squid form.
         */
        public Option<Boolean> renderHeldItemWhenHotbarInvisible = new Option<>("render_held_item_when_hotbar_invisible", false);
        /**
         * How far the status bars should shift when the hotbar is invisible.
         */
        public Option<Integer> invisibleHotbarStatusBarsShift = new Option<>("invisible_hotbar_status_bars_shift", -22);
        /**
         * Enable or disable disabling the crosshair when in squid form.
         */
        public Option<Boolean> invisibleCrosshairWhenSquid = new Option<>("invisible_crosshair_when_squid", true);
    }

    public static InkGroup INK = new InkGroup();
    public static class InkGroup {
        /**
         * Enable or disable color lock.
         */
        public Option<Boolean> colorLock = new Option<>("color_lock", false);
        // /**
        //  * Enable or disable a weapon's durability bar's colour to match the player's ink colour.
        //  */
        // public Option dynamicInkDurabilityColor<Boolean> = new Option<>("dynamic_ink_durability_color", true);
        /**
         * Enable or disable an ink-colored crosshair when in squid form.
         */
        public Option<Boolean> inkColoredCrosshairWhenSquid = new Option<>("ink_colored_crosshair_when_squid", true);
        /**
         * Choose where the ink amount indicator is displayed, if at all.
         */
        public EnumOption<InkAmountIndicator> inkAmountIndicator = new EnumOption<>("ink_amount_indicator", InkAmountIndicator.class, InkAmountIndicator.HOTBAR);
        /**
         * Enable or disable the ink amount indicator always being visible if an ink tank is present.
         */
        public Option<Boolean> inkAmountIndicatorAlwaysVisible = new Option<>("ink_amount_indicator_always_visible", false);
        /**
         * Enable or disable the ink amount indicator displaying exclamation points when full or empty.
         */
        public Option<Boolean> inkAmountIndicatorExclamations = new Option<>("ink_amount_exclamations", false);
        /**
         * When the ink amount indicator low ink warning should display.
         */
        public RangedOption<Integer> inkAmountIndicatorExclamationsMin = new RangedOption<>("ink_amount_exclamations_min", 12, 0, 100);
        /**
         * When the ink amount indicator full ink warning should display.
         */
        public RangedOption<Integer> inkAmountIndicatorExclamationsMax = new RangedOption<>("ink_amount_exclamations_max", 92, 0, 100);
    }

    /**
     * A configuration option.
     */
    public static class Option<T> {
        private final String id;
        public T value;
        protected final T defaultValue;

        /**
         * Instantiates a new configuration option.
         *
         * @param id The option's identifier.
         * @param defaultVal The option's default value.
         */
        private Option(String id, T defaultVal) {
            this.id = id;
            this.defaultValue = defaultVal;
            this.value = this.defaultValue;

            SplatcraftConfigManager.OPTIONS.add(this);
        }

        public T getDefault() {
            return this.defaultValue;
        }
        public void setValue(T value) {
            this.value = value;
        }

        public String getId() {
            return this.id;
        }

        public String getValueForSave() {
            return String.valueOf(this.value);
        }
    }
    public static class RangedOption<T extends Number> extends Option<T> {
        private final T min;
        private final T max;

        /**
         * Instantiates a new ranged configuration option.
         *
         * @param id         The option's identifier.
         * @param defaultVal The option's default value.
         * @param min        The option's minimum value.
         * @param max        The option's maximum value.
         */
        private RangedOption(String id, T defaultVal, T min, T max) {
            super(id, defaultVal);
            this.min = min;
            this.max = max;
        }

        public T getMin() {
            return this.min;
        }
        public T getMax() {
            return this.max;
        }
    }
    public static class EnumOption<T extends Enum<?>> extends Option<T> {
        private final Class<T> clazz;

        /**
         * Instantiates a new ranged configuration option.
         *
         * @param id         The option's identifier.
         * @param defaultVal The option's default value.
         */
        private EnumOption(String id, Class<T> clazz, T defaultVal) {
            super(id, defaultVal);
            this.clazz = clazz;
        }

        public Class<T> getClazz() {
            return this.clazz;
        }

        @Override
        public String getValueForSave() {
            return this.value.toString();
        }
    }
}
