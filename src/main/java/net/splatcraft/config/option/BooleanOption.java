package net.splatcraft.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class BooleanOption extends Option<Boolean> {
    protected BooleanOption(Boolean defaultValue) {
        super(defaultValue);
    }

    public static BooleanOption of(boolean defaultValue) {
        return new BooleanOption(defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.getValue());
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isBoolean()) {
            this.setValue(primitive.getAsBoolean());
        } else invalidConfig(json);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void addConfigEntries(ConfigCategory category, Identifier id, ConfigEntryBuilder builder) {
        category.addEntry(
            builder.startBooleanToggle(this.getTitle(id), this.getValue())
                   .setDefaultValue(this.getDefaultValue())
                   .setSaveConsumer(this::setValue)
                   .setTooltip(this.getTooltip(id))
                   .build()
        );
    }
}
