package net.splatcraft.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class IntOption extends Option<Integer> {
    private final Integer min;
    private final Integer max;

    protected IntOption(Integer defaultValue, Integer min, Integer max) {
        super(defaultValue);
        this.min = min;
        this.max = max;
    }

    public static IntOption of(Integer defaultValue, Integer min, Integer max) {
        return new IntOption(defaultValue, min, max);
    }

    public static IntOption ofMin(Integer defaultValue, Integer min) {
        return of(defaultValue, min, null);
    }

    public static IntOption ofMax(Integer defaultValue, Integer max) {
        return of(defaultValue, null, max);
    }

    public static IntOption ofFree(Integer defaultValue) {
        return of(defaultValue, null, null);
    }

    public boolean isRanged() {
        return this.min != null && this.max != null;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.getValue());
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isNumber()) {
            this.setValue(primitive.getAsInt());
        } else invalidConfig(json);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void addConfigEntries(ConfigCategory category, Identifier id, ConfigEntryBuilder builder) {
        if (this.isRanged()) {
            category.addEntry(
                builder.startIntSlider(this.getTitle(id), this.getValue(), this.min, this.max)
                       .setMin(this.min)
                       .setMax(this.max)
                       .setDefaultValue(this.getDefaultValue())
                       .setSaveConsumer(this::setValue)
                       .setTooltip(this.getTooltip(id))
                       .setTextGetter(i -> this.getValueText(id, i))
                       .build()
            );
        } else {
            IntFieldBuilder field = builder.startIntField(this.getTitle(id), this.getValue())
                                           .setDefaultValue(this.getDefaultValue())
                                           .setSaveConsumer(this::setValue)
                                           .setTooltip(this.getTooltip(id));
            if (this.min != null) field.setMin(this.min);
            if (this.max != null) field.setMax(this.max);
            category.addEntry(field.build());
        }
    }

    @Environment(EnvType.CLIENT)
    private Text getValueText(Identifier id, Integer value) {
        return new TranslatableText("%s.value".formatted(this.getTranslationKey(id)), value);
    }
}
