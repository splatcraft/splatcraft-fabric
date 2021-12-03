package net.splatcraft.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class EnumOption<T extends Enum<T>> extends Option<T> {
    private final Class<T> clazz;

    public EnumOption(Class<T> clazz, T defaultValue) {
        super(defaultValue);
        this.clazz = clazz;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(String.valueOf(this.getValue()));
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isString()) {
            this.value = T.valueOf(this.clazz, primitive.getAsString());
        } else {
            throw new RuntimeException("Invalid config for %s: %s".formatted(this, json));
        }
    }
}
