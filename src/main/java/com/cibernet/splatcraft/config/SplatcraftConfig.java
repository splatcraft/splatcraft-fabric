package com.cibernet.splatcraft.config;

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
    }

    /**
     * A configuration option.
     */
    public static class Option {
        private final String id;
        public Object value;
        private final Object defaultValue;

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
        public float getDefaultFloat() {
            if (value instanceof Float) return (Float)this.defaultValue;
            else throw new RuntimeException();
        }

        public String getId() {
            return id;
        }
    }
    public static class RangedOption extends Option {
        private final Object min;
        private final Object max;

        /**
         * Instantiates a new ranged configuration option.
         *
         * @param id         The option's identifier.
         * @param defaultVal The option's default value.
         * @param min        The option's minimum value.
         * @param max        The option's maximum value.
         */
        private RangedOption(String id, Object defaultVal, Object min, Object max) {
            super(id, defaultVal);
            this.min = min;
            this.max = max;
        }

        public float getMinFloat() {
            if (value instanceof Float) return (Float)this.min;
            else throw new RuntimeException();
        }
        public float getMaxFloat() {
            if (value instanceof Float) return (Float)this.max;
            else throw new RuntimeException();
        }

        public int getMinInt() {
            if (value instanceof Integer) return (Integer)this.min;
            else throw new RuntimeException();
        }
        public int getMaxInt() {
            if (value instanceof Integer) return (Integer)this.max;
            else throw new RuntimeException();
        }
    }
}
