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
        public Option holdStageBarrierToRender = new Option("hold_stage_barrier_to_render", true);
        /**
         * How far away stage barriers or voids will render away from you.
         */
        public RangedOption barrierRenderDistance = new RangedOption("barrier_render_distance", 16, 4, 80);
        /**
         * Enable or disable inked blocks' color layer being transparent.
         */
        public Option inkedBlocksColorLayerIsTransparent = new Option("inked_blocks_color_layer_is_transparent", true);
    }

    public static UIGroup UI = new UIGroup();
    public static class UIGroup {
        /**
         * Choose when view bobbing should occur when in squid form, if at all.
         */
        public EnumOption<PreventBobView> preventBobViewWhenSquid = new EnumOption<>("prevent_bob_view_when_squid", PreventBobView.class, PreventBobView.ALWAYS);
        /**
         * Enable or disable disabling the hotbar when in squid form.
         */
        public Option invisibleHotbarWhenSquid = new Option("invisible_hotbar_when_squid", true);
        /**
         * How far the status bars should shift when the hotbar is invisible.
         */
        public Option invisibleHotbarStatusBarsShift = new Option("invisible_hotbar_status_bars_shift", -22);
        /**
         * Enable or disable disabling the crosshair when in squid form.
         */
        public Option invisibleCrosshairWhenSquid = new Option("invisible_crosshair_when_squid", true);
        /**
         * Enable or disable an ink-colored crosshair when in squid form.
         */
        public Option inkColoredCrosshairWhenSquid = new Option("ink_colored_crosshair_when_squid", true);
        /**
         * Choose where the ink amount indicator is displayed, if at all.
         */
        public EnumOption<InkAmountIndicator> inkAmountIndicator = new EnumOption<>("ink_amount_indicator", InkAmountIndicator.class, InkAmountIndicator.HOTBAR);
        /**
         * Enable or disable the ink amount indicator always being visible if an ink tank is present.
         */
        public Option inkAmountIndicatorAlwaysVisible = new Option("ink_amount_indicator_always_visible", false);
    }

    public static ColorsGroup COLORS = new ColorsGroup();
    public static class ColorsGroup {
        /**
         * Enable or disable color lock.
         */
        public Option colorLock = new Option("color_lock", false);
        /**
         * Enable or disable a weapon's durability bar's colour to match the player's ink colour.
         */
        public Option dynamicInkDurabilityColor = new Option("dynamic_ink_durability_color", true);
    }

    /**
     * A configuration option.
     */
    public static class Option {
        private final String id;
        public Object value;
        protected final Object defaultValue;

        /**
         * Instantiates a new configuration option.
         *
         * @param id The option's identifier.
         * @param defaultVal The option's default value.
         */
        private Option(String id, Object defaultVal) {
            this.id = id;
            this.defaultValue = defaultVal;
            this.value = this.defaultValue;
        }

        public boolean getBoolean() {
            if (value instanceof Boolean) return (Boolean)this.value;
            else throw new RuntimeException();
        }
        public int getInt() {
            if (value instanceof Integer) return (Integer)this.value;
            else throw new RuntimeException();
        }
        public float getFloat() {
            if (value instanceof Float) return (Float)this.value;
            else throw new RuntimeException();
        }

        public Boolean getDefaultBoolean() {
            if (value instanceof Boolean) return (Boolean)this.defaultValue;
            else throw new RuntimeException();
        }
        public int getDefaultInt() {
            if (value instanceof Integer) return (Integer)this.defaultValue;
            else throw new RuntimeException();
        }

        public Object getDefault() {
            return this.defaultValue;
        }

        public String getId() {
            return id;
        }
    }
    public static class RangedOption extends Option {
        private final Integer min;
        private final Integer max;

        /**
         * Instantiates a new ranged configuration option.
         *
         * @param id         The option's identifier.
         * @param defaultVal The option's default value.
         * @param min        The option's minimum value.
         * @param max        The option's maximum value.
         */
        private RangedOption(String id, Object defaultVal, Integer min, Integer max) {
            super(id, defaultVal);
            this.min = min;
            this.max = max;
        }

        public int getMinInt() {
            if (value instanceof Integer) return this.min;
            else throw new RuntimeException();
        }
        public int getMaxInt() {
            if (value instanceof Integer) return this.max;
            else throw new RuntimeException();
        }
    }
    public static class EnumOption <T extends Enum<?>> extends Option {
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

        @SuppressWarnings("unchecked")
        public T getEnum() {
            if (value instanceof Enum<?>) return (T) this.value;
            else throw new RuntimeException();
        }
        @SuppressWarnings("unchecked")
        public T getDefaultEnum() {
            if (value instanceof Enum<?>) return (T) this.defaultValue;
            else throw new RuntimeException();
        }

        public String getString() {
            if (value instanceof Enum<?>) return this.value.toString();
            else throw new RuntimeException();
        }
    }
}
