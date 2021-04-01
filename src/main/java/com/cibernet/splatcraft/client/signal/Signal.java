package com.cibernet.splatcraft.client.signal;

import com.cibernet.splatcraft.Splatcraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class Signal {
    public final Identifier id;
    public final TranslatableText text;

    public Signal(Identifier id) {
        this.id = id;
        this.text = new TranslatableText(Splatcraft.MOD_ID + ".signal." + id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Signal signal = (Signal) o;
        return Objects.equals(id, signal.id);
    }

    public Signal next() {
        int newId = SignalRegistryManager.SIGNALS.indexOf(this.id) + 1;
        return SignalRegistryManager.fromIndex(newId > (long) SignalRegistryManager.SIGNALS.size() - 1 ? 0 : newId);
    }
}
