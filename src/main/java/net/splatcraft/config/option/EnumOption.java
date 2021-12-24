package net.splatcraft.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
    public void addConfigEntries(ConfigCategory category, Identifier id, ConfigEntryBuilder builder) {
        category.addEntry(
            builder.startEnumSelector(this.getTitle(id), this.clazz, this.getValue())
                   .setDefaultValue(this.getDefaultValue())
                   .setSaveConsumer(this::setValue)
                   .setTooltip(this.getTooltip(id))
                   .setEnumNameProvider(e -> this.getValueText(id, e))
                   .build()
        );
    }

    @Environment(EnvType.CLIENT)
    private Text getValueText(Identifier id, Enum<?> value) {
        return new TranslatableText("%s.value.%s".formatted(this.getTranslationKey(id), value.toString().toLowerCase()));
    }
}
