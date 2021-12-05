package net.splatcraft.config.option;

import com.google.gson.JsonElement;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

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

    public abstract AbstractConfigListEntry<T> createConfigListEntry(Identifier id, ConfigEntryBuilder builder);

    public TranslatableText getTitle(Identifier id) {
        return new TranslatableText("config.%s.%s".formatted(id.getNamespace(), id.getPath()));
    }

    public TranslatableText getTooltip(Identifier id) {
        return new TranslatableText("%s.tooltip".formatted(this.getTitle(id).getKey()));
    }

    @Override
    public String toString() {
        return "Option{" + "value=" + value + '}';
    }
}
