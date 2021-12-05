package net.splatcraft.config.option;

import com.google.gson.JsonElement;

public abstract class Option<T> {
    private final T defaultValue;
    protected T value;

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

    public abstract JsonElement toJson();
    public abstract void fromJson(JsonElement json);

    protected void invalidConfig(JsonElement json) {
        throw new RuntimeException("Invalid config for %s: %s".formatted(this, json));
    }

    @Override
    public String toString() {
        return "Option{" + "value=" + value + '}';
    }
}
