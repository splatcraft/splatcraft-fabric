package net.splatcraft.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class EnumOption<T extends Enum<T>> extends Option<T> {
    private final Class<T> clazz;

    protected EnumOption(Class<T> clazz, T defaultValue) {
        super(defaultValue);
        this.clazz = clazz;
    }

    public static <T extends Enum<T>> EnumOption<T> of(Class<T> clazz, T defaultValue) {
        return new EnumOption<>(clazz, defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(String.valueOf(this.getValue()));
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isString()) {
            this.setValue(T.valueOf(this.clazz, primitive.getAsString()));
        } else invalidConfig(json);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AbstractConfigListEntry<T> createConfigListEntry(Identifier id, ConfigEntryBuilder builder) {
        return builder.startEnumSelector(this.getTitle(id), this.clazz, this.getValue())
                      .setDefaultValue(this.getDefaultValue())
                      .setSaveConsumer(this::setValue)
                      .setTooltip(this.getTooltip(id))
                      .build();
    }
}
