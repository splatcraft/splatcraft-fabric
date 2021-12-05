package net.splatcraft.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanOption extends Option<Boolean> {
    public BooleanOption(Boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.value);
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isBoolean()) {
            this.value = primitive.getAsBoolean();
        } else invalidConfig(json);
    }
}
