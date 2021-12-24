package net.splatcraft.config.option;

import com.google.gson.JsonElement;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Optional;

public abstract class Option<T> {
    private final T defaultValue;
    private T value;

    public Option(T defaultValue) {
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract JsonElement toJson();
    public abstract void fromJson(JsonElement json);

    protected void invalidConfig(JsonElement json) {
        throw new RuntimeException("Invalid config for %s: %s".formatted(this, json));
    }

    @Environment(EnvType.CLIENT)
    public abstract void addConfigEntries(ConfigCategory category, Identifier id, ConfigEntryBuilder builder);

    @Environment(EnvType.CLIENT)
    public String getTranslationKey(Identifier id) {
        return "config.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    @Environment(EnvType.CLIENT)
    public TranslatableText getTitle(Identifier id) {
        return new TranslatableText(this.getTranslationKey(id));
    }

    @Environment(EnvType.CLIENT)
    public Optional<Text[]> getTooltip(Identifier id) {
        String key = "%s.tooltip".formatted(this.getTranslationKey(id));
        return I18n.hasTranslation(key) ? Optional.of(new Text[]{ new TranslatableText(key) }) : Optional.empty();
    }

    @Override
    public String toString() {
        return "Option{" + "value=" + value + '}';
    }
}
