package net.splatcraft.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class IntOption extends Option<Integer> {
    public IntOption(Integer defaultValue) {
        super(defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.value);
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isNumber()) {
            this.value = primitive.getAsInt();
        } else invalidConfig(json);
    }
}
